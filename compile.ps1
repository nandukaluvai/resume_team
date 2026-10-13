# PowerShell Compilation Script
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host " Compiling Profile Management System (Java Swing)..." -ForegroundColor Cyan
Write-Host "=======================================================" -ForegroundColor Cyan

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

if (-not (Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }
if (-not (Test-Path "data")) { New-Item -ItemType Directory -Path "data" | Out-Null }

$sources = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
$sourcesFile = "sources_temp.txt"
[System.IO.File]::WriteAllLines($sourcesFile, $sources)

javac -encoding UTF-8 -cp "lib/*" -d bin "@$sourcesFile"
$compileStatus = $LASTEXITCODE
Remove-Item $sourcesFile -Force -ErrorAction SilentlyContinue

if ($compileStatus -eq 0) {
    Write-Host "[SUCCESS] All Java classes compiled successfully into bin/" -ForegroundColor Green
    Write-Host "Execute .\run.ps1 to launch the application." -ForegroundColor Yellow
} else {
    Write-Host "[ERROR] Compilation failed with error code $compileStatus" -ForegroundColor Red
    exit $compileStatus
}
