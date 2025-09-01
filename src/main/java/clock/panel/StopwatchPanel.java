package clock.panel;

import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;

/**
 * Stopwatch Panel
 * <p>
 * The stopwatch panel is used to set a 'timer' that counts up.
 * You can have multiple stopwatches, each with their own name,
 * however a default name of "Stopwatch" + the current count of
 * stopwatches + 1, will be set if no name is provided. This will
 * allow you to have multiple stopwatches for various purposes.
 * Once you click the start button, the stopwatch will begin
 * counting up until the user stops it. (although it will
 * be stopped automatically if the stopwatch were to reach 24
 * hours). The left side of the panel will show the stopwatch's
 * time in digital and analogue format, allowing you to choose
 * which mode to view the time in. The right side of the panel
 * will show the Laps that have been recorded, and all the stop
 * watches that have been created.
 *
 * @author michael ball
 * @version 2.9
 */
public class StopwatchPanel extends ClockPanel implements Runnable
{
    private static final Logger logger = LogManager.getLogger(StopwatchPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private Thread thread;
    private int xcenter = 175, ycenter = 175, lastxs = 0, lastys = 0, lastxm = 0, lastym = 0, lastxh = 0, lastyh = 0;
    private ClockFrame clockFrame;
    private Clock clock;
    private String clockText = EMPTY;
    // Used to display the stopwatch time in two modes
    private JPanel displayTimePanel,
                   displayLapsPanel;
                   //lapsPanel;
                   //stopwatchesPanel;
    private JButton lapButton, // toggles to reset when stopwatch is stopped
                    startButton; // toggles to stop when stopwatch is started
    private JTextField stopwatchNameField;
    private boolean showDigitalPanel;

    /**
     * Main constructor for creating the StopwatchPanel
     * @param clockFrame the clockFrame object reference
     */
    public StopwatchPanel(ClockFrame clockFrame)
    {
        super();
        initialize(clockFrame);
        SwingUtilities.updateComponentTreeUI(this);
        logger.info("Finished creating StopwatchPanel");
    }

    /**
     * Sets up the default actions for the analogue clock panel
     * @param clockFrame the clockFrame reference
     */
    public void initialize(ClockFrame clockFrame)
    {
        setClockFrame(clockFrame);
        setClock(clockFrame.getClock());
        setMaximumSize(ClockFrame.clockDefaultSize);
        setPreferredSize(ClockFrame.clockDefaultSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(layout);
        setGridBagConstraints(new GridBagConstraints());
        setupComponents();
        addComponentsToPanel();
    }

    /**
     * Sets up the components for the stopwatch panel
     */
    private void setupComponents()
    {
        logger.debug("Setting up components for StopwatchPanel");
        lapButton = new JButton(LAP);
        lapButton.setFont(ClockFrame.font20);
        lapButton.setOpaque(true);
        lapButton.setName(LAP + BUTTON);
        lapButton.setBackground(Color.BLACK);
        lapButton.setForeground(Color.BLUE);
        //lapButton.setMaximumSize(new Dimension(100, 50)); // umm, idk

        stopwatchNameField = new JTextField(EMPTY, 4);
        stopwatchNameField.setFont(ClockFrame.font20);
        stopwatchNameField.setOpaque(true);
        stopwatchNameField.setName(STOPWATCH + TEXT_FIELD);
        stopwatchNameField.setBackground(Color.WHITE);
        stopwatchNameField.setForeground(Color.BLACK);
        //stopwatchNameField.setMaximumSize(new Dimension(200, 30)); // umm, idk
        stopwatchNameField.setToolTipText("Enter Stopwatch Name");

        startButton = new JButton(START);
        startButton.setFont(ClockFrame.font20);
        startButton.setOpaque(true);
        startButton.setName(START + BUTTON);
        startButton.setBackground(Color.BLACK);
        startButton.setForeground(Color.BLUE);
        //startButton.setMaximumSize(new Dimension(100, 50)); // umm, idk

        createDisplayTimePanel();
        // Laps
        createLapsPanel();

        setupDefaults();
    }

    /**
     * Sets up the default values for the stopwatch panel
     */
    public void setupDefaults()
    {
        logger.debug("Setting up default values for StopwatchPanel");
        setupSettingsMenu();
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        clockFrame.setTitle(STOPWATCH+SPACE+PANEL);
        //start();
        ((DigitalDisplayPanel)displayTimePanel).start();
        ((LapsDisplayPanel)displayLapsPanel).start();
        //setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    /**
     * Add components to the panels as needed
     */
    private void addComponentsToPanel()
    {
        // Implement
        logger.info("addComponentsToPanel");
        addComponent(displayTimePanel,0,0,1,1,0,0, 1, 1, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(displayLapsPanel,0,1,1,1,0,0, 1, 1, GridBagConstraints.BOTH, new Insets(0,0,0,0));

        //addComponent(lapButton, 1, 0, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        //addComponent(stopwatchNameField, 1, 1, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        //addComponent(startButton, 1, 2, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));


    }

    /**
     * The main method used for adding
     * components to the setup timer panel
     * @param cpt       the component to add
     * @param gridy     the y position or the row
     * @param gridx     the x position or the column
     * @param gwidth    the width how many columns it takes up
     * @param gheight   the height how many rows it takes up
     * @param ipadx     the x padding
     * @param ipady     the y padding
     * @param weightx   the x weight
     * @param weighty   the y weight
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

    @Override
    public void setupSettingsMenu()
    {
        // Implement
    }

    private void createDisplayTimePanel()
    {
        displayTimePanel = new DigitalDisplayPanel(lapButton, startButton, stopwatchNameField);
        // Set layouts and properties for each panel
    }

    private void createLapsPanel()
    {
        displayLapsPanel = new LapsDisplayPanel();
        // Set layouts and properties for each panel
    }

    @Override
    public void printStackTrace(Exception e, String message)
    {

    }

    /**
     * Starts the analogue clock panel thread
     * and internally calls the run method.
     */
    public void start()
    {
        logger.debug("starting stopwatch panel");
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    /** Stops the timer panel thread. */
    public void stop()
    {
        logger.debug("stopping stopwatch panel");
        thread = null;
        ((DigitalDisplayPanel)displayTimePanel).stop();
        ((LapsDisplayPanel)displayLapsPanel).stop();
    }

    /** Repaints the stopwatch panel */
    @Override
    public void run()
    {
        while (thread != null)
        {
            try
            {
                sleep(1000);
            }
            catch (InterruptedException e)
            { printStackTrace(e, e.getMessage()); }
        }
    }

    /**
     * Paints the appropriate panel
     * @param g the graphics object
     */
    @Override
    public void paint(Graphics g)
    {
//        if (isShowDigitalPanel())
//        {
//            drawDigitalClock(g);
//        }
//        else
//        {
//            drawAnalogueClock(g);
//        }
    }

    /**
     * Draws the digital clock
     * @param g the graphics object
     */
    public void drawDigitalClock(Graphics g)
    {
        logger.info("drawing digital clock");
        g.setFont(ClockFrame.font20);
        if (clock.isShowFullDate()) g.setFont(ClockFrame.font40);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, ClockFrame.clockDefaultSize.width, ClockFrame.clockDefaultSize.height);

        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics(g.getFont()); // Get FontMetrics for string width calculation

        // Strings to draw
        String dateStr;
        String timeStr;

        dateStr = "00:00:00"; //clock.defaultText(1); // time
        timeStr = "Press Start"; clock.defaultText(2); // stopwatch status
        // Calculate centered x positions
        int dateWidth = fm.stringWidth(dateStr);
        int timeWidth = fm.stringWidth(timeStr);
        int panelWidth = displayTimePanel.getWidth();

        int dateX = (panelWidth - dateWidth);
        int timeX = (panelWidth - timeWidth);

        //int baseY = ClockFrame.clockDefaultSize.height / 2;
        int baseY = displayTimePanel.getHeight();

        g.drawString(dateStr, dateX, baseY + 60);
        g.drawString(timeStr, timeX, baseY + 90);
        g.setColor(Color.BLACK);
    }

    /**
     * Draws the analogue clock
     * @param g the graphics object
     */
    public void drawAnalogueClock(Graphics g)
    {
        logger.info("painting analogue clock panel");
        int xhour, yhour, xminute, yminute, xsecond, ysecond, second, minute, hour;

        if (isShowDigitalPanel())
        { clockText = clock.defaultText(1); }

        // space

        logger.info("drawing structure");
        g.setFont(ClockFrame.analogueFont);
        g.setColor(Color.BLACK);
        g.fillOval(xcenter - 150, ycenter - 150, 300, 300);

        if (clockFrame.getAnalogueClockPanel().isShowDigitalTimeOnAnalogueClock())
        {
            g.setColor(Color.WHITE);
            g.drawString(clockText, xcenter - 50, ycenter + 90); // 170
        }

        g.setColor(Color.WHITE);
        g.drawString(ONE, xcenter + 60, ycenter - 110);
        g.drawString(TWO, xcenter + 110, ycenter - 60);
        g.drawString(THREE, xcenter + 135, ycenter);
        g.drawString(FOUR, xcenter + 110, ycenter + 60);
        g.drawString(FIVE, xcenter + 70, ycenter + 110);
        g.drawString(SIX, xcenter - 10, ycenter + 145);
        g.drawString(SEVEN, xcenter - 80, ycenter + 110);
        g.drawString(EIGHT, xcenter - 120, ycenter + 60);
        g.drawString(NINE, xcenter - 145, ycenter);
        g.drawString(TEN, xcenter - 130, ycenter - 60);
        g.drawString(ELEVEN, xcenter - 80, ycenter - 110);
        g.drawString(TWELVE, xcenter - 10, ycenter - 130);
        g.setColor(Color.BLACK); // needed to avoid second hand delay UI issue

        // space

        second = clock.getTime().getSecond();
        minute = clock.getTime().getMinute();
        hour = clock.getTime().getHour();
        xsecond = (int)(Math.cos(second * 3.14f / 30 - 3.14f / 2) * 120 + xcenter);
        ysecond = (int)(Math.sin(second * 3.14f / 30 - 3.14f / 2) * 120 + ycenter);
        xminute = (int)(Math.cos(minute * 3.14f / 30 - 3.14f / 2) * 100 + xcenter);
        yminute = (int)(Math.sin(minute * 3.14f / 30 - 3.14f / 2) * 100 + ycenter);
        xhour = (int)(Math.cos((hour*30 + (double)minute/2) * 3.14f / 180 - 3.14f / 2) * 80 + xcenter);
        yhour = (int)(Math.sin((hour*30 + (double)minute/2) * 3.14f / 180 - 3.14f / 2) * 80 + ycenter);
        // Erase if necessary, and redraw

        // second hand start
        //g.setColor(Color.RED);
        if (xsecond != lastxs || ysecond != lastys) { g.drawLine(xcenter, ycenter, lastxs, lastys); }
        if (xminute != lastxm || yminute != lastym)
        {
            g.drawLine(xcenter, ycenter - 1, lastxm, lastym);
            g.drawLine(xcenter - 1, ycenter, lastxm, lastym);
        }
        if (xhour != lastxh || yhour != lastyh)
        {
            g.drawLine(xcenter, ycenter - 1, lastxh, lastyh);
            g.drawLine(xcenter - 1, ycenter, lastxh, lastyh);
        }
        // second
        g.setColor(Color.RED);
        g.drawLine(xcenter, ycenter, xsecond, ysecond);
        // minute
        g.setColor(Color.BLUE);
        g.drawLine(xcenter, ycenter - 1, xminute, yminute);
        g.drawLine(xcenter - 1, ycenter, xminute, yminute);
        // hour
        g.setColor(Color.BLUE);
        g.drawLine(xcenter, ycenter - 1, xhour, yhour);
        g.drawLine(xcenter - 1, ycenter, xhour, yhour);
        lastxs = xsecond;
        lastys = ysecond;
        lastxm = xminute;
        lastym = yminute;
        lastxh = xhour;
        lastyh = yhour;
    }

    @Override
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set"); }
    public void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; logger.debug("clockFrame set"); }
    public void setStopwatchLayout(GridBagLayout gridBagLayout) { this.layout = gridBagLayout; logger.debug("constraints set"); }
    private void setGridBagLayout(GridBagLayout layout) { setLayout(layout); this.layout = layout; logger.debug("GridBagLayout set"); }
    public void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
    public void setShowDigitalPanel(boolean showDigitalPanel) { this.showDigitalPanel = showDigitalPanel; logger.debug("showDigitalPanel set to {}", showDigitalPanel); }

    public Clock getClock() { return clock; }
    public ClockFrame getClockFrame() { return clockFrame; }
    public GridBagLayout getStopwatchLayout() { return layout; }
    public GridBagConstraints getGridBagConstraints() { return constraints; }
    public boolean isShowDigitalPanel() { return showDigitalPanel;  }

}

class DigitalDisplayPanel extends JPanel implements Runnable {

    private static final Logger logger = LogManager.getLogger(DigitalDisplayPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private Thread thread;
    private JButton lapButton, // toggles to
                    startButton; // toggles
    private JTextField stopwatchNameField;

    public DigitalDisplayPanel(JButton lapButton, JButton startButton, JTextField stopwatchNameField)
    {
        super();
        setGridBagLayout(new GridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        setLayout(layout);
        setSize(ClockFrame.analogueSize);
        setMinimumSize(ClockFrame.analogueSize);
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        this.lapButton = lapButton;
        this.startButton = startButton;
        this.stopwatchNameField = stopwatchNameField;
        start();
    }

    /**
     * Starts the digital clock panel thread
     * and internally calls the run method.
     */
    public void start()
    {
        logger.debug("starting digital clock panel");
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    /** Stops the digital clock panel thread. */
    public void stop()
    {
        logger.debug("stopping digital clock panel");
        thread = null;
    }

    /** Repaints the stopwatch panel */
    @Override
    public void run()
    {
        while (thread != null)
        {
            try
            {
                repaint(); // goes to paint
                sleep(1000);
            }
            catch (InterruptedException e)
            {}
        }
    }

    @Override
    public void paint(Graphics g) {
        drawDigitalClock(g);
    }

    public void drawDigitalClock(Graphics g)
    {
        logger.info("drawing digital clock panel");
        g.setFont(ClockFrame.font20);
        //if (clock.isShowFullDate()) g.setFont(ClockFrame.font40);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 350, 400);

        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics(g.getFont()); // Get FontMetrics for string width calculation

        // Strings to draw
        String dateStr;
        String timeStr;

        dateStr = "00:00:00"; //clock.defaultText(1); // time
        timeStr = "Press Start"; //clock.defaultText(2); // stopwatch status
        // Calculate centered x positions
        int dateWidth = fm.stringWidth(dateStr);
        int timeWidth = fm.stringWidth(timeStr);
        int panelWidth = this.getWidth();

//        int dateX = (panelWidth - dateWidth);
//        int timeX = (panelWidth - timeWidth);
        int dateX = (panelWidth - dateWidth) / 2;

        int timeX = (panelWidth - timeWidth) / 2;

        //int baseY = ClockFrame.clockDefaultSize.height / 2;
        //int baseY = this.getHeight();
        int baseY = this.getHeight() / 2;

        /*
        When you want to center text horizontally in Java Swing, you need to
        know how wide the text will be when rendered. FontMetrics provides the
        pixel width of a string for the current font. By subtracting the string
        width from the panel width and dividing by 2, you get the x-coordinate
        where the text should start so that it is centered. This ensures the same
        amount of space on both sides of the text, making it visually centered
        regardless of the string's length or font.
         */
        g.drawString(dateStr, dateX, baseY - 20);  // + 60
        g.drawString(timeStr, timeX, baseY + 20);  // + 90
        g.setColor(Color.BLACK);
    }

    private void setGridBagLayout(GridBagLayout layout) { setLayout(layout); this.layout = layout; logger.debug("GridBagLayout set"); }
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
}

class LapsDisplayPanel extends JPanel implements Runnable {

    private static final Logger logger = LogManager.getLogger(LapsDisplayPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private Thread thread;
    private int xcenter = 175, ycenter = 175, lastxs = 0, lastys = 0, lastxm = 0, lastym = 0, lastxh = 0, lastyh = 0;

    public LapsDisplayPanel()
    {
        super();
        setGridBagLayout(new GridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        setLayout(layout);
        setSize(ClockFrame.analogueSize);
        setMinimumSize(ClockFrame.analogueSize);
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        start();
    }

    /**
     * Starts the laps display panel thread
     * and internally calls the run method.
     */
    public void start()
    {
        logger.debug("starting laps display panel");
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    /** Stops the laps display panel thread. */
    public void stop()
    {
        logger.debug("stopping laps display panel");
        thread = null;
    }

    /** Repaints the stopwatch panel */
    @Override
    public void run()
    {
        while (thread != null)
        {
            try
            {
                repaint(); // goes to paint
                sleep(1000);
            }
            catch (InterruptedException e)
            {}
        }
    }

    @Override
    public void paint(Graphics g) {
        drawAnalogueClock(g);
    }

    /**
     * Draws the analogue clock
     * @param g the graphics object
     */
    public void drawAnalogueClock(Graphics g)
    {
        logger.info("painting analogue clock panel");
        int width = getWidth();
        int height = getHeight();
        int diameter = Math.min(width, height) - 20; // leave some margin
        int radius = diameter / 2;
        int xcenter = width / 2;
        int ycenter = height / 2;

        g.setFont(ClockFrame.analogueFont);
        g.setColor(Color.BLACK);
        g.fillOval(xcenter - radius, ycenter - radius, diameter, diameter);

        g.setColor(Color.WHITE);
        g.drawString("Press Start", xcenter - 50, ycenter + (int)(radius * 0.45));  // adjust radius number as needed

        // Draw numbers (adjust positions for new radius)
        g.drawString(ONE,    xcenter + (int)(radius * 0.4),  ycenter - (int)(radius * 0.75));
        g.drawString(TWO,    xcenter + (int)(radius * 0.75), ycenter - (int)(radius * 0.4));
        g.drawString(THREE,  xcenter + radius - 10,          ycenter);
        g.drawString(FOUR,   xcenter + (int)(radius * 0.75), ycenter + (int)(radius * 0.4));
        g.drawString(FIVE,   xcenter + (int)(radius * 0.4),  ycenter + (int)(radius * 0.75));
        g.drawString(SIX,    xcenter - 10,                   ycenter + radius - 5);
        g.drawString(SEVEN,  xcenter - (int)(radius * 0.5),  ycenter + (int)(radius * 0.7));
        g.drawString(EIGHT,  xcenter - (int)(radius * 0.8),  ycenter + (int)(radius * 0.4));
        g.drawString(NINE,   xcenter - radius + 5,           ycenter);
        g.drawString(TEN,    xcenter - (int)(radius * 0.8),  ycenter - (int)(radius * 0.4));
        g.drawString(ELEVEN, xcenter - (int)(radius * 0.5),  ycenter - (int)(radius * 0.7));
        g.drawString(TWELVE, xcenter - 10,                   ycenter - radius + 20);

        g.setColor(Color.BLACK);

        // Example hands (all zero for now)
        int second = 0, minute = 0, hour = 0;
        int xsecond = (int)(Math.cos(second * Math.PI / 30 - Math.PI / 2) * (radius * 0.8) + xcenter);
        int ysecond = (int)(Math.sin(second * Math.PI / 30 - Math.PI / 2) * (radius * 0.8) + ycenter);
        int xminute = (int)(Math.cos(minute * Math.PI / 30 - Math.PI / 2) * (radius * 0.65) + xcenter);
        int yminute = (int)(Math.sin(minute * Math.PI / 30 - Math.PI / 2) * (radius * 0.65) + ycenter);
        int xhour = (int)(Math.cos((hour*30 + (double)minute/2) * Math.PI / 180 - Math.PI / 2) * (radius * 0.45) + xcenter);
        int yhour = (int)(Math.sin((hour*30 + (double)minute/2) * Math.PI / 180 - Math.PI / 2) * (radius * 0.45) + ycenter);

        // Draw hands
        g.setColor(Color.RED);
        g.drawLine(xcenter, ycenter, xsecond, ysecond);
        g.setColor(Color.BLUE);
        g.drawLine(xcenter, ycenter, xminute, yminute);
        g.drawLine(xcenter, ycenter, xhour, yhour);
    }

    private void setGridBagLayout(GridBagLayout layout) { setLayout(layout); this.layout = layout; logger.debug("GridBagLayout set"); }
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
}