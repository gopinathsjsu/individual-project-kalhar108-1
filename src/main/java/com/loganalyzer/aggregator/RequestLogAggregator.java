package com.loganalyzer.aggregator;

import com.loganalyzer.model.LogEntry;
import com.loganalyzer.model.RequestLog;
import com.google.gson.JsonObject;

import java.util.*;

public class RequestLogAggregator implements LogAggregator {

    @Override
    public boolean canAggregate(String logType) {
        return "REQUEST".equals(logType);
    }

    @Override
    public JsonObject aggregate(List<LogEntry> logs) {
        JsonObject result = new JsonObject();

        // Group logs by request URL
        Map<String, List<RequestLog>> logsByUrl = new HashMap<>();
        
        for (LogEntry log : logs) {
            if (log instanceof RequestLog) {
                RequestLog reqLog = (RequestLog) log;
                logsByUrl
                    .computeIfAbsent(reqLog.getRequestUrl(), k -> new ArrayList<>())
                    .add(reqLog);
            }
        }

        // Calculate statistics for each URL
        for (Map.Entry<String, List<RequestLog>> entry : logsByUrl.entrySet()) {
            String url = entry.getKey();
            List<RequestLog> urlLogs = entry.getValue();
            
            JsonObject urlStats = new JsonObject();
            
            // Response time statistics
            JsonObject responseTimes = calculateResponseTimeStats(urlLogs);
            urlStats.add("response_times", responseTimes);
            
            // Status code counts
            JsonObject statusCodes = calculateStatusCodeCounts(urlLogs);
            urlStats.add("status_codes", statusCodes);
            
            result.add(url, urlStats);
        }

        return result;
    }

    @Override
    public String getOutputFileName() {
        return "request.json";
    }

    private JsonObject calculateResponseTimeStats(List<RequestLog> logs) {
        JsonObject stats = new JsonObject();
        
        List<Integer> responseTimes = new ArrayList<>();
        for (RequestLog log : logs) {
            responseTimes.add(log.getResponseTimeMs());
        }
        
        Collections.sort(responseTimes);
        
        if (!responseTimes.isEmpty()) {
            int min = responseTimes.get(0);
            int max = responseTimes.get(responseTimes.size() - 1);
            
            stats.addProperty("min", min);
            stats.addProperty("95_percentile", calculatePercentile(responseTimes, 95));
            stats.addProperty("max", max);
        }
        
        return stats;
    }

    private int calculatePercentile(List<Integer> sortedValues, int percentile) {
        if (sortedValues.isEmpty()) {
            return 0;
        }
        
        int index = (int) Math.ceil((percentile / 100.0) * sortedValues.size()) - 1;
        index = Math.max(0, Math.min(index, sortedValues.size() - 1));
        
        return sortedValues.get(index);
    }

    private JsonObject calculateStatusCodeCounts(List<RequestLog> logs) {
        JsonObject counts = new JsonObject();
        
        Map<String, Integer> statusCategoryCounts = new HashMap<>();
        statusCategoryCounts.put("2XX", 0);
        statusCategoryCounts.put("4XX", 0);
        statusCategoryCounts.put("5XX", 0);
        
        for (RequestLog log : logs) {
            int status = log.getResponseStatus();
            String category = getStatusCategory(status);
            if (category != null) {
                statusCategoryCounts.put(category, statusCategoryCounts.get(category) + 1);
            }
        }
        
        for (Map.Entry<String, Integer> entry : statusCategoryCounts.entrySet()) {
            counts.addProperty(entry.getKey(), entry.getValue());
        }
        
        return counts;
    }

    private String getStatusCategory(int status) {
        if (status >= 200 && status < 300) {
            return "2XX";
        } else if (status >= 400 && status < 500) {
            return "4XX";
        } else if (status >= 500 && status < 600) {
            return "5XX";
        }
        return null;
    }
}
