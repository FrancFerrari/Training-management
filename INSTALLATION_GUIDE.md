# INSTALLATION_GUIDE.md - Detailed Installation Guide

## Prerequisites
- Computer with at least 2GB of RAM
- Internet connection (for downloading dependencies)
- About 500MB of free space

## Step 1: Install Java Development Kit (JDK)

### On Windows
1. Download JDK 11+ from: https://www.oracle.com/java/technologies/downloads/
2. Run the installer
3. Follow installation instructions
4. Add Java to PATH environment variables:
   - Right-click on "Computer" → Properties
   - Environment Variables
   - PATH Variable → Add `C:\Program Files\Java\jdk-XX\bin`

### On macOS
```bash
brew install openjdk@11
# Or download from https://www.oracle.com/java/technologies/downloads/
```

### On Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

### Verifica Installazione
```bash
java -version
javac -version
```

## Step 2: Install Maven

### On Windows
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Maven`
3. Add to PATH: `C:\Program Files\Maven\bin`

### Su macOS
```bash
brew install maven
```

### Su Linux
```bash
sudo apt install maven
```

### Verifica Installazione
```bash
mvn --version
```

## Step 3: Clone/Download the Project

```bash
# Navigate to desired directory
cd ~/Projects

# If you have Git
git clone <repository-url>

# Otherwise download ZIP file and extract
unzip AllenamentoManager.zip
cd AllenamentoManager
```

## Step 4: Compile the Project

```bash
# Position in project root
cd AllenamentoManager

# Run compilation
mvn clean compile

# You should see: [INFO] BUILD SUCCESS
```

If you see dependency errors, try:
```bash
mvn clean install -DskipTests
```

## Step 5: Compile the Executable JAR

```bash
# From project root:
mvn clean package assembly:single
```

This will create the file: `target/allenamento-manager-1.0.0-jar-with-dependencies.jar`

## Step 6: Run the Application

### Metodo 1: JAR Eseguibile con Script (macOS)
**CONSIGLIATO**: Usa lo script launcher fornito:
```bash
./run.sh
```

### Metodo 1b: Script Manuale su macOS
```bash
java -XstartOnFirstThread -jar target/allenamento-manager-1.0.0-jar-with-dependencies.jar
```

### Metodo 1c: Maven Build (per sviluppo)
```bash
mvn clean package assembly:single
```

### Metodo 2: JAR Eseguibile (Sconsigliato)
```bash
# Crea il JAR
mvn clean package assembly:single

# Su macOS, il comando diretto non funziona. Usa run.sh (vedi sotto)
```

### Metodo 2b: Script Launcher macOS (CONSIGLIATO)
```bash
# Su macOS, usa lo script run.sh fornito
./run.sh
```

**Nota**: Su macOS, JavaFX richiede l'opzione `-XstartOnFirstThread` che è già inclusa nello script.

### Metodo 3: IDE (IntelliJ IDEA o Eclipse)
1. Apri il progetto nell'IDE
2. Tasto destro su `AllenamentoManagerApp.java`
3. Click su "Run" (oppure Shift+F10 in IntelliJ)

## Verifiche Post-Installazione

1. **Database**: Controlla che sia stato creato `allenamenti.db` nella root
2. **Directory Immagini**: La directory `data/images` deve essere creata
3. **UI**: L'interfaccia deve caricarsi completamente (tabella + pulsanti visibili)
4. **Funzionalità**: 
   - Crea un nuovo allenamento
   - Aggiungi un'immagine
   - Cerca per nome
   - Elimina l'allenamento

## Problemi Comuni

### "Command not found: java"
La variabile PATH non è configurata. Aggiungi Java a PATH e riavvia il terminale.

### "Could not find JavaFX"
Assicurati di usare `mvn javafx:run` e non `mvn run`.

### "Database locked"
Il database è già in uso. Chiudi altre istanze dell'app e riprova.

### Errore su macOS: "Cannot open" 
Se si apre con l'antiquato Java Runtime, specifica esplicitamente:
```bash
/usr/libexec/java_home -v 11 --exec java -jar target/*.jar
```

## Prossimi Passi

1. Leggi il README.md per la documentazione completa
2. Sperimenta con la creazione di allenamenti
3. Carica alcune immagini di prova
4. Usa la ricerca per filtrare gli allenamenti

## Per Sviluppatori

Se vuoi contribuire:
1. Fai una copia del repository
2. Crea un nuovo branch: `git checkout -b feature/mia-feature`
3. Implementa le modifiche
4. Testa completamente
5. Fai un push e crea una Pull Request

## Contatti Support

In caso di problemi, controlla:
- I log nella console
- Il file README.md
- Che tutte le dipendenze siano state installate: `mvn dependency:tree`
