package com.loganalyzer.aggregator;

import com.loganalyzer.model.APMLog;
import com.loganalyzer.model.LogEntry;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class APMLogAggregatorTest {

    private APMLogAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new APMLogAggregator();
    }

    @Test
    void testCanAggregate() {
        assertTrue(aggregator.canAggregate("APM"));
        assertFalse(aggregator.canAggregate("APPLICATION"));
        assertFalse(aggregator.canAggregate("REQUEST"));
    }

    @Test
    void testGetOutputFileName() {
        assertEquals("apm.json", aggregator.getOutputFileName());
    }

    @Test
    void testAggregateCPUMetrics() {
        List<LogEntry> logs = new ArrayList<>();
        logs.add(new APMLog("2024-02-24T16:22:15Z", "webserver1", "cpu_usage_percent", 72.0, new HashMap<>()));
        logs.add(new APMLog("2024-02-24T16:23:30Z", "webserver2", "cpu_usage_percent", 65.0, new HashMap<>()));
        
        JsonObject result = aggregator.aggregate(logs);
        
        assertTrue(result.has("cpu_usage_percent"));
        JsonObject cpuStats = result.getAsJsonObject("cpu_usage_percent");
        
        assertEquals(65.0, cpuStats.get("minimum").getAsDouble());
        assertEquals(68.5, cpuStats.get("median").getAsDouble());
        assertEquals(68.5, cpuStats.get("average").getAsDouble());
        assertEquals(72.0, cpuStats.get("max").getAsDouble());
    }

    @Test
    void testAggregateMultipleMetricTypes() {
        List<LogEntry> logs = new ArrayList<>();
        logs.add(new APMLog("2024-02-24T16:22:15Z", "webserver1", "cpu_usage_percent", 72.0, new HashMap<>()));
        logs.add(new APMLog("2024-02-24T16:22:30Z", "webserver1", "memory_usage_percent", 85.0, new HashMap<>()));
        logs.add(new APMLog("2024-02-24T16:22:45Z", "webserver1", "disk_usage_percent", 68.0, new HashMap<>()));
        
        JsonObject result = aggregator.aggregate(logs);
        
        assertTrue(result.has("cpu_usage_percent"));
        assertTrue(result.has("memory_usage_percent"));
        assertTrue(result.has("disk_usage_percent"));
    }

    @Test
    void testAggregateEmptyList() {
        List<LogEntry> logs = new ArrayList<>();
        JsonObject result = aggregator.aggregate(logs);
        
        assertTrue(result.entrySet().isEmpty());
    }

    @Test
    void testAggregateOddNumberOfValues() {
        List<LogEntry> logs = new ArrayList<>();
        logs.add(new APMLog("2024-02-24T16:22:15Z", "webserver1", "cpu_usage_percent", 60.0, new HashMap<>()));
        logs.add(new APMLog("2024-02-24T16:22:30Z", "webserver1", "cpu_usage_percent", 78.0, new HashMap<>()));
        logs.add(new APMLog("2024-02-24T16:22:45Z", "webserver1", "cpu_usage_percent", 90.0, new HashMap<>()));
        
        JsonObject result = aggregator.aggregate(logs);
        JsonObject cpuStats = result.getAsJsonObject("cpu_usage_percent");
        
        assertEquals(78.0, cpuStats.get("median").getAsDouble(), "Median of [60, 78, 90] should be 78");
    }
}
