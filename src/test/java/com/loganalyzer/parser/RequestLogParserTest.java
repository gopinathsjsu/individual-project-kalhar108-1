package com.loganalyzer.parser;

import com.loganalyzer.model.LogEntry;
import com.loganalyzer.model.RequestLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RequestLogParserTest {

    private RequestLogParser parser;

    @BeforeEach
    void setUp() {
        parser = new RequestLogParser();
    }

    @Test
    void testCanParseWithValidRequestFields() {
        Map<String, String> fields = new HashMap<>();
        fields.put("request_method", "GET");
        fields.put("request_url", "/api/status");
        fields.put("response_status", "200");
        fields.put("response_time_ms", "100");
        
        assertTrue(parser.canParse(fields));
    }

    @Test
    void testCanParseWithMissingRequestMethod() {
        Map<String, String> fields = new HashMap<>();
        fields.put("request_url", "/api/status");
        fields.put("response_status", "200");
        fields.put("response_time_ms", "100");
        
        assertFalse(parser.canParse(fields));
    }

    @Test
    void testCanParseWithMissingRequestUrl() {
        Map<String, String> fields = new HashMap<>();
        fields.put("request_method", "GET");
        fields.put("response_status", "200");
        fields.put("response_time_ms", "100");
        
        assertFalse(parser.canParse(fields));
    }

    @Test
    void testCanParseWithMissingResponseStatus() {
        Map<String, String> fields = new HashMap<>();
        fields.put("request_method", "GET");
        fields.put("request_url", "/api/status");
        fields.put("response_time_ms", "100");
        
        assertFalse(parser.canParse(fields));
    }

    @Test
    void testCanParseWithMissingResponseTime() {
        Map<String, String> fields = new HashMap<>();
        fields.put("request_method", "GET");
        fields.put("request_url", "/api/status");
        fields.put("response_status", "200");
        
        assertFalse(parser.canParse(fields));
    }

    @Test
    void testParseValidGetRequest() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:40Z");
        fields.put("request_method", "GET");
        fields.put("request_url", "/api/status");
        fields.put("response_status", "200");
        fields.put("response_time_ms", "100");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        assertTrue(entry instanceof RequestLog);
        
        RequestLog reqLog = (RequestLog) entry;
        assertEquals("GET", reqLog.getRequestMethod());
        assertEquals("/api/status", reqLog.getRequestUrl());
        assertEquals(200, reqLog.getResponseStatus());
        assertEquals(100, reqLog.getResponseTimeMs());
        assertEquals("webserver1", reqLog.getHost());
        assertEquals("2024-02-24T16:22:40Z", reqLog.getTimestamp());
        assertEquals("REQUEST", reqLog.getLogType());
    }

    @Test
    void testParseValidPostRequest() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:25Z");
        fields.put("request_method", "POST");
        fields.put("request_url", "/api/update");
        fields.put("response_status", "202");
        fields.put("response_time_ms", "200");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        RequestLog reqLog = (RequestLog) entry;
        assertEquals("POST", reqLog.getRequestMethod());
        assertEquals(202, reqLog.getResponseStatus());
    }

    @Test
    void testParseWith404Error() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:23:10Z");
        fields.put("request_method", "GET");
        fields.put("request_url", "/home");
        fields.put("response_status", "404");
        fields.put("response_time_ms", "25");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        RequestLog reqLog = (RequestLog) entry;
        assertEquals(404, reqLog.getResponseStatus());
    }

    @Test
    void testParseWith500Error() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:23:40Z");
        fields.put("request_method", "POST");
        fields.put("request_url", "/api/status");
        fields.put("response_status", "500");
        fields.put("response_time_ms", "300");
        fields.put("host", "webserver2");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        RequestLog reqLog = (RequestLog) entry;
        assertEquals(500, reqLog.getResponseStatus());
    }

    @Test
    void testParseWithInvalidStatusCode() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:40Z");
        fields.put("request_method", "GET");
        fields.put("request_url", "/api/status");
        fields.put("response_status", "invalid");
        fields.put("response_time_ms", "100");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNull(entry, "Should return null for invalid status code");
    }

    @Test
    void testParseWithInvalidResponseTime() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:40Z");
        fields.put("request_method", "GET");
        fields.put("request_url", "/api/status");
        fields.put("response_status", "200");
        fields.put("response_time_ms", "not_a_number");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNull(entry, "Should return null for invalid response time");
    }

    @Test
    void testParseWithLongUrl() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:40Z");
        fields.put("request_method", "GET");
        fields.put("request_url", "/api/v1/users/123/profile/settings/notifications");
        fields.put("response_status", "200");
        fields.put("response_time_ms", "150");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        RequestLog reqLog = (RequestLog) entry;
        assertEquals("/api/v1/users/123/profile/settings/notifications", reqLog.getRequestUrl());
    }

    @Test
    void testParseWithQueryParameters() {
        Map<String, String> fields = new HashMap<>();
        fields.put("timestamp", "2024-02-24T16:22:40Z");
        fields.put("request_method", "GET");
        fields.put("request_url", "/api/search?q=test&limit=10");
        fields.put("response_status", "200");
        fields.put("response_time_ms", "75");
        fields.put("host", "webserver1");
        
        LogEntry entry = parser.parse(fields);
        
        assertNotNull(entry);
        RequestLog reqLog = (RequestLog) entry;
        assertTrue(reqLog.getRequestUrl().contains("?"));
    }
}
