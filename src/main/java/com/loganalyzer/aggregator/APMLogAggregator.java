package com.loganalyzer.aggregator;

import com.loganalyzer.model.APMLog;
import com.loganalyzer.model.LogEntry;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

public class APMLogAggregator implements LogAggregator {

    @Override
    public boolean canAggregate(String logType) {
        return "APM".equals(logType);
    }

    @Override
    public JsonObject aggregate(List<LogEntry> logs) {
        JsonObject result = new JsonObject();

        // Group logs by metric type
        Map<String, List<Double>> metricValues = new HashMap<>();
        
        for (LogEntry log : logs) {
            if (log instanceof APMLog) {
                APMLog apmLog = (APMLog) log;
                metricValues
                    .computeIfAbsent(apmLog.getMetric(), k -> new ArrayList<>())
                    .add(apmLog.getValue());
            }
        }

        // Calculate statistics for each metric
        for (Map.Entry<String, List<Double>> entry : metricValues.entrySet()) {
            String metric = entry.getKey();
            List<Double> values = entry.getValue();
            
            if (!values.isEmpty()) {
                JsonObject stats = new JsonObject();
                Collections.sort(values);
                
                double min = values.get(0);
                double max = values.get(values.size() - 1);
                double median = calculateMedian(values);
                double average = values.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

                stats.addProperty("minimum", formatNumber(min));
                stats.addProperty("median", formatNumber(median));
                stats.addProperty("average", formatNumber(average));
                stats.addProperty("max", formatNumber(max));
                
                result.add(metric, stats);
            }
        }

        return result;
    }

    private Number formatNumber(double value) {
        if (value == (long) value) {
            return (long) value;
        }
        return value;
    }

    @Override
    public String getOutputFileName() {
        return "apm.json";
    }

    private double calculateMedian(List<Double> sortedValues) {
        int size = sortedValues.size();
        if (size == 0) {
            return 0.0;
        }
        
        if (size % 2 == 0) {
            // Even number of elements - average of middle two
            return (sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2.0;
        } else {
            // Odd number of elements - middle element
            return sortedValues.get(size / 2);
        }
    }
}
