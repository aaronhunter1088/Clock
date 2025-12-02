package clock.panel;

import clock.entity.Clock;
import clock.entity.ClockMenuBar;
import clock.entity.Panel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static clock.entity.Panel.*;
import static clock.util.Constants.*;

/**
 * ClockFrame
 * <p>
 * The main panel used to display the clock application.
 *
 * @author michael ball
 * @version since 2.9
 */
public class ClockFrame extends JFrame
{
    @Serial
    private static final long serialVersionUID = 2L;
    private static final Logger logger = LogManager.getLogger(ClockFrame.class);

    public final static Dimension clockDefaultSize = new Dimension(700, 300);
    public final static Dimension analogueSize = new Dimension(350, 400);
    public final static Dimension panelSize = new Dimension(400, 300);
    public final static Dimension alarmSize = new Dimension(200,100);
    public final static Dimension timerSize = new Dimension(400, 300);
    public final static Font font60 = new Font("Courier New", Font.BOLD, 60);
    public final static Font font50 = new Font("Courier New", Font.BOLD, 50);
    public final static Font font40 = new Font("Courier New", Font.BOLD, 40);
    public final static Font font20 = new Font("Courier New", Font.BOLD, 20);
    public final static Font font10 = new Font("Courier New", Font.BOLD, 10);
    public final static Font analogueFont = new Font("TimesRoman", Font.BOLD, 20);
    private Panel panelType;
    private ClockMenuBar menuBar;
    private ClockPanel currentPanel,
                       digitalClockPanel,
                       analogueClockPanel,
                       alarmPanel,
                       timerPanel,
                       stopwatchPanel;
    private Clock clock;
    private ScheduledExecutorService scheduler;

    /**
     * Default constructor for ClockFrame
     * Initializes the clock with default settings
     */
    public ClockFrame()
    {
        super(CLOCK);
        initialize(null);
    }

    /**
     * Constructor for ClockFrame with a specific panel type
     * @param panelType the type of panel to display
     */
    public ClockFrame(clock.entity.Panel panelType)
    {
        super(CLOCK);
        setPanelType(panelType);
        initialize(null);
    }

    /**
     * Constructor for ClockFrame with a clock
     * @param clock the clock to use
     */
    public ClockFrame(Clock clock)
    {
        super(TEST + SPACE + CLOCK);
        logger.info("Creating ClockFrame with test clock");
        initialize(clock);
    }

    /**
     * Initializes the ClockFrame with the given clock.
     * @param clock the clock to use for initialization
     */
    private void initialize(Clock clock)
    {
        logger.info("Initializing ClockFrame");
        getContentPane().setBackground(Color.BLACK);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(200, 200, clockDefaultSize.width, clockDefaultSize.height);
        setSize(clockDefaultSize);
        ImageIcon icon = createImageIcon("src/main/resources/images/clockIcon.png");
        final Taskbar taskbar = Taskbar.getTaskbar();
        taskbar.setIconImage(icon.getImage());
        setIconImage(icon.getImage());
        setLocationRelativeTo(null); // loads the GUI in the center of the screen
        setResizable(false);
        setClock(clock != null ? clock : new Clock());
        setScheduler(Executors.newScheduledThreadPool(25));
        setupMenuBar(); // daylightSavingsTimeEnabled directly influences menu bar setup
        setDigitalClockPanel(new DigitalClockPanel(this));
        setAnalogueClockPanel(new AnalogueClockPanel(this));
        setAlarmPanel(new AlarmPanel(this));
        setTimerPanel(new TimerPanel(this));
        setStopwatchPanel(new StopwatchPanel(this));
        changePanels(panelType != null ? panelType : PANEL_DIGITAL_CLOCK);
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     * @param path the path of the image
     */
    private ImageIcon createImageIcon(String path)
    {
        logger.debug("createImageIcon");
        ImageIcon retImageIcon = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path.substring(19));
        if (resource != null) { retImageIcon = new ImageIcon(resource); }
        else {
            resource = classLoader.getResource(path.substring(19));
            if (resource != null) { retImageIcon = new ImageIcon(resource); }
            else { logger.error("The path '" + path + "' you provided cannot find a resource. Returning null"); }
        }
        return retImageIcon;
    }

    /**
     * Sets up the menu bar
     */
    private void setupMenuBar()
    {
        logger.info("setup menubar");
        UIManager.put("MenuItem.background", Color.BLACK);
        menuBar = new ClockMenuBar(this);
        setJMenuBar(menuBar);
    }

