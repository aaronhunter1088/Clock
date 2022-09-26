package Clock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import static Clock.ClockConstants.*;
import static Clock.ClockConstants.FULL_TIME_SETTING;

/**
 * The menu bar for the Clock.
 *
 * Option to stop Alarm coming in 2.5
 *
 * @author Michael Ball
 * @version 2.5
 */
public class ClockMenuBar extends JMenuBar {
    private static final long serialVersionUID = 1L;
    // Two main menu options
    protected JMenu settingsMenu;
    protected JMenu featuresMenu;
    // Options for Settings
    protected JMenuItem militaryTimeSetting;
    protected JMenuItem fullTimeSetting;
    protected JMenuItem partialTimeSetting;
    // Options for Features
    protected JMenuItem clockFeature;
    protected JMenu alarmFeature_Menu;
    protected JMenuItem timerFeature;
    // Options for alarmFeature_Menu
    protected JMenuItem setAlarms;

    public ClockMenuBar(Clock clock)
    {
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
        // Features menu choices
        setClockFeature(new JMenuItem("View Clock"));
        getClockFeature().setAccelerator(KeyStroke.getKeyStroke(
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
        getFeaturesMenu().add(getClockFeature());
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
        getClockFeature().setForeground(Color.WHITE);
        getTimerFeature().setForeground(Color.WHITE);
        getSetAlarms().setForeground(Color.WHITE);
        getSetAlarms().setBackground(Color.BLACK);
        // Set functionality
        // Settings Actions for Settings menu
        getMilitaryTimeSetting().addActionListener(action -> {
            if (clock.isShowMilitaryTime())
            {
                clock.setShowMilitaryTime(false);
                getMilitaryTimeSetting().setText(HIDE + SPACE + MILITARY_TIME_SETTING);
            }
            else
            {
                clock.setShowMilitaryTime(true);
                getMilitaryTimeSetting().setText(SHOW + SPACE + STANDARD_TIME_SETTING);
            }
        });
        getFullTimeSetting().addActionListener(action -> {
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
        // Features Actions for Features menu
        getClockFeature().addActionListener(action -> clock.changeToClockPanel());
        getSetAlarms().addActionListener(action -> clock.changeToAlarmPanel());
        getTimerFeature().addActionListener(action -> clock.changeToTimerPanel());
        // Add both menus to main menu
        add(getSettingsMenu());
        add(getFeaturesMenu());
    }
    // Getters
    public JMenu getSettingsMenu() { return this.settingsMenu; }
    public JMenu getFeaturesMenu() { return this.featuresMenu; }
    public JMenu getAlarmFeature_Menu() { return this.alarmFeature_Menu; }
    public JMenuItem getMilitaryTimeSetting() { return this.militaryTimeSetting; }
    public JMenuItem getFullTimeSetting() { return this.fullTimeSetting; }
    public JMenuItem getPartialTimeSetting() { return this.partialTimeSetting; }
    public JMenuItem getClockFeature() { return this.clockFeature; }
    public JMenuItem getSetAlarms() { return this.setAlarms; }
    public JMenuItem getTimerFeature() { return this.timerFeature; }
    // Setters
    protected void setSettingsMenu(JMenu settingsMenu) { this.settingsMenu = settingsMenu; }
    protected void setFeaturesMenu(JMenu featuresMenu) { this.featuresMenu = featuresMenu; }
    protected void setAlarmFeature_Menu(JMenu alarmsMenu) { this.alarmFeature_Menu = alarmsMenu; }
    protected void setMilitaryTimeSetting(JMenuItem militaryTimeSetting) { this.militaryTimeSetting = militaryTimeSetting; }
    protected void setFullTimeSetting(JMenuItem fullTimeSetting) { this.fullTimeSetting = fullTimeSetting; }
    protected void setPartialTimeSetting(JMenuItem partialTimeSetting) { this.partialTimeSetting = partialTimeSetting; }
    protected void setClockFeature(JMenuItem clockFeature) { this.clockFeature = clockFeature; }
    protected void setSetAlarms(JMenuItem setAlarms) { this.setAlarms = setAlarms; }
    protected void setTimerFeature(JMenuItem timerFeature) { this.timerFeature = timerFeature; }
    // class methods
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}
