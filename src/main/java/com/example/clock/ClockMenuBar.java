package com.example.clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static com.example.clock.ClockConstants.*;
import static com.example.clock.ClockPanel.*;

/**
 * The menu bar for the Clock.
 * @author Michael Ball
*  @version 2.8
 */
public class ClockMenuBar extends JMenuBar
{
    private static final Logger logger = LogManager.getLogger(ClockMenuBar.class);
    private final Clock clock;
    // Two main menu options
    private JMenu settingsMenu;
    private JMenu featuresMenu;
    // Options for Settings
    private JMenuItem militaryTimeSetting;
    private JMenuItem fullTimeSetting;
    private JMenuItem partialTimeSetting;
    private JMenuItem toggleDSTSetting;
    private JMenuItem showDigitalTimeSettingOnAnalogueClockSetting;
    private JMenu changeTimeZone;
    private List<JMenuItem> timezones;
    // Options for Features
    private JMenuItem digitalClockFeature;
    private JMenuItem analogueClockFeature;
    private JMenu alarmFeature_Menu;
    private JMenuItem timerFeature;
    // Options for alarmFeature_Menu
    private JMenuItem setAlarms;

    /**
     * The main constructor for the clock menu bar.
     * It creates a Settings and Features menu options,
     * each with several items to choose from.
     */
    ClockMenuBar(Clock clock)
    {
        this.clock = clock;
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        // Menu options
        settingsMenu = new JMenu(SETTINGS);
        featuresMenu = new JMenu(FEATURES) ;
        // Settings menu choices
        setMilitaryTimeSetting(new JMenuItem(SHOW+SPACE+MILITARY_TIME_SETTING));
        getMilitaryTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
        getMilitaryTimeSetting().setForeground(Color.WHITE);

        setFullTimeSetting(new JMenuItem(SHOW+SPACE+FULL_TIME_SETTING));
        getFullTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        getFullTimeSetting().setForeground(Color.WHITE);

        setPartialTimeSetting(new JMenuItem(SHOW+SPACE+PARTIAL_TIME_SETTING));
        getPartialTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        getPartialTimeSetting().setForeground(Color.WHITE);

        setToggleDSTSetting(new JMenuItem(Turn+SPACE+(clock.isDaylightSavingsTimeEnabled()?off:on)+SPACE+DST_SETTING));
        getToggleDSTSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        getToggleDSTSetting().setForeground(Color.WHITE);

        setShowDigitalTimeOnAnalogueClockSetting(new JMenuItem(HIDE+SPACE+DIGITAL_TIME));
        getShowDigitalTimeOnAnalogueClockSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        getShowDigitalTimeOnAnalogueClockSetting().setForeground(Color.WHITE);

        setChangeTimeZoneMenu(new JMenu(SHOW+SPACE+TIME_ZONES));
        setTimeZones(Arrays.asList(new JMenuItem(HAWAII), new JMenuItem(ALASKA),
                new JMenuItem(PACIFIC), new JMenuItem(CENTRAL), new JMenuItem(EASTERN) ));
        getTimezones().forEach(this::setupTimezone);
        setCurrentTimeZone();

        // Features menu choices
        setDigitalClockFeature(new JMenuItem(VIEW_DIGITAL_CLOCK));
        getDigitalClockFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));

        setAnalogueClockFeature(new JMenuItem(VIEW_ANALOGUE_CLOCK));
        getAnalogueClockFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));

        setAlarmFeature_Menu(new JMenu(VIEW_ALARMS));
        setSetAlarms(new JMenuItem(SET_ALARMS));
        getAlarmFeature_Menu().add(getSetAlarms());
        getSetAlarms().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));

        setTimerFeature(new JMenuItem(VIEW_TIMER));
        getTimerFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));

        // Add options to Features Menu
        getFeaturesMenu().add(getDigitalClockFeature());
        getFeaturesMenu().add(getAnalogueClockFeature());
        getFeaturesMenu().add(getAlarmFeature_Menu());
        getFeaturesMenu().add(getTimerFeature());

        getSettingsMenu().setOpaque(false);
        getSettingsMenu().setForeground(Color.WHITE);
        getSettingsMenu().setBackground(Color.BLACK);
        getChangeTimeZoneMenu().setForeground(Color.WHITE);
        getChangeTimeZoneMenu().setBackground(Color.BLACK);
        getSetAlarms().setForeground(Color.WHITE);
        getSetAlarms().setBackground(Color.BLACK);
        getFeaturesMenu().setOpaque(false);
        getFeaturesMenu().setForeground(Color.WHITE);
        getFeaturesMenu().setBackground(Color.BLACK);
        // Menu Items for Settings and Features
        getAlarmFeature_Menu().setOpaque(true);
        getAlarmFeature_Menu().setForeground(Color.WHITE);
        getAlarmFeature_Menu().setBackground(Color.BLACK);
        getDigitalClockFeature().setForeground(Color.WHITE);
        getAnalogueClockFeature().setForeground(Color.WHITE);
        getTimerFeature().setForeground(Color.WHITE);
        // Set functionality for Settings menu
        getMilitaryTimeSetting().addActionListener(action -> {
            logger.info("clicked show military time setting");
            if (clock.isShowMilitaryTime()) {
                clock.setShowMilitaryTime(false);
                getMilitaryTimeSetting().setText(SHOW+SPACE+MILITARY_TIME_SETTING);
            }
            else {
                clock.setShowMilitaryTime(true);
                getMilitaryTimeSetting().setText(SHOW+SPACE+STANDARD_TIME_SETTING);
            }
        });
        getFullTimeSetting().addActionListener(action -> {
            logger.info("clicked show full time setting");
            if (clock.isShowFullDate()) {
                clock.setShowFullDate(false);
                clock.setShowPartialDate(false);
                getFullTimeSetting().setText(SHOW+SPACE+FULL_TIME_SETTING);
            }
            else {
                clock.setShowFullDate(true);
                clock.setShowPartialDate(false);
                getFullTimeSetting().setText(HIDE+SPACE+FULL_TIME_SETTING);
            }
            getPartialTimeSetting().setText(SHOW+SPACE+PARTIAL_TIME_SETTING);
        });
        getPartialTimeSetting().addActionListener(action -> {
            logger.info("clicked show partial time setting");
            if (clock.isShowPartialDate()) {
                clock.setShowPartialDate(false);
                clock.setShowFullDate(false);
                getPartialTimeSetting().setText(SHOW+SPACE+PARTIAL_TIME_SETTING);
            }
            else {
                clock.setShowPartialDate(true);
                clock.setShowFullDate(false);
                getPartialTimeSetting().setText(HIDE+SPACE+PARTIAL_TIME_SETTING);
            }
            getFullTimeSetting().setText(SHOW+SPACE+FULL_TIME_SETTING);
        });
        getToggleDSTSetting().addActionListener(action -> {
            var isEnabled = clock.isDaylightSavingsTimeEnabled();
            logger.debug("toggling dst to be {}", !isEnabled);
            clock.setDaylightSavingsTimeEnabled(!isEnabled);
            getToggleDSTSetting().setText(Turn+SPACE+(clock.isDaylightSavingsTimeEnabled()?off:on)+SPACE+DST_SETTING);
            logger.debug("setting text: '{}'", getToggleDSTSetting().getText());
        });
        getShowDigitalTimeOnAnalogueClockSetting().addActionListener(action -> {
            logger.info("clicked show digital time or hide on analogue clock");
            logger.info("show digital time: {}", clock.isShowDigitalTimeOnAnalogueClock());
            if (clock.isShowDigitalTimeOnAnalogueClock())
            {
                clock.getAnalogueClockPanel().setClockText(EMPTY);
                clock.getAnalogueClockPanel().repaint();
                clock.setShowDigitalTimeOnAnalogueClock(false);
                getShowDigitalTimeOnAnalogueClockSetting().setText(SHOW+SPACE+DIGITAL_TIME);
            } else
            {
                clock.getAnalogueClockPanel().setClockText(clock.getTimeAsStr());
                clock.getAnalogueClockPanel().repaint();
                clock.setShowDigitalTimeOnAnalogueClock(true);
                getShowDigitalTimeOnAnalogueClockSetting().setText(HIDE+SPACE+DIGITAL_TIME);
            }
        });
        // Set functionality for Features menu
        getDigitalClockFeature().addActionListener(action -> clock.changePanels(PANEL_DIGITAL_CLOCK, false));
        getAnalogueClockFeature().addActionListener(action -> clock.changePanels(PANEL_ANALOGUE_CLOCK, false));
        getSetAlarms().addActionListener(action -> clock.changePanels(PANEL_ALARM, true));
        getTimerFeature().addActionListener(action -> clock.changePanels(PANEL_TIMER, false));
        // Add both menus to main menu
        add(getSettingsMenu());
        add(getFeaturesMenu());
        logger.info("Finished creating Clock menubar");
    }

    /**
     * Sets up the timezone menu item
     * @param timezone the timezone menu item to set up
     */
    void setupTimezone(JMenuItem timezone)
    {
        logger.debug("setup timezone for {}", timezone.getText());
        timezone.addActionListener(l -> clock.updateTheTime(timezone));
        timezone.setForeground(Color.WHITE);
        timezone.setBackground(Color.BLACK);
        getChangeTimeZoneMenu().add(timezone);
    }

    /**
     * Updates the text on the currently selected timezone
     * so that it's clear which timezone is currently selected.
     */
    void setCurrentTimeZone()
    {
        ZoneId currentTimezone = clock.getTimezone();
        timezones.forEach(timezone -> {
            if (timezone.getText().equals(clock.getPlainTimezoneFromZoneId(currentTimezone))) {
                if (!timezone.getText().contains(STAR)) {
                    timezone.setText(timezone.getText()+SPACE+STAR);
                } else {
                    logger.info("selected timezone already has *");
                    logger.info("no change to timezone: {}", clock.getPlainTimezoneFromZoneId(currentTimezone));
                }
            } else {
                var tzName = timezone.getText().replace(STAR,EMPTY).trim();
                timezone.setText(tzName);
            }
        });
    }

    /**
     * Paints the background of the menu bar black.
     * @param g the graphics object
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        logger.info("paint component");
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth()-1, getHeight()-1);
    }

    /* Getters */
    JMenu getSettingsMenu() { return this.settingsMenu; }
    JMenu getFeaturesMenu() { return this.featuresMenu; }
    JMenu getAlarmFeature_Menu() { return this.alarmFeature_Menu; }
    JMenuItem getMilitaryTimeSetting() { return this.militaryTimeSetting; }
    JMenuItem getFullTimeSetting() { return this.fullTimeSetting; }
    JMenuItem getPartialTimeSetting() { return this.partialTimeSetting; }
    JMenuItem getToggleDSTSetting() { return toggleDSTSetting; }
    JMenuItem getShowDigitalTimeOnAnalogueClockSetting() { return this.showDigitalTimeSettingOnAnalogueClockSetting; }
    JMenu getChangeTimeZoneMenu() { return this.changeTimeZone; }
    java.util.List<JMenuItem> getTimezones() { return this.timezones; }
    JMenuItem getDigitalClockFeature() { return this.digitalClockFeature; }
    JMenuItem getAnalogueClockFeature() { return this.analogueClockFeature; }
    JMenuItem getSetAlarms() { return this.setAlarms; }
    JMenuItem getTimerFeature() { return this.timerFeature; }

    /* Setters */
    protected void setSettingsMenu(JMenu settingsMenu) { this.settingsMenu = settingsMenu; }
    protected void setFeaturesMenu(JMenu featuresMenu) { this.featuresMenu = featuresMenu; }
    protected void setAlarmFeature_Menu(JMenu alarmsMenu) { this.alarmFeature_Menu = alarmsMenu; }
    protected void setMilitaryTimeSetting(JMenuItem militaryTimeSetting) { this.militaryTimeSetting = militaryTimeSetting; }
    protected void setFullTimeSetting(JMenuItem fullTimeSetting) { this.fullTimeSetting = fullTimeSetting; }
    protected void setPartialTimeSetting(JMenuItem partialTimeSetting) { this.partialTimeSetting = partialTimeSetting; }
    protected void setToggleDSTSetting(JMenuItem toggleDSTSetting) { this.toggleDSTSetting = toggleDSTSetting; }
    protected void setShowDigitalTimeOnAnalogueClockSetting(JMenuItem showDigitalTimeSettingOnAnalogueClockSetting) { this.showDigitalTimeSettingOnAnalogueClockSetting = showDigitalTimeSettingOnAnalogueClockSetting; }
    protected void setChangeTimeZoneMenu(JMenu changeTimeZone) { this.changeTimeZone = changeTimeZone; }
    protected void setDigitalClockFeature(JMenuItem digitalClockFeature) { this.digitalClockFeature = digitalClockFeature; }
    protected void setAnalogueClockFeature(JMenuItem analogueClockFeature) { this.analogueClockFeature = analogueClockFeature; }
    protected void setTimeZones(java.util.List<JMenuItem> timezones) { this.timezones = timezones;}
    protected void setSetAlarms(JMenuItem setAlarms) { this.setAlarms = setAlarms; }
    protected void setTimerFeature(JMenuItem timerFeature) { this.timerFeature = timerFeature; }
}