    /**
     * Changes the panel based on the provided value
     * @param changePanelType the panel to change to
     */
    public void changePanels(clock.entity.Panel changePanelType)
    {
        if (changePanelType != panelType || !isVisible())
        {
            logger.info("change panels");
            if (currentPanel != null) remove(currentPanel);
            if (currentPanel instanceof DigitalClockPanel dcp)
                dcp.stop();
            else if (currentPanel instanceof AnalogueClockPanel acp)
                acp.stop();
            else if (currentPanel instanceof TimerPanel tp)
                tp.stop();
            else if (currentPanel instanceof AlarmPanel ap)
                ap.stop();
            else if (currentPanel instanceof StopwatchPanel sp)
                sp.stop();
            showPanel(changePanelType);
            repaint();
            setVisible(true);
        }
    }

    /**
     * Updates the Clock to display a new panel
     * based on the provided clockPanel value.
     * @param clockPanel the panel to update to
     */
    private void showPanel(clock.entity.Panel clockPanel)
    {
        logger.debug("updating to {}", clockPanel);
        switch (clockPanel)
        {
            case PANEL_DIGITAL_CLOCK -> changeToDigitalClockPanel();
            case PANEL_ANALOGUE_CLOCK -> changeToAnalogueClockPanel();
            case PANEL_ALARM -> changeToAlarmPanel();
            case PANEL_TIMER -> changeToTimerPanel();
            case PANEL_STOPWATCH -> changeToStopwatchPanel();
        }
    }

    /**
     * Changes the panel to the digital clock panel
     */
    private void changeToDigitalClockPanel()
    {
        logger.info("change to digital clock");
        add(digitalClockPanel);
        setCurrentPanel(digitalClockPanel);
        getDigitalClockPanel().setupDefaultValues();
        setSize(clockDefaultSize);
        setBackground(Color.BLACK);
        setPanelType(PANEL_DIGITAL_CLOCK);
    }

    /**
     * Changes the panel to the analogue clock panel
     */
    private void changeToAnalogueClockPanel()
    {
        logger.info("change to analogue clock");
        add(analogueClockPanel);
        setCurrentPanel(analogueClockPanel);
        getAnalogueClockPanel().setupDefaultValues();
        setSize(analogueClockPanel.getMaximumSize());
        setBackground(Color.BLACK);
        setPanelType(PANEL_ANALOGUE_CLOCK);
    }

    /**
     * Changes the panel to the alarm panel
     */
    private void changeToAlarmPanel()
    {
        logger.info("change to alarm panel.");
        add(alarmPanel);
        setCurrentPanel(alarmPanel);
        getAlarmPanel().setupDefaultValues();
        setSize(clockDefaultSize);
        setPanelType(PANEL_ALARM);
    }

    /**
     * Changes the panel to the timer panel
     */
    private void changeToTimerPanel()
    {
        logger.info("change to timer panel");
        add(timerPanel);
        setCurrentPanel(timerPanel);
        getTimerPanel().setupDefaultValues();
        setSize(clockDefaultSize);
        setPanelType(PANEL_TIMER);
    }

    /**
     * Changes the panel to the stopwatch panel
     */
    private void changeToStopwatchPanel()
    {
        logger.debug("change to stopwatch panel");
        add(stopwatchPanel);
        setCurrentPanel(stopwatchPanel);
        getStopwatchPanel().setupDefaults();
        setSize(stopwatchPanel.getMaximumSize());
        setPanelType(PANEL_STOPWATCH);
    }

    /**
     * Updates the current time based on the selected timezone
     * @param timezone the timezone to update the time to
     */
    public void updateClockTimezone(JMenuItem timezone)
    {
        logger.info("clicked on {} timezone. updating the time", timezone.getText());
        LocalDateTime ldt = determineNewTimeFromSelectedTimeZone(timezone.getText().replace(STAR,EMPTY).trim());
        clock.setTheTime(ldt);
        clock.setTimeZone(clock.getZoneIdFromTimezoneButtonText(timezone.getText().replace(STAR,EMPTY).trim()));
        menuBar.setCurrentTimeZone();
    }

    /**
     * Returns a new LocalDateTime from the selected timezone
     * @param timezone the timezone to determine the new time from
     * @return LocalDateTime the new currentTime in the selected timezone
     */
    private LocalDateTime determineNewTimeFromSelectedTimeZone(String timezone)
    {
        return switch (timezone) {
            case HAWAII -> LocalDateTime.now(ZoneId.of(PACIFIC_HONOLULU));
            case ALASKA -> LocalDateTime.now(ZoneId.of(AMERICA_ANCHORAGE));
            case PACIFIC -> LocalDateTime.now(ZoneId.of(AMERICA_LOS_ANGELES));
            case CENTRAL -> LocalDateTime.now(ZoneId.of(AMERICA_CHICAGO));
            case EASTERN -> LocalDateTime.now(ZoneId.of(AMERICA_NEW_YORK));
            case MOUNTAIN -> LocalDateTime.now(ZoneId.of(AMERICA_DENVER));
            default -> LocalDateTime.now(ZoneId.systemDefault());
        };
    }

