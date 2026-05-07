package com.loganalyzer.parser;

import com.loganalyzer.model.APMLog;
import com.loganalyzer.model.LogEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class APMLogParserTest {

    private APMLogParser parser;

    @BeforeEach
    void setUp() {
        parser = new APMLogParser();
    }

    @Test
    void testCanParseWithValidAPMFields() {
        Map<String, String> fields = new HashMap<>();
        fields.put("metric", "cpu_usage_percent");
        fields.put("value", "72");
        
        assertTrue(parser.canParse(fields));
    }

    @Test
    void testCanParseWithMissingMetric() {
        Map<String, String> fields = new HashMap<>();
        fields.put("value", "72");
        
        assertFalse(parser.canParse(fields));
    }

    @Test
    void testCanParseWithMissingValue() {
        Map<String, String> fields = new HashMap<>();
        fields.put("metric", "cpu_usage_percent");
        
        assertFalse(parser.canParse(fields));
    }

    @Test
    void testParseValidAPMLog() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:15Z");
        fields.put("metric", "cpu_usage_percent");
        fields.put("host", "webserver1");
        fields.put("value", "72.5");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        assertTrue(entry instanceof APMLog);
        
        APMLog apmLog = (APMLog) entry;
        assertEquals("cpu_usage_percent", apmLog.getMetric());
        assertEquals(72.5, apmLog.getValue(), 0.001);
        assertEquals("webserver1", apmLog.getHost());
        assertEquals("2024-02-24T16:22:15Z", apmLog.getTimestamp());
        assertEquals("APM", apmLog.getLogType());
    }

    @Test
    void testParseWithInvalidNumericValue() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:15Z");
        fields.put("metric", "cpu_usage_percent");
        fields.put("host", "webserver1");
        fields.put("value", "invalid_number");
        
        LogEntry entry = parser.parse(fields);
        
        assertNull(entry, "Should return null for invalid numeric value");
    }

    @Test
    void testParseWithMissingTimestamp() {
        Map<String, String> fields = new HashMap<>();
        fields.put("metric", "cpu_usage_percent");
        fields.put("host", "webserver1");
        fields.put("value", "72");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry, "Should still parse even with missing timestamp");
    }

    @Test
    void testParseWithNegativeValue() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:15Z");
        fields.put("metric", "temperature");
        fields.put("host", "webserver1");
        fields.put("value", "-10.5");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        APMLog apmLog = (APMLog) entry;
        assertEquals(-10.5, apmLog.getValue(), 0.001);
    }

    @Test
    void testParseWithZeroValue() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:15Z");
        fields.put("metric", "errors");
        fields.put("host", "webserver1");
        fields.put("value", "0");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        APMLog apmLog = (APMLog) entry;
        assertEquals(0.0, apmLog.getValue(), 0.001);
    }

    @Test
    void testParseWithLargeValue() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:15Z");
        fields.put("metric", "network_bytes_in");
        fields.put("host", "webserver1");
        fields.put("value", "543210.789");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        APMLog apmLog = (APMLog) entry;
        assertEquals(543210.789, apmLog.getValue(), 0.001);
    }
}
