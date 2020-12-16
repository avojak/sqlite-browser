package com.avojak.app.sqlite.browser.model;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class StatementResult {

    private final List<String> columnNames;
    private final List<List<String>> rows;
    private final long duration;

    public StatementResult(final List<String> columnNames, final List<List<String>> rows, final long duration) {
        this.columnNames = checkNotNull(columnNames, "columnNames cannot be null");
        this.rows = checkNotNull(rows, "rows cannot be null");
        this.duration = duration;
        checkArgument(duration >= 0, "duration must be non-negative");
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public long getDuration() {
        return duration;
    }

}
