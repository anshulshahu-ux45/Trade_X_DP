@echo off
title TradeForge - Design Pattern Lab
echo ========================================================================
echo   Launching TradeForge (Design Pattern Lab - 10 GoF Patterns Desktop App)
echo ========================================================================
echo.

REM Configure MySQL credentials
set TRADEFORGE_DB_USER=root
set TRADEFORGE_DB_PASSWORD=anshul
set TRADEFORGE_DB_URL=jdbc:mysql://localhost:3306/tradeforge_db?serverTimezone=UTC^&allowPublicKeyRetrieval=true^&useSSL=false

echo Checking for built JAR...
if not exist "target\tradeforge.jar" (
    echo Building TradeForge with Maven...
    call mvn clean package -DskipTests=true
    if %errorlevel% neq 0 (
        echo [ERROR] Build failed! Please ensure Maven and JDK 11+ are installed.
        pause
        exit /b %errorlevel%
    )
)

echo Starting TradeForge Desktop Application...
java -jar target\tradeforge.jar
if %errorlevel% neq 0 (
    echo.
    echo [NOTE] If launching failed, try running directly via Maven:
    echo   mvn exec:java
    pause
)
