package com.loganalyzer.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;

public class JSONWriter {
    private final Gson gson;

    public JSONWriter() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void writeToFile(JsonObject jsonObject, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(jsonObject, writer);
        }
    }
}
