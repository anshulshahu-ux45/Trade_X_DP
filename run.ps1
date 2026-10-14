# TradeForge PowerShell Launcher
Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host "  Launching TradeForge (Design Pattern Lab - 10 GoF Patterns Desktop App)" -ForegroundColor Cyan
Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host ""

$env:TRADEFORGE_DB_USER = "root"
$env:TRADEFORGE_DB_PASSWORD = "anshul"
$env:TRADEFORGE_DB_URL = "jdbc:mysql://localhost:3306/tradeforge_db?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false"

if (!(Test-Path "target\tradeforge.jar")) {
    Write-Host "Building TradeForge JAR with Maven..." -ForegroundColor Yellow
    mvn clean package -DskipTests=true
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Maven build failed!" -ForegroundColor Red
        Exit $LASTEXITCODE
    }
}

Write-Host "Starting TradeForge Desktop Application..." -ForegroundColor Green
java -jar target\tradeforge.jar
