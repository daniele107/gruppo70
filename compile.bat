@echo off
cd /d %~dp0

REM Try Maven wrapper first (downloads Maven automatically if needed)
echo Trying Maven compilation using wrapper...
echo This ensures all dependencies (Jackson, SLF4J, Logback, etc.) are correctly resolved.
echo Running: mvnw.cmd clean compile
mvnw.cmd clean compile

if %errorlevel% neq 0 (
    echo Maven wrapper compilation failed with exit code: %errorlevel%
    echo.
    echo If you have Maven installed, you can also run: mvn clean compile
    echo.
    echo Falling back to manual compilation (requires JAR files to be present)...
    goto :manual_compile
) else (
    echo Maven wrapper compilation successful!
    goto :maven_success
)

:manual_compile
echo Performing manual compilation with required dependencies...

REM Clean target directory
if exist target\classes rmdir /s /q target\classes
mkdir target\classes

REM Compile basic components first
echo Compiling database components...
javac -encoding UTF-8 -cp "postgresql.jar" -d target/classes src/main/java/database/*.java
if %errorlevel% neq 0 (
    echo Error compiling database components
    exit /b %errorlevel%
)

REM Compile model components first (required by DAO interfaces)
echo Compiling model components...
javac -encoding UTF-8 -cp "target/classes;postgresql.jar;javax.mail.jar;jackson-databind-2.15.2.jar;jackson-core-2.15.2.jar;jackson-annotations-2.15.2.jar" -d target/classes src/main/java/model/*.java
if %errorlevel% neq 0 (
    echo Error compiling model components
    exit /b %errorlevel%
)

echo Compiling DAO interfaces...
javac -encoding UTF-8 -cp "target/classes;postgresql.jar;javax.mail.jar;jackson-databind-2.15.2.jar;jackson-core-2.15.2.jar;jackson-annotations-2.15.2.jar" -d target/classes src/main/java/dao/*.java
if %errorlevel% neq 0 (
    echo Error compiling DAO interfaces
    exit /b %errorlevel%
)

echo Compiling DAO implementations...
javac -encoding UTF-8 -cp "target/classes;postgresql.jar;javax.mail.jar" -d target/classes src/main/java/dao/impl/*.java
if %errorlevel% neq 0 (
    echo Error compiling DAO implementations
    exit /b %errorlevel%
)

echo Compiling PostgreSQL DAO implementations...
javac -encoding UTF-8 -cp "target/classes;postgresql.jar;javax.mail.jar;jackson-databind-2.15.2.jar;jackson-core-2.15.2.jar;jackson-annotations-2.15.2.jar" -d target/classes src/main/java/dao/postgres/*.java
if %errorlevel% neq 0 (
    echo Error compiling PostgreSQL DAO implementations
    exit /b %errorlevel%
)

echo Compiling controller...
javac -encoding UTF-8 -cp "target/classes;postgresql.jar;javax.mail.jar;jackson-databind-2.15.2.jar;jackson-core-2.15.2.jar;jackson-annotations-2.15.2.jar" -d target/classes src/main/java/controller/*.java
if %errorlevel% neq 0 (
    echo Error compiling controller
    exit /b %errorlevel%
)

echo Compiling GUI components...
javac -encoding UTF-8 -cp "target/classes;postgresql.jar;javax.mail.jar;jackson-databind-2.15.2.jar;jackson-core-2.15.2.jar;jackson-annotations-2.15.2.jar" -d target/classes src/main/java/gui/*.java
if %errorlevel% neq 0 (
    echo Error compiling GUI components
    exit /b %errorlevel%
)

echo Compiling service components...
javac -encoding UTF-8 -cp "target/classes;postgresql.jar;javax.mail.jar;jackson-databind-2.15.2.jar;jackson-core-2.15.2.jar;jackson-annotations-2.15.2.jar" -d target/classes src/main/java/service/*.java
if %errorlevel% neq 0 (
    echo Error compiling service components
    exit /b %errorlevel%
)

echo Compiling main application...
javac -encoding UTF-8 -cp "target/classes;postgresql.jar;javax.mail.jar;jackson-databind-2.15.2.jar;jackson-core-2.15.2.jar;jackson-annotations-2.15.2.jar" -d target/classes src/main/java/app/Main.java
if %errorlevel% neq 0 (
    echo Error compiling main application
    exit /b %errorlevel%
)

echo Manual compilation completed successfully!
goto :end

:maven_success
echo Creating executable JAR...
mvn package -DskipTests
if %errorlevel% neq 0 (
    echo JAR creation failed!
    exit /b %errorlevel%
)

echo Compilation and packaging completed successfully!
echo The executable JAR is available in the target/ directory as: Applicativo-1.0-SNAPSHOT.jar

:end
echo Build completed!
