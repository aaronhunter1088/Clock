package clock.entity;

import clock.panel.AlarmPanel;
import clock.panel.ClockFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.Serial;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static clock.util.Constants.*;
import static clock.entity.Panel.*;

/**
 * The menu bar for the Clock.
 *
 * @author michael ball
 * @version since 1.0
 */
public class ClockMenuBar extends JMenuBar implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;
    /** The logger */
    private static final Logger logger = LogManager.getLogger(ClockMenuBar.class);
    /** Reference to the clock frame */
    private transient ClockFrame clockFrame;
    /** Reference to the clock */
    private transient Clock clock;

    // Menu Options
    /** Settings menu */
    private transient JMenu settingsMenu;
    /** Change timezone submenu */
    private transient JMenu changeTimeZoneMenuSetting;
    /** List of default timezones (american) */
    private transient List<JMenuItem> timezones;
    /** Feature menu */
    private transient JMenu featuresMenu;
    /** Help menu */
    private transient JMenu helpMenu;

    // Options for Settings
    /** Show/Hide military time (digital only) */
    private transient JMenuItem militaryTimeSetting;
    /** Show/Hide full time (digital only) */
    private transient JMenuItem fullTimeSetting;
    /** Show/Hide partial time (digital only) */
    private transient JMenuItem partialTimeSetting;
    /** Turn off/on daylight savings time */
    private transient JMenuItem toggleDSTSetting;
    /** Show/Hide date and time on analogue clock (analogue clock only) */
    private transient JMenuItem showDateAndTimeOnAnalogueClockSetting;

    /** Pause/Resume all alarms (alarms only) */
    private transient JMenuItem pauseResumeAllAlarmsSetting;
    /** Reset alarms panel (alarms only) */
    private transient JMenuItem resetAlarmsPanelSetting;

    /** Pause/Resume all timers (timers only) */
    private transient JMenuItem pauseResumeAllTimersSetting;
    /** Reset timers panel (timers only) */
    private transient JMenuItem resetTimersPanelSetting;

    /** Show/Hide analogue clock or digital clock on the stopwatch panel (stopwatches only) */
    private transient JMenuItem showAnalogueTimePanel;
    /** Reverse laps (stopwatches only) */
    private transient JMenuItem reverseLapsSetting;
    /** Reset stopwatch panel (stopwatches only) */
    private transient JMenuItem resetStopwatchesPanelSetting;
    /** Reset laps for the current stopwatch (stopwatches only) */
    private transient JMenuItem resetLapsSetting;
    /** Reset laps for all stopwatches (stopwatches only) */
    private transient JMenuItem resetAllLapsSetting;

    // Options for Features
    /** Show the digital clock panel feature */
    private transient JMenuItem digitalClockFeature;
    /** Show the analogue clock panel feature */
    private transient JMenuItem analogueClockFeature;
    /** Show the alarms panel feature */
    private transient JMenuItem alarmsFeature;
    /** Show the timers panel feature */
    private transient JMenuItem timerFeature;
    /** Show the stopwatches panel feature */
    private transient JMenuItem stopwatchFeature;

    // Options for Help
    /** Show the help panel feature */
    private transient JMenuItem helpFeature;

    /**
     * The main constructor for the clock menu bar.
     * It creates a Settings and Features menu options,
     * each with several items to choose from.
     *
     * @param clockFrame the parent ClockFrame this menu bar is attached to
     */
    public ClockMenuBar(ClockFrame clockFrame)
    {
        logger.info("Creating Clock menubar");
        setClockFrame(clockFrame);
        setClock(clockFrame.getClock());
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        // Menu options
        setSettingsMenu(new JMenu(SETTINGS));
        getSettingsMenu().setName(SETTINGS);
        setFeaturesMenu(new JMenu(FEATURES));
        getFeaturesMenu().setName(FEATURES);
        setupHelpMenu(new JMenu(HELP));
        getTheHelpMenu().setName(HELP);
        // Settings menu choices
        if (clock.isShowMilitaryTime()) {
            setMilitaryTimeSetting(new JMenuItem(SHOW + SPACE + STANDARD_TIME_SETTING));
            getMilitaryTimeSetting().setName("Displays the Time in Standard Time. CTRL + M. Ex: 08:50:00 AM");
        } else {
            setMilitaryTimeSetting(new JMenuItem(SHOW + SPACE + MILITARY_TIME_SETTING));
            getMilitaryTimeSetting().setName("Displays the Time in Military Time. CTRL + M. Ex: 0850 hours 30");
        }
        getMilitaryTimeSetting().setForeground(Color.WHITE);
        getMilitaryTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
        getMilitaryTimeSetting().addActionListener(this::toggleMilitaryTimeSetting);

        setFullTimeSetting(new JMenuItem(SHOW + SPACE + FULL_TIME_SETTING));
        getFullTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        getFullTimeSetting().setForeground(Color.WHITE);
        getFullTimeSetting().setName("Display the Time in Full Time. Updates Partial Time Setting. CTRL + F. Ex: FRIDAY, JUNE 12, 2026");
        getFullTimeSetting().addActionListener(this::toggleShowFullTimeSetting);

        setPartialTimeSetting(new JMenuItem(SHOW + SPACE + PARTIAL_TIME_SETTING));
        getPartialTimeSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        getPartialTimeSetting().setForeground(Color.WHITE);
        getPartialTimeSetting().setName("Display Time in Partial Time. Updates Full Time Setting. CTRL + P. Ex: FRI JUN 12, 2026");
        getPartialTimeSetting().addActionListener(this::togglePartialTimeSetting);

        setToggleDSTSetting(new JMenuItem(Turn + SPACE + off + SPACE + DST_SETTING));
        getToggleDSTSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.SHIFT_DOWN_MASK));
        getToggleDSTSetting().setForeground(Color.WHITE);
        getToggleDSTSetting().setName("Turn off/on Daylight Savings Time. Shift + T.");
        getToggleDSTSetting().addActionListener(this::toggleDSTSetting);

        setShowDateAndTimeOnAnalogueClockSetting(new JMenuItem(HIDE + SPACE + DATE.toLowerCase() + SPACE + AND.toLowerCase() + SPACE + TIME.toLowerCase()));
        getShowDateAndTimeOnAnalogueClockSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        getShowDateAndTimeOnAnalogueClockSetting().setForeground(Color.WHITE);
        getShowDateAndTimeOnAnalogueClockSetting().setName("Show or hide the digital time on the analogue clock panel. CTRL + E.");
        getShowDateAndTimeOnAnalogueClockSetting().addActionListener(this::toggleDateAndTimeOnAnalogueClockSetting);

        setChangeTimeZoneMenu(new JMenu(CHANGE + SPACE + TIME_ZONES));
        setTimeZones(new ArrayList<>()
        {{
            add(new JMenuItem(HAWAII));
            add(new JMenuItem(ALASKA));
            add(new JMenuItem(PACIFIC));
            add(new JMenuItem(CENTRAL));
            add(new JMenuItem(EASTERN));
            add(new JMenuItem(MOUNTAIN));
        }});
        getTimezones().forEach(this::setupTimezone);
        JMenuItem userTimezone = new JMenuItem(SELECT_ZONE_ID);
        userTimezone.setName("User Time Zone");
        userTimezone.setForeground(Color.WHITE);
        userTimezone.setBackground(Color.BLACK);
        userTimezone.addActionListener(_ -> {
            final ZoneOption[] zoneOptions = ZoneId.SHORT_IDS.entrySet().stream()
                    .map(entry -> new ZoneOption(
                            ZoneId.of(entry.getValue()).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                            entry.getKey()
                    ))
                    .sorted(Comparator.comparing(ZoneOption::displayName))
                    .toArray(ZoneOption[]::new);

            final JComboBox<ZoneOption> comboBox = new JComboBox<>(zoneOptions);
            final JTextField rawInput = new JTextField("Ex: Africa/Cairo, Europe/London, UTC", 20);
            rawInput.setToolTipText("Ex: Africa/Cairo, Europe/London, UTC");

            final JPanel panel = new JPanel(new GridLayout(0, 2, 5, 8));
            panel.add(new JLabel("Select from list:"));
            panel.add(comboBox);
            panel.add(new JLabel("Or enter a zone ID:"));
            panel.add(rawInput);

            final int result = JOptionPane.showConfirmDialog(
                    this, panel, CHANGE + SPACE + TIME_ZONES,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String raw = rawInput.getText().trim();
            if (raw.contains("Ex:")) raw = EMPTY;
            if (!raw.isBlank()) {
                try {
                    ZoneId.of(raw); // validate before doing anything
                    final JMenuItem tzItem = new JMenuItem(ZoneId.of(raw).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                    tzItem.setName(raw);
                    clockFrame.updateClockTimezone(tzItem);
                }
                catch (DateTimeException e) {
                    logger.warn("Invalid zone ID entered: '{}'. No change made.", raw);
                }
            } else {
                final ZoneOption selected = (ZoneOption) comboBox.getSelectedItem();
                if (selected != null) {
                    final JMenuItem tzItem = new JMenuItem(selected.key());
                    tzItem.setName(ZoneId.SHORT_IDS.get(selected.key()));
                    clockFrame.updateClockTimezone(tzItem);
                    tzItem.setText(selected.displayName() + SPACE + STAR);
                }
            }
        });
        getChangeTimeZoneMenu().add(userTimezone);
        getChangeTimeZoneMenu().setName(
                """
                        Changes the timezone to the selected choice and updates the time.
                        You can choose from a predefined list of timezones, displayed using
                        the timezone's display name, or enter a timezone, like Africa/Cairo
                        or America/Chicago. If the value given is valid, the time will be
                        updated and a new timezone will be added to the list to choose from.
                        """
        );
        setCurrentTimeZone();

        setPauseResumeAllTimersSetting(new JMenuItem(PAUSE + SPACE + ALL + SPACE + TIMER + S.toLowerCase()));
        getPauseResumeAllTimersSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        getPauseResumeAllTimersSetting().setForeground(Color.WHITE);
        getPauseResumeAllTimersSetting().setName("Pause/Resume All Timers. CTRL + P.");
        getPauseResumeAllTimersSetting().addActionListener(this::togglePauseResumeAllTimersSetting);

        setResetTimersPanelSetting(new JMenuItem(RESET + SPACE + PANEL));
        getResetTimersPanelSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        getResetTimersPanelSetting().setForeground(Color.WHITE);
        getResetTimersPanelSetting().setName("Clears the Timer Panel completely. CTRL + R.");
        getResetTimersPanelSetting().addActionListener(this::toggleResetTimersPanelSetting);

        setPauseResumeAllAlarmsSetting(new JMenuItem(PAUSE + SPACE + ALL + SPACE + ALARM + S.toLowerCase()));
        getPauseResumeAllAlarmsSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        getPauseResumeAllAlarmsSetting().setForeground(Color.WHITE);
        getPauseResumeAllAlarmsSetting().setName("Pause/Resume All Alarms. CTRL + P.");
        getPauseResumeAllAlarmsSetting().addActionListener(this::togglePauseResumeAllAlarmsSetting);

        setResetAlarmsPanelSetting(new JMenuItem(RESET + SPACE + PANEL));
        getResetAlarmsPanelSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        getResetAlarmsPanelSetting().setForeground(Color.WHITE);
        getResetAlarmsPanelSetting().setName("Clears the Alarms Panel completely. CTRL + R.");
        getResetAlarmsPanelSetting().addActionListener(this::toggleResetAlarmsPanelSetting);

        setShowAnalogueTimePanel(new JMenuItem(SHOW + SPACE + ANALOGUE + SPACE + TIME));
        getShowAnalogueTimePanel().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.SHIFT_DOWN_MASK));
        //getShowAnalogueTimePanel().setAccelerator(null); // T is used for View Timers
        getShowAnalogueTimePanel().setForeground(Color.WHITE);
        getShowAnalogueTimePanel().setName("Show the analogue clock panel. Shift + C.");
        getShowAnalogueTimePanel().addActionListener(this::toggleTimePanels);

        setReverseLaps(new JMenuItem(REVERSE + SPACE + LAPS));
        getReverseLaps().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK)); // F for Flip
        getReverseLaps().setForeground(Color.WHITE);
        getReverseLaps().setName("Toggles the order of the laps displayed from ascending to descending or back the other way. CTRL + F.");
        getReverseLaps().addActionListener(this::toggleReverseLapsSetting);

        setResetLapsSetting(new JMenuItem(RESET + SPACE + LAPS));
        getResetLapsSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        getResetLapsSetting().setForeground(Color.WHITE);
        getResetLapsSetting().setName("Clears the Laps Panel for the current stopwatch completely. CTRL + L.");
        getResetLapsSetting().addActionListener(_ -> clockFrame.getStopwatchPanel().resetLapsPanel());

        setResetAllLapsSetting(new JMenuItem(RESET + SPACE + ALL + SPACE + LAPS));
        getResetAllLapsSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.SHIFT_DOWN_MASK));
        getResetAllLapsSetting().setForeground(Color.WHITE);
        getResetAllLapsSetting().setName("Clears the Laps Panel for all stopwatches completely. Shift + L.");
        getResetAllLapsSetting().addActionListener(_ -> clockFrame.getStopwatchPanel().resetAllLapsPanel());

        setResetStopwatchesPanelSetting(new JMenuItem(RESET + SPACE + PANEL));
        getResetStopwatchesPanelSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        getResetStopwatchesPanelSetting().setForeground(Color.WHITE);
        getResetStopwatchesPanelSetting().setName("Clears the Stopwatches Panel completely. CTRL + R.");
        getResetStopwatchesPanelSetting().addActionListener(_ -> clockFrame.getStopwatchPanel().resetStopwatchPanel());

        // Features menu choices
        setDigitalClockFeature(new JMenuItem(VIEW_DIGITAL_CLOCK));
        getDigitalClockFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        getDigitalClockFeature().setName("View the digital clock panel. CTRL + D.");
        getDigitalClockFeature().addActionListener(_ -> clockFrame.changePanels(PANEL_DIGITAL_CLOCK));

        setAnalogueClockFeature(new JMenuItem(VIEW_ANALOGUE_CLOCK));
        getAnalogueClockFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        getAnalogueClockFeature().setName("View the analogue clock panel. CTRL + C.");
        getAnalogueClockFeature().addActionListener(_ -> clockFrame.changePanels(PANEL_ANALOGUE_CLOCK));

        setAlarmsFeature(new JMenuItem(VIEW_ALARMS));
        getAlarmsFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        getAlarmsFeature().setName("View the alarms panel. CTRL + A.");
        getAlarmsFeature().addActionListener(_ -> clockFrame.changePanels(PANEL_ALARM));

        setTimerFeature(new JMenuItem(VIEW_TIMERS));
        getTimerFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        getTimerFeature().setName("View the timers panel. CTRL + T.");
        getTimerFeature().addActionListener(_ -> clockFrame.changePanels(PANEL_TIMER));

        setStopwatchFeature(new JMenuItem(VIEW_STOPWATCHES));
        getStopwatchFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        getStopwatchFeature().setName("View the stopwatch panel. CTRL + S.");
        getStopwatchFeature().addActionListener(_ -> clockFrame.changePanels(PANEL_STOPWATCH));

        // Help Menu
        setTheHelpFeature(new JMenuItem(VIEW_HELP));
        getTheHelpFeature().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        getTheHelpFeature().setForeground(Color.WHITE);
        getTheHelpFeature().setName("This is the help panel. It provides information about the current panel and how to " +
                "use the features and settings in the menu. CTRL + H.");
        getTheHelpFeature().addActionListener(this::performTheHelpMenuAction);
        // Add options to Features Menu, consistent for each panel
        getFeaturesMenu().add(getDigitalClockFeature());
        getFeaturesMenu().add(getAnalogueClockFeature());
        getFeaturesMenu().add(getAlarmsFeature());
        getFeaturesMenu().add(getTimerFeature());
        getFeaturesMenu().add(getStopwatchFeature());
        // Setup Settings Menu, options are unique by each panel* (mostly unique)
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
        // Setup Help Menu
        getTheHelpMenu().setOpaque(false);
        getTheHelpMenu().setForeground(Color.WHITE);
        getTheHelpMenu().setBackground(Color.BLACK);
        getTheHelpMenu().add(helpFeature);
        // Menu Items for Settings and Features
        getDigitalClockFeature().setForeground(Color.WHITE);
        getAnalogueClockFeature().setForeground(Color.WHITE);
        getTimerFeature().setForeground(Color.WHITE);
        getStopwatchFeature().setForeground(Color.WHITE);
        // Add both menus to main menu
        add(getSettingsMenu());
        add(getFeaturesMenu());
        add(getTheHelpMenu());
        logger.info("Finished creating Clock menubar");
    }

    /**
     * Display the text for About Calculator menu item
     *
     * @param action the click action
     */
    private void performTheHelpMenuAction(ActionEvent action)
    {
        JTextArea message = new JTextArea(getHelpText(), 10, 30);
        message.setWrapStyleWord(true);
        message.setEditable(false);
        message.setOpaque(false);
        message.setLineWrap(true);
        message.setFont(UIManager.getFont("Label.font"));

        JScrollPane scrollPane = new JScrollPane(message, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setSize(new Dimension(400, 300));
        SwingUtilities.updateComponentTreeUI(this);
        String headerText = "Viewing " + clockFrame.getPanelType().getText() + SPACE + HELP;
        JOptionPane.showMessageDialog(this, scrollPane, headerText, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Returns the help text for the current panel.
     * This method builds the help text for each
     * setting and feature in the menu bar and any
     * new option that comes later.
     *
     * @return the help text for the current panel
     */
    public String getHelpText()
    {
        JMenuBar menuBar = clockFrame.getClockMenuBar();
        List<JMenu> menus = Arrays.stream(menuBar.getComponents()).filter(c -> c instanceof JMenu).map(c -> (JMenu) c).toList();

        // Determine the panel-specific description once, based on the active panel.
        String panelSpecificText = switch (clockFrame.getPanelType()) {
            case PANEL_DIGITAL_CLOCK -> """
                    The digital clock displays the date and time. There are two different ways to view the date.
                    You can turn off or on daylight savings time. When off, when daylight savings would take
                    effect, this clock will not and the regular time will proceed. When on by default, daylight
                    savings time will take place.
                    You can also adjust the timezone to any other USA recognized timezone for the United States.
                    """;
            case PANEL_ANALOGUE_CLOCK -> """
                    The analogue clock displays the time with hour, minute, and second hands. You can choose to show
                    or hide the digital date and time on the analogue clock panel. You can also turn on or off
                    daylight savings time and change the timezone.
                    """;
            case PANEL_ALARM -> """
                    Use the alarms panel to set multiple alarms. You can choose to pause and resume all
                    alarms, or reset the entire panel. When an alarm goes off, you can choose to snooze or dismiss
                    the alarm. Snoozing will snooze the alarm for 7 minutes. If you do not hit snooze, and a
                    minute has gone by, the alarm will automatically begin snoozing for you. Edit an alarm
                    by clicking on the Sleeping .zZ button.
                    """;
            case PANEL_TIMER -> """
                    Use the timers panel to set multiple timers. You can choose to pause and resume all
                    timers, or reset the entire panel. When a timer goes off, you can choose to restart or dismiss
                    the timer. Restarting will restart the timer for you with the same time that you originally
                    set it for. If you do not hit restart, and a minute has gone by, the timer will automatically
                    begin restarting for you.
                    """;
            case PANEL_STOPWATCH -> """
                    Use the stopwatches panel to set multiple stopwatches. You can choose to show or hide the
                    analogue clock panel, which will show the time with hour, minute, and second hands. You can
                    also reverse the order of the laps displayed in the laps panel from ascending to descending
                    or back the other way. You can reset the laps for a single stopwatch or for all stopwatches.
                    You can also reset the entire stopwatches panel.
                    """;
        };

        // Build the menu listing — one line per item showing its label and its help name.
        StringBuilder menuAndItemsText = new StringBuilder();
        for (JMenu menu : menus) {
            String nameOfMenu = menu.getName(); // Settings, Features, Help
            menuAndItemsText.append(NEWLINE).append(nameOfMenu).append(COLON).append(NEWLINE);

            List<JMenuItem> allMenuItems = Arrays.stream(menu.getMenuComponents())
                    .filter(c -> c instanceof JMenuItem)
                    .map(c -> (JMenuItem) c)
                    .toList();
            for (JMenuItem menuItem : allMenuItems) {
                menuAndItemsText.append(menuItem.getText())
                        .append(COLON).append(SPACE)
                        .append(menuItem.getName())
                        .append(NEWLINE);
            }
        }

        return """
                Panel: %s
                %s
                %s
                """.formatted(
                clockFrame.getPanelType().getText(),
                panelSpecificText,
                menuAndItemsText.toString()
        );
    }

    /**
     * Sets up the timezone menu item
     *
     * @param timezone the timezone menu item to set up
     */
    public void setupTimezone(JMenuItem timezone)
    {
        logger.debug("setup timezone for {}", timezone.getText());
        timezone.addActionListener(_ -> clockFrame.updateClockTimezone(timezone));
        timezone.setForeground(Color.WHITE);
        timezone.setBackground(Color.BLACK);
        timezone.setName(timezone.getText());
        getChangeTimeZoneMenu().add(timezone);
    }

    /**
     * Updates the text on the currently selected timezone
     * so that it's clear which timezone is currently selected.
     */
    public void setCurrentTimeZone()
    {
        timezones.forEach(menuItem -> {
            final String cleanText = menuItem.getText().replace(STAR, EMPTY).trim();
            final String resolutionKey = (menuItem.getName() != null && !menuItem.getName().isBlank())
                    ? menuItem.getName() : cleanText;
            final ZoneId menuZoneId = clock.getZoneIdFromTimezoneButtonText(resolutionKey);
            if (clock.getTimezone().equals(menuZoneId)) {
                if (!menuItem.getText().contains(STAR)) {
                    menuItem.setText(cleanText + SPACE + STAR);
                } else {
                    logger.info("selected timezone already has *");
                    logger.info("no change to timezone: {}", clock.getPlainTimezoneFromZoneId(clock.getTimezone()));
                }
            } else {
                menuItem.setText(cleanText);
            }
        });
    }

    /**
     * Paints the background of the menu bar black.
     *
     * @param g the graphics object
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        logger.debug("paint component");
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    /**
     * Toggles the military time setting.
     *
     * @param action the triggering ActionEvent (unused)
     */
    protected void toggleMilitaryTimeSetting(ActionEvent action)
    {
        logger.debug("clicked show military time setting");
        clock.setShowMilitaryTime(!clock.isShowMilitaryTime());
        if (clock.isShowMilitaryTime()) {
            getMilitaryTimeSetting().setText(SHOW + SPACE + STANDARD_TIME_SETTING);
            getMilitaryTimeSetting().setName("Displays the Time in Standard Time. CTRL + M. Ex: 08:50:00 AM");
        } else {
            getMilitaryTimeSetting().setText(SHOW + SPACE + MILITARY_TIME_SETTING);
            getMilitaryTimeSetting().setName("Displays the Time in Military Time. CTRL + M. Ex: 0850 hours 30");

        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * Toggles the full time setting.
     *
     * @param action the triggering ActionEvent (unused)
     */
    protected void toggleShowFullTimeSetting(ActionEvent action)
    {
        logger.debug("clicked show full time setting");
        if (clock.isShowFullDate()) {
            getFullTimeSetting().setText(SHOW + SPACE + FULL_TIME_SETTING);
        } else {
            getFullTimeSetting().setText(HIDE + SPACE + FULL_TIME_SETTING);
        }
        clock.setShowFullDate(!clock.isShowFullDate());
        clock.setShowPartialDate(false);
        getPartialTimeSetting().setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
    }

    /**
     * Toggles the partial time setting.
     *
     * @param action the triggering ActionEvent (unused)
     */
    protected void togglePartialTimeSetting(ActionEvent action)
    {
        logger.debug("clicked show partial time setting");
        if (clock.isShowPartialDate()) {
            getPartialTimeSetting().setText(SHOW + SPACE + PARTIAL_TIME_SETTING);
        } else {
            getPartialTimeSetting().setText(HIDE + SPACE + PARTIAL_TIME_SETTING);
        }
        clock.setShowPartialDate(!clock.isShowPartialDate());
        clock.setShowFullDate(false);
        getFullTimeSetting().setText(SHOW + SPACE + FULL_TIME_SETTING);
    }

    /**
     * Toggles the Daylight Savings Time setting.
     *
     * @param action the triggering ActionEvent (unused)
     */
    protected void toggleDSTSetting(ActionEvent action)
    {
        var isEnabled = clock.isDaylightSavingsTimeEnabled();
        logger.debug("toggling dst to be {}", !isEnabled);
        clock.setDaylightSavingsTimeEnabled(!isEnabled);
        getToggleDSTSetting().setText(Turn + SPACE + (clock.isDaylightSavingsTimeEnabled() ? off : on) + SPACE + DST_SETTING);
        logger.debug("setting text: '{}'", getToggleDSTSetting().getText());
    }

    /**
     * Toggles the digital time on the analogue clock setting.
     *
     * @param action the triggering ActionEvent (unused)
     */
    protected void toggleDateAndTimeOnAnalogueClockSetting(ActionEvent action)
    {
        logger.debug("clicked toggle date and time on analogue clock setting");
        boolean showingDateAndTime = clockFrame.getAnalogueClockPanel().isShowDateAndTimeOnAnalogueClock();
        if (showingDateAndTime) {
            getShowDateAndTimeOnAnalogueClockSetting().setText(SHOW + SPACE + DATE.toLowerCase() + SPACE + AND.toLowerCase() + SPACE + TIME.toLowerCase());
        } else {
            getShowDateAndTimeOnAnalogueClockSetting().setText(HIDE + SPACE + DATE.toLowerCase() + SPACE + AND.toLowerCase() + SPACE + TIME.toLowerCase());
        }
        clockFrame.getAnalogueClockPanel().setShowDateAndTimeOnAnalogueClock(!showingDateAndTime);
        clockFrame.getAnalogueClockPanel().repaint();
    }

    /**
     * Toggles the pause/resume all timers setting.
     *
     * @param action the triggering ActionEvent (unused)
     */
    protected void togglePauseResumeAllTimersSetting(ActionEvent action)
    {
        if (clock.getListOfTimers().isEmpty()) {
            logger.debug("no timers to pause/resume");
        } else {
            logger.debug("clicked pause/resume all timers setting");
            if (getPauseResumeAllTimersSetting().getText().equals(PAUSE + SPACE + ALL + SPACE + TIMER + S.toLowerCase())) {
                clock.getListOfTimers().forEach(Timer::pauseTimer);
                getPauseResumeAllTimersSetting().setText(RESUME + SPACE + ALL + SPACE + TIMER + S.toLowerCase());
                getPauseResumeAllTimersSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
            } else {
                clock.getListOfTimers().forEach(Timer::resumeTimer);
                getPauseResumeAllTimersSetting().setText(PAUSE + SPACE + ALL + SPACE + TIMER + S.toLowerCase());
                getPauseResumeAllTimersSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
            }
        }
    }

    /**
     * Toggles the reset timers panel setting.
     *
     * @param action the triggering ActionEvent (unused)
     */
    protected void toggleResetTimersPanelSetting(ActionEvent action)
    {
        logger.debug("clicked reset timers panel setting");
        clockFrame.getTimerPanel().resetTimerPanel();
    }

    /**
     * Toggles the pause/resume all alarms setting.
     *
     * @param action the triggering ActionEvent (unused)
     */
    protected void togglePauseResumeAllAlarmsSetting(ActionEvent action)
    {
        if (clock.getListOfAlarms().isEmpty()) {
            logger.debug("no alarms to pause/resume");
        } else {
            logger.debug("clicked pause/resume all alarms setting");
            if (getPauseResumeAllAlarmsSetting().getText().equals(PAUSE + SPACE + ALL + SPACE + ALARM + S.toLowerCase())) {
                clock.getListOfAlarms().forEach(Alarm::pauseAlarm);
                getPauseResumeAllAlarmsSetting().setText(RESUME + SPACE + ALL + SPACE + ALARM + S.toLowerCase());
                getPauseResumeAllAlarmsSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
            } else {
                clock.getListOfAlarms().forEach(Alarm::resumeAlarm);
                getPauseResumeAllAlarmsSetting().setText(PAUSE + SPACE + ALL + SPACE + ALARM + S.toLowerCase());
                getPauseResumeAllAlarmsSetting().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
            }
        }
    }

    /**
     * Toggles the reset alarms panel setting.
     *
     * @param action the triggering ActionEvent (unused)
     */
    protected void toggleResetAlarmsPanelSetting(ActionEvent action)
    {
        logger.debug("clicked reset alarms panel setting");
        AlarmPanel alarmPanel = clockFrame.getAlarmPanel();
        alarmPanel.resetAlarmPanel();
        alarmPanel.resetTableAndMenu();
    }

    /**
     * Toggles between digital and analogue panels on the stopwatch panel.
     *
     * @param action the triggering ActionEvent (unused)
     */
    protected void toggleTimePanels(ActionEvent action)
    {
        clockFrame.getStopwatchPanel().toggleStopwatchClockPanel();
        if (!clockFrame.getStopwatchPanel().getDisplayTimePanel().isShowAnaloguePanel()) {
            getShowAnalogueTimePanel().setText(SHOW + SPACE + ANALOGUE + SPACE + "Time");
            getShowAnalogueTimePanel().setName("Show the analogue clock panel. Shift + C.");
        } else {
            getShowAnalogueTimePanel().setText(SHOW + SPACE + DIGITAL + SPACE + "Time");
            getShowAnalogueTimePanel().setName("Show the digital clock panel. Shift + C.");
        }
    }

    /**
     * Toggles the reverse laps setting for the stopwatch panel.
     *
     * @param action the triggering ActionEvent (unused)
     */
    protected void toggleReverseLapsSetting(ActionEvent action)
    {
        logger.debug("clicked reverse laps setting");
        if (!clockFrame.getStopwatchPanel().getDisplayLapsPanel().isLapsReversed) {
            clockFrame.getStopwatchPanel().getDisplayLapsPanel().isLapsReversed = true;
            getReverseLaps().setText(RESTORE + SPACE + LAPS);
        } else {
            clockFrame.getStopwatchPanel().getDisplayLapsPanel().isLapsReversed = false;
            getReverseLaps().setText(REVERSE + SPACE + LAPS);
        }
        clockFrame.getStopwatchPanel().getDisplayLapsPanel().updateLabelsAndStopwatchTable();
        repaint();
    }

    /**
     * Returns the settings menu
     *
     * @return the settings menu
     */
    public JMenu getSettingsMenu()
    {
        return this.settingsMenu;
    }

    /**
     * Returns the features menu
     *
     * @return the features menu
     */
    public JMenu getFeaturesMenu()
    {
        return this.featuresMenu;
    }

    /**
     * Returns the help menu
     *
     * @return the help menu
     */
    public JMenu getTheHelpMenu()
    {
        return this.helpMenu;
    }

    /**
     * Returns the alarms feature menu item
     *
     * @return the alarms feature menu item
     */
    public JMenuItem getAlarmsFeature()
    {
        return this.alarmsFeature;
    }

    /**
     * Returns the military time setting menu item
     *
     * @return the military time setting menu item
     */
    public JMenuItem getMilitaryTimeSetting()
    {
        return this.militaryTimeSetting;
    }

    /**
     * Returns the full time setting menu item
     *
     * @return the full time setting menu item
     */
    public JMenuItem getFullTimeSetting()
    {
        return this.fullTimeSetting;
    }

    /**
     * Returns the partial time setting menu item
     *
     * @return the partial time setting menu item
     */
    public JMenuItem getPartialTimeSetting()
    {
        return this.partialTimeSetting;
    }

    /**
     * Returns the toggle DST setting menu item
     *
     * @return the toggle DST setting menu item
     */
    public JMenuItem getToggleDSTSetting()
    {
        return toggleDSTSetting;
    }

    /**
     * Returns the pause/resume all timers setting menu item
     *
     * @return the pause/resume all timers setting menu item
     */
    public JMenuItem getPauseResumeAllTimersSetting()
    {
        return pauseResumeAllTimersSetting;
    }

    /**
     * Returns the reset timers panel setting menu item
     *
     * @return the reset timers panel setting menu item
     */
    public JMenuItem getResetTimersPanelSetting()
    {
        return resetTimersPanelSetting;
    }

    /**
     * Returns the pause/resume all alarms setting menu item
     *
     * @return the pause/resume all alarms setting menu item
     */
    public JMenuItem getPauseResumeAllAlarmsSetting()
    {
        return pauseResumeAllAlarmsSetting;
    }

    /**
     * Returns the reset alarms panel setting menu item
     *
     * @return the reset alarms panel setting menu item
     */
    public JMenuItem getResetAlarmsPanelSetting()
    {
        return resetAlarmsPanelSetting;
    }

    /**
     * Returns the show date and time on analogue clock setting menu item
     *
     * @return the show date and time on analogue clock setting menu item
     */
    public JMenuItem getShowDateAndTimeOnAnalogueClockSetting()
    {
        return this.showDateAndTimeOnAnalogueClockSetting;
    }

    /**
     * Returns the show analogue time panel menu item
     *
     * @return the show analogue time panel menu item
     */
    public JMenuItem getShowAnalogueTimePanel()
    {
        return this.showAnalogueTimePanel;
    }

    /**
     * Returns the reverse laps setting menu item
     *
     * @return the reverse laps setting menu item
     */
    public JMenuItem getReverseLaps()
    {
        return this.reverseLapsSetting;
    }

    /**
     * Returns the change time zone menu
     *
     * @return the change time zone sub-menu
     */
    public JMenu getChangeTimeZoneMenu()
    {
        return this.changeTimeZoneMenuSetting;
    }

    /**
     * Returns the list of timezone menu items
     *
     * @return the list of timezone menu items
     */
    public java.util.List<JMenuItem> getTimezones()
    {
        return this.timezones;
    }

    /**
     * Returns the digital clock feature menu item
     *
     * @return the digital clock feature menu item
     */
    public JMenuItem getDigitalClockFeature()
    {
        return this.digitalClockFeature;
    }

    /**
     * Returns the analogue clock feature menu item
     *
     * @return the analogue clock feature menu item
     */
    public JMenuItem getAnalogueClockFeature()
    {
        return this.analogueClockFeature;
    }

    /**
     * Returns the timer feature menu item
     *
     * @return the timer feature menu item
     */
    public JMenuItem getTimerFeature()
    {
        return this.timerFeature;
    }

    /**
     * Returns the stopwatch feature menu item
     *
     * @return the stopwatch feature menu item
     */
    public JMenuItem getStopwatchFeature()
    {
        return this.stopwatchFeature;
    }

    /**
     * Returns the help feature menu item
     *
     * @return the help feature menu item
     */
    public JMenuItem getTheHelpFeature()
    {
        return this.helpFeature;
    }

    /**
     * Returns the clock frame
     *
     * @return the clock frame reference
     */
    public ClockFrame getClockFrame()
    {
        return this.clockFrame;
    }

    /**
     * Returns the clock
     *
     * @return the clock reference
     */
    public Clock getClock()
    {
        return this.clock;
    }

    /**
     * Returns the reset all laps setting
     *
     * @return resetAllLapsSeting
     */
    public JMenuItem getResetAllLapsSetting()
    {
        return resetAllLapsSetting;
    }

    /**
     * Return the reset stopwatches panel setting
     *
     * @return resetStopwatchesPanelSetting
     */
    public JMenuItem getResetStopwatchesPanelSetting()
    {
        return resetStopwatchesPanelSetting;
    }

    /**
     * Return the reset laps setting
     *
     * @return resetLapsSetting
     */
    public JMenuItem getResetLapsSetting()
    {
        return resetLapsSetting;
    }

    /**
     * Sets the settings menu
     *
     * @param settingsMenu the settings menu to set
     */
    protected void setSettingsMenu(JMenu settingsMenu)
    {
        this.settingsMenu = settingsMenu;
        logger.debug("settings menu");
    }

    /**
     * Sets the features menu
     *
     * @param featuresMenu the features menu to set
     */
    protected void setFeaturesMenu(JMenu featuresMenu)
    {
        this.featuresMenu = featuresMenu;
        logger.debug("features menu");
    }

    /**
     * Sets the help menu item
     *
     * @param helpMenu the help menu to set
     */
    protected void setupHelpMenu(JMenu helpMenu)
    {
        this.helpMenu = helpMenu;
        logger.debug("help menu");
    }

    /**
     * Sets the alarms feature menu item
     *
     * @param alarmsFeature the alarm menu item to set
     */
    protected void setAlarmsFeature(JMenuItem alarmsFeature)
    {
        this.alarmsFeature = alarmsFeature;
        logger.debug("alarms feature");
    }

    /**
     * Sets the military time setting menu item
     *
     * @param militaryTimeSetting the military time setting menu item to set
     */
    protected void setMilitaryTimeSetting(JMenuItem militaryTimeSetting)
    {
        this.militaryTimeSetting = militaryTimeSetting;
        logger.debug("military time setting");
    }

    /**
     * Sets the full time setting menu item
     *
     * @param fullTimeSetting the full time setting menu item to set
     */
    protected void setFullTimeSetting(JMenuItem fullTimeSetting)
    {
        this.fullTimeSetting = fullTimeSetting;
        logger.debug("full time setting");
    }

    /**
     * Sets the partial time setting menu item
     *
     * @param partialTimeSetting the partial time setting menu item to set
     */
    protected void setPartialTimeSetting(JMenuItem partialTimeSetting)
    {
        this.partialTimeSetting = partialTimeSetting;
        logger.debug("partial time setting");
    }

    /**
     * Sets the toggle DST setting menu item
     *
     * @param toggleDSTSetting the toggle DST setting menu item to set
     */
    protected void setToggleDSTSetting(JMenuItem toggleDSTSetting)
    {
        this.toggleDSTSetting = toggleDSTSetting;
        logger.debug("toggle dst setting");
    }

    /**
     * Sets the pause/resume all timers setting menu item
     *
     * @param pauseResumeAllTimersSetting the pause/resume all timers setting menu item to set
     */
    protected void setPauseResumeAllTimersSetting(JMenuItem pauseResumeAllTimersSetting)
    {
        this.pauseResumeAllTimersSetting = pauseResumeAllTimersSetting;
        logger.debug("pause/resume all timers setting");
    }

    /**
     * Sets the reset timers panel setting menu item
     *
     * @param resetTimersPanelSetting the reset timers panel setting menu item to set
     */
    protected void setResetTimersPanelSetting(JMenuItem resetTimersPanelSetting)
    {
        this.resetTimersPanelSetting = resetTimersPanelSetting;
        logger.debug("reset timers panel setting");
    }

    /**
     * Sets the pause/resume all alarms setting menu item
     *
     * @param pauseResumeAllAlarmsSetting the pause/resume all alarms setting menu item to set
     */
    protected void setPauseResumeAllAlarmsSetting(JMenuItem pauseResumeAllAlarmsSetting)
    {
        this.pauseResumeAllAlarmsSetting = pauseResumeAllAlarmsSetting;
        logger.debug("pause/resume all alarms setting");
    }

    /**
     * Sets the reset alarms panel setting menu item
     *
     * @param resetAlarmsPanelSetting the reset alarms panel setting menu item to set
     */
    protected void setResetAlarmsPanelSetting(JMenuItem resetAlarmsPanelSetting)
    {
        this.resetAlarmsPanelSetting = resetAlarmsPanelSetting;
        logger.debug("reset alarms panel setting");
    }

    /**
     * Sets the show date and time on analogue clock setting menu item
     *
     * @param showDateAndTimeOnAnalogueClockSetting the show date and time on analogue clock setting menu item to set
     */
    protected void setShowDateAndTimeOnAnalogueClockSetting(JMenuItem showDateAndTimeOnAnalogueClockSetting)
    {
        this.showDateAndTimeOnAnalogueClockSetting = showDateAndTimeOnAnalogueClockSetting;
        logger.debug("show date and time on analogue clock setting");
    }

    /**
     * Sets the show analogue time panel menu item
     *
     * @param showAnalogueTimePanel the show analogue time panel menu item to set
     */
    protected void setShowAnalogueTimePanel(JMenuItem showAnalogueTimePanel)
    {
        this.showAnalogueTimePanel = showAnalogueTimePanel;
        logger.debug("show analogue time panel");
    }

    /**
     * Sets the change time zone menu
     *
     * @param changeTimeZone the change time zone menu to set
     */
    protected void setChangeTimeZoneMenu(JMenu changeTimeZone)
    {
        this.changeTimeZoneMenuSetting = changeTimeZone;
        logger.debug("change time zone menu");
    }

    /**
     * Sets the digital clock feature menu item
     *
     * @param digitalClockFeature the digital clock feature menu item to set
     */
    protected void setDigitalClockFeature(JMenuItem digitalClockFeature)
    {
        this.digitalClockFeature = digitalClockFeature;
        logger.debug("digital clock feature");
    }

    /**
     * Sets the analogue clock feature menu item
     *
     * @param analogueClockFeature the analogue clock feature menu item to set
     */
    protected void setAnalogueClockFeature(JMenuItem analogueClockFeature)
    {
        this.analogueClockFeature = analogueClockFeature;
        logger.debug("analogue clock feature");
    }

    /**
     * Sets the reverse laps setting menu item
     *
     * @param reverseLapsSetting the reverse laps setting menu item to set
     */
    protected void setReverseLaps(JMenuItem reverseLapsSetting)
    {
        this.reverseLapsSetting = reverseLapsSetting;
        logger.debug("reverse laps setting");
    }

    /**
     * Sets the list of timezone menu items
     *
     * @param timezones the list of timezone menu items to set
     */
    protected void setTimeZones(java.util.List<JMenuItem> timezones)
    {
        this.timezones = timezones;
        logger.debug("timezones list");
    }

    /**
     * Sets the timer feature menu item
     *
     * @param timerFeature the timer feature menu item to set
     */
    protected void setTimerFeature(JMenuItem timerFeature)
    {
        this.timerFeature = timerFeature;
        logger.debug("timer feature");
    }

    /**
     * Sets the stopwatch feature menu item
     *
     * @param stopwatchFeature the stopwatch feature menu item to set
     */
    protected void setStopwatchFeature(JMenuItem stopwatchFeature)
    {
        this.stopwatchFeature = stopwatchFeature;
        logger.debug("stopwatch feature");
    }

    /**
     * Sets the help feature menu item
     *
     * @param helpFeature the help feature menu item to set
     */
    protected void setTheHelpFeature(JMenuItem helpFeature)
    {
        this.helpFeature = helpFeature;
        logger.debug("help feature");
    }

    /**
     * Sets the clock frame
     *
     * @param clockFrame the clock frame to set
     */
    protected void setClockFrame(ClockFrame clockFrame)
    {
        this.clockFrame = clockFrame;
        logger.debug("clock frame");
    }

    /**
     * Sets the clock
     *
     * @param clock the clock to set
     */
    protected void setClock(Clock clock)
    {
        this.clock = clock;
        logger.debug("clock");
    }

    /**
     * Sets the reset all laps setting
     *
     * @param resetAllLapsSetting the reset all laps menu item to set
     */
    public void setResetAllLapsSetting(JMenuItem resetAllLapsSetting)
    {
        this.resetAllLapsSetting = resetAllLapsSetting;
    }

    /**
     * Sets the reset laps setting
     *
     * @param resetLapsSetting the reset laps menu item to set
     */
    public void setResetLapsSetting(JMenuItem resetLapsSetting)
    {
        this.resetLapsSetting = resetLapsSetting;
    }

    /**
     * Sets the reset stopwatches panel setting
     *
     * @param resetStopwatchesPanelSetting the reset stopwatches menu item to set
     */
    public void setResetStopwatchesPanelSetting(JMenuItem resetStopwatchesPanelSetting)
    {
        this.resetStopwatchesPanelSetting = resetStopwatchesPanelSetting;
    }

    /**
     * A display-only wrapper for a SHORT_IDS timezone entry.
     * {@link JOptionPane} calls {@code toString()} on each entry in the selection list,
     * so the readable name is shown while the raw key is retained for clock updates.
     *
     * @param displayName the human-readable timezone name (e.g. "Central Time")
     * @param key         the raw SHORT_IDS key (e.g. "CST")
     */
    private record ZoneOption(String displayName, String key)
    {
        @Override
        public String toString()
        {
            return displayName;
        }
    }
}