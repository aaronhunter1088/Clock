package org.example.clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.ZoneId;
import java.util.Arrays;

import static org.example.clock.ClockConstants.*;

/**
 * The menu bar for the Clock.
 * @author Michael Ball
 * @version 2.6
 */
public class ClockMenuBar extends JMenuBar {
    private static final Logger logger = LogManager.getLogger(ClockMenuBar.class);
    Clock clock;
    // Two main menu options
    JMenu settingsMenu;
    JMenu featuresMenu;
    // Options for Settings
    JMenuItem militaryTimeSetting;
    JMenuItem fullTimeSetting;
    JMenuItem partialTimeSetting;
    JMenuItem showDigitalTimeSettingOnAnalogueClockSetting;
    JMenu changeTimeZone;
    java.util.List<JMenuItem> timezones;
    // Options for Features
    JMenuItem digitalClockFeature;
    JMenuItem analogueClockFeature;
    JMenu alarmFeature_Menu;
    JMenuItem timerFeature;
    // Options for alarmFeature_Menu
    JMenuItem setAlarms;

    /**
     * The main constructor for the clock menu bar.
     * It creates a Settings and Features menu options,
     * each with several items to choose from.
     */
    ClockMenuBar(Clock clock) {
        this.clock = clock;
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        // Menu options
        setSettingsMenu(new JMenu("Settings"));
        setFeaturesMenu(new JMenu("Features"));
        // Settings menu choices
        setMilitaryTimeSetting(new JMenuItem(SHOW + SPACE + MILITARY_TIME_SETTING));
        getMilitaryTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
        getMilitaryTimeSetting().setForeground(Color.WHITE);

        setFullTimeSetting(new JMenuItem(SHOW + SPACE + FULL_TIME_SETTING));
        getFullTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        getFullTimeSetting().setForeground(Color.WHITE);

        setPartialTimeSetting(new JMenuItem(SHOW + SPACE + PARTIAL_TIME_SETTING));
        getPartialTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        getPartialTimeSetting().setForeground(Color.WHITE);

        setShowDigitalTimeOnAnalogueClockSetting(new JMenuItem(HIDE + SPACE + DIGITAL_TIME));
        getShowDigitalTimeOnAnalogueClockSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        getShowDigitalTimeOnAnalogueClockSetting().setForeground(Color.WHITE);

        setChangeTimeZoneMenu(new JMenu(SHOW + SPACE + TIME_ZONES));
        setTimeZones(Arrays.asList(new JMenuItem("Hawaii"), new JMenuItem("Alaska"),
                new JMenuItem("Pacific"), new JMenuItem("Central"), new JMenuItem("Eastern") ));
        getTimezones().forEach(this::setupTimezone);
        setCurrentTimeZone();

        // Features menu choices
        setDigitalClockFeature(new JMenuItem("View Digital Clock"));
        getDigitalClockFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));

        setAnalogueClockFeature(new JMenuItem("View Analogue Clock"));
        getAnalogueClockFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));

        setAlarmFeature_Menu(new JMenu("View Alarms"));
        setSetAlarms(new JMenuItem("Set Alarms"));
        getAlarmFeature_Menu().add(getSetAlarms());
        getSetAlarms().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));

        setTimerFeature(new JMenuItem("View Timer"));
        getTimerFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        setDigitalClockSettingsOnMenuBar();
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
        //getTimezones().forEach(timezone -> timezone.setForeground(Color.WHITE));
        //getTimezones().forEach(timezone -> timezone.setBackground(Color.BLACK));
        // Set functionality for Settings menu
        getMilitaryTimeSetting().addActionListener(action -> {
            logger.info("clicked show military time setting");
            if (clock.isShowMilitaryTime()) {
                clock.setShowMilitaryTime(false);
                getMilitaryTimeSetting().setText(SHOW + SPACE + MILITARY_TIME_SETTING);
            }
            else {
                clock.setShowMilitaryTime(true);
                getMilitaryTimeSetting().setText(SHOW + SPACE + STANDARD_TIME_SETTING);
            }
        });
        getFullTimeSetting().addActionListener(action -> {
            logger.info("clicked show full time setting");
            if (clock.isShowFullDate()) {
                clock.setShowFullDate(false);
                clock.setShowPartialDate(false);
                getFullTimeSetting().setText(SHOW + SPACE + FULL_TIME_SETTING);
            }
            else {
                clock.setShowFullDate(true);
                clock.setShowPartialDate(false);
                getFullTimeSetting().setText(HIDE + SPACE + FULL_TIME_SETTING);
            }
            getPartialTimeSetting().setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
        });
        getPartialTimeSetting().addActionListener(action -> {
            logger.info("clicked show partial time setting");
            if (clock.isShowPartialDate()) {
                clock.setShowPartialDate(false);
                clock.setShowFullDate(false);
                getPartialTimeSetting().setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
            }
            else {
                clock.setShowPartialDate(true);
                clock.setShowFullDate(false);
                getPartialTimeSetting().setText(HIDE + SPACE + PARTIAL_TIME_SETTING);
            }
            getFullTimeSetting().setText(SHOW + SPACE + FULL_TIME_SETTING);
        });
        getShowDigitalTimeOnAnalogueClockSetting().addActionListener(action -> {
            logger.info("clicked show digital time or hide on analogue clock");
            logger.info("show digital time: " + clock.isShowDigitalTimeOnAnalogueClock());
            if (clock.isShowDigitalTimeOnAnalogueClock())
            {
                clock.getAnalogueClockPanel().setClockText("");
                clock.getAnalogueClockPanel().repaint();
                clock.setShowDigitalTimeOnAnalogueClock(false);
                getShowDigitalTimeOnAnalogueClockSetting().setText(SHOW + SPACE + DIGITAL_TIME);
            } else
            {
                clock.getAnalogueClockPanel().setClockText(clock.getTimeAsStr());
                clock.getAnalogueClockPanel().repaint();
                clock.setShowDigitalTimeOnAnalogueClock(true);
                getShowDigitalTimeOnAnalogueClockSetting().setText(HIDE + SPACE + DIGITAL_TIME);
            }
        });
        // Set functionality for Features menu
        getDigitalClockFeature().addActionListener(action -> clock.changeToDigitalClockPanel());
        getAnalogueClockFeature().addActionListener(action -> clock.changeToAnalogueClockPanel());
        getSetAlarms().addActionListener(action -> clock.changeToAlarmPanel(true));
        getTimerFeature().addActionListener(action -> clock.changeToTimerPanel());
        // Add both menus to main menu
        add(getSettingsMenu());
        add(getFeaturesMenu());
        logger.info("Finished creating Clock menu bar");
    }

    private void setupTimezone(JMenuItem timezone) {
        logger.info("setup timezone for {}", timezone.getText());
        timezone.addActionListener(l -> clock.updateTheTime(timezone));
        timezone.setForeground(Color.WHITE);
        timezone.setBackground(Color.BLACK);
        getChangeTimeZoneMenu().add(timezone);
    }

    private void setDigitalClockSettingsOnMenuBar() {
        // Add digital clock settings to Menu
        settingsMenu.add(getMilitaryTimeSetting());
        settingsMenu.add(getFullTimeSetting());
        settingsMenu.add(getPartialTimeSetting());
        settingsMenu.add(getChangeTimeZoneMenu());
    }

    /**
     * Updates the text on the currently selected timezone
     * so that it's clear which timezone is currently selected.
     */
    void setCurrentTimeZone() {
        ZoneId currentTimezone = clock.getTimezone();
        timezones.forEach(timezone -> {
            if (timezone.getText().equals(clock.getPlainTimezoneFromZoneId(currentTimezone))) {
                if (!timezone.getText().contains("*")) {
                    timezone.setText(timezone.getText() + " *");
                } else {
                    logger.info("selected timezone already has *");
                    logger.info("no change to timezone: {}", clock.getPlainTimezoneFromZoneId(currentTimezone));
                }
            } else {
                var tzName = timezone.getText().replace("*","").trim();
                timezone.setText(tzName);
            }
        });
    }

    // Getters
    public JMenu getSettingsMenu() { return this.settingsMenu; }
    public JMenu getFeaturesMenu() { return this.featuresMenu; }
    public JMenu getAlarmFeature_Menu() { return this.alarmFeature_Menu; }
    public JMenuItem getMilitaryTimeSetting() { return this.militaryTimeSetting; }
    public JMenuItem getFullTimeSetting() { return this.fullTimeSetting; }
    public JMenuItem getPartialTimeSetting() { return this.partialTimeSetting; }
    public JMenuItem getShowDigitalTimeOnAnalogueClockSetting() { return this.showDigitalTimeSettingOnAnalogueClockSetting; }
    public JMenu getChangeTimeZoneMenu() { return this.changeTimeZone; }
    public java.util.List<JMenuItem> getTimezones() { return this.timezones; }
    public JMenuItem getDigitalClockFeature() { return this.digitalClockFeature; }
    public JMenuItem getAnalogueClockFeature() { return this.analogueClockFeature; }
    public JMenuItem getSetAlarms() { return this.setAlarms; }
    public JMenuItem getTimerFeature() { return this.timerFeature; }

    // Setters
    void setSettingsMenu(JMenu settingsMenu) { this.settingsMenu = settingsMenu; }
    void setFeaturesMenu(JMenu featuresMenu) { this.featuresMenu = featuresMenu; }
    void setAlarmFeature_Menu(JMenu alarmsMenu) { this.alarmFeature_Menu = alarmsMenu; }
    void setMilitaryTimeSetting(JMenuItem militaryTimeSetting) { this.militaryTimeSetting = militaryTimeSetting; }
    void setFullTimeSetting(JMenuItem fullTimeSetting) { this.fullTimeSetting = fullTimeSetting; }
    void setPartialTimeSetting(JMenuItem partialTimeSetting) { this.partialTimeSetting = partialTimeSetting; }
    void setShowDigitalTimeOnAnalogueClockSetting(JMenuItem showDigitalTimeSettingOnAnalogueClockSetting) { this.showDigitalTimeSettingOnAnalogueClockSetting = showDigitalTimeSettingOnAnalogueClockSetting; }
    void setChangeTimeZoneMenu(JMenu changeTimeZone) { this.changeTimeZone = changeTimeZone; }
    void setDigitalClockFeature(JMenuItem digitalClockFeature) { this.digitalClockFeature = digitalClockFeature; }
    void setAnalogueClockFeature(JMenuItem analogueClockFeature) { this.analogueClockFeature = analogueClockFeature; }
    void setTimeZones(java.util.List<JMenuItem> timezones) { this.timezones = timezones;}
    void setSetAlarms(JMenuItem setAlarms) { this.setAlarms = setAlarms; }
    void setTimerFeature(JMenuItem timerFeature) { this.timerFeature = timerFeature; }

    @Override
    protected void paintComponent(Graphics g) {
        logger.info("paintComponent");
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}
