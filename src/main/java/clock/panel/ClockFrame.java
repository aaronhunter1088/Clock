package clock.panel;

import clock.contract.IClockPanel;
import clock.entity.Clock;
import clock.entity.ClockMenuBar;
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

import static clock.panel.Panel.*;
import static clock.util.Constants.*;

/**
 * The ClockFrame is the main panel to display
 * the clock and its various components.
 *
 * @author michael ball
 * @version 2.9
 */
public class ClockFrame extends JFrame implements IClockPanel {

    @Serial
    private static final long serialVersionUID = 2L;
    private static final Logger logger = LogManager.getLogger(ClockFrame.class);

    public final static Dimension clockDefaultSize = new Dimension(700, 300);
    public final static Dimension panelSize = new Dimension(400, 300);
    public final static Dimension alarmSize = new Dimension(200,100);
    public final static Font font60 = new Font("Courier New", Font.BOLD, 60);
    public final static Font font50 = new Font("Courier New", Font.BOLD, 50);
    public final static Font font40 = new Font("Courier New", Font.BOLD, 40);
    public final static Font font20 = new Font("Courier New", Font.BOLD, 20);
    public final static Font font10 = new Font("Courier New", Font.BOLD, 10);
    public final static Font analogueFont = new Font("TimesRoman", Font.BOLD, 20);
    private Panel panelType = PANEL_DIGITAL_CLOCK; // Default panel type
    private ClockPanel currentPanel;
    private ClockMenuBar menuBar;
    private DigitalClockPanel digitalClockPanel;
    private AnalogueClockPanel analogueClockPanel;
    private AlarmPanel alarmPanel;
    //private TimerPanel timerPanel;
    private TimerPanel timerPanel;
    private StopwatchPanel stopwatchPanel;
    private Clock clock;

    private ScheduledExecutorService scheduler;

    /**
     * Default constructor for ClockFrame
     * Initializes the clock with default settings
     */
    public ClockFrame() {
        super(CLOCK);
        initialize(null);
    }

    public ClockFrame(Panel panelType) {
        super(CLOCK);
        initialize(null);
        this.panelType = panelType;
        addComponentsToPanel();
    }

    /**
     * Constructor for ClockFrame with a clock
     * @param clock the clock to use
     */
    public ClockFrame(Clock clock) {
        super("Test" + SPACE + CLOCK);
        logger.info("Creating ClockFrame with test clock");
        initialize(clock);
    }

