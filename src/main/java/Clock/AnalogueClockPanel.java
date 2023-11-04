package Clock;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import javax.swing.JPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnalogueClockPanel extends JPanel implements IClockPanel, Runnable
{
    private static final Logger logger = LogManager.getLogger(AnalogueClockPanel.class);
    Thread thread = null;
    SimpleDateFormat formatter = new SimpleDateFormat("s", Locale.getDefault());
    Date currentDate;
    int xcenter = 175, ycenter = 175, lastxs = 0, lastys = 0, lastxm = 0, lastym = 0, lastxh = 0, lastyh = 0;
    Clock clock;
    public PanelType panelType;
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private String CLOCK_TEXT = "";

    /**
     * Default constructor
     * @param clock the clock reference
     */
    public AnalogueClockPanel(Clock clock)
    {
        super();
        setupDefaultActions(clock);
        logger.info("Finished creating AnalogueClock Panel");
    }
    public AnalogueClockPanel()
    {
        setupDefaultActions();
        logger.info("Finished creating AnalogueClock Panel");
    }

    public void setClock(Clock clock) { this.clock = clock ;}
    protected void setPanelType(PanelType panelType) { this.panelType = panelType; }
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    protected void setClockText(String clockText) { this.CLOCK_TEXT = clockText; }

    public Clock getClock() { return this.clock; }
    public PanelType getPanelType() { return this.panelType; }
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public String getCLOCK_TEXT() { return this.CLOCK_TEXT; }
    private void drawStructure(Graphics g)
    {
        logger.info("drawStructure");
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        g.setColor(Color.BLACK);
        g.fillOval(xcenter - 150, ycenter - 150, 300, 300);

        g.setColor(Color.BLUE);
        g.drawString(CLOCK_TEXT, 120, 260);

        g.setColor(Color.WHITE);
        g.drawString("1", xcenter + 60, ycenter - 110);
        g.drawString("2", xcenter + 110, ycenter - 60);
        g.drawString("3", xcenter + 135, ycenter);
        g.drawString("4", xcenter + 110, ycenter + 60);
        g.drawString("5", xcenter + 60, ycenter + 110);
        g.drawString("6", xcenter - 10, ycenter + 145);
        g.drawString("7", xcenter - 120, ycenter + 60);
        g.drawString("8", xcenter - 80, ycenter + 110);
        g.drawString("9", xcenter - 145, ycenter);
        g.drawString("10", xcenter - 130, ycenter - 60);
        g.drawString("11", xcenter - 80, ycenter - 110);
        g.drawString("12", xcenter - 10, ycenter - 130);
        g.setColor(Color.BLACK); // needed to avoid second hand delay UI issue
    }

    public void paint(Graphics g)
    {
        logger.info("painting analogue clock panel");
        int xhour, yhour, xminute, yminute, xsecond, ysecond, second, minute, hour;
        currentDate = java.util.Date.from(getClock().getDate().atTime(getClock().getHours(), getClock().getMinutes(), getClock().getSeconds())
                .atZone(ZoneId.systemDefault())
                .toInstant());
        if (clock.isShowDigitalTimeOnAnalogueClock()) {
            setClockText(clock.getTimeAsStr());
        }
        drawStructure(g);

        formatter.applyPattern("s");
        second = Integer.parseInt(formatter.format(currentDate));
        formatter.applyPattern("m");
        minute = Integer.parseInt(formatter.format(currentDate));
        formatter.applyPattern("h");
        hour = Integer.parseInt(formatter.format(currentDate));
        xsecond = (int)(Math.cos(second * 3.14f / 30 - 3.14f / 2) * 120 + xcenter);
        ysecond = (int)(Math.sin(second * 3.14f / 30 - 3.14f / 2) * 120 + ycenter);
        xminute = (int)(Math.cos(minute * 3.14f / 30 - 3.14f / 2) * 100 + xcenter);
        yminute = (int)(Math.sin(minute * 3.14f / 30 - 3.14f / 2) * 100 + ycenter);
        xhour = (int)(Math.cos((hour * 30 + minute / 2) * 3.14f / 180 - 3.14f / 2) * 80 + xcenter);
        yhour = (int)(Math.sin((hour * 30 + minute / 2) * 3.14f / 180 - 3.14f / 2) * 80 + ycenter);
        // Erase if necessary, and redraw

        // second hand start
        //g.setColor(Color.RED);
        if (xsecond != lastxs || ysecond != lastys)
        {
            g.drawLine(xcenter, ycenter, lastxs, lastys);
        }
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

    public void start(AnalogueClockPanel panel)
    {
        logger.info("start analogue clock");
        if (thread == null)
        {
            thread = new Thread(panel);
            thread.start();
        }
    }

    public void stop()
    {
        logger.info("stopping analogue thread");
        thread = null;
    }

    public void run()
    {
        logger.info("running analogue clock");
        while (thread != null)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {}
            repaint();
        }
        thread = null;
    }

    public void update(Graphics g)
    {
        logger.info("updating graphics");
        paint(g);
    }

    public void setupDefaultActions(Clock clock)
    {
        logger.info("setupDefaultActions with Clock");
        setClock(clock);
        setClockText(clock.getTimeAsStr());
        getClock().setShowDigitalTimeOnAnalogueClock(true);
        getClock().getClockMenuBar().getShowDigitalTimeOnAnalogueClockSetting().setText(ClockConstants.HIDE + ClockConstants.SPACE + ClockConstants.DIGITAL_TIME);
        setupDefaultActions();
    }

    public void setupDefaultActions()
    {
        logger.info("setupDefaultActions");
        setPanelType(PanelType.ANALOGUE_CLOCK);
        setMaximumSize(new Dimension(350, 400));
        setGridBagLayout(new GridBagLayout());
        setLayout(layout);
        setGridBagConstraints(new GridBagConstraints());
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        start(this);
    }

    @Override
    public void addComponentsToPanel() {
        logger.info("addComponentsToPanel");
    }

    @Override
    public void printStackTrace(Exception e, String message) {
        logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        {
            logger.error(ste.toString());
        }
    }

}