    /**
     * Quickly clears all options from the
     * settings menu.
     */
    void clearSettingsMenu()
    { getClockMenuBar().getSettingsMenu().removeAll(); }

    /**
     * Creates and shows the GUI for the Clock application.
     * This method is invoked in Main.
     */
    public static ClockFrame createAndShowGUI()
    {
        logger.info("Starting Clock...");
        ClockFrame clockFrame = new ClockFrame();
        clockFrame.start();
        return clockFrame;
    }

    /**
     * Creates and shows the GUI for the Clock application.
     * This method is invoked in Main when testing the
     * application with a specific clock.
     * @param clock the clock to use for testing
     */
    public static ClockFrame createAndShowGUI(Clock clock)
    {
        logger.info("Starting TestClock...");
        ClockFrame clockFrame = new ClockFrame(clock);
        clockFrame.start();
        return clockFrame;
    }

    /**
     * Creates and shows the GUI for the Clock application
     * with a specific panel type.
     * @param panelType the panel type to display
     */
    public static ClockFrame createAndShowGUI(Panel panelType)
    {
        logger.info("Starting Clock with panel type: {}", panelType);
        ClockFrame clockFrame = new ClockFrame(panelType);
        clockFrame.start();
        return clockFrame;
    }

    /**
     * Starts the clock and schedules the
     * tasks to run at a fixed rate.
     */
    void start()
    {
        scheduler.execute(clock);
    }

    /**
     * Stops the clock and all scheduled tasks.
     */
    public void stop()
    {
        setClock(null);
        setScheduler(null);
    }

    /** Returns the panel type */
    public clock.entity.Panel getPanelType() { return panelType; }
    /** Returns the current panel */
    public ClockPanel getCurrentPanel() { return currentPanel; }
    /** Returns the digital clock panel */
    public DigitalClockPanel getDigitalClockPanel() { return (DigitalClockPanel) digitalClockPanel; }
    /** Returns the analogue clock panel */
    public AnalogueClockPanel getAnalogueClockPanel() { return (AnalogueClockPanel) analogueClockPanel; }
    /** Returns the alarm panel */
    public AlarmPanel getAlarmPanel() { return (AlarmPanel) alarmPanel; }
    /** Returns the timer panel */
    public TimerPanel getTimerPanel() { return (TimerPanel) timerPanel; }
    /** Returns the stopwatch panel */
    public StopwatchPanel getStopwatchPanel() { return (StopwatchPanel) stopwatchPanel; }
    /** Returns the menu bar */
    public ClockMenuBar getClockMenuBar() { return menuBar; }
    /** Returns the clock */
    public Clock getClock() { return clock; }
    /** Returns the scheduler */
    public ScheduledExecutorService getScheduler() { return scheduler; }

    /** Sets the panel type */
    public void setPanelType(Panel panelType) { this.panelType = panelType; logger.debug("set panel type to {}", panelType); }
    /** Sets the current panel */
    public void setCurrentPanel(ClockPanel currentPanel) { this.currentPanel = currentPanel; logger.debug("currentPanel set"); }
    /** Sets the digital clock panel */
    public void setDigitalClockPanel(DigitalClockPanel digitalClockPanel) { this.digitalClockPanel = digitalClockPanel; logger.debug("digitalClockPanel set"); }
    /** Sets the analogue clock panel */
    public void setAnalogueClockPanel(AnalogueClockPanel analogueClockPanel) { this.analogueClockPanel = analogueClockPanel; logger.debug("analogueClockPanel set"); }
    /** Sets the alarm panel */
    public void setAlarmPanel(AlarmPanel alarmPanel) { this.alarmPanel = alarmPanel; logger.debug("alarmPanel set"); }
    /** Sets the timer panel */
    public void setTimerPanel(TimerPanel timerPanel) { this.timerPanel = timerPanel; logger.debug("timerPanel set"); }
    /** Sets the stopwatch panel */
    public void setStopwatchPanel(StopwatchPanel stopwatchPanel) { this.stopwatchPanel = stopwatchPanel; logger.debug("stopwatchPanel set"); }
    /** Sets the clock */
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set to {}", clock); }
    /** Sets the scheduler */
    public void setScheduler(ScheduledExecutorService scheduler) { this.scheduler = scheduler; logger.debug("scheduler set"); }
}


