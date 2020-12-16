package com.avojak.app.sqlite.browser.ui.impl;

import com.avojak.app.sqlite.browser.model.StatementResult;
import com.avojak.app.sqlite.browser.ui.MainView;
import com.avojak.app.sqlite.browser.ui.MainViewListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class MainViewImpl implements MainView {

    private final List<MainViewListener> listeners = new ArrayList<>();
    private final Composite parent;

    private Table outputTable;

    public MainViewImpl(final Composite parent) {
        this.parent = checkNotNull(parent, "parent cannot be null");

        final Composite baseComposite = new Composite(parent, SWT.NONE);
        baseComposite.setLayout(new GridLayout());
        baseComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final SashForm sashForm = new SashForm(baseComposite, SWT.HORIZONTAL);
        sashForm.setLayout(new GridLayout(2, false));
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createPrimaryViewArea(sashForm);
        createSecondaryViewArea(sashForm);

        final Composite statusComposite = new Composite(baseComposite, SWT.NONE);
        statusComposite.setLayout(new GridLayout());
        statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        sashForm.setWeights(new int[]{70, 30});
    }

    private void createPrimaryViewArea(final Composite parent) {
        final Composite baseComposite = new Composite(parent, SWT.NONE);
        baseComposite.setLayout(new GridLayout());
        baseComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite configurationComposite = new Composite(baseComposite, SWT.NONE);
        final GridLayout configurationCompositeGridLayout = new GridLayout(2, false);
        configurationCompositeGridLayout.marginHeight = 0;
        configurationCompositeGridLayout.marginWidth = 0;
        configurationComposite.setLayout(configurationCompositeGridLayout);
        configurationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        final Button fileChooserButton = new Button(configurationComposite, SWT.PUSH);
        fileChooserButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        fileChooserButton.setText("Select Database File…");

        final Text filePathText = new Text(configurationComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        filePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

        final SashForm sashForm = new SashForm(baseComposite, SWT.VERTICAL);
        sashForm.setLayout(new GridLayout());
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite inputComposite = new Composite(sashForm, SWT.NONE);
        final GridLayout inputCompositeGridLayout = new GridLayout();
        inputCompositeGridLayout.marginHeight = 0;
        inputCompositeGridLayout.marginWidth = 0;
        inputComposite.setLayout(inputCompositeGridLayout);
        inputComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Text statementInput = new Text(inputComposite, SWT.MULTI | SWT.BORDER);
        statementInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Button runButton = new Button(inputComposite, SWT.PUSH);
        runButton.setText("Run");

        final Composite outputComposite = new Composite(sashForm, SWT.NONE);
        final GridLayout outputCompositeGridLayout = new GridLayout();
        outputCompositeGridLayout.marginHeight = 0;
        outputCompositeGridLayout.marginWidth = 0;
        outputComposite.setLayout(outputCompositeGridLayout);
        outputComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        outputTable = new Table(outputComposite, SWT.VIRTUAL | SWT.BORDER);
        outputTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        outputTable.setHeaderVisible(true);
        outputTable.setLinesVisible(true);

        sashForm.setWeights(new int[]{50, 50});

        fileChooserButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final FileDialog dialog = new FileDialog(parent.getShell());
                dialog.setText("Select Database File");
                dialog.setFilterExtensions(new String[]{"*.db"});
                dialog.setFilterPath(System.getProperty("user.home"));
                final String filePath = dialog.open();
                if (filePath != null) {
                    filePathText.setText(filePath);
                }
            }
        });

        runButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                for (final MainViewListener listener : listeners) {
                    listener.onRunButtonClicked(filePathText.getText(), statementInput.getText());
                }
            }
        });
    }

    private void createSecondaryViewArea(final Composite parent) {
        final Composite baseComposite = new Composite(parent, SWT.NONE);
        baseComposite.setLayout(new GridLayout());
        baseComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Label historyLabel = new Label(baseComposite, SWT.NONE);
        historyLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        historyLabel.setText("Statement History");

        final Table historyTable = new Table(baseComposite, SWT.VIRTUAL | SWT.BORDER);
        historyTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        historyTable.setHeaderVisible(true);
        historyTable.setLinesVisible(true);

        final TableColumn fileColumn = new TableColumn(historyTable, SWT.NONE);
        fileColumn.setText("Database File");
        fileColumn.pack();

        final TableColumn statementColumn = new TableColumn(historyTable, SWT.NONE);
        statementColumn.setText("Statement");
        statementColumn.pack();
    }

    @Override
    public void addListener(final MainViewListener listener) {
        listeners.add(checkNotNull(listener, "listener cannot be null"));
    }

    @Override
    public void displayResult(final StatementResult result) {
        parent.getDisplay().asyncExec(() -> {
            for (final String columnName : result.getColumnNames()) {
                final TableColumn column = new TableColumn(outputTable, SWT.NONE);
                column.setText(columnName);
            }
            for (final List<String> row : result.getRows()) {
                final TableItem item = new TableItem(outputTable, SWT.NONE);
                for (int i = 0; i < row.size(); i++) {
                    item.setText(i, row.get(i));
                }
            }
//            for (final TableColumn column : outputTable.getColumns()) {
//                column.pack();
//            }
            outputTable.layout();
        });
    }

    @Override
    public void clearResults() {
        parent.getDisplay().asyncExec(() -> {
            for (final TableItem item : outputTable.getItems()) {
                item.dispose();
            }
            for (final TableColumn column : outputTable.getColumns()) {
                column.dispose();
            }
            outputTable.pack();
            outputTable.layout();
        });
    }

    @Override
    public void clearStatus() {
        parent.getDisplay().asyncExec(() -> {
            // TODO
        });
    }

}
