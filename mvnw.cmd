@echo off
setlocal

REM Set Maven home if not already set
if not defined MAVEN_HOME (
    set MAVEN_HOME=%~dp0maven
)

REM Check if Maven exists
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Maven not found. Downloading Maven...
    if not exist maven.zip (
        echo Downloading Maven...
        REM Try curl first
        curl -L -o maven.zip "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip" --ssl-no-revoke
        if %errorlevel% neq 0 (
            echo Curl failed, trying alternative method...
            REM Alternative: use PowerShell for download
            powershell -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile 'maven.zip' -UseBasicParsing"
            if %errorlevel% neq 0 (
                echo Failed to download Maven. Please install Maven manually or download the zip file manually from:
                echo https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip
                echo and place it in this directory as maven.zip
                exit /b 1
            )
        )
    )
    echo Extracting Maven...
    powershell -Command "Expand-Archive -Path 'maven.zip' -DestinationPath 'maven' -Force"
    if %errorlevel% neq 0 (
        echo Failed to extract Maven. Please check your internet connection.
        exit /b 1
    )
    del maven.zip
)

REM Run Maven with all arguments passed to this script
"%MAVEN_HOME%\bin\mvn.cmd" %*
endlocal
