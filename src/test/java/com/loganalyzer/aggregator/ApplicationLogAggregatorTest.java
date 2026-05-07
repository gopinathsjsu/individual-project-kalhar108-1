package com.loganalyzer.aggregator;

import com.loganalyzer.model.ApplicationLog;
import com.loganalyzer.model.LogEntry;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationLogAggregatorTest {

    private ApplicationLogAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new ApplicationLogAggregator();
    }

    @Test
    void testCanAggregate() {
        assertTrue(aggregator.canAggregate("APPLICATION"));
        assertFalse(aggregator.canAggregate("APM"));
        assertFalse(aggregator.canAggregate("REQUEST"));
    }

    @Test
    void testGetOutputFileName() {
        assertEquals("application.json", aggregator.getOutputFileName());
    }

    @Test
    void testAggregateByLevel() {
        List<LogEntry> logs = new ArrayList<>();
        logs.add(new ApplicationLog("2024-02-24T16:22:20Z", "webserver1", "INFO", "Message 1", new HashMap<>()));
        logs.add(new ApplicationLog("2024-02-24T16:22:35Z", "webserver1", "ERROR", "Message 2", new HashMap<>()));
        logs.add(new ApplicationLog("2024-02-24T16:22:50Z", "webserver1", "DEBUG", "Message 3", new HashMap<>()));
        logs.add(new ApplicationLog("2024-02-24T16:23:05Z", "webserver1", "INFO", "Message 4", new HashMap<>()));
        logs.add(new ApplicationLog("2024-02-24T16:23:20Z", "webserver1", "WARNING", "Message 5", new HashMap<>()));
        logs.add(new ApplicationLog("2024-02-24T16:23:35Z", "webserver2", "ERROR", "Message 6", new HashMap<>()));
        logs.add(new ApplicationLog("2024-02-24T16:23:50Z", "webserver2", "INFO", "Message 7", new HashMap<>()));
        
        JsonObject result = aggregator.aggregate(logs);
        
        assertEquals(2, result.get("ERROR").getAsInt());
        assertEquals(3, result.get("INFO").getAsInt());
        assertEquals(1, result.get("DEBUG").getAsInt());
        assertEquals(1, result.get("WARNING").getAsInt());
    }

    @Test
    void testAggregateEmptyList() {
        List<LogEntry> logs = new ArrayList<>();
        JsonObject result = aggregator.aggregate(logs);
        
        assertTrue(result.entrySet().isEmpty());
    }
}
