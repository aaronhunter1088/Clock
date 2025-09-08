package clock.panel;

import clock.entity.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.lang.Thread.sleep;

class DisplayLapsPanel extends JPanel implements Runnable {

    private static final Logger logger = LogManager.getLogger(DisplayLapsPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    public Thread thread;
    private Stopwatch stopwatch;
    private JTable stopwatchTable;

    public DisplayLapsPanel()
    {
        super();
        setGridBagLayout(new GridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        setPreferredSize(ClockFrame.analogueSize);
        setMinimumSize(ClockFrame.analogueSize);
        setMaximumSize(ClockFrame.analogueSize);
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setDefaultLayout();
    }

    /**
     * Starts the display laps panel thread
     * and internally calls the run method.
     */
    public void start()
    {
        logger.debug("starting display laps panel");
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    /** Stops the display laps panel thread. */
    public void stop()
    {
        logger.debug("stopping laps display panel");
        thread = null;
    }

    /** Repaints the display laps panel */
    @Override
    public void run()
    {
        while (thread != null)
        {
            try
            {
                //repaint(); // goes to paint
                // TODO: Add table of laps with stopwatch name
                updateLabelsAndStopwatchTable();
                // Label should be clickable.
                sleep(50);
            }
            catch (InterruptedException e)
            {}
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    // TODO: We only create one table, but it's a table per stopwatch
    public void updateLabelsAndStopwatchTable()
    {
        resetPanel();
        List<Stopwatch> stopwatches = getStopwatch().getClock().getListOfStopwatches();
        stopwatches.forEach(stopwatch -> {
            // generate label or something clickable.
            JLabel label = new JLabel(stopwatch.getName());
            label.setForeground(Color.WHITE);
            label.setFont(ClockFrame.font20);
            // generate table to display laps.
            String[] columnNames = {"Lap #", "Time"};
            Object[][] data = new Object[stopwatch.getLaps().size()][2];
            for (int i = 0; i < stopwatch.getLaps().size(); i++) {
                data[i][0] = stopwatch.getLaps().get(i).getLapNumber();
                data[i][1] = stopwatch.getLaps().get(i).getLapTime();
            }
            stopwatchTable = new JTable(data, columnNames);
            //stopwatchTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
            constraints.anchor = GridBagConstraints.FIRST_LINE_START;
            addComponent(label, 0, 0, 1, 1, 5,5,1,1, GridBagConstraints.NONE, new Insets(5,5,5,5));
            JScrollPane scrollPane = new JScrollPane(stopwatchTable);
            constraints = new GridBagConstraints();
            addComponent(scrollPane, 1, 0, 1, 1, 5,5,1,1, GridBagConstraints.BOTH, new Insets(5,5,5,5));
        });
        revalidate();
        repaint();
    }

    public void setDefaultLayout()
    {
        JLabel label = new JLabel("Sw1");
        label.setForeground(Color.WHITE);
        label.setFont(ClockFrame.font20);

        String[] columnNames = {"Lap #", "Time"};
        // add a space or some gap, if needed
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        addComponent(label, 0, 0, 1, 1, 5,5,1,0, GridBagConstraints.NONE, new Insets(5,5,5,5));
        stopwatchTable = new JTable(new Object[0][0], columnNames);
        JScrollPane scrollPane = new JScrollPane(stopwatchTable);
        //constraints = new GridBagConstraints();
        addComponent(scrollPane, 1, 0, 1, 1, 5,5,1,1, GridBagConstraints.BOTH, new Insets(5,5,5,5));
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

    public Stopwatch getStopwatch() { return stopwatch; }
}
