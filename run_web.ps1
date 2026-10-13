# PowerShell Web Server Launcher
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

if (-not (Test-Path "bin\com\pms\web\WebServer.class")) {
    Write-Host "Compiling project first..." -ForegroundColor Yellow
    & .\compile.ps1
}

Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host " Profile Management Web Application is launching!" -ForegroundColor Green
Write-Host " Access URL: http://localhost:8080" -ForegroundColor Yellow
Write-Host " Press Ctrl+C in terminal to stop server." -ForegroundColor Gray
Write-Host "=======================================================" -ForegroundColor Cyan

java -cp "bin;lib/*" com.pms.web.WebServer
