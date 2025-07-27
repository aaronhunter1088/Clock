package clock.panel;

import clock.contract.IClockPanel;
import clock.entity.Alarm;
import clock.entity.Clock;
import clock.entity.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.util.concurrent.TimeUnit;

import static clock.panel.ClockPanel.PANEL_DIGITAL_CLOCK;
import static clock.util.Constants.*;
import static java.lang.Thread.sleep;

/**
 * The DigitalClockPanel is the main panel and is
 * visible first to the user. Here you can
 * see the time and date.
 * Clicking on the menu options under
 * Settings can change how the time and date
 * look.
 *
 * @author michael ball
*  @version 1.0
 */
public class DigitalClockPanel extends JPanel implements IClockPanel, Runnable
{
    private static final Logger logger = LogManager.getLogger(DigitalClockPanel.class);
    public static final ClockPanel PANEL = PANEL_DIGITAL_CLOCK;
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private Thread thread = null;
    private int xcenter = Clock.defaultSize.height/2;
    private Clock clock;
    private String row1 = EMPTY, row2 = EMPTY;

    /**
     * The main constructor for the digital clock panel
     * @param clock the clock object reference
     */
    public DigitalClockPanel(Clock clock)
    {
        super();
        setupDefaultActions(clock);
        logger.info("Finished creating DigitalClock Panel");
    }

    /**
     * Sets up the default actions for the digital clock panel
     * @param clock the clock reference
     */
    public void setupDefaultActions(Clock clock)
    {
        logger.debug("setup default actions with clock");
        this.clock = clock;
        row1 = clock.defaultText(1);
        row2 = clock.defaultText(2);
        setupSettingsMenu();
        setMaximumSize(Clock.defaultSize);
        setGridBagLayout(new GridBagLayout()); // sets layout
        setLayout(layout);
        setGridBagConstraints(new GridBagConstraints());
        //constraints.fill = GridBagConstraints.HORIZONTAL;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setForeground(Color.BLACK);
        start(this);
    }

    /**
     * This method sets up the settings menu for the
     * digital clock panel.
     */
    public void setupSettingsMenu()
    {
        clock.clearSettingsMenu();
        clock.getClockMenuBar().getSettingsMenu().add(clock.getClockMenuBar().getMilitaryTimeSetting());
        clock.getClockMenuBar().getSettingsMenu().add(clock.getClockMenuBar().getFullTimeSetting());
        clock.getClockMenuBar().getSettingsMenu().add(clock.getClockMenuBar().getPartialTimeSetting());
        clock.getClockMenuBar().getSettingsMenu().add(clock.getClockMenuBar().getToggleDSTSetting());
        clock.getClockMenuBar().getSettingsMenu().add(clock.getClockMenuBar().getChangeTimeZoneMenu());
    }

    /**
     * Starts the analogue clock
     * @param panel the analogue clock panel
     */
    public void start(DigitalClockPanel panel)
    {
        logger.info("starting digital clock");
        if (thread == null)
        {
            thread = new Thread(panel);
            thread.start();
        }
    }

    /**
     * Stops the digital clock
     */
    public void stop()
    {
        logger.info("stopping digital thread");
        thread = null;
    }

    /**
     * Repaints the digital clock after it has been updated
     */
    public void run()
    {
        logger.info("starting digital clock");
        while (thread != null)
        {
            try {
                sleep(1000);
            }
            catch (InterruptedException e) { printStackTrace(e, e.getMessage());}
            repaint(); // goes to paint
        }
    }

    /**
     * Paints the analogue clock panel
     * @param g the graphics object
     */
    @Override
    public void paint(Graphics g)
    {
        logger.info("painting analogue clock panel");
        drawStructure(g);
    }

    /**
     * Updates the digital clock
     * @param g the graphics object
     */
    @Override
    public void update(Graphics g)
    {
        logger.info("updating graphics");
        paint(g);
    }

    /**
     * Draws the analogue clock
     * @param g the graphics object
     */
    public void drawStructure(Graphics g)
    {
        logger.info("drawing structure");
        g.setFont(Clock.font60);
        if (clock.isShowFullDate()) g.setFont(Clock.font40);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Clock.defaultSize.width, Clock.defaultSize.height);

        g.setColor(Color.WHITE);
        // Get FontMetrics for string width calculation
        FontMetrics fm = g.getFontMetrics(g.getFont());

        // Strings to draw
        String dateStr = clock.defaultText(1);
        String timeStr = clock.defaultText(2);
        // Adjust as needed
        if (clock.getListOfAlarms().stream().anyMatch(Alarm::isAlarmGoingOff))
        {
            var activeAlarms = clock.getListOfAlarms().stream().filter(Alarm::isAlarmGoingOff).toList();
            dateStr = activeAlarms.size() == 1
                    ? (activeAlarms.getFirst().getName() != null)
                        ? activeAlarms.getFirst().getName()
                        : "One Alarm"
                    : "Many Alarms";
            timeStr = clock.defaultText(9);
        }
        // Show which timer is going off
        else if (clock.getCurrentPanel() instanceof TimerPanel2 timerPanel)
        {
            var activeTimers = timerPanel.getActiveTimers().stream().filter(Timer::isTimerGoingOff).toList();
            dateStr = activeTimers.size() == 1 ? "One Timer" : "Many Timers";
            timeStr = activeTimers.size() == 1 ? is+SPACE+going_off : are+SPACE+going_off;
        }

