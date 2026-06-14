package clock.panel;

import clock.entity.Alarm;
import clock.entity.Clock;
import clock.entity.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;

/**
 * Digital Clock Panel
 * <p>
 * Used to view the date and time. You can view the time
 * in regular or military time, show different date views,
 * turn on/off Daylight Savings Time and change the time
 * zone.
 *
 * @author michael ball
 * @version since 1.0
 */
public class DigitalClockPanel extends ClockPanel implements Runnable
{
    private static final Logger logger = LogManager.getLogger(DigitalClockPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private Thread thread = null;
    private Clock clock;
    private ClockFrame clockFrame;

    /**
     * The main constructor for the digital clock panel
     * @param clockFrame the clockFrame object reference
     */
    public DigitalClockPanel(ClockFrame clockFrame)
    {
        super();
        initialize(clockFrame);
        logger.info("Finished creating DigitalClock Panel");
    }

    /**
     * Sets up the default actions for the digital clock panel
     * @param clockFrame the clockFrame reference
     */
    public void initialize(ClockFrame clockFrame)
    {
        logger.debug("setup default actions with clock");
        setClockFrame(clockFrame);
        setClock(clockFrame.getClock());
        setGridBagLayout(new GridBagLayout()); // sets layout
        setLayout(layout);
        setGridBagConstraints(new GridBagConstraints());
        setupDefaultValues();
    }

    /**
     * Sets up the default visual properties and starts the panel thread.
     */
    public void setupDefaultValues()
    {
        setMaximumSize(ClockFrame.clockDefaultSize);
        setupSettingsMenu();
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setForeground(Color.WHITE);
        clockFrame.setTitle(DIGITAL+SPACE+CLOCK);
        start();
    }

    /**
     * This method sets up the settings menu for the
     * digital clock panel.
     */
    public void setupSettingsMenu()
    {
        clockFrame.clearSettingsMenu();
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getMilitaryTimeSetting());
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getFullTimeSetting());
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getPartialTimeSetting());
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getToggleDSTSetting());
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getChangeTimeZoneMenu());
    }

    /**
     * Starts the digital clock panel thread
     * and internally calls the run method.
     */
    public void start()
    {
        logger.debug("starting digital panel");
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    /** Stops the timer panel thread. */
    public void stop()
    {
        logger.debug("stopping digital panel");
        thread = null;
    }

    /**
     * Repaints the digital clock after it has been updated
     */
    @Override
    public void run()
    {
        logger.debug("starting digital clock");
        while (thread != null)
        {
            try
            {
                repaint(); // goes to paint
                sleep(1000);
            }
            catch (InterruptedException e)
            {
                printStackTrace(e, e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Paints the digital clock panel
     * @param g the graphics object
     */
    @Override
    public void paint(Graphics g)
    {
        logger.debug("painting digital clock panel");
        drawStructure(g);
    }

    /**
     * Draws the digital clock
     * @param g the graphics object
     */
    public void drawStructure(Graphics g)
    {
        logger.debug("drawing structure");
        g.setFont(ClockFrame.font60);
        if (clock.isShowFullDate()) g.setFont(ClockFrame.font40);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, ClockFrame.clockDefaultSize.width, ClockFrame.clockDefaultSize.height);

        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics(g.getFont()); // Get FontMetrics for string width calculation

        // Strings to draw
        String dateStr;
        String timeStr;

        java.util.List<Alarm> clockAlarms = clock.getListOfAlarms();
        java.util.List<Timer> clockTimers = clock.getListOfTimers();
        long alarmsGoingOffCount = clockAlarms.stream().filter(Alarm::isAlarmGoingOff).count();
        boolean alarmsGoingOff = alarmsGoingOffCount > 0;
        long timersGoingOffCount = clockTimers.stream().filter(Timer::isTimerGoingOff).count();
        boolean timersGoingOff = timersGoingOffCount > 0;

        // If we have no Alarms or Timers going off
        if (!alarmsGoingOff && !timersGoingOff)
        {
            dateStr = clock.defaultText(1);
            timeStr = clock.defaultText(2);
        }
        // If we have only Alarms going off
        else if (alarmsGoingOff && !timersGoingOff)
        {
            var activeAlarms = clock.getListOfAlarms().stream().filter(Alarm::isAlarmGoingOff).toList();
            // Display Alarm Name or "Many Alarms" if more than one
            dateStr = activeAlarms.size() == 1
                    ? (activeAlarms.getFirst().getName() != null)
                        ? activeAlarms.getFirst().getName()
                        : activeAlarms.toString()
                    : "Many Alarms";
            timeStr = clock.defaultText(9);
        }
        // If we have only Timers going off
        else if (!alarmsGoingOff) //if (timersGoingOff && !alarmsGoingOff)
        {
            var activeTimers = clock.getListOfTimers().stream().filter(Timer::isTimerGoingOff).toList();
            // Display Timer Name or "Many Timers" if more than one
            dateStr = activeTimers.size() == 1
                    ? (activeTimers.getFirst().getName() != null)
                        ? activeTimers.getFirst().getName()
                        : activeTimers.toString()
                    : "Many Timers";
            timeStr = activeTimers.size() == 1 ? is+SPACE+going_off : are+SPACE+going_off;
        }
        // If we have both Alarms and Timers going off
        else
        {
            var activeAlarms = clock.getListOfAlarms().stream().filter(Alarm::isAlarmGoingOff).toList();
            var activeTimers = clock.getListOfTimers().stream().filter(Timer::isTimerGoingOff).toList();

            g.setFont(ClockFrame.font40);
            fm = g.getFontMetrics(g.getFont());
            String alarmVerb = activeAlarms.size() == 1 ? is : are;
            String timerVerb = activeTimers.size() == 1 ? is : are;
            dateStr = activeAlarms.size() + SPACE + ALARM + SPACE + alarmVerb + SPACE + going_off;
            timeStr = activeTimers.size() + SPACE + TIMER + SPACE + timerVerb + SPACE + going_off;
        }
        // Calculate centered x positions
        int dateWidth = fm.stringWidth(dateStr);
        int timeWidth = fm.stringWidth(timeStr);
        int panelWidth = getWidth();

        int dateX = (panelWidth - dateWidth) / 2;
        int timeX = (panelWidth - timeWidth) / 2;

        int baseY = ClockFrame.clockDefaultSize.height / 2;

        g.drawString(dateStr, dateX, baseY - 30);
        g.drawString(timeStr, timeX, baseY + 30);
        g.setColor(Color.BLACK);
    }

    /**
     * Returns the clock frame
     * @return the clock frame reference
     */
    public ClockFrame getClockFrame() { return this.clockFrame; }
    /**
     * Returns the layout manager
     * @return the GridBagLayout used by this panel
     */
    public GridBagLayout getGridBagLayout() { return this.layout; }
    /**
     * Returns the grid bag constraints
     * @return the GridBagConstraints used by this panel
     */
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    /**
     * Returns the clock
     * @return the clock reference
     */
    public Clock getClock() { return this.clock; }

    /**
     * Sets the clock frame
     * @param clockFrame the clock frame reference
     */
    private void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; logger.debug("clockFrame set"); }
    /**
     * Sets the layout manager
     * @param layout the GridBagLayout to use
     */
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    /**
     * Sets the grid bag constraints
     * @param constraints the GridBagConstraints to use
     */
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    /**
     * Sets the clock
     * @param clock the clock reference
     */
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set in DigitalClockPanel"); }
}