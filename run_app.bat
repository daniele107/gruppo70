@echo off
cd /d %~dp0

echo Avvio Hackathon Manager...
echo.

REM Imposta il classpath con tutte le dipendenze
set CLASSPATH=target/classes;lib/postgresql.jar;lib/javax.mail.jar;lib/jackson-databind-2.15.2.jar;lib/jackson-core-2.15.2.jar;lib/jackson-annotations-2.15.2.jar

REM Avvia l'applicazione
echo Eseguendo: java -cp "%CLASSPATH%" app.Main
java -cp "%CLASSPATH%" app.Main

if %errorlevel% neq 0 (
    echo.
    echo Errore nell'avvio dell'applicazione. Exit code: %errorlevel%
    echo.
    echo Verifica che:
    echo 1. Java 17 sia installato
    echo 2. Le dipendenze JAR siano nella directory lib/
    echo 3. Il database PostgreSQL sia configurato correttamente
    echo.
    pause
) else (
    echo.
    echo Applicazione avviata con successo!
)
