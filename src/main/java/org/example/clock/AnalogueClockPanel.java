package org.example.clock;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import javax.swing.JPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Thread.sleep;

/**
 * The AnalogueClockPanel is used to view the time
 * in analogue mode. The time will still show up
 * below the center in digital format. If you wish
 * to hide this, the settings allows for that.
 *
 * @author michael ball
 * @version 2.6
 */
public class AnalogueClockPanel extends JPanel implements ClockConstants, IClockPanel, Runnable {
    private static final Logger logger = LogManager.getLogger(AnalogueClockPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    Thread thread = null;
    SimpleDateFormat formatter = new SimpleDateFormat("s", Locale.getDefault());
    Date currentDate;
    int xcenter = 175, ycenter = 175, lastxs = 0, lastys = 0, lastxm = 0, lastym = 0, lastxh = 0, lastyh = 0;
    Clock clock;
    PanelType panelType;
    private String clockText = "";

    /**
     * Default constructor
     * @param clock the clock reference
     */
    public AnalogueClockPanel(Clock clock) {
        super();
        setupDefaultActions(clock);
        logger.info("Finished creating AnalogueClock Panel");
    }

    @Override
    public void setClock(Clock clock) { this.clock = clock ;}
    @Override
    public void setPanelType(PanelType panelType) { this.panelType = panelType; }
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    protected void setClockText(String clockText) { this.clockText = clockText; }

    public Clock getClock() { return this.clock; }
    public PanelType getPanelType() { return this.panelType; }
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public String getClockText() { return this.clockText; }

    void drawStructure(Graphics g) {
        logger.info("drawStructure");
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        g.setColor(Color.BLACK);
        g.fillOval(xcenter - 150, ycenter - 150, 300, 300);

        g.setColor(Color.BLUE);
        g.drawString(clockText, 120, 260);

        g.setColor(Color.WHITE);
        g.drawString("1", xcenter + 60, ycenter - 110);
        g.drawString("2", xcenter + 110, ycenter - 60);
        g.drawString("3", xcenter + 135, ycenter);
        g.drawString("4", xcenter + 110, ycenter + 60);
        g.drawString("5", xcenter + 60, ycenter + 110);
        g.drawString("6", xcenter - 10, ycenter + 145);
        g.drawString("8", xcenter - 120, ycenter + 60);
        g.drawString("7", xcenter - 80, ycenter + 110);
        g.drawString("9", xcenter - 145, ycenter);
        g.drawString("10", xcenter - 130, ycenter - 60);
        g.drawString("11", xcenter - 80, ycenter - 110);
        g.drawString("12", xcenter - 10, ycenter - 130);
        g.setColor(Color.BLACK); // needed to avoid second hand delay UI issue
    }

    void start(AnalogueClockPanel panel) {
        logger.info("start analogue clock");
        if (thread == null) {
            thread = new Thread(panel);
            thread.start();
        }
    }

    void stop() {
        logger.info("stopping analogue thread");
        thread = null;
    }

    void setupDefaultActions(Clock clock) {
        logger.info("setupDefaultActions with Clock");
        setClock(clock);
        setClockText(clock.getTimeAsStr());
        setupSettingsMenu();
        setDefaults();
    }

    protected void setupSettingsMenu() {
        clock.getClockMenuBar().getSettingsMenu().removeAll();
        clock.getClockMenuBar().getSettingsMenu().add(clock.getClockMenuBar().getShowDigitalTimeOnAnalogueClockSetting());
        clock.setShowDigitalTimeOnAnalogueClock(true);
        clock.getClockMenuBar().getShowDigitalTimeOnAnalogueClockSetting().setText(ClockConstants.HIDE + ClockConstants.SPACE + ClockConstants.DIGITAL_TIME);
    }

    void setDefaults() {
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
    public void run() {
        logger.info("starting analogue clock");
        while (thread != null) {
            try { sleep(1000); }
            catch (InterruptedException e) { printStackTrace(e, e.getMessage());}
            repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
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
        if (xsecond != lastxs || ysecond != lastys) { g.drawLine(xcenter, ycenter, lastxs, lastys); }
        if (xminute != lastxm || yminute != lastym) {
            g.drawLine(xcenter, ycenter - 1, lastxm, lastym);
            g.drawLine(xcenter - 1, ycenter, lastxm, lastym);
        }
        if (xhour != lastxh || yhour != lastyh) {
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

    @Override
    public void update(Graphics g) {
        logger.info("updating graphics");
        paint(g);
    }

    @Override
    public void addComponentsToPanel() { /* no operation */ }

}