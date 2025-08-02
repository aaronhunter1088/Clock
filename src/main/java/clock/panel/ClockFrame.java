package clock.panel;

import clock.contract.IClockPanel;
import clock.entity.Alarm;
import clock.entity.Clock;
import clock.entity.ClockMenuBar;
import clock.entity.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static clock.panel.Panel.*;
import static clock.util.Constants.*;

/**
 * The ClockFrame is the main panel to display
 * the clock and its various components.
 *
 * @author michael ball
 *  @version 2.9
 */
public class ClockFrame extends JFrame implements IClockPanel, Runnable {

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
    private Panel clockPanel;
    private ClockPanel currentPanel;
    private ClockMenuBar menuBar;
    private DigitalClockPanel digitalClockPanel;
    private AnalogueClockPanel analogueClockPanel;
    private AlarmPanel alarmPanel;
    private TimerPanel timerPanel;
    private TimerPanel2 timerPanel2;
//    private StopwatchPanel stopwatchPanel;
    private Clock clock;
    private List<Alarm> listOfAlarms;
    private List<Timer> listOfTimers;
    private ScheduledFuture<?> countdownFuture;
    private ScheduledExecutorService scheduler;

    /**
     * Default constructor for ClockFrame
     * Initializes the clock with default settings
     */
    public ClockFrame() {
        super();
        initialize(null);
    }

    /**
     * Constructor for ClockFrame with a test clock
     * @param testClock the clock to use for testing
     */
    public ClockFrame(Clock testClock) {
        super();
        logger.info("Creating ClockFrame with test clock");
        initialize(testClock);
    }

    private void initialize(Clock testing) {
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
        if (testing != null && testing.isTestingClock()) {
            clock = testing;
            clock.setClockFrame(this);
        } else {
            createClock();
        }
        listOfAlarms = new ArrayList<>();
        listOfTimers = new ArrayList<>();
        scheduler = Executors.newScheduledThreadPool(25);
        setupMenuBar(); // daylightSavingsTimeEnabled directly influences menu bar setup
        if (clock.isTodayDaylightSavingsTime()) { clock.setTodayMatchesDSTDate(true); }
        digitalClockPanel = new DigitalClockPanel(this);
        analogueClockPanel = new AnalogueClockPanel(this);
        alarmPanel = new AlarmPanel(this);
        timerPanel = new TimerPanel(this);
        timerPanel2 = new TimerPanel2(this);
        clock.setLeapYear(clock.getDate().isLeapYear());
        changePanels(PANEL_DIGITAL_CLOCK, false);
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

    private void createClock() {
        logger.info("Creating Clock");
        clock = new Clock(this);
        clock.setTimeZone(clock.getZoneIdFromTimezoneButtonText(EMPTY));
        clock.setDaylightSavingsTimeEnabled(false);
        clock.setShowDigitalTimeOnAnalogueClock(true);
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
            case PANEL_TIMER2 -> changeToTimerPanel2();
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
        clockPanel = DigitalClockPanel.PANEL;
        repaint();
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
        clockPanel = AnalogueClockPanel.PANEL;
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
        clockPanel = PANEL_ALARM;
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
        clockPanel = PANEL_TIMER;
        timerPanel.setupSettingsMenu();
        timerPanel.updateLabels();
    }

    /**
     * Changes the panel to the timer panel
     */
    public void changeToTimerPanel2()
    {
        logger.info("change to timer panel");
        add(timerPanel2);
        currentPanel = timerPanel2;
        setSize(clockDefaultSize);
        clockPanel = PANEL_TIMER2;
        timerPanel2.setupSettingsMenu();
    }

//    public void changeToStopwatchPanel()
//    {
//        logger.debug("change to stopwatch panel");
//        add(stopwatchPanel);
//        currentPanel = stopwatchPanel;
//        setSize(clockDefaultSize);
//        clockPanel = PANEL_STOPWATCH;
//        //timerPanel.setupSettingsMenu();
//        //timerPanel.updateLabels();
//    }

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
            default -> LocalDateTime.now();
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
            }
        };

        scheduler.scheduleAtFixedRate(taskRunner.apply(clock::tick), 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(taskRunner.apply(clock::setActiveAlarms), 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(taskRunner.apply(clock::triggerAlarms), 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(taskRunner.apply(clock::updateTimersTable), 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(taskRunner.apply(clock::triggerTimers), 0, 1, TimeUnit.SECONDS);
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

    }

    @Override
    public void setClock(Clock clock) {

    }

    @Override
    public void setupSettingsMenu() {

    }

    @Override
    public void printStackTrace(Exception e, String message) {

    }

    @Override
    public void run() {

    }

    public Panel getClockPanel() {
        return clockPanel;
    }

    public void setClockPanel(Panel clockPanel) {
        this.clockPanel = clockPanel;
    }

    public ClockPanel getCurrentPanel() {
        return currentPanel;
    }

    public void setCurrentPanel(ClockPanel currentPanel) {
        this.currentPanel = currentPanel;
    }

//    @Override
//    public ClockMenuBar getMenuBar() {
//        return menuBar;
//    }

    public void setMenuBar(ClockMenuBar menuBar) {
        this.menuBar = menuBar;
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

    public TimerPanel getTimerPanel() {
        return timerPanel;
    }

    public ClockMenuBar getClockMenuBar() {
        return menuBar;
    }

    public void setTimerPanel(TimerPanel timerPanel) {
        this.timerPanel = timerPanel;
    }

    public TimerPanel2 getTimerPanel2() {
        return timerPanel2;
    }

    public void setTimerPanel2(TimerPanel2 timerPanel2) {
        this.timerPanel2 = timerPanel2;
    }

//    public StopwatchPanel getStopwatchPanel() {
//        return stopwatchPanel;
//    }

//    public void setStopwatchPanel(StopwatchPanel stopwatchPanel) {
//        this.stopwatchPanel = stopwatchPanel;
//    }

    public Clock getClock() {
        return clock;
    }

    public List<Alarm> getListOfAlarms() {
        return listOfAlarms;
    }

    public void setListOfAlarms(List<Alarm> listOfAlarms) {
        this.listOfAlarms = listOfAlarms;
    }

    public List<Timer> getListOfTimers() {
        return listOfTimers;
    }

    public void setListOfTimers(List<Timer> listOfTimers) {
        this.listOfTimers = listOfTimers;
    }

    public ScheduledFuture<?> getCountdownFuture() {
        return countdownFuture;
    }

    public void setCountdownFuture(ScheduledFuture<?> countdownFuture) {
        this.countdownFuture = countdownFuture;
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }
}


