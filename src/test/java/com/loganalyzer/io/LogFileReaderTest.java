package com.loganalyzer.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogFileReaderTest {

    private LogFileReader reader;
    private File testFile;

    @BeforeEach
    void setUp() {
        reader = new LogFileReader();
    }

    @AfterEach
    void tearDown() {
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testReadValidLogFile() throws IOException {
        testFile = createTestFile("test_valid.txt", 
            "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72\n" +
            "timestamp=2024-02-24T16:22:20Z level=INFO message=\"Test\" host=webserver1\n" +
            "timestamp=2024-02-24T16:22:25Z request_method=GET request_url=\"/api\" response_status=200 response_time_ms=100 host=webserver1"
        );

        List<String> lines = reader.readLogFile(testFile.getAbsolutePath());

        assertNotNull(lines);
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).contains("cpu_usage_percent"));
        assertTrue(lines.get(1).contains("INFO"));
        assertTrue(lines.get(2).contains("request_method"));
    }

    @Test
    void testReadEmptyFile() throws IOException {
        testFile = createTestFile("test_empty.txt", "");

        List<String> lines = reader.readLogFile(testFile.getAbsolutePath());

        assertNotNull(lines);
        assertEquals(0, lines.size());
    }

    @Test
    void testReadFileWithEmptyLines() throws IOException {
        testFile = createTestFile("test_empty_lines.txt", 
            "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72\n" +
            "\n" +
            "timestamp=2024-02-24T16:22:20Z level=INFO message=\"Test\" host=webserver1\n" +
            "\n\n" +
            "timestamp=2024-02-24T16:22:25Z request_method=GET request_url=\"/api\" response_status=200 response_time_ms=100 host=webserver1"
        );

        List<String> lines = reader.readLogFile(testFile.getAbsolutePath());

        assertEquals(3, lines.size(), "Empty lines should be filtered out");
    }

    @Test
    void testReadFileWithWhitespaceLines() throws IOException {
        testFile = createTestFile("test_whitespace.txt", 
            "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72\n" +
            "   \n" +
            "timestamp=2024-02-24T16:22:20Z level=INFO message=\"Test\" host=webserver1\n" +
            "\t\n"
        );

        List<String> lines = reader.readLogFile(testFile.getAbsolutePath());

        assertEquals(2, lines.size(), "Whitespace-only lines should be filtered out");
    }

    @Test
    void testReadSingleLineFile() throws IOException {
        testFile = createTestFile("test_single.txt", 
            "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72"
        );

        List<String> lines = reader.readLogFile(testFile.getAbsolutePath());

        assertEquals(1, lines.size());
    }

    @Test
    void testReadNonExistentFile() {
        assertThrows(IOException.class, () -> {
            reader.readLogFile("non_existent_file.txt");
        });
    }

    @Test
    void testReadFileWithLongLines() throws IOException {
        String longLine = "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72 " +
                         "extra_field1=value1 extra_field2=value2 extra_field3=value3 extra_field4=value4";
        testFile = createTestFile("test_long.txt", longLine);

        List<String> lines = reader.readLogFile(testFile.getAbsolutePath());

        assertEquals(1, lines.size());
        assertTrue(lines.get(0).length() > 100);
    }

    @Test
    void testReadFileWithSpecialCharacters() throws IOException {
        testFile = createTestFile("test_special.txt", 
            "timestamp=2024-02-24T16:22:20Z level=ERROR message=\"Error: @#$%^&*()\" host=webserver1"
        );

        List<String> lines = reader.readLogFile(testFile.getAbsolutePath());

        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("@#$%"));
    }

    private File createTestFile(String filename, String content) throws IOException {
        File file = new File(filename);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }
}
