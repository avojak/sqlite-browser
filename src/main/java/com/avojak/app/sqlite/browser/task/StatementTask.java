package com.avojak.app.sqlite.browser.task;

import com.avojak.app.sqlite.browser.model.StatementResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class StatementTask implements Callable<StatementResult> {

    private final String databaseFile;
    private final String statement;

    public StatementTask(final String databaseFile, final String statement) {
        this.databaseFile = checkNotNull(databaseFile, "databaseFile cannot be null");
        checkArgument(!databaseFile.trim().isEmpty(), "databaseFile cannot be empty");
        this.statement = checkNotNull(statement, "statement cannot be null");
        checkArgument(!statement.trim().isEmpty(), "statement cannot be empty");
    }

    @Override
    public StatementResult call() throws Exception {
        try (final Connection connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", databaseFile))) {
            final PreparedStatement preparedStatement = connection.prepareStatement(statement);
            final long start = System.currentTimeMillis();
            final ResultSet resultSet = preparedStatement.executeQuery();
            final long end = System.currentTimeMillis();
            final ResultSetMetaData metaData = resultSet.getMetaData();

            final List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            final List<List<String>> rows = new ArrayList<>();
            while (resultSet.next()) {
                final List<String> row = new ArrayList<>();
                for (final String columnName : columnNames) {
                    row.add(resultSet.getString(columnName));
                }
                rows.add(row);
            }

            return new StatementResult(columnNames, rows, end - start);
        }
    }

}
