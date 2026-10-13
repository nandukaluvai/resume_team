@echo off
echo =======================================================
echo  Compiling Profile Management System (Java Swing)...
echo =======================================================

cd /d "%~dp0"
if not exist "bin" mkdir bin
if not exist "data" mkdir data

dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "lib/*" -d bin @sources.txt
set COMPILE_RESULT=%ERRORLEVEL%
del sources.txt

if %COMPILE_RESULT% NEQ 0 (
    echo [ERROR] Compilation failed with error code %COMPILE_RESULT%
    pause
    exit /b %COMPILE_RESULT%
)

echo [SUCCESS] Compilation completed successfully into bin/
echo Run 'run.bat' to launch the application.
