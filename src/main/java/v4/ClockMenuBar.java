package v4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;

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
        setMilitaryTimeSetting(new JMenuItem(SHOW + SPACE + MILITARY_TIME_SETTING));
        getMilitaryTimeSetting().setForeground(Color.WHITE);

        setFullTimeSetting(new JMenuItem(SHOW + SPACE + FULL_TIME_SETTING));
        getFullTimeSetting().setForeground(Color.WHITE);

        setPartialTimeSetting(new JMenuItem(SHOW + SPACE + PARTIAL_TIME_SETTING));
        getPartialTimeSetting().setForeground(Color.WHITE);
        // Features menu choices
        setAlarmFeature_Menu(new JMenu("View Alarms"));
        setClockFeature(new JMenuItem("View Clock"));
        getClockFeature().setAccelerator(KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));

        setSetAlarms(new JMenuItem("Set Alarms"));
        getSetAlarms().setAccelerator(KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        // Add options to Settings Menu
        getSettingsMenu().add(getMilitaryTimeSetting());
        getSettingsMenu().add(getFullTimeSetting());
        getSettingsMenu().add(getPartialTimeSetting());
        // Add options to Features Menu
        getFeaturesMenu().add(getClockFeature());
        getFeaturesMenu().add(getAlarmFeature_Menu());
        getAlarmFeature_Menu().add(getSetAlarms());

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
    // Setters
    protected void setSettingsMenu(JMenu settingsMenu) { this.settingsMenu = settingsMenu; }
    protected void setFeaturesMenu(JMenu featuresMenu) { this.featuresMenu = featuresMenu; }
    protected void setAlarmFeature_Menu(JMenu alarmsMenu) { this.alarmFeature_Menu = alarmsMenu; }
    protected void setMilitaryTimeSetting(JMenuItem militaryTimeSetting) { this.militaryTimeSetting = militaryTimeSetting; }
    protected void setFullTimeSetting(JMenuItem fullTimeSetting) { this.fullTimeSetting = fullTimeSetting; }
    protected void setPartialTimeSetting(JMenuItem partialTimeSetting) { this.partialTimeSetting = partialTimeSetting; }
    protected void setClockFeature(JMenuItem clockFeature) { this.clockFeature = clockFeature; }
    protected void setSetAlarms(JMenuItem setAlarms) { this.setAlarms = setAlarms; }
    // class methods
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}
