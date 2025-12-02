package clock.panel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;

/**
 * DisplayTimePanel
 * <p>
 * A DisplayTimePanel is a JPanel that displays the time of a Stopwatch
 * in either digital or analogue format. It implements Runnable to allow
 * for continuous updating of the display while the stopwatch is running.
 *
 * @author michael ball
 * @version since 2.9
 */
public class DisplayTimePanel extends JPanel implements Runnable
{
    private static final Logger logger = LogManager.getLogger(DisplayTimePanel.class);
    public Thread thread;
    private boolean showAnaloguePanel = false;
    public String clockText = "00:00.000";
    public static String startText = "00:00.000"; // default text
    private StopwatchPanel stopwatchPanel;

    public DisplayTimePanel(StopwatchPanel stopwatchPanel)
    {
        super();
        this.stopwatchPanel = stopwatchPanel;
        setPreferredSize(ClockFrame.analogueSize);
        setMinimumSize(ClockFrame.analogueSize);
        setMaximumSize(ClockFrame.analogueSize);
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        repaint();
    }

    /**
     * Starts the digital stopwatch panel thread
     * and internally calls the run method.
     */
    public void start()
    {
        logger.debug("starting digital stopwatch panel");
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    /** Stops the digital stopwatch panel thread. */
    public void stop()
    {
        logger.debug("stopping digital stopwatch panel");
        // TODO: Is this right? Is there a better way to do this?
        thread = null;
        setClockText(DisplayTimePanel.startText);
        if (stopwatchPanel.getCurrentStopwatch() != null)
        {
            stopwatchPanel.getCurrentStopwatch().pauseStopwatch();
        }
    }

    /** Resumes the stopwatch panel thread */
    public void resume()
    {
        logger.debug("resuming display stopwatch panel");
        start();
    }

    /** Repaints the stopwatch panel */
    @Override
    public void run()
    {
        while (thread != null)
        {
            try
            {
                if (!stopwatchPanel.getCurrentStopwatch().isStarted())
                {
                    stopwatchPanel.getCurrentStopwatch().startStopwatch();
                }
                revalidate();
                repaint(); // goes to paint
                sleep(1);
            }
            catch (Exception e)
            {
                logger.error("Exception in DisplayTimePanel run: {}", e.getMessage());
            }
        }
    }

    /** Paints the appropriate clock for the stopwatch panel */
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        if (showAnaloguePanel)
        {
            setClockText(stopwatchPanel.getCurrentStopwatch() == null ? clockText : stopwatchPanel.getCurrentStopwatch().elapsedFormatted(stopwatchPanel.getCurrentStopwatch().getAccumMilli(), STOPWATCH_PARSE_FORMAT));
            drawAnalogueClock(g);
        }
        else
        {
            setClockText(stopwatchPanel.getCurrentStopwatch() == null ? clockText : stopwatchPanel.getCurrentStopwatch().elapsedFormatted(stopwatchPanel.getCurrentStopwatch().getAccumMilli(), STOPWATCH_READING_FORMAT));
            drawDigitalClock(g);
        }
    }

    public void drawDigitalClock(Graphics g)
    {
        logger.debug("drawing display time panel");
        g.setFont(ClockFrame.font20);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 350, 400);

        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics(g.getFont()); // Get FontMetrics for string width calculation

        // Strings to draw
        String dateStr;
        String timeStr;

        dateStr = stopwatchPanel.getCurrentStopwatch() == null ? startText : clockText;
        timeStr = ""; //clock.defaultText(2); // stopwatch status
        // Calculate centered x positions
        int dateWidth = fm.stringWidth(dateStr);
        int timeWidth = fm.stringWidth(timeStr);
        int panelWidth = this.getWidth();

        int dateX = (panelWidth - dateWidth) / 2;

        int timeX = (panelWidth - timeWidth) / 2;

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
        logger.debug("painting analogue clock panel");
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
        g.drawString(clockText, xcenter - g.getFontMetrics().stringWidth(clockText) / 2, ycenter + (int)(radius * 0.5));  // adjust radius number as needed