        // Calculate centered x positions
        int dateWidth = fm.stringWidth(dateStr);
        int timeWidth = fm.stringWidth(timeStr);
        int panelWidth = getWidth();

        int dateX = (panelWidth - dateWidth) / 2;
        int timeX = (panelWidth - timeWidth) / 2;

        int baseY = Clock.defaultSize.height / 2;

        g.drawString(dateStr, dateX, baseY - 30);
        g.drawString(timeStr, timeX, baseY + 30);
        g.setColor(Color.BLACK);
    }

    /**
     * This method adds the components to the digital clock panel
     */
    @Override
    public void addComponentsToPanel()
    { /* no operation */ }

    /**
     * This method prints the stack trace of an exception
     * that may occur when the digital panel is in use.
     * @param e the exception
     * @param message the message to print
     */
    public void printStackTrace(Exception e, String message)
    {
        if (null != message) logger.error(message);
        else logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        { logger.error(ste.toString()); }
    }

    /**
     * This method prints the stack trace of an exception
     * that may occur when the digital panel is in use.
     * @param e the exception
     */
    void printStackTrace(Exception e)
    { printStackTrace(e, EMPTY); }

    private void updateAlarms()
    {
        clock.getListOfAlarms().forEach((alarm) -> {
            for(DayOfWeek day : alarm.getDays()) {
                if (alarm.getAlarmAsString().equals(clock.getClockTimeAsAlarmString())
                        &&
                        clock.getSeconds() == 0
                        &&
                        day == clock.getDayOfWeek()) {
                    // time for alarm to be triggered
                    alarm.setIsAlarmGoingOff(true);
                    //alarm.triggerAlarm(getScheduler());
                    alarm.setIsAlarmGoingOff(true);
                    //setActiveAlarm(alarm);
                    //setAlarmIsGoingOff(true);
                    logger.info("Alarm " + alarm + " matches clock's time. ");
                    logger.info("Sounding alarm...");
                }
        /* TODO: Check if this is still necessary
           Above should match alarm.toString() to clock.getClockTimeAsAlarmString()
           So if alarm time is same as clock's time (as shown as alarm string)
         */
                else if (clock.isShowMilitaryTime()) { // if in military time, change clocks hours back temporarily
                    if (clock.getHours() > 12) {
                        int tempHour = clock.getHours()-12;
                        String tempHourAsStr = (tempHour < 10) ? "0"+tempHour : String.valueOf(tempHour);
                        if (alarm.toString().equals(tempHourAsStr+":"+clock.getMinutesAsStr()+" "+clock.getAMPM())
                                &&
                                day == clock.getDayOfWeek()) {
                            // time for alarm to be triggered on
                            //setActiveAlarm(alarm);
                            alarm.setIsAlarmGoingOff(true);
                            //setAlarmIsGoingOff(true);
                            logger.info("Alarm " + alarm + " matches clock's time. ");
                            logger.info("Sounding alarm...");
                        }
                    }
                    else {
                        if (alarm.toString().equals(clock.getHoursAsStr()+":"+clock.getMinutesAsStr()+" "+clock.getAMPM())
                                &&
                                day == clock.getDayOfWeek()) {
                            // time for alarm to be triggered on
                            //setActiveAlarm(alarm);
                            alarm.setIsAlarmGoingOff(true);
                            //setAlarmIsGoingOff(true);
                            logger.info("Alarm " + alarm + " matches clock's time. ");
                            logger.info("Sounding alarm...");
                        }
                    }
                }
            }
            // update lbl1 and lbl2 to display alarm
            // user must "view that alarm" to turn it off
        });
    }

    /**
     * The main method used for adding components
     * to a panel
     * @param cpt       the component to add
     * @param gridy     the y position
     * @param gridx     the x position
     * @param gwidth    the width
     * @param gheight   the height
     * @param ipadx     the x padding
     * @param ipady     the y padding
     */
    void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady)
    {
        logger.info("addComponent");
        getGridBagConstraints().gridx = gridx;
        getGridBagConstraints().gridy = gridy;
        getGridBagConstraints().gridwidth = (int)Math.ceil(gwidth);
        getGridBagConstraints().gridheight = (int)Math.ceil(gheight);
        getGridBagConstraints().ipadx = ipadx;
        getGridBagConstraints().ipady = ipady;
        getGridBagConstraints().fill = GridBagConstraints.NONE;
        getGridBagConstraints().insets = new Insets(0,0,0,0);
        getGridBagLayout().setConstraints(cpt, getGridBagConstraints());
        add(cpt);
    }

    /* Getters */
    GridBagLayout getGridBagLayout() { return this.layout; }
    GridBagConstraints getGridBagConstraints() { return this.constraints; }
    Clock getClock() { return this.clock; }

    /* Setters */
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setClock(Clock clock) { this.clock = clock; }
}