    private void initialize(Clock clock)
    {
        logger.info("Initializing ClockFrame");
        getContentPane().setBackground(Color.BLACK);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(200, 200, clockDefaultSize.width, clockDefaultSize.height);
        setSize(clockDefaultSize);
        ImageIcon icon = createImageIcon("src/main/resources/images/clockImageIcon.png");
        final Taskbar taskbar = Taskbar.getTaskbar();
        taskbar.setIconImage(icon.getImage());
        setIconImage(icon.getImage());
        setLocationRelativeTo(null); // loads the GUI in the center of the screen
        setVisible(true);
        setResizable(false);
        logger.info("Creating {} Clock", clock != null ? "Test" : "Regular");
        setClock(clock != null ? clock : new Clock());
        scheduler = Executors.newScheduledThreadPool(25);
        setupMenuBar(); // daylightSavingsTimeEnabled directly influences menu bar setup
        digitalClockPanel = new DigitalClockPanel(this);
        analogueClockPanel = new AnalogueClockPanel(this);
        alarmPanel = new AlarmPanel(this);
        timerPanel = new TimerPanel(this);
        addComponentsToPanel();
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
     * Creates and shows the GUI for the Clock application.
     * This method is invoked in Main when testing the
     * application with a specific clock.
     * @param clock the clock to use for testing
     */
    public static void createAndShowGUI(Clock clock)
    {
        logger.info("Starting TestClock...");
        ClockFrame clockFrame = new ClockFrame(clock);
        clockFrame.start();
    }

    /**
     * Creates and shows the GUI for the Clock application.
     * This method is invoked in Main.
     */
    public static void createAndShowGUI()
    {
        logger.info("Starting Clock...");
        ClockFrame clockFrame = new ClockFrame();
        clockFrame.start();
    }

    /**
     * Creates and shows the GUI for the Clock application
     * with a specific panel type.
     * @param panelType the panel type to display
     */
    public static void createAndShowGUI(Panel panelType)
    {
        logger.info("Starting Clock with panel type: {}", panelType);
        ClockFrame clockFrame = new ClockFrame(panelType);
        clockFrame.start();
    }

    /**
     * Sets up the menu bar
     */
    public void setupMenuBar()
    {
        logger.info("setup menubar");
        UIManager.put("MenuItem.background", Color.BLACK);
        menuBar = new ClockMenuBar(this);
        setJMenuBar(menuBar);
    }

    /**
     * Changes the panels based on the provided clockPanel value
     * @param clockPanel the panel to change to
     */
    public void changePanels(Panel clockPanel, boolean resetValues)
    {
        logger.info("change panels");
        if (currentPanel != null) remove(currentPanel);
        if (currentPanel instanceof DigitalClockPanel dcp)
            dcp.stop();
        if (currentPanel instanceof AnalogueClockPanel acp)
            acp.stop();
        if (currentPanel instanceof TimerPanel tp)
            tp.stop();
        showPanel(clockPanel, resetValues);
        repaint();
        setVisible(true);
    }

    /**
     * Updates the Clock to display a new panel
     * based on the provided clockPanel value.
     * @param clockPanel the panel to update to
     */
    public void showPanel(Panel clockPanel, boolean resetValues)
    {
        logger.debug("updating to {}", clockPanel);
        switch (clockPanel)
        {
            case PANEL_DIGITAL_CLOCK -> changeToDigitalClockPanel();
            case PANEL_ANALOGUE_CLOCK -> changeToAnalogueClockPanel();
            case PANEL_ALARM -> changeToAlarmPanel(resetValues);
            case PANEL_TIMER -> changeToTimerPanel();
            //case PANEL_STOPWATCH -> changeToStopwatchPanel();
        }
    }

    /**
     * Changes the panel to the digital clock panel
     */
    public void changeToDigitalClockPanel()
    {
        logger.info("change to digital clock");
        add(digitalClockPanel);
        currentPanel = digitalClockPanel;
        digitalClockPanel.setupDefaultActions(this);
        setSize(clockDefaultSize);
        setBackground(Color.BLACK);
        panelType = DigitalClockPanel.PANEL;
    }

    /**
     * Changes the panel to the analogue clock panel
     */
    public void changeToAnalogueClockPanel()
    {
        logger.info("change to analogue clock");
        add(analogueClockPanel);
        currentPanel = analogueClockPanel;
        analogueClockPanel.setupDefaultActions(this);
        setSize(analogueClockPanel.getMaximumSize());
        setBackground(Color.BLACK);
        panelType = AnalogueClockPanel.PANEL;
        analogueClockPanel.setupSettingsMenu();
    }

    /**
     * Changes the panel to the alarm panel
     * @param resetValues if the values should be reset
     */
    public void changeToAlarmPanel(boolean resetValues)
    {
        logger.info("change to alarm panel. reset values: {}", resetValues);
        add(alarmPanel);
        currentPanel = alarmPanel;
        if (resetValues)
        {
            alarmPanel.getJTextField1().setText(EMPTY);
            alarmPanel.getJTextField2().setText(EMPTY);
            alarmPanel.getJTextField3().setText(EMPTY);
            alarmPanel.resetJCheckBoxes();
            alarmPanel.resetJTextArea(); // so error alarms don't show up after navigating out and back in
            alarmPanel.getJAlarmLbl4().setText("Current Alarms");
        }
        setSize(clockDefaultSize);
        panelType = PANEL_ALARM;
        alarmPanel.setupSettingsMenu();
    }

    /**
     * Changes the panel to the timer panel
     */
    public void changeToTimerPanel()
    {
        logger.info("change to timer panel");
        add(timerPanel);
        currentPanel = timerPanel;
        setSize(clockDefaultSize);
        panelType = PANEL_TIMER;
        timerPanel.setupSettingsMenu();
    }

    /**
     * Changes the panel to the stopwatch panel
     */
    public void changeToStopwatchPanel()
    {
        logger.debug("change to stopwatch panel");
        add(stopwatchPanel);
        currentPanel = stopwatchPanel;
        setSize(clockDefaultSize);
        panelType = PANEL_STOPWATCH;
        stopwatchPanel.setupSettingsMenu();
    }

    /**
     * Updates the current time based on the selected timezone
     * @param timezone the timezone to update the time to
     */
    public void updateTheTime(JMenuItem timezone)
    {
        logger.info("clicked on {} timezone. updating the time", timezone.getText());
        LocalDateTime ldt = determineNewTimeFromSelectedTimeZone(timezone.getText());
        clock.setTheTime(ldt);
        clock.setTimeZone(clock.getZoneIdFromTimezoneButtonText(timezone.getText()));
        menuBar.refreshTimezones();
        menuBar.setCurrentTimeZone();
    }

    /**
     * Returns a new LocalDateTime from the selected timezone
     * @param timezone the timezone to determine the new time from
     * @return LocalDateTime the new currentTime in the selected timezone
     */
    public LocalDateTime determineNewTimeFromSelectedTimeZone(String timezone)
    {
        return switch (timezone) {
            case HAWAII -> LocalDateTime.now(ZoneId.of(PACIFIC_HONOLULU));
            case ALASKA -> LocalDateTime.now(ZoneId.of(AMERICA_ANCHORAGE));
            case PACIFIC -> LocalDateTime.now(ZoneId.of(AMERICA_LOS_ANGELES));
            case CENTRAL -> LocalDateTime.now(ZoneId.of(AMERICA_CHICAGO));
            case EASTERN -> LocalDateTime.now(ZoneId.of(AMERICA_NEW_YORK));
            default -> LocalDateTime.now(ZoneId.systemDefault());
        };
    }

    /**
     * Quickly clears all options from the
     * settings menu.
     */
    public void clearSettingsMenu()
    { getClockMenuBar().getSettingsMenu().removeAll(); }

    /**
     * Starts the clock and schedules the
     * tasks to run at a fixed rate.
     */
    public void start()
    {
        // Wrap tasks to prevent exceptions from killing scheduled execution
        Function<Runnable, Runnable> taskRunner = task -> () -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("Scheduled task failed: {}", task, e);
                logger.warn("""
                        Because the schedule task failed, the application will now exit.
                        Please check the logs for more information.
                        """);
                System.exit(1);
            }
        };