        // Draw numbers (adjust positions for new radius)
        g.drawString(ZERO+FIVE,    xcenter + (int)(radius * 0.3),  ycenter - (int)(radius * 0.7));
        g.drawString(TEN,    xcenter + (int)(radius * 0.6), ycenter - (int)(radius * 0.4));
        g.drawString(ONE+FIVE,  xcenter + (int)(radius * 0.8),          ycenter);
        g.drawString(TWO+ZERO,   xcenter + (int)(radius * 0.6), ycenter + (int)(radius * 0.4));
        g.drawString(TWO+FIVE,   xcenter + (int)(radius * 0.3),  ycenter + (int)(radius * 0.7));
        g.drawString(THREE+ZERO,    xcenter - 10,                   ycenter + radius - 5);
        g.drawString(THREE+FIVE,  xcenter - (int)(radius * 0.5),  ycenter + (int)(radius * 0.7));
        g.drawString(FOUR+ZERO,  xcenter - (int)(radius * 0.8),  ycenter + (int)(radius * 0.4));
        g.drawString(FOUR+FIVE,   xcenter - radius + 5,           ycenter);
        g.drawString(FIVE+ZERO,    xcenter - (int)(radius * 0.8),  ycenter - (int)(radius * 0.4));
        g.drawString(FIVE+FIVE, xcenter - (int)(radius * 0.5),  ycenter - (int)(radius * 0.7));
        g.drawString(SIX+ZERO, xcenter - 10,                   ycenter - radius + 20);

        g.setColor(Color.BLACK);

        // Derive milliseconds from seconds (assuming getStopwatch().getSeconds() returns total seconds with millisecond precision)
        String time = stopwatchPanel.getCurrentStopwatch() == null ? startText : clockText;
        String[] parts = time.split(COLON);

        double milliseconds = Double.parseDouble(parts[2]);
        long minutes = Long.parseLong(parts[0]);
        int seconds = Integer.parseInt(parts[1]);

        // Millisecond hand (1 rotation = 1000 ms)
        //double millisecondAngle = (milliseconds) * 2 * Math.PI - Math.PI / 2;
        //int xmillisecond = (int) (Math.cos(millisecondAngle) * (radius * 0.8) + xcenter);
        //int ymillisecond = (int) (Math.sin(millisecondAngle) * (radius * 0.8) + ycenter);

        // Second hand (1 rotation = 60 s, includes ms for smoothness)
        double secondAngle = ((seconds + milliseconds / 1000) / 60) * 2 * Math.PI - Math.PI / 2;
        int xsecond = (int) (Math.cos(secondAngle) * (radius * 0.65) + xcenter);
        int ysecond = (int) (Math.sin(secondAngle) * (radius * 0.65) + ycenter);

        // Minute hand (1 rotation = 60 min, includes seconds for smoothness)
        double minuteAngle = ((minutes + (seconds + milliseconds / 1000) / 60) / 60) * 2 * Math.PI - Math.PI / 2;
        int xminute = (int) (Math.cos(minuteAngle) * (radius * 0.45) + xcenter);
        int yminute = (int) (Math.sin(minuteAngle) * (radius * 0.45) + ycenter);

        // Draw hands
        //g.setColor(Color.RED);
        //g.drawLine(xcenter, ycenter, xmillisecond, ymillisecond);
        g.setColor(Color.BLUE);
        g.drawLine(xcenter, ycenter, xsecond, ysecond);
        g.setColor(Color.GREEN);
        g.drawLine(xcenter, ycenter, xminute, yminute);
    }

    /** Returns isShowAnaloguePanel */
    public boolean isShowAnaloguePanel() { return showAnaloguePanel; }
    /** Returns the clock text */
    public String getClockText() { return clockText; }
    /** Returns if the thread is running */
    public boolean isRunning() { return thread != null; }

    /** Sets the showAnaloguePanel flag */
    public void setShowAnaloguePanel(boolean showAnaloguePanel) { this.showAnaloguePanel = showAnaloguePanel; logger.debug("showAnaloguePanel set to {}", showAnaloguePanel); }
    /** Sets the clock text */
    public void setClockText(String clockText) { this.clockText = clockText; logger.debug("clockText set to {}", clockText); }
}

