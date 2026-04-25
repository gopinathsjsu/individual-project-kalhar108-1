package com.loganalyzer.parser;

import com.loganalyzer.model.LogEntry;
import java.util.Map;

public abstract class LogParser {
    protected LogParser nextParser;

    public LogParser setNext(LogParser next) {
        this.nextParser = next;
        return next;
    }

    public LogEntry handle(Map<String, String> fields) {
        if (canParse(fields)) {
            try {
                return parse(fields);
            } catch (Exception e) {
                System.err.println("WARN: Error parsing with " + this.getClass().getSimpleName() + 
                                 ": " + e.getMessage());
                // Try next parser even if this one failed
                if (nextParser != null) {
                    return nextParser.handle(fields);
                }
                return null;
            }
        } else if (nextParser != null) {
            return nextParser.handle(fields);
        }
        
        return null;
    }

    protected abstract boolean canParse(Map<String, String> fields);

    protected abstract LogEntry parse(Map<String, String> fields);
}
