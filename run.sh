#!/bin/bash
# Script to run Training Manager on macOS

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
JAR_FILE="$SCRIPT_DIR/target/allenamento-manager-1.0.0-jar-with-dependencies.jar"

# Use Java 21 if available, otherwise use system version
if [ -d "/opt/homebrew/opt/openjdk@21/bin" ]; then
    export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"
fi

# Options required for JavaFX
JAVAFX_OPTIONS=""

echo "========================================"
echo "  Starting Training Manager"
echo "========================================"
echo "JAR: $JAR_FILE"
echo ""

NEEDS_BUILD=false

if [ ! -f "$JAR_FILE" ]; then
    NEEDS_BUILD=true
fi

if [ "$NEEDS_BUILD" = false ] && find "$SCRIPT_DIR/src/main" -type f \( -name "*.java" -o -name "*.fxml" \) -newer "$JAR_FILE" | grep -q .; then
    NEEDS_BUILD=true
fi

if [ "$NEEDS_BUILD" = true ]; then
    echo "🔧 Building (JAR missing or outdated)..."
    (cd "$SCRIPT_DIR" && mvn -q clean package assembly:single)
    if [ $? -ne 0 ]; then
        echo "❌ Maven build error"
        exit 1
    fi
    echo "✅ Build completed"
    echo ""
fi

echo "✅ Starting application..."
echo "💡 If you don't see the window, check bottom-right of screen (might be minimized)"
echo "💡 Java version: $(java -version 2>&1 | head -1)"
echo ""

# Change to application directory to resolve relative paths
cd "$SCRIPT_DIR"

java $JAVAFX_OPTIONS -jar "$JAR_FILE"
