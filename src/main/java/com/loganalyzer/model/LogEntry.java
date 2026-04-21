package com.loganalyzer.model;

import java.util.Map;

public abstract class LogEntry {
    protected String timestamp;
    protected String host;
    protected Map<String, String> rawFields;

    public LogEntry(String timestamp, String host, Map<String, String> rawFields) {
        this.timestamp = timestamp;
        this.host = host;
        this.rawFields = rawFields;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getHost() {
        return host;
    }

    public Map<String, String> getRawFields() {
        return rawFields;
    }

    public abstract String getLogType();
}
