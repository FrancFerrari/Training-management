@echo off
REM Script to run Training Manager on Windows

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set JAR_FILE=%SCRIPT_DIR%target\allenamento-manager-1.0.0-jar-with-dependencies.jar

echo Starting Training Manager...
echo JAR: %JAR_FILE%

if not exist "%JAR_FILE%" (
    echo Error: JAR file not found!
    echo Run first: mvn clean package assembly:single
    pause
    exit /b 1
)

REM Change to application directory to resolve relative paths
cd /d "%SCRIPT_DIR"

java -jar "%JAR_FILE%"
pause
