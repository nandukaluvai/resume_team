@echo off
cd /d "%~dp0"
echo =======================================================
echo  Starting Profile Management Web Server (Java SE)...
echo =======================================================

if not exist "bin\com\pms\web\WebServer.class" (
    echo Compiling first...
    call compile.bat
)

echo.
echo =======================================================
echo  Access URL: http://localhost:8080
echo  Press Ctrl+C to stop the web server
echo =======================================================
echo.

java -cp "bin;lib/*" com.pms.web.WebServer
