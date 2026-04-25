package com.loganalyzer.parser;

import com.loganalyzer.model.LogEntry;
import java.util.*;

public class LogParserFactory {
    private final LogParser chainHead;

    public LogParserFactory() {
        // Build the chain of responsibility
        // Order matters: APM -> Application -> Request
        this.chainHead = new APMLogParser();
        LogParser applicationParser = new ApplicationLogParser();
        LogParser requestParser = new RequestLogParser();
        
        // Link the chain explicitly using setNext()
        this.chainHead.setNext(applicationParser).setNext(requestParser);
    }

    public Map<String, String> parseLogLine(String logLine) {
        Map<String, String> fields = new HashMap<>();
        
        // Split by spaces, but respect quoted values
        String[] parts = logLine.split("\\s+(?=\\w+=)");
        
        for (String part : parts) {
            int equalsIndex = part.indexOf('=');
            if (equalsIndex > 0) {
                String key = part.substring(0, equalsIndex);
                String value = part.substring(equalsIndex + 1);
                
                // Remove quotes if present
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                
                fields.put(key, value);
            }
        }
        
        return fields;
    }

    public LogEntry parse(String logLine) {
        if (logLine == null || logLine.trim().isEmpty()) {
            return null;
        }

        Map<String, String> fields = parseLogLine(logLine);
        
        // Start the chain of responsibility
        return chainHead.handle(fields);
    }

    public LogParser getChainHead() {
        return chainHead;
    }
}