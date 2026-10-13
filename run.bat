@echo off
cd /d "%~dp0"
echo =======================================================
echo  Launching Profile Management System...
echo =======================================================

if not exist "bin\com\pms\Main.class" (
    echo Binary not found. Compiling first...
    call compile.bat
)

java -cp "bin;lib/*" com.pms.Main
