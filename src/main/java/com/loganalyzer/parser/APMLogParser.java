package com.loganalyzer.parser;

import com.loganalyzer.model.APMLog;
import com.loganalyzer.model.LogEntry;
import java.util.Map;

public class APMLogParser extends LogParser {
    
    @Override
    protected boolean canParse(Map<String, String> fields) {
        return fields.containsKey("metric") && fields.containsKey("value");
    }

    @Override
    protected LogEntry parse(Map<String, String> fields) {
        try {
            String timestamp = fields.get("timestamp");
            String host = fields.get("host");
            String metric = fields.get("metric");
            double value = Double.parseDouble(fields.get("value"));
            
            return new APMLog(timestamp, host, metric, value, fields);
        } catch (NumberFormatException | NullPointerException e) {
            // Log parsing failed, return null to indicate corrupted/incompatible log
            return null;
        }
    }
}
