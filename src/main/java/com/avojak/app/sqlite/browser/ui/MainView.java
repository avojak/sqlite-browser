package com.avojak.app.sqlite.browser.ui;

import com.avojak.app.sqlite.browser.model.StatementResult;

public interface MainView {

    void addListener(final MainViewListener listener);

    void displayResult(final StatementResult result);

    void clearResults();

    void clearStatus();

}
