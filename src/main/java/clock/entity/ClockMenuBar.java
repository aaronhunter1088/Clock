package clock.entity;

import clock.panel.ClockFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import static clock.util.Constants.*;
import static clock.entity.Panel.*;

/**
 * The menu bar for the Clock.
 *
 * @author Michael Ball
*  @version 1.0
 */
public class ClockMenuBar extends JMenuBar
{
    private static final Logger logger = LogManager.getLogger(ClockMenuBar.class);
    private final ClockFrame clockFrame;
    private final Clock clock;
    // Two main menu options
    private JMenu settingsMenu,
                  featuresMenu;
    // Options for Settings
    private JMenuItem militaryTimeSetting,
                      fullTimeSetting,
                      partialTimeSetting,
                      toggleDSTSetting,
                      showDigitalTimeSettingOnAnalogueClockSetting,
                      pauseResumeAllTimersSetting,
                      resetTimersPanelSetting,
                      pauseResumeAllAlarmsSetting,
                      resetAlarmsPanelSetting,
    // Options for Features
                      digitalClockFeature,
                      analogueClockFeature,
                      alarmsFeature,
                      timerFeature,
                      stopwatchFeature;
    private JMenu changeTimeZone;
    private List<JMenuItem> timezones;


    /**
     * The main constructor for the clock menu bar.
     * It creates a Settings and Features menu options,
     * each with several items to choose from.
     */
    public ClockMenuBar(ClockFrame clockFrame)
    {
        logger.info("Creating Clock menubar");
        this.clockFrame = clockFrame;
        this.clock = clockFrame.getClock();
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        // Menu options
        settingsMenu = new JMenu(SETTINGS);
        featuresMenu = new JMenu(FEATURES) ;
        // Settings menu choices
        setMilitaryTimeSetting(new JMenuItem(clock.isShowMilitaryTime()?HIDE+SPACE+MILITARY_TIME_SETTING:SHOW+SPACE+MILITARY_TIME_SETTING));
        getMilitaryTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
        getMilitaryTimeSetting().setForeground(Color.WHITE);
        getMilitaryTimeSetting().addActionListener(action -> {
            logger.debug("clicked show military time setting");
            if (clock.isShowMilitaryTime()) {
                clock.setShowMilitaryTime(false);
                getMilitaryTimeSetting().setText(SHOW+SPACE+MILITARY_TIME_SETTING);
            }
            else {
                clock.setShowMilitaryTime(true);
                getMilitaryTimeSetting().setText(SHOW+SPACE+STANDARD_TIME_SETTING);
            }
        });

        setFullTimeSetting(new JMenuItem(SHOW+SPACE+FULL_TIME_SETTING));
        getFullTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        getFullTimeSetting().setForeground(Color.WHITE);
        getFullTimeSetting().addActionListener(action -> {
            logger.debug("clicked show full time setting");
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

        setPartialTimeSetting(new JMenuItem(SHOW+SPACE+PARTIAL_TIME_SETTING));
        getPartialTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        getPartialTimeSetting().setForeground(Color.WHITE);
        getPartialTimeSetting().addActionListener(action -> {
            logger.debug("clicked show partial time setting");
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

        setToggleDSTSetting(new JMenuItem(Turn+SPACE+off+SPACE+DST_SETTING));
        getToggleDSTSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.SHIFT_DOWN_MASK));
        getToggleDSTSetting().setForeground(Color.WHITE);
        getToggleDSTSetting().addActionListener(action -> {
            var isEnabled = clock.isDaylightSavingsTimeEnabled();
            logger.debug("toggling dst to be {}", !isEnabled);
            clock.setDaylightSavingsTimeEnabled(!isEnabled);
            getToggleDSTSetting().setText(Turn+SPACE+(clock.isDaylightSavingsTimeEnabled()?off:on)+SPACE+DST_SETTING);
            logger.debug("setting text: '{}'", getToggleDSTSetting().getText());
        });

        setShowDigitalTimeOnAnalogueClockSetting(new JMenuItem(HIDE+SPACE+DIGITAL_TIME));
        getShowDigitalTimeOnAnalogueClockSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        getShowDigitalTimeOnAnalogueClockSetting().setForeground(Color.WHITE);
        getShowDigitalTimeOnAnalogueClockSetting().addActionListener(action -> {
            boolean showingDigitalTime = clockFrame.getAnalogueClockPanel().isShowDigitalTimeOnAnalogueClock();
            logger.debug("show digital time: {}", showingDigitalTime);
            if (showingDigitalTime)
            { getShowDigitalTimeOnAnalogueClockSetting().setText(SHOW+SPACE+DIGITAL_TIME); }
            else
            { getShowDigitalTimeOnAnalogueClockSetting().setText(HIDE+SPACE+DIGITAL_TIME); }
            clockFrame.getAnalogueClockPanel().setShowDigitalTimeOnAnalogueClock(!showingDigitalTime);
            clockFrame.getAnalogueClockPanel().repaint();
        });

        setChangeTimeZoneMenu(new JMenu(CHANGE+SPACE+TIME_ZONES));
        setTimeZones(List.of(new JMenuItem(HAWAII), new JMenuItem(ALASKA),
                             new JMenuItem(PACIFIC), new JMenuItem(CENTRAL),
                             new JMenuItem(EASTERN), new JMenuItem(MOUNTAIN) ));
        getTimezones().forEach(this::setupTimezone);
        setCurrentTimeZone();

        setPauseResumeAllTimersSetting(new JMenuItem(PAUSE+SPACE+ALL+SPACE+TIMER+S.toLowerCase()));
        getPauseResumeAllTimersSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        getPauseResumeAllTimersSetting().setForeground(Color.WHITE);
        getPauseResumeAllTimersSetting().addActionListener(action -> {
            if (clock.getListOfTimers().isEmpty())
            {
                logger.debug("no timers to pause/resume");
            }
            else
            {
                logger.debug("clicked pause/resume all timers setting");
                if (getPauseResumeAllTimersSetting().getText().equals(PAUSE+SPACE+ALL+SPACE+TIMER+S.toLowerCase())) {
                    clock.getListOfTimers().forEach(Timer::pauseTimer);
                    getPauseResumeAllTimersSetting().setText(RESUME+SPACE+ALL+SPACE+TIMER+S.toLowerCase());
                    getPauseResumeAllTimersSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
                } else {
                    clock.getListOfTimers().forEach(Timer::resumeTimer);
                    getPauseResumeAllTimersSetting().setText(PAUSE+SPACE+ALL+SPACE+TIMER+S.toLowerCase());
                    getPauseResumeAllTimersSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
                }
            }
        });

        setResetTimersPanelSetting(new JMenuItem(RESET+SPACE+TIMER+S.toLowerCase()));
        getResetTimersPanelSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        getResetTimersPanelSetting().setForeground(Color.WHITE);
        getResetTimersPanelSetting().addActionListener(action -> {
            logger.debug("clicked reset timers panel setting");
            clockFrame.getTimerPanel().resetTimerPanel();
        });

        setPauseResumeAllAlarmsSetting(new JMenuItem(PAUSE+SPACE+ALL+SPACE+ALARM+S.toLowerCase()));
        getPauseResumeAllAlarmsSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        getPauseResumeAllAlarmsSetting().setForeground(Color.WHITE);
        getPauseResumeAllAlarmsSetting().addActionListener(action -> {
            if (clock.getListOfAlarms().isEmpty())
            {
                logger.debug("no alarms to pause/resume");
            }
            else
            {
                logger.debug("clicked pause/resume all alarms setting");
                if (getPauseResumeAllAlarmsSetting().getText().equals(PAUSE+SPACE+ALL+SPACE+ALARM+S.toLowerCase())) {
                    clock.getListOfAlarms().forEach(Alarm::pauseAlarm);
                    getPauseResumeAllAlarmsSetting().setText(RESUME+SPACE+ALL+SPACE+ALARM+S.toLowerCase());
                    getPauseResumeAllAlarmsSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
                } else {
                    clock.getListOfAlarms().forEach(Alarm::resumeAlarm);
                    getPauseResumeAllAlarmsSetting().setText(PAUSE+SPACE+ALL+SPACE+ALARM+S.toLowerCase());
                    getPauseResumeAllAlarmsSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
                }
            }
        });

        setResetAlarmsPanelSetting(new JMenuItem(RESET+SPACE+ALARM+S.toLowerCase()));
        getResetAlarmsPanelSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        getResetAlarmsPanelSetting().setForeground(Color.WHITE);
        getResetAlarmsPanelSetting().addActionListener(action -> {
            logger.debug("clicked reset alarms panel setting");
            clockFrame.getAlarmPanel().resetAlarmPanel();
            clockFrame.getAlarmPanel().getAlarmsTable().setModel(new javax.swing.table.DefaultTableModel());
            clock.getListOfAlarms().forEach(alarm -> {
                alarm.stopAlarm();
            });
            clock.getListOfAlarms().clear();
            logger.info("all alarms cleared");
        });

        // Features menu choices
        setDigitalClockFeature(new JMenuItem(VIEW_DIGITAL_CLOCK));
        getDigitalClockFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        getDigitalClockFeature().addActionListener(action -> clockFrame.changePanels(PANEL_DIGITAL_CLOCK));

        setAnalogueClockFeature(new JMenuItem(VIEW_ANALOGUE_CLOCK));
        getAnalogueClockFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        getAnalogueClockFeature().addActionListener(action -> clockFrame.changePanels(PANEL_ANALOGUE_CLOCK));

        setAlarmsFeature(new JMenuItem(VIEW_ALARMS));
        getAlarmsFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        getAlarmsFeature().addActionListener(action -> clockFrame.changePanels(PANEL_ALARM));

        setTimerFeature(new JMenuItem(VIEW_TIMERS));
        getTimerFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        getTimerFeature().addActionListener(action -> clockFrame.changePanels(PANEL_TIMER));

        setStopwatchFeature(new JMenuItem(VIEW_STOPWATCH));
        getStopwatchFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        getStopwatchFeature().addActionListener(action -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            JOptionPane.showMessageDialog(
                    window,
                    "No implementation yet for Stopwatch.\n",
                    "Stopwatch",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Add options to Features Menu
        getFeaturesMenu().add(getDigitalClockFeature());
        getFeaturesMenu().add(getAnalogueClockFeature());
        getFeaturesMenu().add(getAlarmsFeature());
        getFeaturesMenu().add(getTimerFeature());
        getFeaturesMenu().add(getStopwatchFeature());

        getSettingsMenu().setOpaque(false);
        getSettingsMenu().setForeground(Color.WHITE);
        getSettingsMenu().setBackground(Color.BLACK);
        getChangeTimeZoneMenu().setForeground(Color.WHITE);
        getChangeTimeZoneMenu().setBackground(Color.BLACK);
        getAlarmsFeature().setForeground(Color.WHITE);
        getAlarmsFeature().setBackground(Color.BLACK);
        getFeaturesMenu().setOpaque(false);
        getFeaturesMenu().setForeground(Color.WHITE);
        getFeaturesMenu().setBackground(Color.BLACK);
        // Menu Items for Settings and Features
        getAlarmsFeature().setForeground(Color.WHITE);
        getAlarmsFeature().setBackground(Color.BLACK);
        getDigitalClockFeature().setForeground(Color.WHITE);
        getAnalogueClockFeature().setForeground(Color.WHITE);
        getTimerFeature().setForeground(Color.WHITE);
        getStopwatchFeature().setForeground(Color.WHITE);
        // Add both menus to main menu
        add(getSettingsMenu());
        add(getFeaturesMenu());
        logger.info("Finished creating Clock menubar");
    }

    /**
     * Sets up the timezone menu item
     * @param timezone the timezone menu item to set up
     */
    public void setupTimezone(JMenuItem timezone)
    {
        logger.debug("setup timezone for {}", timezone.getText());
        timezone.addActionListener(l -> clockFrame.updateClockTimezone(timezone));
        timezone.setForeground(Color.WHITE);
        timezone.setBackground(Color.BLACK);
        getChangeTimeZoneMenu().add(timezone);
    }

    /**
     * Updates the text on the currently selected timezone
     * so that it's clear which timezone is currently selected.
     */
    public void setCurrentTimeZone()
    {
        timezones.forEach(menuItem -> {
            if (clock.getPlainTimezoneFromZoneId(clock.getTimezone()).equals(menuItem.getText().replace(STAR,EMPTY).trim())) {
                if (!menuItem.getText().contains(STAR)) {
                    menuItem.setText(menuItem.getText()+SPACE+STAR);
                } else {
                    logger.info("selected timezone already has *");
                    logger.info("no change to timezone: {}", clock.getPlainTimezoneFromZoneId(clock.getTimezone()));
                }
            } else {
                var tzName = menuItem.getText().replace(STAR,EMPTY).trim();
                menuItem.setText(tzName);
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
    public JMenu getSettingsMenu() { return this.settingsMenu; }
    public JMenu getFeaturesMenu() { return this.featuresMenu; }
    public JMenuItem getAlarmsFeature() { return this.alarmsFeature; }
    public JMenuItem getMilitaryTimeSetting() { return this.militaryTimeSetting; }
    public JMenuItem getFullTimeSetting() { return this.fullTimeSetting; }
    public JMenuItem getPartialTimeSetting() { return this.partialTimeSetting; }
    public JMenuItem getToggleDSTSetting() { return toggleDSTSetting; }
    public JMenuItem getPauseResumeAllTimersSetting() { return pauseResumeAllTimersSetting; }
    public JMenuItem getResetTimersPanelSetting() { return resetTimersPanelSetting; }
    public JMenuItem getPauseResumeAllAlarmsSetting() { return pauseResumeAllAlarmsSetting; }
    public JMenuItem getResetAlarmsPanelSetting() { return resetAlarmsPanelSetting; }
    public JMenuItem getShowDigitalTimeOnAnalogueClockSetting() { return this.showDigitalTimeSettingOnAnalogueClockSetting; }
    public JMenu getChangeTimeZoneMenu() { return this.changeTimeZone; }
    public java.util.List<JMenuItem> getTimezones() { return this.timezones; }
    public JMenuItem getDigitalClockFeature() { return this.digitalClockFeature; }
    public JMenuItem getAnalogueClockFeature() { return this.analogueClockFeature; }
    public JMenuItem getTimerFeature() { return this.timerFeature; }
    public JMenuItem getStopwatchFeature() { return this.stopwatchFeature; }

    /* Setters */
    protected void setSettingsMenu(JMenu settingsMenu) { this.settingsMenu = settingsMenu; logger.debug("settings menu"); }
    protected void setFeaturesMenu(JMenu featuresMenu) { this.featuresMenu = featuresMenu; logger.debug("features menu"); }
    protected void setAlarmsFeature(JMenuItem alarmsFeature) { this.alarmsFeature = alarmsFeature; logger.debug("alarms feature"); }
    protected void setMilitaryTimeSetting(JMenuItem militaryTimeSetting) { this.militaryTimeSetting = militaryTimeSetting; logger.debug("military time setting"); }
    protected void setFullTimeSetting(JMenuItem fullTimeSetting) { this.fullTimeSetting = fullTimeSetting; logger.debug("full time setting"); }
    protected void setPartialTimeSetting(JMenuItem partialTimeSetting) { this.partialTimeSetting = partialTimeSetting; logger.debug("partial time setting"); }
    protected void setToggleDSTSetting(JMenuItem toggleDSTSetting) { this.toggleDSTSetting = toggleDSTSetting; logger.debug("toggle dst setting"); }
    protected void setPauseResumeAllTimersSetting(JMenuItem pauseResumeAllTimersSetting) { this.pauseResumeAllTimersSetting = pauseResumeAllTimersSetting; logger.debug("pause/resume all timers setting"); }
    protected void setResetTimersPanelSetting(JMenuItem resetTimersPanelSetting) { this.resetTimersPanelSetting = resetTimersPanelSetting; logger.debug("reset timers panel setting"); }
    protected void setPauseResumeAllAlarmsSetting(JMenuItem pauseResumeAllAlarmsSetting) { this.pauseResumeAllAlarmsSetting = pauseResumeAllAlarmsSetting; logger.debug("pause/resume all alarms setting"); }
    protected void setResetAlarmsPanelSetting(JMenuItem resetAlarmsPanelSetting) { this.resetAlarmsPanelSetting = resetAlarmsPanelSetting; logger.debug("reset alarms panel setting"); }
    protected void setShowDigitalTimeOnAnalogueClockSetting(JMenuItem showDigitalTimeSettingOnAnalogueClockSetting) { this.showDigitalTimeSettingOnAnalogueClockSetting = showDigitalTimeSettingOnAnalogueClockSetting; logger.debug("show digital time on analogue clock setting"); }
    protected void setChangeTimeZoneMenu(JMenu changeTimeZone) { this.changeTimeZone = changeTimeZone; logger.debug("change time zone menu"); }
    protected void setDigitalClockFeature(JMenuItem digitalClockFeature) { this.digitalClockFeature = digitalClockFeature; logger.debug("digital clock feature"); }
    protected void setAnalogueClockFeature(JMenuItem analogueClockFeature) { this.analogueClockFeature = analogueClockFeature; logger.debug("analogue clock feature"); }
    protected void setTimeZones(java.util.List<JMenuItem> timezones) { this.timezones = timezones; logger.debug("timezones list"); }
    protected void setTimerFeature(JMenuItem timerFeature) { this.timerFeature = timerFeature; logger.debug("timer feature"); }
    protected void setStopwatchFeature(JMenuItem stopwatchFeature) { this.stopwatchFeature = stopwatchFeature; logger.debug("stopwatch feature");}
}