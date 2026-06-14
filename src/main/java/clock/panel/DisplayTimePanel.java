package clock.panel;

import clock.entity.Clock;
import clock.entity.Stopwatch;
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
 * This is the left panel of the {@link StopwatchPanel}. See {@link DisplayLapsPanel}
 * for the right side.
 *
 * @author michael ball
 * @version since 2.9
 */
public class DisplayTimePanel extends JPanel implements Runnable
{
    /** The logger */
    private static final Logger logger = LogManager.getLogger(DisplayTimePanel.class);
    /** The panel thread */
    public Thread thread;
    /** Whether the analogue clock view is active (false = digital). */
    private boolean showAnaloguePanel = false;
    /** The current stopwatch time text displayed on the panel. */
    public String clockText = "00:00.000";
    /** The default/reset stopwatch display text. */
    public static String startText = "00:00.000"; // default text
    /** Reference to the stopwatch panel */
    private final StopwatchPanel stopwatchPanel;

    /**
     * Constructs a DisplayTimePanel attached to the given stopwatch panel.
     * @param stopwatchPanel the parent StopwatchPanel
     */
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
        logger.debug("stopping digital time stopwatch panel");
        thread = null;
        setClockText(startText);
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
                Stopwatch currentStopwatch = stopwatchPanel.getCurrentStopwatch();
                if (currentStopwatch != null &&
                        (!currentStopwatch.isStarted() && currentStopwatch.getName() != null))
                {
                    Clock clock = currentStopwatch.getClock();
                    currentStopwatch.startStopwatch(clock.getScheduledExecutorService());
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

    /**
     * Draws the digital stopwatch time and label strings onto the panel.
     * @param g the Graphics context to draw on
     */
    public void drawDigitalClock(Graphics g)
    {
        //logger.debug("drawing display time panel");
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
        g.drawString(ZERO+FIVE, xcenter + (int)(radius * 0.3),  ycenter - (int)(radius * 0.7));
        g.drawString(TEN+EMPTY, xcenter + (int)(radius * 0.6), ycenter - (int)(radius * 0.4));
        g.drawString(ONE+FIVE,  xcenter + (int)(radius * 0.8),          ycenter);
        g.drawString(TWO+ZERO,  xcenter + (int)(radius * 0.6), ycenter + (int)(radius * 0.4));
        g.drawString(TWO+FIVE,  xcenter + (int)(radius * 0.3),  ycenter + (int)(radius * 0.7));
        g.drawString(THREE+ZERO,xcenter - 10,                   ycenter + radius - 5);
        g.drawString(THREE+FIVE,xcenter - (int)(radius * 0.5),  ycenter + (int)(radius * 0.7));
        g.drawString(FOUR+ZERO, xcenter - (int)(radius * 0.8),  ycenter + (int)(radius * 0.4));
        g.drawString(FOUR+FIVE, xcenter - radius + 5,           ycenter);
        g.drawString(FIVE+ZERO, xcenter - (int)(radius * 0.8),  ycenter - (int)(radius * 0.4));
        g.drawString(FIVE+FIVE, xcenter - (int)(radius * 0.5),  ycenter - (int)(radius * 0.7));
        g.drawString(SIX+ZERO,  xcenter - 10,                   ycenter - radius + 20);

        g.setColor(Color.BLACK);

        // Derive time directly from the stopwatch using the colon-delimited parse format so
        // the split always yields 3 parts regardless of what clockText currently holds.
        // Using clockText here caused a race with the repaint thread, which continuously
        // overwrites clockText with the dot-delimited reading format.
        final Stopwatch currentStopwatch = stopwatchPanel.getCurrentStopwatch();
        String time = currentStopwatch == null ? "00:00:000" :
                currentStopwatch.elapsedFormatted(currentStopwatch.getAccumMilli(), STOPWATCH_PARSE_FORMAT);
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

    /**
     * Returns isShowAnaloguePanel
     * @return true if the analogue panel is displayed
     */
    public boolean isShowAnaloguePanel() { return showAnaloguePanel; }
    /**
     * Returns the clock text
     * @return the current clock display text
     */
    public String getClockText() { return clockText; }
    /**
     * Returns if the thread is running
     * @return true if the display thread is running
     */
    public boolean isRunning() { return thread != null; }

    /**
     * Sets the showAnaloguePanel flag
     * @param showAnaloguePanel true to display analogue clock view
     */
    public void setShowAnaloguePanel(boolean showAnaloguePanel) { this.showAnaloguePanel = showAnaloguePanel; logger.debug("showAnaloguePanel set to {}", showAnaloguePanel); }
    /**
     * Sets the clock text
     * @param clockText the time string to display
     */
    public void setClockText(String clockText) { this.clockText = clockText; /* logger.debug("clockText set to {}", clockText); */ }
}
