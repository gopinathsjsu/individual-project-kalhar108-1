package com.loganalyzer.parser;

import com.loganalyzer.model.APMLog;
import com.loganalyzer.model.ApplicationLog;
import com.loganalyzer.model.LogEntry;
import com.loganalyzer.model.RequestLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogParserFactoryTest {

    private LogParserFactory parserFactory;

    @BeforeEach
    void setUp() {
        parserFactory = new LogParserFactory();
    }

    @Test
    void testParseAPMLog() {
        String logLine = "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72";
        LogEntry entry = parserFactory.parse(logLine);
        
        assertNotNull(entry);
        assertTrue(entry instanceof APMLog);
        
        APMLog apmLog = (APMLog) entry;
        assertEquals("cpu_usage_percent", apmLog.getMetric());
        assertEquals(72.0, apmLog.getValue());
        assertEquals("webserver1", apmLog.getHost());
    }

    @Test
    void testParseApplicationLog() {
        String logLine = "timestamp=2024-02-24T16:22:20Z level=INFO message=\"Scheduled maintenance starting\" host=webserver1";
        LogEntry entry = parserFactory.parse(logLine);
        
        assertNotNull(entry);
        assertTrue(entry instanceof ApplicationLog);
        
        ApplicationLog appLog = (ApplicationLog) entry;
        assertEquals("INFO", appLog.getLevel());
        assertEquals("Scheduled maintenance starting", appLog.getMessage());
        assertEquals("webserver1", appLog.getHost());
    }

    @Test
    void testParseRequestLog() {
        String logLine = "timestamp=2024-02-24T16:22:25Z request_method=POST request_url=\"/api/update\" response_status=202 response_time_ms=200 host=webserver1";
        LogEntry entry = parserFactory.parse(logLine);
        
        assertNotNull(entry);
        assertTrue(entry instanceof RequestLog);
        
        RequestLog reqLog = (RequestLog) entry;
        assertEquals("POST", reqLog.getRequestMethod());
        assertEquals("/api/update", reqLog.getRequestUrl());
        assertEquals(202, reqLog.getResponseStatus());
        assertEquals(200, reqLog.getResponseTimeMs());
    }

    @Test
    void testParseCorruptedLog() {
        String logLine = "this is a corrupted log line without proper format";
        LogEntry entry = parserFactory.parse(logLine);
        
        assertNull(entry, "Corrupted logs should return null");
    }

    @Test
    void testParseEmptyLine() {
        LogEntry entry = parserFactory.parse("");
        assertNull(entry);
        
        entry = parserFactory.parse(null);
        assertNull(entry);
    }

    @Test
    void testParseInvalidAPMLog() {
        // Missing value field
        String logLine = "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1";
        LogEntry entry = parserFactory.parse(logLine);
        
        assertNull(entry, "Invalid APM log should return null");
    }

    @Test
    void testMultipleMetricTypes() {
        String[] logLines = {
            "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72",
            "timestamp=2024-02-24T16:22:30Z metric=memory_usage_percent host=webserver1 value=85",
            "timestamp=2024-02-24T16:22:45Z metric=disk_usage_percent host=webserver1 value=68"
        };
        
        for (String line : logLines) {
            LogEntry entry = parserFactory.parse(line);
            assertNotNull(entry);
            assertTrue(entry instanceof APMLog);
        }
    }
}
