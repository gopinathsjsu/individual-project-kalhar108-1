package com.loganalyzer.model;

import java.util.Map;

public class RequestLog extends LogEntry {
    private String requestMethod;
    private String requestUrl;
    private int responseStatus;
    private int responseTimeMs;

    public RequestLog(String timestamp, String host, String requestMethod, String requestUrl,
                      int responseStatus, int responseTimeMs, Map<String, String> rawFields) {
        super(timestamp, host, rawFields);
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.responseStatus = responseStatus;
        this.responseTimeMs = responseTimeMs;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public int getResponseTimeMs() {
        return responseTimeMs;
    }

    @Override
    public String getLogType() {
        return "REQUEST";
    }

    @Override
    public String toString() {
        return "RequestLog{" +
                "timestamp='" + timestamp + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", responseStatus=" + responseStatus +
                ", responseTimeMs=" + responseTimeMs +
                ", host='" + host + '\'' +
                '}';
    }
}
