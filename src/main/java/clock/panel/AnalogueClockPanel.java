package clock.panel;

import java.awt.*;

import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;
import static clock.panel.Panel.PANEL_ANALOGUE_CLOCK;

/**
 * The AnalogueClockPanel is used to view the time
 * in analogue mode. The time will still show up
 * below the center in digital format. If you wish
 * to hide this, the settings allows for that.
 *
 * @author michael ball
 * @version 2.6
 */
public class AnalogueClockPanel extends ClockPanel implements Runnable
{
    private static final Logger logger = LogManager.getLogger(AnalogueClockPanel.class);
    public static final Panel PANEL = PANEL_ANALOGUE_CLOCK;
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private Thread thread = null;
    private int xcenter = 175, ycenter = 175, lastxs = 0, lastys = 0, lastxm = 0, lastym = 0, lastxh = 0, lastyh = 0;
    private ClockFrame clockFrame;
    private Clock clock;
    private String clockText = EMPTY;
    private boolean showDigitalTimeOnAnalogueClock;

    /**
     * Default constructor
     * @param clockFrame the clockFrame reference
     */
    public AnalogueClockPanel(ClockFrame clockFrame)
    {
        super();
        setupDefaultActions(clockFrame);
        logger.info("Finished creating AnalogueClock Panel");
    }

    /**
     * Sets up the default actions for the analogue clock panel
     * @param clockFrame the clockFrame reference
     */
    public void setupDefaultActions(ClockFrame clockFrame)
    {
        logger.debug("setup default actions with clock");
        setClockFrame(clockFrame);
        setClock(clockFrame.getClock());
        setClockText(clock.getTimeAsStr());
        setupSettingsMenu();
        setMaximumSize(new Dimension(350, 400));
        setGridBagLayout(new GridBagLayout()); // sets layout
        setLayout(layout);
        setGridBagConstraints(new GridBagConstraints());
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        start(this);
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
        clockFrame.getClockMenuBar().getShowDigitalTimeOnAnalogueClockSetting().setText(HIDE+SPACE+DIGITAL_TIME);
        setShowDigitalTimeOnAnalogueClock(true);
    }

    /**
     * Starts the analogue clock
     * @param panel the analogue clock panel
     */
    public void start(AnalogueClockPanel panel)
    {
        logger.info("starting analogue clock panel");
        if (thread == null)
        {
            thread = new Thread(panel);
            thread.start();
        }
    }

    /**
     * Stops the analogue clock
     */
    public void stop()
    {
        logger.info("stopping analogue clock panel");
        thread = null;
    }

    /**
     * Repaints the analogue clock after it has been updated
     */
    public void run()
    {
        logger.info("starting analogue clock");
        while (thread != null)
        {
            try { sleep(1000); }
            catch (InterruptedException e) { printStackTrace(e, e.getMessage());}
            repaint();
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
        { setClockText(clock.getTimeAsStr()); }
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
     * Updates the analogue clock
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
        g.setFont(ClockFrame.analogueFont);
        g.setColor(Color.BLACK);
        g.fillOval(xcenter - 150, ycenter - 150, 300, 300);

        if (isShowDigitalTimeOnAnalogueClock())
        {
            g.setColor(Color.WHITE);
            g.drawString(clockText, xcenter - 50, ycenter + 90); // 170
        }

        g.setColor(Color.WHITE);
        g.drawString(ONE, xcenter + 60, ycenter - 110);
        g.drawString(TWO, xcenter + 110, ycenter - 60);
        g.drawString(THREE, xcenter + 135, ycenter);
        g.drawString(FOUR, xcenter + 110, ycenter + 60);
        g.drawString(FIVE, xcenter + 60, ycenter + 110);
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
     * This method adds the components to the analogue clock panel
     * Currently no-operation set.
     */
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
        if (null != message)
            logger.error(message);
        else
            logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace()) {
            logger.error(ste.toString());
        }
    }

    /* Getters */
    public ClockFrame getClockFrame() { return this.clockFrame; }
    public Clock getClock() { return this.clock; }
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public String getClockText() { return this.clockText; }
    public boolean isShowDigitalTimeOnAnalogueClock() { return showDigitalTimeOnAnalogueClock; }

    /* Setters */
    private void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; logger.debug("clockFrame set"); }
    private void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    private void setClockText(String clockText) { this.clockText = clockText; }
    public void setClock(Clock clock) { this.clock = clock; logger.debug("Clock set in AnalogueClockPanel"); }
    public void setShowDigitalTimeOnAnalogueClock(boolean showDigitalTimeOnAnalogueClock)  { this.showDigitalTimeOnAnalogueClock = showDigitalTimeOnAnalogueClock; }
}