        scheduler.schedule(taskRunner.apply(clock), 0, TimeUnit.SECONDS);
    }

    /**
     * Stops the clock and all scheduled tasks.
     */
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    @Override
    public void addComponentsToPanel() {
        changePanels(panelType, false);
    }

    @Override
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void setupSettingsMenu() {
        // handled in the panel
    }

    @Override
    public void printStackTrace(Exception e, String message)
    {}

    public Panel getPanelType() {
        return panelType;
    }

    public void setPanelType(Panel panelType) {
        this.panelType = panelType;
    }

    public ClockPanel getCurrentPanel() {
        return currentPanel;
    }

    public void setCurrentPanel(ClockPanel currentPanel) {
        this.currentPanel = currentPanel;
    }

    public DigitalClockPanel getDigitalClockPanel() {
        return digitalClockPanel;
    }

    public void setDigitalClockPanel(DigitalClockPanel digitalClockPanel) {
        this.digitalClockPanel = digitalClockPanel;
    }

    public AnalogueClockPanel getAnalogueClockPanel() {
        return analogueClockPanel;
    }

    public void setAnalogueClockPanel(AnalogueClockPanel analogueClockPanel) {
        this.analogueClockPanel = analogueClockPanel;
    }

    public AlarmPanel getAlarmPanel() {
        return alarmPanel;
    }

    public void setAlarmPanel(AlarmPanel alarmPanel) {
        this.alarmPanel = alarmPanel;
    }

//    public TimerPanel getTimerPanel() {
//        return timerPanel;
//    }

    public ClockMenuBar getClockMenuBar() {
        return menuBar;
    }

//    public void setTimerPanel(TimerPanel timerPanel) {
//        this.timerPanel = timerPanel;
//    }

    public TimerPanel getTimerPanel() {
        return timerPanel;
    }

    public void setTimerPanel(TimerPanel timerPanel) {
        this.timerPanel = timerPanel;
    }

    public StopwatchPanel getStopwatchPanel() {
        return stopwatchPanel;
    }

    public void setStopwatchPanel(StopwatchPanel stopwatchPanel) {
        this.stopwatchPanel = stopwatchPanel;
    }

    public Clock getClock() {
        return clock;
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }
}


