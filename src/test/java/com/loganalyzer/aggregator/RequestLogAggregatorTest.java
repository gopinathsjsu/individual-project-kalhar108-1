package com.loganalyzer.aggregator;

import com.loganalyzer.model.LogEntry;
import com.loganalyzer.model.RequestLog;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestLogAggregatorTest {

    private RequestLogAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new RequestLogAggregator();
    }

    @Test
    void testCanAggregate() {
        assertTrue(aggregator.canAggregate("REQUEST"));
        assertFalse(aggregator.canAggregate("APM"));
        assertFalse(aggregator.canAggregate("APPLICATION"));
    }

    @Test
    void testGetOutputFileName() {
        assertEquals("request.json", aggregator.getOutputFileName());
    }

    @Test
    void testAggregateRequestLogs() {
        List<LogEntry> logs = new ArrayList<>();
        logs.add(new RequestLog("2024-02-24T16:22:25Z", "webserver1", "POST", "/api/update", 202, 200, new HashMap<>()));
        logs.add(new RequestLog("2024-02-24T16:22:40Z", "webserver1", "GET", "/api/status", 200, 100, new HashMap<>()));
        logs.add(new RequestLog("2024-02-24T16:23:25Z", "webserver1", "GET", "/api/status", 200, 150, new HashMap<>()));
        
        JsonObject result = aggregator.aggregate(logs);
        
        assertTrue(result.has("/api/update"));
        assertTrue(result.has("/api/status"));
        
        JsonObject updateStats = result.getAsJsonObject("/api/update");
        JsonObject updateResponseTimes = updateStats.getAsJsonObject("response_times");
        JsonObject updateStatusCodes = updateStats.getAsJsonObject("status_codes");
        
        assertEquals(200, updateResponseTimes.get("min").getAsInt());
        assertEquals(200, updateResponseTimes.get("max").getAsInt());
        assertEquals(1, updateStatusCodes.get("2XX").getAsInt());
        assertEquals(0, updateStatusCodes.get("4XX").getAsInt());
        assertEquals(0, updateStatusCodes.get("5XX").getAsInt());
    }

    @Test
    void testAggregateMultipleStatusCodes() {
        List<LogEntry> logs = new ArrayList<>();
        logs.add(new RequestLog("2024-02-24T16:22:40Z", "webserver1", "GET", "/api/status", 200, 100, new HashMap<>()));
        logs.add(new RequestLog("2024-02-24T16:23:25Z", "webserver1", "GET", "/api/status", 200, 150, new HashMap<>()));
        logs.add(new RequestLog("2024-02-24T16:23:40Z", "webserver2", "POST", "/api/status", 500, 300, new HashMap<>()));
        logs.add(new RequestLog("2024-02-24T16:23:55Z", "webserver2", "GET", "/api/status", 200, 180, new HashMap<>()));
        logs.add(new RequestLog("2024-02-24T16:23:10Z", "webserver1", "GET", "/home", 404, 25, new HashMap<>()));
        
        JsonObject result = aggregator.aggregate(logs);
        
        JsonObject statusStats = result.getAsJsonObject("/api/status");
        JsonObject statusCodes = statusStats.getAsJsonObject("status_codes");
        
        assertEquals(3, statusCodes.get("2XX").getAsInt());
        assertEquals(0, statusCodes.get("4XX").getAsInt());
        assertEquals(1, statusCodes.get("5XX").getAsInt());
        
        JsonObject homeStats = result.getAsJsonObject("/home");
        JsonObject homeStatusCodes = homeStats.getAsJsonObject("status_codes");
        
        assertEquals(0, homeStatusCodes.get("2XX").getAsInt());
        assertEquals(1, homeStatusCodes.get("4XX").getAsInt());
    }

    @Test
    void testResponseTimePercentiles() {
        List<LogEntry> logs = new ArrayList<>();
        logs.add(new RequestLog("2024-02-24T16:22:40Z", "webserver1", "GET", "/api/test", 200, 100, new HashMap<>()));
        logs.add(new RequestLog("2024-02-24T16:23:25Z", "webserver1", "GET", "/api/test", 200, 150, new HashMap<>()));
        logs.add(new RequestLog("2024-02-24T16:23:40Z", "webserver2", "GET", "/api/test", 200, 180, new HashMap<>()));
        logs.add(new RequestLog("2024-02-24T16:23:55Z", "webserver2", "GET", "/api/test", 200, 200, new HashMap<>()));
        
        JsonObject result = aggregator.aggregate(logs);
        JsonObject testStats = result.getAsJsonObject("/api/test");
        JsonObject responseTimes = testStats.getAsJsonObject("response_times");
        
        assertEquals(100, responseTimes.get("min").getAsInt());
        assertEquals(200, responseTimes.get("max").getAsInt());
        assertTrue(responseTimes.has("95_percentile"));
        assertFalse(responseTimes.has("50_percentile"));
        assertFalse(responseTimes.has("90_percentile"));
        assertFalse(responseTimes.has("99_percentile"));
    }

    @Test
    void testAggregateEmptyList() {
        List<LogEntry> logs = new ArrayList<>();
        JsonObject result = aggregator.aggregate(logs);
        
        assertTrue(result.entrySet().isEmpty());
    }
}
