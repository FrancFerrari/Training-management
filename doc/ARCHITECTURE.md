# ARCHITECTURE.md - Application Architecture and Design

## Overview

Allenamento Manager uses the **Model-View-Controller (MVC)** architecture to separate business logic from user interface presentation.

```
┌─────────────────────────────────────────────────────┐
│              LIVELLO PRESENTAZIONE                   │
│  (View Layer - JavaFX UI Components)                │
│  - MainWindow (finestra principale)                  │
│  - AllenamentoDialog (dialog create/update)          │
│  - AllenamentoDetailWindow (dettagli)                │
└────────────────────┬────────────────────────────────┘
                     │
                     ▼
         ┌───────────────────────┐
         │   CONTROLLER          │
         │  AllenamentoController│
         │  (Logica Applicativa) │
         └────────────┬──────────┘
                      │
        ┌─────────────┼──────────────┐
        ▼             ▼              ▼
    ┌─────────┐ ┌──────────┐ ┌──────────────┐
    │ DAO     │ │Utility   │ │ Model        │
    │Layer    │ │Layer     │ │ (POJO)       │
    │         │ │          │ │              │
    │ • Alle  │ │ • Image  │ │ • Allenamento
    │   namentoDAO  │   Manager │ │ • Immagine   │
    │ • Immagine    │          │ │              │
    │   DAO   │ │          │ │              │
    └────┬────┘ └──────────┘ └──────────────┘
         │
         ▼
    ┌─────────────────────┐
    │  DATABASE           │
    │  (SQLite)           │
    │  - allenamenti      │
    │  - immagini         │
    └─────────────────────┘

         FILE SYSTEM
    ┌─────────────────────┐
    │  data/images/       │
    │  (Image Files)      │
    └─────────────────────┘
```

## Main Components

### 1. Model Layer (`com.allenamento.model`)

**Allenamento.java**
- Represents a training in the system
- Contains: id, nome, eta, tipo, descrizione, note, dates, images
- Is a serializable POJO (Plain Old Java Object) class
- Manages a collection of associated images

**Immagine.java**
- Represents an image attached to a training
- Contains: id, allenamentoId, filename, path, dataUpload
- Link to filesystem through path

### 2. DAO Layer (`com.allenamento.dao`)

**DatabaseConnection.java**
- Singleton pattern to ensure a single active connection
- Handles automatic SQL schema initialization
- Thread-safe for multi-threading

**AllenamentoDAO.java**
- CRUD operations for trainings
- Methods: insert, update, delete, getById, getAll, search
- Executes parameterized queries (SQL injection prevention)
- Mapping from ResultSet to Java objects

**ImmagineDAO.java**
- CRUD operations for images
- Specialized methods: getByAllenamentoId
- Handles references between trainings and images

### 3. Controller Layer (`com.allenamento.controller`)

**AllenamentoController.java**
- Intermediary between View and Model
- Coordinates business logic operations
- Manages:
  - Creating/modifying/deleting trainings
  - Loading images
  - Cascade deletion (when deleting training, also deletes images)
  - "Current" training state

### 4. View Layer (`com.allenamento.view`)

**MainWindow.java**
- Main application window
- Extends JavaFX `Application`
- Components:
  - Header with title and action buttons
  - Training table (TableView)
  - Search bar with filters
- Manages UI refresh after operations

**AllenamentoDialog.java**
- Modal dialog for create/update
- Extends `Dialog<Allenamento>`
- Validation of required fields
- Populates fields if in edit mode

**AllenamentoDetailWindow.java**
- Detail window for a training
- Shows:
  - All model information
  - Image gallery
  - Options to add/remove images
- Uses FlowPane for gallery

### 5. Utility Layer (`com.allenamento.util`)

**ImageManager.java**
- Centralized image file management
- Functions:
  - File type validation
  - Unique name generation (UUID + timestamp)
  - Saving to disk
  - Deletion from disk
- Validated formats: JPG, PNG, GIF, BMP

### 6. Entry Point

**AllenamentoManagerApp.java**
- Application entry point
- Contains main() method
- Launches MainWindow (Application extension)

## Used Design Patterns

### 1. Singleton Pattern
**DatabaseConnection**: Ensures a single instance of database connection
```java
public static synchronized DatabaseConnection getInstance() {
    if (instance == null) {
        instance = new DatabaseConnection();
    }
    return instance;
}
```

### 2. DAO (Data Access Object) Pattern
Encapsulates database access in dedicated classes:
- AllenamentoDAO → operations on trainings
- ImmagineDAO → operations on images

### 3. MVC Pattern
Clear separation between:
- **Model**: data representation
- **View**: presentation (JavaFX)
- **Controller**: logic and coordination

