package com.avojak.app.sqlite.browser;

import com.avojak.app.sqlite.browser.ui.impl.MainViewImpl;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.util.concurrent.Executors;

public class SQLiteBrowserApplication {

    public static void main(final String... args) {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new GridLayout());
        shell.setText("SQLite Browser");

        final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        final MainPresenter presenter = new MainPresenter(new MainViewImpl(shell), executorService);

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        presenter.dispose();
        display.dispose();
    }

}
