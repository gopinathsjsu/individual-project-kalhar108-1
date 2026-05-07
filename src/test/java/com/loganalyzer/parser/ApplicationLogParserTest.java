package com.loganalyzer.parser;

import com.loganalyzer.model.ApplicationLog;
import com.loganalyzer.model.LogEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationLogParserTest {

    private ApplicationLogParser parser;

    @BeforeEach
    void setUp() {
        parser = new ApplicationLogParser();
    }

    @Test
    void testCanParseWithValidApplicationFields() {
        Map<String, String> fields = new HashMap<>();
        fields.put("level", "INFO");
        fields.put("message", "Application started");
        
        assertTrue(parser.canParse(fields));
    }

    @Test
    void testCanParseWithMissingLevel() {
        Map<String, String> fields = new HashMap<>();
        fields.put("message", "Application started");
        
        assertFalse(parser.canParse(fields));
    }

    @Test
    void testCanParseWithMissingMessage() {
        Map<String, String> fields = new HashMap<>();
        fields.put("level", "INFO");
        
        assertFalse(parser.canParse(fields));
    }

    @Test
    void testParseValidApplicationLog() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:20Z");
        fields.put("level", "INFO");
        fields.put("message", "Scheduled maintenance starting");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        assertTrue(entry instanceof ApplicationLog);
        
        ApplicationLog appLog = (ApplicationLog) entry;
        assertEquals("INFO", appLog.getLevel());
        assertEquals("Scheduled maintenance starting", appLog.getMessage());
        assertEquals("webserver1", appLog.getHost());
        assertEquals("2024-02-24T16:22:20Z", appLog.getTimestamp());
        assertEquals("APPLICATION", appLog.getLogType());
    }

    @Test
    void testParseErrorLevel() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:35Z");
        fields.put("level", "ERROR");
        fields.put("message", "Update process failed");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        ApplicationLog appLog = (ApplicationLog) entry;
        assertEquals("ERROR", appLog.getLevel());
    }

    @Test
    void testParseDebugLevel() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:50Z");
        fields.put("level", "DEBUG");
        fields.put("message", "Retrying update process");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        ApplicationLog appLog = (ApplicationLog) entry;
        assertEquals("DEBUG", appLog.getLevel());
    }

    @Test
    void testParseWarningLevel() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:23:20Z");
        fields.put("level", "WARNING");
        fields.put("message", "High memory usage detected");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        ApplicationLog appLog = (ApplicationLog) entry;
        assertEquals("WARNING", appLog.getLevel());
    }

    @Test
    void testParseWithEmptyMessage() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:20Z");
        fields.put("level", "INFO");
        fields.put("message", "");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        ApplicationLog appLog = (ApplicationLog) entry;
        assertEquals("", appLog.getMessage());
    }

    @Test
    void testParseWithLongMessage() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:20Z");
        fields.put("level", "ERROR");
        fields.put("message", "This is a very long error message that contains detailed information about what went wrong in the application");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        ApplicationLog appLog = (ApplicationLog) entry;
        assertTrue(appLog.getMessage().length() > 50);
    }

    @Test
    void testParseWithSpecialCharactersInMessage() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:20Z");
        fields.put("level", "INFO");
        fields.put("message", "Message with special chars: @#$%^&*()");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        ApplicationLog appLog = (ApplicationLog) entry;
        assertTrue(appLog.getMessage().contains("@#$%"));
    }
}
