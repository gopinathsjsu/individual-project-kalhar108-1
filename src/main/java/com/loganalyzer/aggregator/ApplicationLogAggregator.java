package com.loganalyzer.aggregator;

import com.loganalyzer.model.ApplicationLog;
import com.loganalyzer.model.LogEntry;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationLogAggregator implements LogAggregator {

    @Override
    public boolean canAggregate(String logType) {
        return "APPLICATION".equals(logType);
    }

    @Override
    public JsonObject aggregate(List<LogEntry> logs) {
        JsonObject result = new JsonObject();

        // Count logs by severity level
        Map<String, Integer> levelCounts = new HashMap<>();
        
        for (LogEntry log : logs) {
            if (log instanceof ApplicationLog) {
                ApplicationLog appLog = (ApplicationLog) log;
                String level = appLog.getLevel();
                levelCounts.put(level, levelCounts.getOrDefault(level, 0) + 1);
            }
        }

        // Add counts to result
        for (Map.Entry<String, Integer> entry : levelCounts.entrySet()) {
            result.addProperty(entry.getKey(), entry.getValue());
        }

        return result;
    }

    @Override
    public String getOutputFileName() {
        return "application.json";
    }
}
