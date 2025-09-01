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
    }

    private void setGridBagLayout(GridBagLayout layout) { setLayout(layout); this.layout = layout; logger.debug("GridBagLayout set"); }
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
}