### 4. Builder-like Pattern in Dialog
`AllenamentoDialog` uses Dialog<T> pattern to return typed results

## Main Application Flows

### Creating a Training
1. User clicks "New Training"
2. MainWindow opens AllenamentoDialog()
3. User fills fields and clicks OK
4. Dialog returns an Allenamento object
5. MainWindow.openNewAllenamentoDialog() calls Controller.createAllenamento()
6. Controller.createAllenamento() calls AllenamentoDAO.insert()
7. DAO generates SQL INSERT and returns ID
8. MainWindow refreshes table

### Search
1. User fills filters in search bar
2. Clicks "Search"
3. MainWindow.performSearch() collects parameters
4. Calls Controller.searchAllenamenti()
5. Controller queries AllenamentoDAO.search() with criteria
6. DAO builds dynamic SQL query with WHERE clause
7. Results are loaded into TableView

### Image Loading
1. In AllenamentoDetailWindow, user clicks "Add Image"
2. FileChooser opens to select file
3. Controller.addImage() is called with File
4. ImageManager.saveImage() copies file to data/images/ with unique name
5. ImmagineDAO.insert() saves reference in database
6. Gallery images are refreshed

## Database Schema

### Table: allenamenti
```sql
CREATE TABLE allenamenti (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    eta TEXT NOT NULL,
    tipo TEXT NOT NULL,
    descrizione TEXT,
    note TEXT,
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_modifica TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Table: immagini
```sql
CREATE TABLE immagini (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    allenamento_id INTEGER NOT NULL,  -- Foreign Key
    filename TEXT NOT NULL,
    path TEXT NOT NULL,
    data_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (allenamento_id) REFERENCES allenamenti(id) ON DELETE CASCADE
);
```

**Relationship**: One-to-Many
- A training can have 0 or more images
- Cascade deletion: removing a training deletes all its images

## Image Management

```
User selects file.jpg
        │
        ▼
ImageManager.saveImage(file)
        │
        ├→ Validates: is it an image?
        │
        ├─→ Generates: UUID_timestamp.jpg
        │
        ├→ Copies: file → data/images/UUID_timestamp.jpg
        │
        └→ Returns: path (e.g.: "data/images/UUID_timestamp.jpg")
                        │
                        ▼
                ImmagineDAO.insert(Immagine)
                        │
                        ▼
                   Database record
                   (path stored)
                        │
                        ▼
                    Display
                    (load from path)
```

## Exceptions and Error Handling

- **DatabaseConnectionException**: DB connection issue → RuntimeException
- **FileIOException**: Image save issue → Logged + null return
- **SQLSyntaxException**: Incorrect SQL queries → Logged + operation fails
- **User Feedback**: Alerts to inform user of successes/errors

## Logging

Uses SLF4J with Simple Logger:
- **DEBUG**: Selected operations (insert/update/delete)
- **INFO**: Successful operations
- **WARN**: Non-critical issues
- **ERROR**: Severe errors

Configuration in: `src/main/resources/simplelogger.properties`

## Performance Considerations

1. **Database Indexes**: Created on name, age, type for fast searches
2. **Lazy Loading**: Images loaded only when needed
3. **Prepared Statements**: SQL injection prevention + performance
4. **Singleton DB Connection**: Reduces connection creation overhead

## Security

1. **SQL Injection Prevention**: Use of PreparedStatement
2. **File Validation**: File type validation before saving
3. **File Naming**: UUID to avoid conflicts and path traversal
4. **Single User**: No authentication (assumes controlled environment)

## Extensibility

To add new features:

1. **New field in Allenamento**:
   - Add property in Allenamento.java
   - Add column in schema.sql
   - Add PreparedStatement in AllenamentoDAO
   - Add TextField in AllenamentoDialog

2. **New search functionality**:
   - Extend Controller.searchAllenamenti()
   - Modify AllenamentoDAO.search()
   - Add UI controls in MainWindow

3. **New data type**:
   - Create Model class
   - Crea DAO class
   - Crea View components
   - Integra in Controller

## Testing

Suggerimenti per unit tests:

```java
@Test
public void testAllenamentoCreation() {
    Allenamento a = new Allenamento("Test", "25", "Cardio", "Test", null);
    int id = dao.insert(a);
    assertTrue(id > 0);
    
    Allenamento retrieved = dao.getById(id);
    assertEquals("Test", retrieved.getNome());
}
```

## Deployment

1. Crea JAR con `mvn clean package assembly:single`
2. Distribuisci il JAR eseguibile
3. L'app creerà database e directory automaticamente
4. Facoltativo: Backup database periodicamente

---

**Questo documento è parte della documentazione di sviluppo per Allenamento Manager v1.0.0**
