# Quick Start Guide - Allenamento Manager

## Quick Execution

### On macOS (Recommended)
```bash
./run.sh
```

### On Windows
Double click on `run.bat` or:
```cmd
run.bat
```

### On Linux
```bash
chmod +x run-linux.sh
./run-linux.sh
```

## If scripts don't work

### macOS/Linux
```bash
java -XstartOnFirstThread -jar target/allenamento-manager-1.0.0-jar-with-dependencies.jar
```

### Windows
```cmd
java -jar target/allenamento-manager-1.0.0-jar-with-dependencies.jar
```

## Prerequisites
- Java 11+ installed
- Compiled JAR file (`target/allenamento-manager-1.0.0-jar-with-dependencies.jar`)

If the JAR doesn't exist, run first:
```bash
mvn clean package assembly:single
```

## Application Usage

1. **Create a Training**: Click "New Training"
2. **Search**: Use filters at the bottom to filter by type, training name, objective, age, training phase, equipment
3. **View Details**: Double click on a training
4. **Add Images**: In the detail window, click "Add Image"
5. **Delete**: Select a training and click "Delete"

## Database

The SQLite database is created automatically:
- File: `allenamenti.db` (in execution directory)
- Images: `data/images/`

To backup:
```bash
cp allenamenti.db allenamenti_backup.db
```

## Troubleshooting

### Error "Module javafx not found" on macOS
Use `./run.sh` which includes the mandatory `-XstartOnFirstThread` option.

### GUI doesn't appear
Make sure you have a relatively recent version of macOS/Windows/Linux with graphics support.

### Database locked
Close all instances of the app and try again.

### "Unsupported JavaFX configuration" warning
It's just a warning, the app continues to work normally.

---

**Version**: 1.0.0  
**Date**: February 2026  
**Required System**: Java 11+
