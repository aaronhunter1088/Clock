package clock.panel;

import clock.entity.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

import static java.lang.Thread.sleep;

class DisplayLapsPanel extends JPanel implements Runnable {

    private static final Logger logger = LogManager.getLogger(DisplayLapsPanel.class);
    public Thread thread;
    private Stopwatch stopwatch;

    public DisplayLapsPanel()
    {
        super();
        setPreferredSize(ClockFrame.analogueSize);
        setMinimumSize(ClockFrame.analogueSize);
        setMaximumSize(ClockFrame.analogueSize);
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
    }

    /**
     * Starts the display laps panel thread
     * and internally calls the run method.
     */
    public void start()
    {
        logger.debug("starting display laps panel");
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    /** Stops the display laps panel thread. */
    public void stop()
    {
        logger.debug("stopping laps display panel");
        thread = null;
    }

    /** Repaints the display laps panel */
    @Override
    public void run()
    {
        while (thread != null)
        {
            try
            {
                //repaint(); // goes to paint
                // TODO: Add table of laps with stopwatch name
                // Label should be clickable.
                sleep(1000);
            }
            catch (InterruptedException e)
            {}
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    public void setStopwatch(Stopwatch stopwatch) { this.stopwatch = stopwatch; logger.debug("stopwatch set to {}", stopwatch); }

    public Stopwatch getStopwatch() { return stopwatch; }
}
