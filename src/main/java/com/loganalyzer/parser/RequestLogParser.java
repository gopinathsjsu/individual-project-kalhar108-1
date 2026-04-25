package com.loganalyzer.parser;

import com.loganalyzer.model.LogEntry;
import com.loganalyzer.model.RequestLog;
import java.util.Map;

public class RequestLogParser extends LogParser {
    
    @Override
    protected boolean canParse(Map<String, String> fields) {
        return fields.containsKey("request_method") && 
               fields.containsKey("request_url") &&
               fields.containsKey("response_status") &&
               fields.containsKey("response_time_ms");
    }

    @Override
    protected LogEntry parse(Map<String, String> fields) {
        try {
            String timestamp = fields.get("timestamp");
            String host = fields.get("host");
            String requestMethod = fields.get("request_method");
            String requestUrl = fields.get("request_url");
            int responseStatus = Integer.parseInt(fields.get("response_status"));
            int responseTimeMs = Integer.parseInt(fields.get("response_time_ms"));
            
            return new RequestLog(timestamp, host, requestMethod, requestUrl, 
                                 responseStatus, responseTimeMs, fields);
        } catch (NumberFormatException | NullPointerException e) {
            // Log parsing failed, return null to indicate corrupted/incompatible log
            return null;
        }
    }
}
