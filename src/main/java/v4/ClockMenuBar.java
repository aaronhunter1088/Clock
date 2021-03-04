package v4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static v4.Clock.*;

/** The menu bar for the Clock.
 *
 * Alarms are working. Adding days.
 *
 * Timers are coming soon!!
 *
 * @author Michael Ball
 * @version 4
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
    // Constructor
    public ClockMenuBar()
    {
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        // Menu options
        setSettingsMenu(new JMenu("Settings"));
        setFeaturesMenu(new JMenu("Features"));
        // Settings menu choices
        setMilitaryTimeSetting(new JMenuItem(ClockConstants.SHOW + ClockConstants.SPACE + ClockConstants.MILITARY_TIME_SETTING));
        getMilitaryTimeSetting().setMnemonic('M');
        getMilitaryTimeSetting().setForeground(Color.WHITE);
        setFullTimeSetting(new JMenuItem(ClockConstants.SHOW + ClockConstants.SPACE + ClockConstants.FULL_TIME_SETTING));
        getFullTimeSetting().setMnemonic('F');
        getFullTimeSetting().setForeground(Color.WHITE);
        setPartialTimeSetting(new JMenuItem(ClockConstants.SHOW + ClockConstants.SPACE + ClockConstants.PARTIAL_TIME_SETTING));
        getPartialTimeSetting().setMnemonic('P');
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
