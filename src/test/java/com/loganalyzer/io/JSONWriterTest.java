package com.loganalyzer.io;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class JSONWriterTest {

    private JSONWriter writer;
    private File testFile;

    @BeforeEach
    void setUp() {
        writer = new JSONWriter();
    }

    @AfterEach
    void tearDown() {
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testWriteEmptyJsonObject() throws IOException {
        testFile = new File("test_empty.json");
        JsonObject json = new JsonObject();

        writer.writeToFile(json, testFile.getAbsolutePath());

        assertTrue(testFile.exists());
        String content = new String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath())));
        assertEquals("{}", content.trim());
    }

    @Test
    void testWriteJsonObjectWithProperties() throws IOException {
        testFile = new File("test_properties.json");
        JsonObject json = new JsonObject();
        json.addProperty("name", "test");
        json.addProperty("value", 123);
        json.addProperty("active", true);

        writer.writeToFile(json, testFile.getAbsolutePath());

        assertTrue(testFile.exists());
        String content = new String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath())));
        assertTrue(content.contains("\"name\": \"test\""));
        assertTrue(content.contains("\"value\": 123"));
        assertTrue(content.contains("\"active\": true"));
    }

    @Test
    void testWriteNestedJsonObject() throws IOException {
        testFile = new File("test_nested.json");
        JsonObject json = new JsonObject();
        JsonObject nested = new JsonObject();
        nested.addProperty("min", 10);
        nested.addProperty("max", 100);
        json.add("stats", nested);

        writer.writeToFile(json, testFile.getAbsolutePath());

        assertTrue(testFile.exists());
        String content = new String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath())));
        assertTrue(content.contains("\"stats\""));
        assertTrue(content.contains("\"min\": 10"));
        assertTrue(content.contains("\"max\": 100"));
    }

    @Test
    void testWritePrettyPrintedJson() throws IOException {
        testFile = new File("test_pretty.json");
        JsonObject json = new JsonObject();
        json.addProperty("field1", "value1");
        json.addProperty("field2", "value2");

        writer.writeToFile(json, testFile.getAbsolutePath());

        String content = new String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath())));
        assertTrue(content.contains("\n"), "JSON should be pretty-printed with newlines");
    }

    @Test
    void testOverwriteExistingFile() throws IOException {
        testFile = new File("test_overwrite.json");
        
        // Write first time
        JsonObject json1 = new JsonObject();
        json1.addProperty("version", 1);
        writer.writeToFile(json1, testFile.getAbsolutePath());

        // Write second time (overwrite)
        JsonObject json2 = new JsonObject();
        json2.addProperty("version", 2);
        writer.writeToFile(json2, testFile.getAbsolutePath());

        String content = new String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath())));
        assertTrue(content.contains("\"version\": 2"));
        assertFalse(content.contains("\"version\": 1"));
    }

    @Test
    void testWriteJsonWithSpecialCharacters() throws IOException {
        testFile = new File("test_special.json");
        JsonObject json = new JsonObject();
        json.addProperty("message", "Error: \"quoted\" value with special chars @#$");

        writer.writeToFile(json, testFile.getAbsolutePath());

        assertTrue(testFile.exists());
        String content = new String(Files.readAllBytes(Paths.get(testFile.getAbsolutePath())));
        assertTrue(content.contains("Error"));
    }

    @Test
    void testWriteToInvalidPath() {
        JsonObject json = new JsonObject();
        
        assertThrows(IOException.class, () -> {
            writer.writeToFile(json, "/invalid/path/that/does/not/exist/file.json");
        });
    }
}
