package org.example.clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * The DigitalClockPanel is the main panel and is
 * visible first to the user. Here you can
 * see the time and date.
 * Clicking on the menu options under
 * Settings can change how the time and date
 * look.
 *
 * @author michael ball
 * @version 2.6
 */
public class DigitalClockPanel extends JPanel implements ClockConstants, IClockPanel {
    private static final Logger logger = LogManager.getLogger(DigitalClockPanel.class);
    GridBagLayout layout;
    GridBagConstraints constraints;
    JLabel label1;
    JLabel label2;
    Clock clock;
    PanelType panelType;

    DigitalClockPanel(Clock clock) {
        super();
        setClock(clock);
        setPanelType(PanelType.DIGITAL_CLOCK);
        setMaximumSize(Clock.defaultSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setupClockPanel(getClock());
        addComponentsToPanel();
        setupSettingsMenu();
        logger.info("Finished creating DigitalClock Panel");
    }

    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clock; }
    public JLabel getLabel1() { return this.label1; }
    public JLabel getLabel2() { return this.label2; }
    public PanelType getPanelType() { return this.panelType; }

    private void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setLabel1(JLabel label1) { this.label1 = label1; }
    public void setLabel2(JLabel label2) { this.label2 = label2; }
    @Override
    public void setClock(Clock clock) { this.clock = clock; }
    @Override
    public void setPanelType(PanelType panelType) { this.panelType = panelType; }

    public void setupClockPanel(Clock clock) {
        logger.info("setupClockPanel");
        setupSettingsMenu();
        clock.setIsDateChanged(false);
        clock.setShowFullDate(false);
        clock.setShowPartialDate(false);
        clock.setShowMilitaryTime(false);
        setLabel1(new JLabel("", SwingConstants.CENTER));
        setLabel2(new JLabel("", SwingConstants.CENTER));
    }

    public void printStackTrace(Exception e, String message)
    {
        if (null != message)
            logger.error(message);
        else
            logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        {
            logger.info(ste.toString());
        }
    }

    protected void printStackTrace(Exception e)
    { printStackTrace(e, ""); }

    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady)
    {
        logger.info("addComponent");
        getGridBagConstraints().gridx = gridx;
        getGridBagConstraints().gridy = gridy;
        getGridBagConstraints().gridwidth = (int)Math.ceil(gwidth);
        getGridBagConstraints().gridheight = (int)Math.ceil(gheight);
        getGridBagConstraints().ipadx = ipadx;
        getGridBagConstraints().ipady = ipady;
        getGridBagConstraints().fill = GridBagConstraints.NONE;
        getGridBagConstraints().insets = new Insets(0,0,0,0);
        getGridBagLayout().setConstraints(cpt, getGridBagConstraints());
        add(cpt);
    }

    @Override
    public void addComponentsToPanel()
    {
        logger.info("addComponentsToPanel");
        addComponent(getLabel1(), 0,0,1,1, 0,0);
        addComponent(getLabel2(), 1,0,1,1, 0,0);
    }

    protected void setupSettingsMenu() {
        this.clock.getClockMenuBar().getSettingsMenu().removeAll(); // easier
        this.clock.getClockMenuBar().getSettingsMenu().add(this.clock.getClockMenuBar().getMilitaryTimeSetting());
        this.clock.getClockMenuBar().getSettingsMenu().add(this.clock.getClockMenuBar().getFullTimeSetting());
        this.clock.getClockMenuBar().getSettingsMenu().add(this.clock.getClockMenuBar().getPartialTimeSetting());
        this.clock.getClockMenuBar().getSettingsMenu().add(this.clock.getClockMenuBar().getChangeTimeZoneMenu());
    }

    public void updateLabels()
    {
        logger.info("updateLabels");
        if (getClock().getAlarmPanel().getCurrentAlarmGoingOff() != null) {
            getLabel1().setText(getClock().defaultText(8));
            getLabel2().setText(getClock().defaultText(9));
        } else { // default date and time
            getLabel1().setText(getClock().defaultText(1));
            getLabel2().setText(getClock().defaultText(2));
        }
        if (getClock().isShowFullDate()) getLabel1().setFont(Clock.font40);
        else if (getClock().isShowPartialDate()) getLabel1().setFont(Clock.font50);
        else getLabel1().setFont(Clock.font60);
        getLabel2().setFont(Clock.font50);
        getLabel1().setForeground(Color.WHITE);
        getLabel2().setForeground(Color.WHITE);
        getClock().repaint();
    }
}