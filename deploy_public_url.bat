@echo off
cd /d "%~dp0"
echo =======================================================
echo  Deploying Public URL for Profile Management System...
echo =======================================================
echo.
echo 1. Ensure run_web.bat is running in another terminal.
echo 2. Generating your public live URL via localtunnel...
echo.

npx -y localtunnel --port 8080
