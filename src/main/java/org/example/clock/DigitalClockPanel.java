package org.example.clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

import static org.example.clock.ClockPanel.PANEL_DIGITAL_CLOCK;

/**
 * The DigitalClockPanel is the main panel and is
 * visible first to the user. Here you can
 * see the time and date.
 * Clicking on the menu options under
 * Settings can change how the time and date
 * look.
 *
 * @author michael ball
 * @version 2.7
 */
public class DigitalClockPanel extends JPanel implements IClockPanel
{
    private static final Logger logger = LogManager.getLogger(DigitalClockPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel label1;
    private JLabel label2;
    private Clock clock;
    private ClockPanel clockPanel;

    /**
     * The main constructor for the digital clock panel
     * @param clock the clock object reference
     */
    DigitalClockPanel(Clock clock)
    {
        super();
        this.clock = clock;
        this.clock.setClockPanel(PANEL_DIGITAL_CLOCK);
        setMaximumSize(Clock.defaultSize);
        layout = new GridBagLayout();
        setLayout(layout);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setupClockPanel();
        addComponentsToPanel();
        setupSettingsMenu();
        logger.info("Finished creating DigitalClock Panel");
    }

    /**
     * This method sets up the digital clock panel.
     */
    public void setupClockPanel()
    {
        logger.info("setup digital clock panel");
        setupSettingsMenu();
        clock.setDateChanged(false);
        clock.setShowFullDate(false);
        clock.setShowPartialDate(false);
        clock.setShowMilitaryTime(false);
        label1 = new JLabel(EMPTY, SwingConstants.CENTER);
        label2 = new JLabel(EMPTY, SwingConstants.CENTER);
    }

    /**
     * This method prints the stack trace of an exception
     * that may occur when the digital panel is in use.
     * @param e the exception
     * @param message the message to print
     */
    public void printStackTrace(Exception e, String message)
    {
        if (null != message) logger.error(message);
        else logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        { logger.error(ste.toString()); }
    }

    /**
     * This method prints the stack trace of an exception
     * that may occur when the digital panel is in use.
     * @param e the exception
     */
    protected void printStackTrace(Exception e)
    { printStackTrace(e, EMPTY); }

    /**
     * The main method used for adding components
     * to a panel
     * @param cpt       the component to add
     * @param gridy     the y position
     * @param gridx     the x position
     * @param gwidth    the width
     * @param gheight   the height
     * @param ipadx     the x padding
     * @param ipady     the y padding
     */
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

    /**
     * This method adds the components to the digital clock panel
     */
    public void addComponentsToPanel()
    {
        logger.info("addComponentsToPanel");
        addComponent(getLabel1(), 0,0,1,1, 0,0);
        addComponent(getLabel2(), 1,0,1,1, 0,0);
    }

    /**
     * This method sets up the settings menu for the
     * digital clock panel.
     */
    public void setupSettingsMenu()
    {
        clock.clearSettingsMenu();
        clock.getClockMenuBar().getSettingsMenu().add(clock.getClockMenuBar().getMilitaryTimeSetting());
        clock.getClockMenuBar().getSettingsMenu().add(clock.getClockMenuBar().getFullTimeSetting());
        clock.getClockMenuBar().getSettingsMenu().add(clock.getClockMenuBar().getPartialTimeSetting());
        clock.getClockMenuBar().getSettingsMenu().add(clock.getClockMenuBar().getToggleDSTSetting());
        clock.getClockMenuBar().getSettingsMenu().add(clock.getClockMenuBar().getChangeTimeZoneMenu());
    }

    /**
     * This method updates the labels on the digital
     * clock panel.
     */
    public void updateLabels()
    {
        logger.info("updateLabels");
        label1.setForeground(Color.WHITE);
        label2.setForeground(Color.WHITE);
        if (clock.getAlarmPanel().getActiveAlarm() != null)
        {
            label1.setText(clock.defaultText(8));
            label2.setText(clock.defaultText(9));
        }
        // default date and time
        else
        {
            label1.setText(clock.defaultText(1));
            label2.setText(clock.defaultText(2));
        }
        if (clock.isShowFullDate()) label1.setFont(Clock.font40);
        else if (clock.isShowPartialDate()) label1.setFont(Clock.font50);
        else label1.setFont(Clock.font60);
        label2.setFont(Clock.font50);
        clock.repaint();
    }

    /* Getters */
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clock; }
    public JLabel getLabel1() { return this.label1; }
    public JLabel getLabel2() { return this.label2; }
    public ClockPanel getPanelType() { return this.clockPanel; }

    /* Setters */
    private void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    public void setLabel1(JLabel label1) { this.label1 = label1; }
    public void setLabel2(JLabel label2) { this.label2 = label2; }
    public void setClock(Clock clock) { this.clock = clock; }
    public void setClockPanel(ClockPanel clockPanel) { this.clockPanel = clockPanel; }
}