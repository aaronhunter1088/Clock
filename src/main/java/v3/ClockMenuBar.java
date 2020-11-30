package v3;

import javax.swing.*;
import java.awt.*;

import static v3.Clock.*;

public class ClockMenuBar extends JMenuBar {
    private static final long serialVersionUID = 1L;
    protected JMenu settingsMenu;
    protected JMenu featuresMenu;
    protected JMenu alarmsMenu;
    protected JMenuItem militaryTimeSetting;
    protected JMenuItem fullTimeSetting;
    protected JMenuItem partialTimeSetting;
    protected JMenuItem clockFeature;
    protected JMenuItem setAlarms;

    public ClockMenuBar()
    {
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        setSettingsMenu(new JMenu("Settings"));
        setFeaturesMenu(new JMenu("Features"));
        setViewAlarmsMenu(new JMenu("View Alarms"));
        setClockFeature(new JMenuItem("View Clock"));
        setAlarms(new JMenuItem("Set Alarm"));

        setMilitaryTimeSetting(new JMenuItem(SHOW + SPACE + MILITARY_TIME_SETTING));
        getMilitaryTimeSetting().setForeground(Color.WHITE);
        setFullTimeSetting(new JMenuItem(SHOW + SPACE + FULL_TIME_SETTING));
        getFullTimeSetting().setForeground(Color.WHITE);
        setPartialTimeSetting(new JMenuItem(SHOW + SPACE + PARTIAL_TIME_SETTING));
        getPartialTimeSetting().setForeground(Color.WHITE);

        // Settings Menu
        getSettingsMenu().add(getMilitaryTimeSetting());
        getSettingsMenu().add(getFullTimeSetting());
        getSettingsMenu().add(getPartialTimeSetting());
        // Features Menu
        getFeaturesMenu().add(getClockFeature());
        getFeaturesMenu().add(getViewAlarmsMenu());
        getViewAlarmsMenu().add(getAlarms());

        getSettingsMenu().setOpaque(false);
        getSettingsMenu().setForeground(Color.WHITE);
        getFeaturesMenu().setOpaque(false);
        getFeaturesMenu().setForeground(Color.WHITE);
        // Menu Items for Settings
        // Menu Items for Features
        getViewAlarmsMenu().setOpaque(true);
        getViewAlarmsMenu().setForeground(Color.WHITE);
        getViewAlarmsMenu().setBackground(Color.BLACK);
        getClockFeature().setForeground(Color.WHITE);
        getAlarms().setForeground(Color.WHITE);
        getAlarms().setBackground(Color.BLACK);
    }

    public JMenu getSettingsMenu() { return this.settingsMenu; }
    public JMenu getFeaturesMenu() { return this.featuresMenu; }
    public JMenu getViewAlarmsMenu() { return this.alarmsMenu; }
    public JMenuItem getMilitaryTimeSetting() { return this.militaryTimeSetting; }
    public JMenuItem getFullTimeSetting() { return this.fullTimeSetting; }
    public JMenuItem getPartialTimeSetting() { return this.partialTimeSetting; }
    public JMenuItem getClockFeature() { return this.clockFeature; }
    public JMenuItem getAlarms() { return this.setAlarms; }

    protected void setSettingsMenu(JMenu settingsMenu) { this.settingsMenu = settingsMenu; }
    protected void setFeaturesMenu(JMenu featuresMenu) { this.featuresMenu = featuresMenu; }
    protected void setViewAlarmsMenu(JMenu alarmsMenu) { this.alarmsMenu = alarmsMenu; }
    protected void setMilitaryTimeSetting(JMenuItem militaryTimeSetting) { this.militaryTimeSetting = militaryTimeSetting; }
    protected void setFullTimeSetting(JMenuItem fullTimeSetting) { this.fullTimeSetting = fullTimeSetting; }
    protected void setPartialTimeSetting(JMenuItem partialTimeSetting) { this.partialTimeSetting = partialTimeSetting; }
    protected void setClockFeature(JMenuItem clockFeature) { this.clockFeature = clockFeature; }
    protected void setAlarms(JMenuItem setAlarms) { this.setAlarms = setAlarms; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}
