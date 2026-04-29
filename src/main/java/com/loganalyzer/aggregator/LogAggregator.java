package com.loganalyzer.aggregator;

import com.loganalyzer.model.LogEntry;
import com.google.gson.JsonObject;
import java.util.List;

public interface LogAggregator {
    boolean canAggregate(String logType);

    JsonObject aggregate(List<LogEntry> logs);

    String getOutputFileName();
}
