#!/bin/bash
# Script to run Training Manager on Linux

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
JAR_FILE="$SCRIPT_DIR/target/allenamento-manager-1.0.0-jar-with-dependencies.jar"

# Options required for JavaFX (none needed with JavaFX 11)
JAVAFX_OPTIONS=""

echo "Starting Training Manager..."
echo "JAR: $JAR_FILE"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found!"
    echo "Run first: mvn clean package assembly:single"
    exit 1
fi

java $JAVAFX_OPTIONS -jar "$JAR_FILE"
