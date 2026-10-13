# PowerShell Run Script
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

if (-not (Test-Path "bin\com\pms\Main.class")) {
    Write-Host "Binary not found. Compiling first..." -ForegroundColor Yellow
    & .\compile.ps1
}

Write-Host "Starting Profile Management System..." -ForegroundColor Green
java -cp "bin;lib/*" com.pms.Main
