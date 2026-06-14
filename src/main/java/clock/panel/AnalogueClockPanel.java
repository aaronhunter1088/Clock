package clock.panel;

import java.awt.*;

import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;

/**
 * Analogue Clock Panel
 * <p>
 * Used to view the time in analogue mode.
 * The time will still show up below the center in
 * its digital format. If you wish to hide this,
 * there is a setting to hide the digital view.
 *
 * @author michael ball
 * @version since 2.6
 */
public class AnalogueClockPanel extends ClockPanel implements Runnable
{
    private static final Logger logger = LogManager.getLogger(AnalogueClockPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private Thread thread = null;
    private int xcenter = 175, ycenter = 175, lastxs = 0, lastys = 0, lastxm = 0, lastym = 0, lastxh = 0, lastyh = 0;
    private ClockFrame clockFrame;
    private Clock clock;
    private String dateText = EMPTY,
                   timeText = EMPTY;
    private boolean showDigitalTimeOnAnalogueClock;

    /**
     * Default constructor
     * @param clockFrame the clockFrame reference
     */
    public AnalogueClockPanel(ClockFrame clockFrame)
    {
        super();
        initialize(clockFrame);
        logger.info("Finished creating AnalogueClock Panel");
    }

    /**
     * Sets up the default actions for the analogue clock panel
     * @param clockFrame the clockFrame reference
     */
    public void initialize(ClockFrame clockFrame)
    {
        logger.debug("initialize analogue clock panel");
        setClockFrame(clockFrame);
        setClock(clockFrame.getClock());
        setTimeText(clock.getTimeAsStr());
        setGridBagLayout(new GridBagLayout()); // sets layout
        setLayout(layout);
        setGridBagConstraints(new GridBagConstraints());
        setupDefaultValues();
    }

    /**
     * Sets up the checkboxes for the Analogue Clock Panel
     */
    public void setupSettingsMenu()
    {
        clockFrame.clearSettingsMenu();
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getShowDigitalTimeOnAnalogueClockSetting());
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getToggleDSTSetting());
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getChangeTimeZoneMenu());
        setShowDigitalTimeOnAnalogueClock(true);
    }

    /**
     * Sets up the default values for the analogue clock panel
     */
    public void setupDefaultValues()
    {
        logger.debug("set default values");
        setupSettingsMenu();
        setMaximumSize(ClockFrame.analogueSize);
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        clockFrame.setTitle(ANALOGUE+SPACE+CLOCK);
        start();
    }

    /**
     * Starts the analogue clock panel thread
     * and internally calls the run method.
     */
    public void start()
    {
        logger.debug("starting analogue clock panel");
        if (thread == null)
        {
            setThread(new Thread(this));
            thread.start();
        }
    }

    /** Stops the timer panel thread. */
    public void stop()
    {
        logger.debug("stopping analogue clock panel");
        setThread(null);
    }

    /**
     * Repaints the analogue clock after it has been updated
     */
    public void run()
    {
        logger.debug("starting analogue clock");
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
     * Paints the analogue clock panel
     * @param g the graphics object
     */
    @Override
    public void paint(Graphics g)
    {
        logger.info("painting analogue clock panel");
        int xhour, yhour, xminute, yminute, xsecond, ysecond, second, minute, hour;

        if (isShowDigitalTimeOnAnalogueClock())
        {
            setDateText(clock.getDateAsStr());
            setTimeText(clock.getTimeAsStr());
        }
        drawStructure(g);
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

    /**
     * Draws the analogue clock
     * @param g the graphics object
     */
    public void drawStructure(Graphics g)
    {
        logger.info("drawing structure");
        g.setFont(ClockFrame.analogueFont);
        g.setColor(Color.BLACK);
        g.fillOval(xcenter - 150, ycenter - 150, 300, 300);

        if (isShowDigitalTimeOnAnalogueClock())
        {
            g.setColor(Color.WHITE);
            final FontMetrics fm = g.getFontMetrics();
            g.drawString(dateText, xcenter - fm.stringWidth(dateText) / 2, ycenter + 70);
            g.drawString(timeText, xcenter - fm.stringWidth(timeText) / 2, ycenter + 90);
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
    }

    /**
     * Returns the clock frame
     * @return the clock frame.
     */
    public ClockFrame getClockFrame() { return this.clockFrame; }
    /**
     * Returns the clock
     * @return the clock.
     */
    public Clock getClock() { return this.clock; }
    /**
     * Returns the clock date text
     * @return the date text
     */
    public String getDateText() { return this.dateText; }
    /**
     * Returns the clock time text
     * @return the time text
     */
    public String getTimeText() { return this.timeText; }
    /**
     * Returns isShowDigitalTimeOnAnalogueClock
     * @return show digital time on analogue clock.
     */
    public boolean isShowDigitalTimeOnAnalogueClock() { return showDigitalTimeOnAnalogueClock; }
    /**
     * Returns the self thread
     * @return the self thread.
     */
    public Thread getThread() { return this.thread; }

    /**
     * Sets the clock frame
     * @param clockFrame the clock frame reference
     */
    private void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; logger.debug("clockFrame set"); }
    /**
     * Sets the layout manager
     * @param layout the GridBagLayout to use
     */
    private void setGridBagLayout(GridBagLayout layout) { this.layout = layout; logger.debug("GridBagLayout set"); }
    /**
     * Sets the grid bag constraints
     * @param constraints the GridBagConstraints to use
     */
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("GridBagConstraints set"); }
    /**
     * Sets the clock date text
     * @param dateText the date string to display
     */
    private void setDateText(String dateText) { this.dateText = dateText; logger.debug("dateTime set"); }
    /**
     * Sets the clock time text
     * @param timeText the time string to display
     */
    private void setTimeText(String timeText) { this.timeText = timeText; logger.debug("clockTime set"); }
    /**
     * Sets the clock
     * @param clock the clock reference
     */
    public void setClock(Clock clock) { this.clock = clock; logger.debug("clock set"); }
    /**
     * Sets isShowDigitalTimeOnAnalogueClock
     * @param showDigitalTimeOnAnalogueClock true to show digital time overlay
     */
    public void setShowDigitalTimeOnAnalogueClock(boolean showDigitalTimeOnAnalogueClock)  { this.showDigitalTimeOnAnalogueClock = showDigitalTimeOnAnalogueClock; logger.debug("showDigitalTimeOnAnalogueClock set to " + showDigitalTimeOnAnalogueClock); }
    /**
     * Sets the self thread
     * @param thread the thread to assign
     */
    private void setThread(Thread thread) { this.thread = thread; logger.debug("thread set");  }
}