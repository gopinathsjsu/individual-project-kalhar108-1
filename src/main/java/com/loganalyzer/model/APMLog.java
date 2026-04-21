package com.loganalyzer.model;

import java.util.Map;

public class APMLog extends LogEntry {
    private String metric;
    private double value;

    public APMLog(String timestamp, String host, String metric, double value, Map<String, String> rawFields) {
        super(timestamp, host, rawFields);
        this.metric = metric;
        this.value = value;
    }

    public String getMetric() {
        return metric;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getLogType() {
        return "APM";
    }

    @Override
    public String toString() {
        return "APMLog{" +
                "timestamp='" + timestamp + '\'' +
                ", metric='" + metric + '\'' +
                ", value=" + value +
                ", host='" + host + '\'' +
                '}';
    }
}
