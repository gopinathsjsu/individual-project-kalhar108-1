package com.loganalyzer;

import com.loganalyzer.aggregator.*;
import com.loganalyzer.io.JSONWriter;
import com.loganalyzer.io.LogFileReader;
import com.loganalyzer.model.LogEntry;
import com.loganalyzer.parser.LogParserFactory;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.*;

public class LogAnalyzerApplication {

    private final LogParserFactory parserFactory;
    private final List<LogAggregator> aggregators;
    private final LogFileReader fileReader;
    private final JSONWriter jsonWriter;

    public LogAnalyzerApplication() {
        this.parserFactory = new LogParserFactory();
        this.fileReader = new LogFileReader();
        this.jsonWriter = new JSONWriter();
        
        // Initialize all aggregators
        this.aggregators = new ArrayList<>();
        this.aggregators.add(new APMLogAggregator());
        this.aggregators.add(new ApplicationLogAggregator());
        this.aggregators.add(new RequestLogAggregator());
    }

    public void processLogFile(String inputFilename) throws IOException {
        // Read all log lines
        List<String> logLines = fileReader.readLogFile(inputFilename);
        
        // Parse logs and categorize by type
        Map<String, List<LogEntry>> logsByType = new HashMap<>();
        
        for (String line : logLines) {
            LogEntry entry = parserFactory.parse(line);
            if (entry != null) {
                logsByType
                    .computeIfAbsent(entry.getLogType(), k -> new ArrayList<>())
                    .add(entry);
            }
            // Silently ignore corrupted/incompatible logs
        }
        
        // Aggregate and write output for each log type
        for (LogAggregator aggregator : aggregators) {
            String logType = getLogTypeForAggregator(aggregator);
            List<LogEntry> logs = logsByType.getOrDefault(logType, new ArrayList<>());
            
            JsonObject aggregatedData = aggregator.aggregate(logs);
            jsonWriter.writeToFile(aggregatedData, aggregator.getOutputFileName());
            
            System.out.println("Generated " + aggregator.getOutputFileName() + 
                             " with " + logs.size() + " log entries");
        }
    }

    private String getLogTypeForAggregator(LogAggregator aggregator) {
        if (aggregator.canAggregate("APM")) return "APM";
        if (aggregator.canAggregate("APPLICATION")) return "APPLICATION";
        if (aggregator.canAggregate("REQUEST")) return "REQUEST";
        return null;
    }

    public static void main(String[] args) {
        if (args.length != 2 || !"--file".equals(args[0])) {
            System.err.println("Usage: java -jar log-analyzer.jar --file <filename.txt>");
            System.exit(1);
        }

        String inputFile = args[1];
        
        try {
            LogAnalyzerApplication app = new LogAnalyzerApplication();
            System.out.println("Processing log file: " + inputFile);
            app.processLogFile(inputFile);
            System.out.println("Log analysis completed successfully!");
        } catch (IOException e) {
            System.err.println("Error processing log file: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
