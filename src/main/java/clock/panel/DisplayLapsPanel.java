package clock.panel;

import clock.entity.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicInteger;

import static clock.util.Constants.*;

/**
 * DisplayLapsPanel
 * <p>
 * A DisplayLapsPanel is a JPanel that displays the laps of a Stopwatch
 * in a table format. Starting by displaying the laps in the order of
 * their creation, you can reverse the order of the laps in the settings.
 * This is the right panel of the {@link StopwatchPanel}. See {@link DisplayTimePanel}
 * for the left side.
 *
 * @author michael ball
 * @version since 2.9
 */
public class DisplayLapsPanel extends JPanel
{
    private static final Logger logger = LogManager.getLogger(DisplayLapsPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private final StopwatchPanel stopwatchPanel;
    /** The panel thread */
    public Thread thread;
    /** The stopwatches' laps table */
    private JTable lapsTable;
    /** The stopwatches table */
    private JTable stopwatchTable;
    /** The stopwatch laps table column names */
    private final String[] lapTableColumnNames = {LAP_SYM, TIME, RECORDED};
    /** The stopwatches table column names */
    private final String[] stopwatchTableColumnNames = {NAME, ELAPSED, SELECT, REMOVE};
    /** Whether laps are displayed in reverse order. */
    public boolean isLapsReversed = false;

    /**
     * Constructs a DisplayLapsPanel attached to the given stopwatch panel.
     * @param stopwatchPanel the parent StopwatchPanel
     */
    public DisplayLapsPanel(StopwatchPanel stopwatchPanel)
    {
        super();
        this.stopwatchPanel = stopwatchPanel;
        setGridBagLayout(new GridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        setPreferredSize(ClockFrame.analogueSize);
        setMinimumSize(ClockFrame.analogueSize);
        setMaximumSize(ClockFrame.analogueSize);
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setDefaultLayout();
    }

    /** The default layout of the panel */
    public void setDefaultLayout()
    {
        String labelText = stopwatchPanel.getCurrentStopwatch() != null ?
                stopwatchPanel.getCurrentStopwatch().getName() : "Sw" + (Stopwatch.stopwatchCounter + 1);
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(ClockFrame.font20);
        // add a space or some gap, if needed
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        addComponent(label, 0, 0, 1, 1, 5,5,1,0, GridBagConstraints.NONE, new Insets(5,5,5,5));

        createDataTable("lapsTable", new Object[0][0], lapTableColumnNames);
        JScrollPane scrollPane = new JScrollPane(lapsTable);
        addComponent(scrollPane, 1, 0, 1, 1, 5,5,1,1, GridBagConstraints.BOTH, new Insets(5,5,5,5));
    }

    /**
     * Rebuilds the laps display and the stopwatch selection table
     * for the currently selected stopwatch.
     */
    public void updateLabelsAndStopwatchTable()
    {
        resetPanel();
        // generate label or something clickable.
        JButton viewAll = new JButton(VIEW_ALL);
        viewAll.setFont(ClockFrame.font20);
        viewAll.setOpaque(true);
        viewAll.setName(VIEW_STOPWATCHES + BUTTON);
        viewAll.setBackground(Color.BLACK);
        viewAll.setForeground(Color.BLUE);
        viewAll.addActionListener(this::viewStopwatchesTable);
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        addComponent(viewAll, 0, 0, 1, 1, 5,5,1,0, GridBagConstraints.NONE, new Insets(5,5,5,5));

        JLabel label = new JLabel(stopwatchPanel.getCurrentStopwatch().getName());
        label.setForeground(Color.WHITE);
        label.setFont(ClockFrame.font20);
        constraints.anchor = GridBagConstraints.CENTER;
        addComponent(label, 0, 1, 1, 1, 5,5,1,0, GridBagConstraints.NONE, new Insets(5,5,5,5));

        Object[][] data = new Object[stopwatchPanel.getCurrentStopwatch().getLaps().size()][3];
        if (isLapsReversed)
        {
            for (int i = stopwatchPanel.getCurrentStopwatch().getLaps().size()-1, j=0; i >= 0; i--) {
                data[j][0] = stopwatchPanel.getCurrentStopwatch().getLaps().get(i).getLapNumber();
                data[j][1] = stopwatchPanel.getCurrentStopwatch().getLaps().get(i).getFormattedLapTime();
                data[j][2] = stopwatchPanel.getCurrentStopwatch().getLaps().get(i).getFormattedRecordedAt();
                j++;
            }
        }
        else
        {
            for (int i = 0; i < stopwatchPanel.getCurrentStopwatch().getLaps().size(); i++) {
                data[i][0] = stopwatchPanel.getCurrentStopwatch().getLaps().get(i).getLapNumber();
                data[i][1] = stopwatchPanel.getCurrentStopwatch().getLaps().get(i).getFormattedLapTime();
                data[i][2] = stopwatchPanel.getCurrentStopwatch().getLaps().get(i).getFormattedRecordedAt();
            }
        }

        createDataTable("lapsTable", data, lapTableColumnNames);
        JScrollPane scrollPane = new JScrollPane(lapsTable);
        constraints = new GridBagConstraints();
        addComponent(scrollPane, 1, 0, 2, 1, 5,5,1,1, GridBagConstraints.BOTH, new Insets(5,5,5,5));
    }

    /**
     * Creates a JTable to display the laps of the current stopwatch.
     * @param data the data to be displayed in the table
     * @param columnNames the names of the columns
     * @return the JTable displaying the data
     */
    private JTable createDataTable(String tableName, Object[][] data, String[] columnNames)
    {
        JTable table = createTable(tableName, data, columnNames);
        table.setPreferredScrollableViewportSize(table.getPreferredSize()); //thanks mKorbel +1 http://stackoverflow.com/questions/10551995/how-to-set-jscrollpane-layout-to-be-the-same-as-jtable
        table.setFont(ClockFrame.font10);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        return table;
    }

    /**
     * Creates a JTable based on the table name provided.
     * @param tableName the name of the table to create
     * @param data the data to be displayed in the table
     * @param columnNames the names of the columns
     * @return the JTable created
     */
    private JTable createTable(String tableName, Object[][] data, String[] columnNames)
    {
        if ("lapsTable".equals(tableName))
        {
            lapsTable = new JTable(new DefaultTableModel(data, columnNames));
            return lapsTable;
        }
        else // if ("stopwatchTable".equals(tableName))
        {
            stopwatchTable = new JTable(new DefaultTableModel(data, columnNames));
            return stopwatchTable;
        }
    }

    /** Displays all the stopwatches in a table */
    private void viewStopwatchesTable(ActionEvent e)
    {
        resetPanel();
        stopwatchPanel.getClock().getListOfStopwatches().forEach(Stopwatch::pauseStopwatch);
        stopwatchPanel.getStartButton().setText(RESUME);
        JLabel label = new JLabel(ALL + SPACE + STOPWATCHES);
        label.setForeground(Color.WHITE);
        label.setFont(ClockFrame.font20);
        // add a space or some gap, if needed
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        addComponent(label, 0, 0, 1, 1, 5,5,1,0, GridBagConstraints.NONE, new Insets(5,5,5,5));

        // generate table to display laps.
        Object[][] data = getStopwatchesData();
        stopwatchTable = createDataTable("stopwatchTable", data, stopwatchTableColumnNames);
        if (stopwatchTable.getModel().getRowCount() != data.length) {
            stopwatchTable.setModel(new javax.swing.table.DefaultTableModel(data, stopwatchTableColumnNames));
            new ButtonColumn(stopwatchTable, buttonAction(2), 2);
            new ButtonColumn(stopwatchTable, buttonAction(3), 3);
        } else {
            AtomicInteger rowIndex = new AtomicInteger();
            stopwatchPanel.getClock().getListOfStopwatches().forEach(stopwatch -> {
                stopwatchTable.setValueAt(stopwatch.getName(), rowIndex.get(), 0);
                stopwatchTable.setValueAt(stopwatch.elapsedFormatted(stopwatch.getAccumMilli(), STOPWATCH_PARSE_FORMAT), rowIndex.get(), 1);
                new ButtonColumn(stopwatchTable, buttonAction(2), 2);
                new ButtonColumn(stopwatchTable, buttonAction(3), 3);
                rowIndex.getAndIncrement();
            });
        }
        JScrollPane scrollPane = new JScrollPane(stopwatchTable);
        addComponent(scrollPane, 1, 0, 1, 1, 5,5,1,1, GridBagConstraints.BOTH, new Insets(5,5,5,5));
        repaint();
    }

    /**
     * Generates the data for the stopwatches table.
     * @return the data for the stopwatches table
     */
    private Object[][] getStopwatchesData()
    {
        return stopwatchPanel.getClock().getListOfStopwatches().stream()
                .map(stopwatch -> new Object[] {
                        stopwatch.getName(),
                        stopwatch.elapsedFormatted(stopwatch.getAccumMilli(), STOPWATCH_PARSE_FORMAT),
                        SELECT,
                        REMOVE
                })
                .toArray(Object[][]::new);
    }

    /**
     * The main method used for adding components
     * to the alarm panel
     * @param cpt       the component to add
     * @param rowy      the y position
     * @param rowx      the x position
     * @param gwidth    the width
     * @param gheight   the height
     * @param ipadx     the x padding
     * @param ipady     the y padding
     * @param weightx   the horizontal weight for GridBagConstraints
     * @param weighty   the vertical weight for GridBagConstraints
     * @param fill      the fill
     * @param insets    the insets
     */
    public void addComponent(Component cpt, int rowy, int rowx, double gwidth, double gheight,
                             int ipadx, int ipady, int weightx, int weighty, int fill, Insets insets)
    {
        logger.debug("adding component to stopwatch panel");
        constraints.gridx = rowx;
        constraints.gridy = rowy;
        constraints.gridwidth = (int)Math.ceil(gwidth);
        constraints.gridheight = (int)Math.ceil(gheight);
        constraints.fill = fill;
        constraints.ipadx = ipadx;
        constraints.ipady = ipady;
        constraints.insets = insets;
        constraints.weightx = Math.max(weightx, 0);
        constraints.weighty = Math.max(weighty, 0);
        layout.setConstraints(cpt,constraints);
        add(cpt);
    }

    /** Removes all components from the panel and repaints it */
    public void resetPanel()
    {
        removeAll();
        revalidate();
        repaint();
    }

    /**
     * This method creates the button action for the
     * buttons in the stopwatches table.
     * @param columnIndex the index of the column where the button
     *                    is located
     * @return the action to be performed when the button is clicked
     */
    public Action buttonAction(int columnIndex)
    {
        return new AbstractAction()
        {
            public void actionPerformed(ActionEvent e) {
                int modelRow = Integer.parseInt(e.getActionCommand());
                String buttonAction = (String) stopwatchTable.getModel().getValueAt(modelRow, columnIndex);
                Stopwatch stopwatch = stopwatchPanel.getClock().getListOfStopwatches().get(modelRow);
                stopwatchPanel.setCurrentStopwatch(stopwatch);
                logger.debug("{} {} at row: {}", buttonAction, stopwatch, modelRow);
                if (buttonAction.equals(SELECT))
                {
                    stopwatchPanel.getStopwatchNameField().setText(stopwatch.getName());
                    stopwatchPanel.getDisplayTimePanel().setClockText(stopwatch.elapsedFormatted(stopwatchPanel.getCurrentStopwatch().getAccumMilli(), STOPWATCH_READING_FORMAT));
                    stopwatchPanel.getDisplayLapsPanel().updateLabelsAndStopwatchTable();
                    stopwatchPanel.getStartButton().setText(RESUME);
                    stopwatchPanel.getLapButton().setText(LAP);
                }
                if (buttonAction.equals(REMOVE))
                {
                    stopwatch.stopStopwatch();
                    stopwatchPanel.getClock().getListOfStopwatches().remove(stopwatch);
                    if (stopwatchPanel.getClock().getListOfStopwatches().isEmpty()) {
                        stopwatchPanel.setCurrentStopwatch(null);
                        stopwatchPanel.getStartButton().setText(START);
                    } else {
                        // set the current stopwatch to the last stopwatch in the list
                        stopwatchPanel.setCurrentStopwatch(stopwatchPanel.getClock().getListOfStopwatches().getLast());
                    }
                    // refresh the stopwatchTable now that a row has been removed
                    stopwatchTable.setModel(new javax.swing.table.DefaultTableModel(getStopwatchesData(), stopwatchTableColumnNames));
                }
            }
        };
    }

    /**
     * Returns the stopwatch panel
     * @return the stopwatch panel reference
     */
    public StopwatchPanel getStopwatchPanel() { return stopwatchPanel; }
    /**
     * Returns the laps table
     * @return the laps table
     */
    public JTable getLapsTable() { return lapsTable; }
    /**
     * Returns the stopwatch table
     * @return the stopwatch table
     */
    public JTable getStopwatchTable() { return stopwatchTable; }
    /**
     * Returns the lap table column names
     * @return the lap table column names
     */
    public String[] getLapTableColumnNames() { return lapTableColumnNames; }
    /**
     * Returns the stopwatch table column names
     * @return the stopwatch table column names
     */
    public String[] getStopwatchTableColumnNames() { return stopwatchTableColumnNames; }
    /**
     * Returns isLapsReversed
     * @return true if laps are displayed in reverse order
     */
    public boolean isLapsReversed() { return isLapsReversed; }

    /**
     * Sets the grid bay layout manager
     * @param layout the GridBagLayout to use
     */
    private void setGridBagLayout(GridBagLayout layout) { setLayout(layout); this.layout = layout; logger.debug("GridBagLayout set"); }
    /**
     * Sets the grid bag constraints
     * @param constraints the GridBagConstraints to use
     */
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
    /**
     * Sets the laps table
     * @param lapsTable the JTable used to display lap data
     */
    public void setLapsTable(JTable lapsTable)
    {
        this.lapsTable = lapsTable;
    }
    /**
     * Sets the stopwatch table
     * @param stopwatchTable the JTable used to display stopwatch data
     */
    public void setStopwatchTable(JTable stopwatchTable)
    {
        this.stopwatchTable = stopwatchTable;
    }
    /**
     * Sets whether laps are displayed in reverse order
     * @param lapsReversed true to show laps in descending order
     */
    public void setLapsReversed(boolean lapsReversed)
    {
        isLapsReversed = lapsReversed;
    }
}
