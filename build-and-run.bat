@echo off
REM Build and Run Script for Log Analyzer

echo ====================================
echo Log Analyzer - Build and Run
echo ====================================
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven from https://maven.apache.org/
    exit /b 1
)

REM Clean and build the project
echo [1/3] Building project...
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed
    exit /b 1
)
echo Build successful!
echo.

REM Run tests
echo [2/3] Running tests...
call mvn test
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Some tests failed
) else (
    echo All tests passed!
)
echo.

REM Run the application with sample input (use shaded JAR)
echo [3/3] Running application with input.txt...
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --file input.txt
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Application execution failed
    exit /b 1
)
echo.

echo ====================================
echo Execution Complete!
echo.
echo Output files generated:
echo - apm.json
echo - application.json  
echo - request.json
echo ====================================
