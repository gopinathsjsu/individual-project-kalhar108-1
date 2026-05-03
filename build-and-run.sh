#!/bin/bash
# Build and Run Script for Log Analyzer

echo "===================================="
echo "Log Analyzer - Build and Run"
echo "===================================="
echo

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven from https://maven.apache.org/"
    exit 1
fi

# Clean and build the project
echo "[1/3] Building project..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "ERROR: Build failed"
    exit 1
fi
echo "Build successful!"
echo

# Run tests
echo "[2/3] Running tests..."
mvn test
if [ $? -ne 0 ]; then
    echo "WARNING: Some tests failed"
else
    echo "All tests passed!"
fi
echo

# Run the application with sample input
echo "[3/3] Running application with input.txt..."
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --file input.txt
if [ $? -ne 0 ]; then
    echo "ERROR: Application execution failed"
    exit 1
fi
echo

echo "===================================="
echo "Execution Complete!"
echo
echo "Output files generated:"
echo "- apm.json"
echo "- application.json"
echo "- request.json"
echo "===================================="
