package Clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import static Clock.ClockConstants.*;
import static Clock.ClockConstants.FULL_TIME_SETTING;

/**
 * The menu bar for the Clock.
 * Option to stop Alarm coming in 2.5
 * @author Michael Ball
 * @version 2.5
 */
public class ClockMenuBar extends JMenuBar {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(ClockMenuBar.class);
    // Two main menu options
    protected JMenu settingsMenu;
    protected JMenu featuresMenu;
    // Options for Settings
    protected JMenuItem militaryTimeSetting;
    protected JMenuItem fullTimeSetting;
    protected JMenuItem partialTimeSetting;
    protected JMenuItem showDigitalTimeSettingOnAnalogueClockSetting;
    // Options for Features
    protected JMenuItem digitalClockFeature;
    protected JMenuItem analogueClockFeature;
    protected JMenu alarmFeature_Menu;
    protected JMenuItem timerFeature;
    // Options for alarmFeature_Menu
    protected JMenuItem setAlarms;

    public ClockMenuBar(Clock clock)
    {
        logger.info("ClockMenuBar");
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        // Menu options
        setSettingsMenu(new JMenu("Settings"));
        setFeaturesMenu(new JMenu("Features"));
        // Settings menu choices
        setMilitaryTimeSetting(new JMenuItem(ClockConstants.SHOW + ClockConstants.SPACE + ClockConstants.MILITARY_TIME_SETTING));
        getMilitaryTimeSetting().setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
        getMilitaryTimeSetting().setForeground(Color.WHITE);
        setFullTimeSetting(new JMenuItem(ClockConstants.SHOW + ClockConstants.SPACE + ClockConstants.FULL_TIME_SETTING));
        getFullTimeSetting().setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        getFullTimeSetting().setForeground(Color.WHITE);
        setPartialTimeSetting(new JMenuItem(ClockConstants.SHOW + ClockConstants.SPACE + ClockConstants.PARTIAL_TIME_SETTING));
        getPartialTimeSetting().setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        getPartialTimeSetting().setForeground(Color.WHITE);
        // analogue clock settings
        setShowDigitalTimeOnAnalogueClockSetting(new JMenuItem(ClockConstants.HIDE + SPACE + DIGITAL_TIME));
        getShowDigitalTimeOnAnalogueClockSetting().setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        getShowDigitalTimeOnAnalogueClockSetting().setForeground(Color.WHITE);

        // Features menu choices
        setDigitalClockFeature(new JMenuItem("View Digital Clock"));
        setAnalogueClockFeature(new JMenuItem("View Analogue Clock"));
        getDigitalClockFeature().setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        getAnalogueClockFeature().setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        setAlarmFeature_Menu(new JMenu("View Alarms"));
        setSetAlarms(new JMenuItem("Set Alarms"));
        getSetAlarms().setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        setTimerFeature(new JMenuItem("View Timer"));
        getTimerFeature().setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        // Add options to Settings Menu
        getSettingsMenu().add(getMilitaryTimeSetting());
        getSettingsMenu().add(getFullTimeSetting());
        getSettingsMenu().add(getPartialTimeSetting());
        // Add options to Features Menu
        getFeaturesMenu().add(getDigitalClockFeature());
        getFeaturesMenu().add(getAnalogueClockFeature());
        getFeaturesMenu().add(getAlarmFeature_Menu());
        getAlarmFeature_Menu().add(getSetAlarms());
        getFeaturesMenu().add(getTimerFeature());

        getSettingsMenu().setOpaque(false);
        getSettingsMenu().setForeground(Color.WHITE);
        getSettingsMenu().setBackground(Color.BLACK);
        getFeaturesMenu().setOpaque(false);
        getFeaturesMenu().setForeground(Color.WHITE);
        getFeaturesMenu().setBackground(Color.BLACK);
        // Menu Items for Settings
        // Menu Items for Features
        getAlarmFeature_Menu().setOpaque(true);
        getAlarmFeature_Menu().setForeground(Color.WHITE);
        getAlarmFeature_Menu().setBackground(Color.BLACK);
        getDigitalClockFeature().setForeground(Color.WHITE);
        getAnalogueClockFeature().setForeground(Color.WHITE);
        getTimerFeature().setForeground(Color.WHITE);
        getSetAlarms().setForeground(Color.WHITE);
        getSetAlarms().setBackground(Color.BLACK);
        // Set functionality
        // Settings Actions for Settings menu
        getMilitaryTimeSetting().addActionListener(action -> {
            logger.info("clicked show military time setting");
            if (clock.isShowMilitaryTime())
            {
                clock.setShowMilitaryTime(false);
                getMilitaryTimeSetting().setText(SHOW + SPACE + MILITARY_TIME_SETTING);
            }
            else
            {
                clock.setShowMilitaryTime(true);
                getMilitaryTimeSetting().setText(SHOW + SPACE + STANDARD_TIME_SETTING);
            }
        });
        getFullTimeSetting().addActionListener(action -> {
            logger.info("clicked show full time setting");
            if (clock.isShowFullDate())
            {
                clock.setShowFullDate(false);
                clock.setShowPartialDate(false);
                getFullTimeSetting().setText(SHOW + SPACE + FULL_TIME_SETTING);
            }
            else
            {
                clock.setShowFullDate(true);
                clock.setShowPartialDate(false);
                getFullTimeSetting().setText(HIDE + SPACE + FULL_TIME_SETTING);
            }
            getPartialTimeSetting().setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
        });
        getPartialTimeSetting().addActionListener(action -> {
            logger.info("clicked show partial time setting");
            if (clock.isShowPartialDate())
            {
                clock.setShowPartialDate(false);
                clock.setShowFullDate(false);
                getPartialTimeSetting().setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
            }
            else
            {
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
        // Features Actions for Features menu
        getDigitalClockFeature().addActionListener(action -> clock.changeToDigitalClockPanel());
        getAnalogueClockFeature().addActionListener(action -> clock.changeToAnalogueClockPanel());
        getSetAlarms().addActionListener(action -> clock.changeToAlarmPanel(true));
        getTimerFeature().addActionListener(action -> clock.changeToTimerPanel());
        // Add both menus to main menu
        add(getSettingsMenu());
        add(getFeaturesMenu());
        logger.info("Finished creating Clock menu bar");
    }
    // Getters
    public JMenu getSettingsMenu() { return this.settingsMenu; }
    public JMenu getFeaturesMenu() { return this.featuresMenu; }
    public JMenu getAlarmFeature_Menu() { return this.alarmFeature_Menu; }
    public JMenuItem getMilitaryTimeSetting() { return this.militaryTimeSetting; }
    public JMenuItem getFullTimeSetting() { return this.fullTimeSetting; }
    public JMenuItem getPartialTimeSetting() { return this.partialTimeSetting; }
    public JMenuItem getShowDigitalTimeOnAnalogueClockSetting() { return this.showDigitalTimeSettingOnAnalogueClockSetting; }
    public JMenuItem getDigitalClockFeature() { return this.digitalClockFeature; }
    public JMenuItem getAnalogueClockFeature() { return this.analogueClockFeature; }
    public JMenuItem getSetAlarms() { return this.setAlarms; }
    public JMenuItem getTimerFeature() { return this.timerFeature; }
    // Setters
    protected void setSettingsMenu(JMenu settingsMenu) { this.settingsMenu = settingsMenu; }
    protected void setFeaturesMenu(JMenu featuresMenu) { this.featuresMenu = featuresMenu; }
    protected void setAlarmFeature_Menu(JMenu alarmsMenu) { this.alarmFeature_Menu = alarmsMenu; }
    protected void setMilitaryTimeSetting(JMenuItem militaryTimeSetting) { this.militaryTimeSetting = militaryTimeSetting; }
    protected void setFullTimeSetting(JMenuItem fullTimeSetting) { this.fullTimeSetting = fullTimeSetting; }
    protected void setPartialTimeSetting(JMenuItem partialTimeSetting) { this.partialTimeSetting = partialTimeSetting; }
    protected void setShowDigitalTimeOnAnalogueClockSetting(JMenuItem showDigitalTimeSettingOnAnalogueClockSetting) { this.showDigitalTimeSettingOnAnalogueClockSetting = showDigitalTimeSettingOnAnalogueClockSetting; }
    protected void setDigitalClockFeature(JMenuItem digitalClockFeature) { this.digitalClockFeature = digitalClockFeature; }
    protected void setAnalogueClockFeature(JMenuItem analogueClockFeature) { this.analogueClockFeature = analogueClockFeature; }
    protected void setSetAlarms(JMenuItem setAlarms) { this.setAlarms = setAlarms; }
    protected void setTimerFeature(JMenuItem timerFeature) { this.timerFeature = timerFeature; }
    // class methods
    @Override
    protected void paintComponent(Graphics g)
    {
        logger.info("paintComponent");
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}
