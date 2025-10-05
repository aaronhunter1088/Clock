package clock.panel;

import clock.entity.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.util.List;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;

class DisplayLapsPanel extends JPanel { //implements Runnable {

    private static final Logger logger = LogManager.getLogger(DisplayLapsPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private StopwatchPanel stopwatchPanel;
    public Thread thread;
    private Stopwatch stopwatch; // current
    private List<Stopwatch> listOfStopwatches; // all
    private JTable lapsTable,
                   stopwatchTable;
    private final String[] columnNames = {"Lap #", "Time", "Recorded"};

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

//    /**
//     * Starts the display laps panel thread
//     * and internally calls the run method.
//     */
//    public void start()
//    {
//        logger.debug("starting display laps panel");
//        if (thread == null)
//        {
//            thread = new Thread(this);
//            thread.start();
//        }
//    }
//
//    /** Stops the display laps panel thread. */
//    public void stop()
//    {
//        logger.debug("stopping laps display panel");
//        thread = null;
//    }
//
//    // TODO: Does this need to be 'running'?
//    /** Repaints the display laps panel */
//    @Override
//    public void run()
//    {
//        while (thread != null)
//        {
//            try
//            {
//                resetPanel();
//                // TODO: Add table of laps with stopwatch name
//                updateLabelsAndStopwatchTable();
//                // Label should be clickable.
//                sleep(50);
//            }
//            catch (InterruptedException e)
//            {}
//        }
//    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    // TODO: We only create one table, but it's a table per stopwatch
    public void updateLabelsAndStopwatchTable()
    {
        resetPanel();
        // generate label or something clickable.
        JButton viewAll = new JButton("View All");
        viewAll.setFont(ClockFrame.font20);
        viewAll.setOpaque(true);
        viewAll.setName(VIEW_STOPWATCH + "es" + BUTTON);
        viewAll.setBackground(Color.BLACK);
        viewAll.setForeground(Color.BLUE);
        viewAll.addActionListener(this::viewStopwatchesTable);
        addComponent(viewAll, 0, 0, 1, 1, 5,5,1,0, GridBagConstraints.NONE, new Insets(5,5,5,5));

        JLabel label = new JLabel(stopwatch.getName());
        label.setForeground(Color.WHITE);
        label.setFont(ClockFrame.font20);

        List<Stopwatch> stopwatches = List.of(stopwatch); //getStopwatch().getClock().getListOfStopwatches();
        stopwatches.forEach(stopwatch -> {
            // generate table to display laps.
            Object[][] data = new Object[stopwatch.getLaps().size()][3];
            // TODO: Reverse the order of the laps so the latest is on top.
            for (int i = 0; i < stopwatch.getLaps().size(); i++) {
                data[i][0] = stopwatch.getLaps().get(i).getLapNumber();
                data[i][1] = stopwatch.getLaps().get(i).getFormattedLapTime();
                data[i][2] = stopwatch.getLaps().get(i).getFormattedDuration();
            }
            lapsTable = new JTable(data, columnNames);
            constraints.anchor = GridBagConstraints.FIRST_LINE_START;
            addComponent(label, 0, 1, 1, 1, 5,5,1,0, GridBagConstraints.NONE, new Insets(5,5,5,5));
            JScrollPane scrollPane = new JScrollPane(lapsTable);
            constraints = new GridBagConstraints();
            addComponent(scrollPane, 1, 0, 2, 1, 5,5,1,1, GridBagConstraints.BOTH, new Insets(5,5,5,5));
        });
        // called immediately after returning
        //revalidate();
        //repaint();
    }

    public void setDefaultLayout()
    {
        JLabel label = new JLabel("Sw1");
        label.setForeground(Color.WHITE);
        label.setFont(ClockFrame.font20);
        // add a space or some gap, if needed
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        addComponent(label, 0, 0, 1, 1, 5,5,1,0, GridBagConstraints.NONE, new Insets(5,5,5,5));

        lapsTable = new JTable(new Object[0][0], columnNames);
        JScrollPane scrollPane = new JScrollPane(lapsTable);
        addComponent(scrollPane, 1, 0, 1, 1, 5,5,1,1, GridBagConstraints.BOTH, new Insets(5,5,5,5));
    }

    public void viewStopwatchesTable(ActionEvent e)
    {
        resetPanel();
        stopwatch.pauseStopwatch();
        stopwatchPanel.getStartButton().setText(RESUME);
        JLabel label = new JLabel("All Stopwatches");
        label.setForeground(Color.WHITE);
        label.setFont(ClockFrame.font20);
        // add a space or some gap, if needed
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        addComponent(label, 0, 0, 1, 1, 5,5,1,0, GridBagConstraints.NONE, new Insets(5,5,5,5));

        String[] stopwatchColumnNames = {"Name of Stopwatch", "Elapsed"};
        // generate table to display laps.
        Object[][] data = new Object[listOfStopwatches.size()][2];
        for (int i = 0; i < listOfStopwatches.size(); i++) {
            data[i][0] = listOfStopwatches.get(i).getName();
            data[i][1] = listOfStopwatches.get(i).elapsedAccumulated();
        }
        stopwatchTable = new JTable(data, stopwatchColumnNames);
        JScrollPane scrollPane = new JScrollPane(stopwatchTable);
        addComponent(scrollPane, 1, 0, 1, 1, 5,5,1,1, GridBagConstraints.BOTH, new Insets(5,5,5,5));
        repaint();
    }

    /**
     * The main method used for adding components
     * to the alarm panel
     * @param cpt       the component to add
     * @param gridy     the y position
     * @param gridx     the x position
     * @param gwidth    the width
     * @param gheight   the height
     * @param ipadx     the x padding
     * @param ipady     the y padding
     * @param fill      the fill
     * @param insets    the insets
     */
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight,
                             int ipadx, int ipady, int weightx, int weighty, int fill, Insets insets)
    {
        logger.debug("add component");
        constraints.gridx = gridx;
        constraints.gridy = gridy;
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

    public void resetPanel()
    {
        removeAll();
        revalidate();
        repaint();
    }

    private void setGridBagLayout(GridBagLayout layout) { setLayout(layout); this.layout = layout; logger.debug("GridBagLayout set"); }
    public void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
    public void setStopwatch(Stopwatch stopwatch) { this.stopwatch = stopwatch; logger.debug("stopwatch set to {}", stopwatch); }

    // currentStopwatch
    public Stopwatch getStopwatch() { return stopwatch; }

    public void setListOfStopwatches(List<Stopwatch> listOfStopwatches) {
        this.listOfStopwatches = listOfStopwatches;
    }
}
