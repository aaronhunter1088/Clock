package clock.panel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

import static clock.util.Constants.*;
import static clock.util.Constants.TWELVE;
import static java.lang.Thread.sleep;

class DisplayLapsPanel extends JPanel implements Runnable {

    private static final Logger logger = LogManager.getLogger(DisplayLapsPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    public Thread thread;
    private int xcenter = 175, ycenter = 175, lastxs = 0, lastys = 0, lastxm = 0, lastym = 0, lastxh = 0, lastyh = 0;

    public DisplayLapsPanel()
    {
        super();
        setPreferredSize(ClockFrame.analogueSize);
        setMinimumSize(ClockFrame.analogueSize);
        setMaximumSize(ClockFrame.analogueSize);
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
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
        super.paint(g);
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

