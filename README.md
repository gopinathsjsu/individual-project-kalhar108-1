# Log Analyzer - Individual Project (Kalhar Mayurbhai Patel - 019140511)
## Project Overview

This is a command-line application for parsing and aggregating different types of log entries from text files. The application implements design patterns (Strategy and Factory) to parse APM logs, Application logs, and Request logs, then generates JSON output files with statistical aggregations.

## Design Patterns Used

### 1. **Strategy Pattern**
   - **Purpose**: Provides different strategies for parsing and aggregating various log types
   - **Implementation**:
     - `LogParser` interface with concrete implementations: `APMLogParser`, `ApplicationLogParser`, `RequestLogParser`
     - `LogAggregator` interface with concrete implementations: `APMLogAggregator`, `ApplicationLogAggregator`, `RequestLogAggregator`
   - **Benefits**: Easy to add new log types without modifying existing code (Open/Closed Principle)

### 2. **Factory Pattern**
   - **Purpose**: Centralizes creation and selection of appropriate parsers
   - **Implementation**: `LogParserFactory` manages parser instances and selects the right one based on log content
   - **Benefits**: Encapsulates parser selection logic, maintains single responsibility

### 3. **Chain of Responsibility**
   - **Purpose**: Tries each parser in sequence until one can handle the log entry
   - **Implementation**: Within `LogParserFactory.parse()` method
   - **Benefits**: Flexible, extensible parsing without complex conditional logic

## Project Structure

```
log-analyzer/
├── pom.xml                          # Maven build configuration
├── input.txt                        # Sample log file
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/loganalyzer/
│   │           ├── LogAnalyzerApplication.java    # Main entry point
│   │           ├── model/
│   │           │   ├── LogEntry.java              # Base log class
│   │           │   ├── APMLog.java                # APM log model
│   │           │   ├── ApplicationLog.java        # Application log model
│   │           │   └── RequestLog.java            # Request log model
│   │           ├── parser/
│   │           │   ├── LogParser.java             # Parser strategy interface
│   │           │   ├── APMLogParser.java          # APM parser implementation
│   │           │   ├── ApplicationLogParser.java  # Application parser implementation
│   │           │   ├── RequestLogParser.java      # Request parser implementation
│   │           │   └── LogParserFactory.java      # Parser factory
│   │           ├── aggregator/
│   │           │   ├── LogAggregator.java         # Aggregator strategy interface
│   │           │   ├── APMLogAggregator.java      # APM aggregator implementation
│   │           │   ├── ApplicationLogAggregator.java  # App aggregator implementation
│   │           │   └── RequestLogAggregator.java  # Request aggregator implementation
│   │           └── io/
│   │               ├── LogFileReader.java         # File reading utility
│   │               └── JSONWriter.java            # JSON writing utility
│   └── test/
│       └── java/
│           └── com/loganalyzer/
│               ├── parser/
│               │   └── LogParserFactoryTest.java
│               └── aggregator/
│                   ├── APMLogAggregatorTest.java
│                   ├── ApplicationLogAggregatorTest.java
│                   └── RequestLogAggregatorTest.java
└── README.md
```

## Log Types and Aggregations

### 1. APM Logs
- **Identified by**: Presence of `metric` and `value` fields
- **Aggregations**: minimum, median, average, max for each metric type
- **Output**: `apm.json`

### 2. Application Logs
- **Identified by**: Presence of `level` and `message` fields
- **Aggregations**: Count by severity level (ERROR, INFO, WARNING, DEBUG)
- **Output**: `application.json`

### 3. Request Logs
- **Identified by**: Presence of `request_method`, `request_url`, `response_status`, `response_time_ms` fields
- **Aggregations**: 
  - Response time statistics (min, max, percentiles) per API route
  - Status code counts by category (2XX, 4XX, 5XX) per API route
- **Output**: `request.json`

## Building the Project

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Build Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package as JAR
mvn clean package
```

The compiled JAR will be located at: `target/log-analyzer-1.0-SNAPSHOT.jar`

## Running the Application

### Command Syntax
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --file <input-file.txt>
```

### Example
```bash
java -jar target/log-analyzer-1.0-SNAPSHOT.jar --file input.txt
```

### Output
The application generates three JSON files in the current directory:
- `apm.json` - APM metrics aggregations
- `application.json` - Application log counts by severity
- `request.json` - Request statistics per API route

## Running Tests

```bash
# Run all tests
mvn test

# Run tests with verbose output
mvn test -X

# Run specific test class
mvn test -Dtest=LogParserFactoryTest
```

## Sample Input Format

```
timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72
timestamp=2024-02-24T16:22:20Z level=INFO message="Scheduled maintenance starting" host=webserver1
timestamp=2024-02-24T16:22:25Z request_method=POST request_url="/api/update" response_status=202 response_time_ms=200 host=webserver1
```

## Sample Output

### apm.json
```json
{
  "cpu_usage_percent": {
    "minimum": 65.0,
    "median": 68.5,
    "average": 68.5,
    "max": 72.0
  },
  "memory_usage_percent": {
    "minimum": 85.0,
    "median": 87.5,
    "average": 87.5,
    "max": 90.0
  }
}
```

### application.json
```json
{
  "ERROR": 2,
  "INFO": 3,
  "DEBUG": 1,
  "WARNING": 1
}
```

### request.json
```json
{
  "/api/status": {
    "response_times": {
      "min": 100,
      "95_percentile": 180,
      "max": 180
    },
    "status_codes": {
      "2XX": 3,
      "4XX": 0,
      "5XX": 1
    }
  }
}
```

## Design Benefits

1. **Extensibility**: New log types can be added by creating new parser and aggregator classes without modifying existing code
2. **Maintainability**: Each component has a single responsibility
3. **Testability**: Each parser and aggregator can be tested independently
4. **Flexibility**: Corrupted or unrecognized logs are silently ignored
5. **Future-proofing**: Design supports different file formats through the Factory pattern

## Error Handling

- **Corrupted logs**: Silently ignored (returns null from parser)
- **Missing files**: IOException with clear error message
- **Invalid arguments**: Usage message displayed
- **Empty output**: JSON files created even with no matching logs

## Dependencies

- **Gson 2.10.1**: JSON serialization/deserialization
- **JUnit 5.9.2**: Unit testing framework

## Author

Kalhar Mayurbhai Patel (019140511)
