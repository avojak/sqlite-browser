package com.avojak.app.sqlite.browser;

import com.avojak.app.sqlite.browser.model.StatementResult;
import com.avojak.app.sqlite.browser.task.StatementTask;
import com.avojak.app.sqlite.browser.ui.MainView;
import com.avojak.app.sqlite.browser.ui.MainViewListener;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.checkerframework.checker.nullness.qual.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MainPresenter implements MainViewListener {

    private final MainView view;
    private final ListeningExecutorService executorService;

    public MainPresenter(final MainView view, final ListeningExecutorService executorService) {
        this.view = checkNotNull(view, "view cannot be null");
        this.executorService = checkNotNull(executorService, "executorService cannot be null");
        view.addListener(this);
    }

    @Override
    public void onRunButtonClicked(final String databaseFile, final String statement) {
        checkNotNull(databaseFile, "databaseFile cannot be null");
        checkArgument(!databaseFile.trim().isEmpty(), "databaseFile cannot be empty");
        checkNotNull(statement, "statement cannot be null");
        checkArgument(!statement.trim().isEmpty(), "statement cannot be empty");

        view.clearResults();
        view.clearStatus();

        final ListenableFuture<StatementResult> future = executorService.submit(new StatementTask(databaseFile, statement));
        Futures.addCallback(future, new FutureCallback<StatementResult>() {
            @Override
            public void onSuccess(@Nullable final StatementResult statementResult) {
                view.displayResult(statementResult);
            }

            @Override
            public void onFailure(final Throwable throwable) {
                // TODO
            }
        }, executorService);
    }

    public void dispose() {
        executorService.shutdownNow();
    }

}
