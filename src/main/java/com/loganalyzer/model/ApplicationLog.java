package com.loganalyzer.model;

import java.util.Map;

public class ApplicationLog extends LogEntry {
    private String level;
    private String message;

    public ApplicationLog(String timestamp, String host, String level, String message, Map<String, String> rawFields) {
        super(timestamp, host, rawFields);
        this.level = level;
        this.message = message;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getLogType() {
        return "APPLICATION";
    }

    @Override
    public String toString() {
        return "ApplicationLog{" +
                "timestamp='" + timestamp + '\'' +
                ", level='" + level + '\'' +
                ", message='" + message + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
