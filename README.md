# Allenamento Manager

## Description
Allenamento Manager is a modern Java desktop application for managing a training database. It allows you to create, modify, delete, and view trainings with support for attached images.

## System Requirements
- **Java**: JDK 11 or higher
- **Maven**: 3.6 or higher
- **Operating System**: Windows, macOS, Linux

## Project Structure

```
AllenamentoManager/
├── src/
│   └── main/
│       ├── java/com/allenamento/
│       │   ├── AllenamentoManagerApp.java          # Main class
│       │   ├── model/
│       │   │   ├── Allenamento.java                # Training Model
│       │   │   └── Immagine.java                   # Image Model
│       │   ├── dao/
│       │   │   ├── DatabaseConnection.java         # DB Connection Management
│       │   │   ├── AllenamentoDAO.java             # Training DAO
│       │   │   └── ImmagineDAO.java                # Image DAO
│       │   ├── controller/
│       │   │   └── AllenamentoController.java      # MVC Controller
│       │   ├── view/
│       │   │   ├── MainWindow.java                 # Main window
│       │   │   ├── AllenamentoDialog.java          # New/Edit Dialog
│       │   │   └── AllenamentoDetailWindow.java    # Details window
│       │   └── util/
│       │       └── ImageManager.java               # Image management
│       └── resources/
├── sql/
│   └── schema.sql                                  # Database schema
├── data/
│   └── images/                                     # Images directory
├── pom.xml                                         # Maven configuration
└── README.md                                       # This file
```

## Architecture

The application follows the **Model-View-Controller (MVC)** pattern:

- **Model**: `Allenamento` and `Immagine` classes that represent data
- **View**: JavaFX components (`MainWindow`, `AllenamentoDialog`, `AllenamentoDetailWindow`)
- **Controller**: `AllenamentoController` that manages application logic
- **DAO**: Classes for database access (Data Access Object pattern)
- **Utility**: `ImageManager` for image file management

## Environment Setup

### 1. Install Java
Verify installation with:
```bash
java -version
javac -version
```

### 2. Install Maven
Verify installation with:
```bash
mvn --version
```

## Compilation

From the project root, run:

```bash
mvn clean package assembly:single
```

This command:
- `clean`: Removes previous compiled files
- `package assembly:single`: Compiles and creates an executable JAR with all dependencies

## Execution

### Option 1: Launcher Scripts (RECOMMENDED)

#### Su macOS/Linux:
```bash
./run.sh
```

#### Su Windows:
```cmd
run.bat
```

### Option 2: Manual Command

#### On macOS/Linux:
```bash
java -XstartOnFirstThread -jar target/allenamento-manager-1.0.0-jar-with-dependencies.jar
```

#### On Windows:
```cmd
java -jar target/allenamento-manager-1.0.0-jar-with-dependencies.jar
```

### Option 3: IDE (IntelliJ IDEA or Eclipse)
1. Open the project
2. Right-click on `MainWindow.java`
3. Select "Run" or "Run As > Java Application"

## Features

### Main Dashboard
- Display of all trainings in a table
- Double click to view details
- Sort by modification date (most recent first)

### Training Management
- **New Training**: "New Training" button to add a new record
- **Edit**: Select a training and click "Edit"
- **Delete**: Select a training and click "Delete" (with confirmation)
- **Refresh**: Button to reload data from database

### Training Fields
- **Name**: Training name
- **Age**: Target age range (e.g.: 25-30, Seniors)
- **Type**: Category (Strength, Cardio, Mobility, Flexibility, Endurance, Balance, Other)
- **Description**: Descriptive text about the training
- **Notes**: Additional information
- **Images**: Supports JPG, PNG, GIF, BMP

### Search and Filters
The bottom search bar allows filtering trainings by:
- **Name**: Search by name (case-insensitive)
- **Age**: Search by age range
- **Type**: Selection from available types dropdown
- **Description**: Text search in description

Filters can be combined. Use "Clear Filters" to reset.

### Training Details
Displays:
- All training information
- Associated images timeline
- Option to add new images
- Option to remove images

## Database

### SQLite
L'applicazione usa SQLite come database locale. Il file del database viene creato automaticamente come `allenamenti.db` nella root del progetto.

### Schema
Il database contiene due tabelle:
- **allenamenti**: Memorizza le informazioni degli allenamenti
- **immagini**: Memorizza i riferimenti alle immagini allegate

Lo schema viene creato automaticamente al primo avvio.

### Backup
Per fare backup del database:
```bash
cp allenamenti.db allenamenti_backup.db
```

## Gestione Immagini

Le immagini vengono salvate nella directory `data/images/`:
- Le immagini sono copiate nel filesystem
- Nel database viene memorizzato il percorso del file
- Le immagini sono generate con un nome univoco (UUID + timestamp)
- Eliminare un allenamento elimina automaticamente le immagini associate

Formati supportati: JPG, JPEG, PNG, GIF, BMP

## Dipendenze

Le dipendenze sono gestite automaticamente da Maven. Principali librerie:
- **JavaFX 20.0.1**: Framework per interfaccia grafica
- **SQLite JDBC 3.44.0**: Driver JDBC per SQLite
- **SLF4J 2.0.9**: Logging framework

## Troubleshooting

### Errore: "Driver SQLite non trovato"
**Soluzione**: Esegui `mvn clean install` per assicurarsi che le dipendenze siano scaricate.

### Errore di compilazione su macOS/Linux
**Soluzione**: Assicurati che Maven sia nelle variabili di ambiente:
```bash
export PATH=/path/to/maven/bin:$PATH
```

### Errore: Finestra bianca o non carica completamente
**Soluzione**: Prova con:
```bash
mvn clean package
java -jar target/allenamento-manager-1.0.0-jar-with-dependencies.jar
```

### Errore di permessi su file immagine
**Soluzione**: Assicurati che la directory `data/images/` sia scrivibile:
```bash
chmod 755 data/images/
```

## Development

### Aggiungere un nuovo tipo di allenamento
Modifica il file `AllenamentoDialog.java` nella linea dove vengono aggiunti i tipi:
```java
tipoCombo.getItems().addAll("Forza", "Cardio", "Mobilità", ..., "Nuovo Tipo");
```

### Estendere il database
Modifica `sql/schema.sql` e il corrispondente DAO se necessario.

### Modificare lo stile dell'interfaccia
Modifica gli stili CSS inline nei file View (es: `-fx-background-color`, `-fx-font-size`).

## Performance e Ottimizzazioni

- Database con indici sui campi di ricerca
- Lazy loading delle immagini nella finestra di dettaglio
- Query prepared statements per prevenire SQL injection
- Pattern Singleton per la connessione al database

## Account e Sicurezza

Non è presente un sistema di login. L'applicazione è single-user. Per una versione multi-utente:
1. Aggiungere una tabella utenti
2. Implementare autenticazione
3. Aggiungere controllo accesso per allenamento

## License

Questo progetto è fornito come-è per scopi educativi.

## Supporto

Per problemi o suggerimenti, controlla:
1. I log nella console (controllare per errori specifici)
2. Il file `allenamenti.db` esiste e non è corrotto
3. Le directory necessarie sono state create
4. Hai Java 11+ e Maven 3.6+ installati

## Changelog

### Versione 1.0.0 (Iniziale)
- Funzionalità CRUD completa
- Interfaccia JavaFX moderna
- Ricerca e filtri
- Gestione immagini
- Database SQLite
- Pattern MVC

---

**Versione**: 1.0.0  
**Data**: February 2026  
**Linguaggio**: Java 11+  
**UI Framework**: JavaFX 20
