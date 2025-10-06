package clock.panel;

import clock.entity.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static clock.util.Constants.*;

/**
 * DisplayLapsPanel
 * <p>
 * A DisplayLapsPanel is a JPanel that displays the laps of a Stopwatch
 * in a table format. It allows users to view all stopwatches and their
 * elapsed times, as well as the laps of the currently selected stopwatch.
 *
 * @author michael ball
 * @version 2.9
 */
public class DisplayLapsPanel extends JPanel
{
    private static final Logger logger = LogManager.getLogger(DisplayLapsPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private final StopwatchPanel stopwatchPanel;
    public Thread thread;
    private JTable lapsTable,
                   stopwatchTable;
    private final String[] columnNames = {"Lap #", "Time", "Recorded"};
    public boolean isLapsReversed = false;

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
        JLabel label = new JLabel("Sw" + (Stopwatch.stopwatchCounter + 1));
        label.setForeground(Color.WHITE);
        label.setFont(ClockFrame.font20);
        // add a space or some gap, if needed
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        addComponent(label, 0, 0, 1, 1, 5,5,1,0, GridBagConstraints.NONE, new Insets(5,5,5,5));

        lapsTable = new JTable(new Object[0][0], columnNames);
        JScrollPane scrollPane = new JScrollPane(lapsTable);
        addComponent(scrollPane, 1, 0, 1, 1, 5,5,1,1, GridBagConstraints.BOTH, new Insets(5,5,5,5));
    }

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
                data[j][2] = stopwatchPanel.getCurrentStopwatch().getLaps().get(i).getFormattedDuration();
                j++;
            }
        }
        else
        {
            for (int i = 0; i < stopwatchPanel.getCurrentStopwatch().getLaps().size(); i++) {
                data[i][0] = stopwatchPanel.getCurrentStopwatch().getLaps().get(i).getLapNumber();
                data[i][1] = stopwatchPanel.getCurrentStopwatch().getLaps().get(i).getFormattedLapTime();
                data[i][2] = stopwatchPanel.getCurrentStopwatch().getLaps().get(i).getFormattedDuration();
            }
        }

        lapsTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(lapsTable);
        constraints = new GridBagConstraints();
        addComponent(scrollPane, 1, 0, 2, 1, 5,5,1,1, GridBagConstraints.BOTH, new Insets(5,5,5,5));

    }

    /** Displays all the stopwatches in a table */
    public void viewStopwatchesTable(ActionEvent e)
    {
        resetPanel();
        stopwatchPanel.getCurrentStopwatch().pauseStopwatch();
        stopwatchPanel.getStartButton().setText(RESUME);
        JLabel label = new JLabel(ALL + SPACE + STOPWATCHES);
        label.setForeground(Color.WHITE);
        label.setFont(ClockFrame.font20);
        // add a space or some gap, if needed
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        addComponent(label, 0, 0, 1, 1, 5,5,1,0, GridBagConstraints.NONE, new Insets(5,5,5,5));

        String[] stopwatchColumnNames = {NAME + SPACE + of + SPACE + STOPWATCH, ELAPSED};
        // generate table to display laps.
        Object[][] data = new Object[stopwatchPanel.getClock().getListOfStopwatches().size()][2];
        for (int i = 0; i < stopwatchPanel.getClock().getListOfStopwatches().size(); i++) {
            data[i][0] = stopwatchPanel.getClock().getListOfStopwatches().get(i).getName();
            data[i][1] = stopwatchPanel.getClock().getListOfStopwatches().get(i).elapsedAccumulated();
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
     * @param rowy      the y position
     * @param rowx      the x position
     * @param gwidth    the width
     * @param gheight   the height
     * @param ipadx     the x padding
     * @param ipady     the y padding
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

    /** Setters */
    private void setGridBagLayout(GridBagLayout layout) { setLayout(layout); this.layout = layout; logger.debug("GridBagLayout set"); }
    public void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
}
