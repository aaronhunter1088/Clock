package clock.panel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

import static clock.util.Constants.*;
import static clock.util.Constants.TWELVE;
import static java.lang.Thread.sleep;

public class DisplayTimePanel extends JPanel implements Runnable {

    private static final Logger logger = LogManager.getLogger(DisplayTimePanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    // TODO: Make private, add get/set methods
    public Thread thread;
    private boolean showAnaloguePanel = false;

    public DisplayTimePanel()
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
    public void paint(Graphics g)
    {
        super.paint(g);
        if (showAnaloguePanel)
        {
            drawAnalogueClock(g);
        }
        else
        {
            drawDigitalClock(g);
        }
    }

    public void drawDigitalClock(Graphics g)
    {
        logger.info("drawing time display panel");
        g.setFont(ClockFrame.font20);
        //if (clock.isShowFullDate()) g.setFont(ClockFrame.font40);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 350, 400);

        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics(g.getFont()); // Get FontMetrics for string width calculation

        // Strings to draw
        String dateStr;
        String timeStr;

        // TODO: Fix
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
    public void setShowAnaloguePanel(boolean showAnaloguePanel) { this.showAnaloguePanel = showAnaloguePanel; logger.debug("showAnaloguePanel set to {}", showAnaloguePanel); }

    public boolean isShowAnaloguePanel() { return showAnaloguePanel; }
}

