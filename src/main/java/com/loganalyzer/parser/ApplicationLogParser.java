package com.loganalyzer.parser;

import com.loganalyzer.model.ApplicationLog;
import com.loganalyzer.model.LogEntry;
import java.util.Map;

public class ApplicationLogParser extends LogParser {
    
    @Override
    protected boolean canParse(Map<String, String> fields) {
        return fields.containsKey("level") && fields.containsKey("message");
    }

    @Override
    protected LogEntry parse(Map<String, String> fields) {
        try {
            String timestamp = fields.get("timestamp");
            String host = fields.get("host");
            String level = fields.get("level");
            String message = fields.get("message");
            
            return new ApplicationLog(timestamp, host, level, message, fields);
        } catch (NullPointerException e) {
            // Log parsing failed, return null to indicate corrupted/incompatible log
            return null;
        }
    }
}
