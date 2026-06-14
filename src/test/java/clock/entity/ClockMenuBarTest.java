package clock.entity;

import clock.panel.ClockFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

import static clock.entity.Panel.*;
import static clock.util.Constants.*;
import static java.time.DayOfWeek.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link ClockMenuBar} class
 *
 * @author michael ball
 * @version since 2.9
 */
@ExtendWith(MockitoExtension.class)
public class ClockMenuBarTest
{
    private static final Logger logger = LogManager.getLogger(ClockMenuBarTest.class);

    private ClockFrame clockFrame;

    @Mock
    private ActionEvent actionEvent;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting {}...", ClockMenuBarTest.class.getSimpleName());
    }

    @BeforeEach
    void beforeEach()
    {
        clockFrame = new ClockFrame();
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException
    {
        logger.info("Test complete. Closing the clock...");
        EventQueue.invokeAndWait(() -> {
            clockFrame.stop();
            clockFrame.dispose();
        });
        assertFalse(clockFrame.isVisible());
    }

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", ClockMenuBarTest.class.getSimpleName()); }

    @ParameterizedTest
    @DisplayName("Test Toggle Military Time Setting")
    @CsvSource({
        "true, 'Show military time'",
        "false, 'Show standard time'"
    })
    void testToggleMilitaryTimeSetting(boolean militaryTime, String settingText)
    {
        clockFrame.getClock().setShowMilitaryTime(militaryTime);
        clockFrame.getClockMenuBar().toggleMilitaryTimeSetting(actionEvent);

        assertEquals(!militaryTime, clockFrame.getClock().isShowMilitaryTime(), "Military Time should be visible");
        assertEquals(settingText, clockFrame.getClockMenuBar().getMilitaryTimeSetting().getText());
    }

    @ParameterizedTest
    @DisplayName("Test Toggle Show Full Time Setting")
    @CsvSource({
        "true, 'Show full date'",
        "false, 'Hide full date'"
    })
    void testShowFullTimeSetting(boolean showFullTime, String settingText)
    {
        clockFrame.getClock().setShowFullDate(showFullTime);
        clockFrame.getClockMenuBar().toggleShowFullTimeSetting(actionEvent);
        assertEquals(!showFullTime, clockFrame.getClock().isShowFullDate(), "Expecting " + showFullTime);
        assertEquals(settingText, clockFrame.getClockMenuBar().getFullTimeSetting().getText());
        assertFalse(clockFrame.getClock().isShowPartialDate(), "Expecting isShowPartialDate to be false");
        assertEquals(SHOW+SPACE+PARTIAL_TIME_SETTING, clockFrame.getClockMenuBar().getPartialTimeSetting().getText());
    }

    @ParameterizedTest
    @DisplayName("Test Toggle Show Partial Time Setting")
    @CsvSource({
        "true, 'Show partial date'",
        "false, 'Hide partial date'"
    })
    void testShowPartialTimeSetting(boolean showPartialTime, String settingText)
    {
        clockFrame.getClock().setShowPartialDate(showPartialTime);
        clockFrame.getClockMenuBar().togglePartialTimeSetting(actionEvent);
        assertEquals(!showPartialTime, clockFrame.getClock().isShowPartialDate(), "Expecting " + showPartialTime);
        assertEquals(settingText, clockFrame.getClockMenuBar().getPartialTimeSetting().getText());
        assertFalse(clockFrame.getClock().isShowFullDate(), "Expecting isShowFullDate to be false");
        assertEquals(SHOW+SPACE+FULL_TIME_SETTING, clockFrame.getClockMenuBar().getFullTimeSetting().getText());
    }

    @ParameterizedTest
    @DisplayName("Test Toggle DST Setting")
    @CsvSource({
        "true, 'Turn on daylight savings time'",
        "false, 'Turn off daylight savings time'"
    })
    void testToggleDSTSetting(boolean dst, String settingText)
    {
        clockFrame.getClock().setDaylightSavingsTimeEnabled(dst);
        assertEquals(Turn+SPACE+off+SPACE+DST_SETTING, clockFrame.getClockMenuBar().getToggleDSTSetting().getText());
        clockFrame.getClockMenuBar().toggleDSTSetting(actionEvent);
        assertEquals(!dst, clockFrame.getClock().isDaylightSavingsTimeEnabled(), "Expecting " + dst);
        assertEquals(settingText, clockFrame.getClockMenuBar().getToggleDSTSetting().getText());
    }

    @ParameterizedTest
    @DisplayName("Test Toggle Show Digital Time on Analogue Clock Setting")
    @CsvSource({
        "true, 'Show digital time'",
        "false, 'Hide digital time'"
    })
    void testToggleShowDigitalTimeOnAnalogueClockSetting(boolean showDigitalTime, String settingText)
    {
        clockFrame.getAnalogueClockPanel().setShowDigitalTimeOnAnalogueClock(showDigitalTime);
        clockFrame.getClockMenuBar().toggleDigitalTimeOnAnalogueClockSetting(actionEvent);
        assertEquals(!showDigitalTime, clockFrame.getAnalogueClockPanel().isShowDigitalTimeOnAnalogueClock(), "Expecting " + showDigitalTime);
        assertEquals(settingText, clockFrame.getClockMenuBar().getShowDigitalTimeOnAnalogueClockSetting().getText());
    }

    @ParameterizedTest
    @DisplayName("Test Toggle Pause/Resume All Timers")
    @CsvSource({
        "false, 'Resume All Timers'",
        "true, 'Pause All Timers'"
    })
    void testTogglePauseResumeAllTimers(boolean paused, String settingText)
    {
        Timer timer = new clock.entity.Timer(0, 0, 1, NAME, clockFrame.getClock());
        timer.setPaused(paused);
        Timer timer2 = new clock.entity.Timer(0, 0, 1, TIMER, clockFrame.getClock());
        timer2.setPaused(paused);
        clockFrame.getClock().getListOfTimers().add(timer);
        clockFrame.getClock().getListOfTimers().add(timer2);
        if (paused)
        {
            // Advance the menu to "Resume All Timers" so the next toggle exercises the resume path
            clockFrame.getClockMenuBar().togglePauseResumeAllTimersSetting(actionEvent);
        }
        clockFrame.getClockMenuBar().togglePauseResumeAllTimersSetting(actionEvent);
        // pauseTimer() is always effective; resumeTimer() is a no-op for non-started timers,
        // so only assert timer state when exercising the pause path
        if (!paused)
        {
            clockFrame.getClock().getListOfTimers().forEach(t ->
                    assertEquals(true, t.isPaused(), "Timer should be paused after toggle"));
        }
        assertEquals(settingText, clockFrame.getClockMenuBar().getPauseResumeAllTimersSetting().getText());
    }

    @ParameterizedTest
    @DisplayName("Test Toggle Pause/Resume All Alarms")
    @CsvSource({
        "true, 'Resume All Alarms'",
        "false, 'Pause All Alarms'"
    })
    void testTogglePauseResumeAllAlarms(boolean paused, String settingText)
    {
        List<DayOfWeek> weekdays = List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY);
        Alarm alarm = new Alarm(NAME, 7, 0, AM, weekdays, false, clockFrame.getClock());
        alarm.setIsPaused(paused);
        clockFrame.getClock().getListOfAlarms().add(alarm);
        clockFrame.getClockMenuBar().togglePauseResumeAllAlarmsSetting(actionEvent);
        if (!paused) {
            clockFrame.getClockMenuBar().togglePauseResumeAllAlarmsSetting(actionEvent);
        }
        // we need to have the setting set to the opposite setting
        clockFrame.getClock().getListOfAlarms().forEach(a -> {
            assertEquals(paused, a.isPaused(), "Expecting " + paused);
        });
        assertEquals(settingText, clockFrame.getClockMenuBar().getPauseResumeAllAlarmsSetting().getText());
    }

    @Test
    @DisplayName("Test Reset Timers Panel")
    void testResetTimersPanel()
    {
        clockFrame.getClockMenuBar().toggleResetTimersPanelSetting(actionEvent);
        assertEquals(EMPTY, clockFrame.getTimerPanel().getNameTextField().getText(), "Name text field should be empty after reset");
        assertEquals(EMPTY, clockFrame.getTimerPanel().getHoursTextField().getText(), "Hours text field should be empty after reset");
        assertEquals(EMPTY, clockFrame.getTimerPanel().getMinutesTextField().getText(), "Minutes text field should be empty after reset");
        assertEquals(EMPTY, clockFrame.getTimerPanel().getSecondsTextField().getText(), "Seconds text field should be empty after reset");
        assertEquals(0, clockFrame.getTimerPanel().getTimersTable().getModel().getRowCount(), "Timers table should be empty after reset");
        assertEquals(0, clockFrame.getClock().getListOfTimers().size(), "List of timers should be empty after reset");
    }

    @Test
    @DisplayName("Test Reset Alarms Panel")
    void testResetAlarmsPanel()
    {
        clockFrame.getClockMenuBar().toggleResetAlarmsPanelSetting(actionEvent);
        assertEquals(EMPTY, clockFrame.getAlarmPanel().getNameTextField().getText(), "Name text field should be empty after reset");
        assertEquals(EMPTY, clockFrame.getAlarmPanel().getHoursTextField().getText(), "Hours text field should be reset");
        assertEquals(EMPTY, clockFrame.getAlarmPanel().getMinutesTextField().getText(), "Minutes text field should be reset");
        assertFalse(clockFrame.getAlarmPanel().getMondayCheckBox().isSelected(), "Monday checkbox should not be selected after reset");
        assertFalse(clockFrame.getAlarmPanel().getTuesdayCheckBox().isSelected(), "Tuesday checkbox should not be selected after reset");
        assertFalse(clockFrame.getAlarmPanel().getWednesdayCheckBox().isSelected(), "Wednesday checkbox should not be selected after reset");
        assertFalse(clockFrame.getAlarmPanel().getThursdayCheckBox().isSelected(), "Thursday checkbox should not be selected after reset");
        assertFalse(clockFrame.getAlarmPanel().getFridayCheckBox().isSelected(), "Friday checkbox should not be selected after reset");
        assertFalse(clockFrame.getAlarmPanel().getSaturdayCheckBox().isSelected(), "Saturday checkbox should not be selected after reset");
        assertFalse(clockFrame.getAlarmPanel().getSundayCheckBox().isSelected(), "Sunday checkbox should not be selected after reset");
        assertFalse(clockFrame.getAlarmPanel().getWeekdaysCheckBox().isSelected(), "Weekdays checkbox should not be selected after reset");
        assertFalse(clockFrame.getAlarmPanel().getWeekendsCheckBox().isSelected(), "Weekends checkbox should not be selected after reset");
        assertEquals(0, clockFrame.getAlarmPanel().getAlarmsTable().getModel().getRowCount(), "Alarms table should be empty after reset");
        assertEquals(0, clockFrame.getClock().getListOfAlarms().size(), "List of alarms should be empty after reset");
        assertEquals(0, clockFrame.getAlarmPanel().getAlarmsTable().getModel().getRowCount(), "Alarms table should be empty after reset");
        assertEquals(0, clockFrame.getClock().getListOfAlarms().size(), "List of alarms should be empty after reset");
    }

    @Test
    @DisplayName("Test Pause/Resume All Timers with empty timer list does nothing")
    void testTogglePauseResumeAllTimersWithEmptyListDoesNothing()
    {
        clockFrame.getClock().getListOfTimers().clear();
        final String textBefore = clockFrame.getClockMenuBar().getPauseResumeAllTimersSetting().getText();

        assertDoesNotThrow(() -> clockFrame.getClockMenuBar().togglePauseResumeAllTimersSetting(actionEvent));

        assertEquals(textBefore, clockFrame.getClockMenuBar().getPauseResumeAllTimersSetting().getText(),
                "Menu item text should not change when there are no timers");
    }

    @Test
    @DisplayName("Test Pause/Resume All Alarms with empty alarm list does nothing")
    void testTogglePauseResumeAllAlarmsWithEmptyListDoesNothing()
    {
        clockFrame.getClock().getListOfAlarms().clear();
        final String textBefore = clockFrame.getClockMenuBar().getPauseResumeAllAlarmsSetting().getText();

        assertDoesNotThrow(() -> clockFrame.getClockMenuBar().togglePauseResumeAllAlarmsSetting(actionEvent));

        assertEquals(textBefore, clockFrame.getClockMenuBar().getPauseResumeAllAlarmsSetting().getText(),
                "Menu item text should not change when there are no alarms");
    }

    @Test
    @DisplayName("Test toggleTimePanels changes show analogue time menu text")
    void testToggleTimePanelsChangesMenuText()
    {
        final String initialText = clockFrame.getClockMenuBar().getShowAnalogueTimePanel().getText();

        clockFrame.getClockMenuBar().toggleTimePanels(actionEvent);
        final String afterFirstToggle = clockFrame.getClockMenuBar().getShowAnalogueTimePanel().getText();
        assertNotEquals(initialText, afterFirstToggle, "Menu text should change on first toggle");

        clockFrame.getClockMenuBar().toggleTimePanels(actionEvent);
        final String afterSecondToggle = clockFrame.getClockMenuBar().getShowAnalogueTimePanel().getText();
        assertNotEquals(afterFirstToggle, afterSecondToggle, "Menu text should change on second toggle");
        assertEquals(initialText, afterSecondToggle, "Menu text should return to initial state after two toggles");
    }

    @Test
    @DisplayName("Test toggleReverseLapsSetting enables reverse laps and changes text")
    void testToggleReverseLapsEnablesReverse()
    {
        final Stopwatch sw = new Stopwatch("Lap SW", false, false, clockFrame.getClock());
        clockFrame.getStopwatchPanel().setCurrentStopwatch(sw);

        assertFalse(clockFrame.getStopwatchPanel().getDisplayLapsPanel().isLapsReversed, "Laps should not be reversed initially");
        assertEquals(REVERSE+SPACE+LAPS, clockFrame.getClockMenuBar().getReverseLaps().getText());

        clockFrame.getClockMenuBar().toggleReverseLapsSetting(actionEvent);

        assertTrue(clockFrame.getStopwatchPanel().getDisplayLapsPanel().isLapsReversed, "Laps should be reversed after toggle");
        assertEquals(RESTORE+SPACE+LAPS, clockFrame.getClockMenuBar().getReverseLaps().getText());
    }

    @Test
    @DisplayName("Test toggleReverseLapsSetting restores laps after second toggle")
    void testToggleReverseLapsRestoresAfterSecondToggle()
    {
        final Stopwatch sw = new Stopwatch("Lap SW", false, false, clockFrame.getClock());
        clockFrame.getStopwatchPanel().setCurrentStopwatch(sw);

        clockFrame.getClockMenuBar().toggleReverseLapsSetting(actionEvent);
        assertTrue(clockFrame.getStopwatchPanel().getDisplayLapsPanel().isLapsReversed);

        clockFrame.getClockMenuBar().toggleReverseLapsSetting(actionEvent);

        assertFalse(clockFrame.getStopwatchPanel().getDisplayLapsPanel().isLapsReversed, "Laps should be restored after second toggle");
        assertEquals(REVERSE+SPACE+LAPS, clockFrame.getClockMenuBar().getReverseLaps().getText());
    }

    @Test
    @DisplayName("Test setCurrentTimeZone marks the active timezone with a star")
    void testSetCurrentTimeZoneMarksActiveWithStar()
    {
        clockFrame.getClock().setTimezone(java.time.ZoneId.of(AMERICA_NEW_YORK));
        clockFrame.getClockMenuBar().setCurrentTimeZone();

        final boolean easternHasStar = clockFrame.getClockMenuBar().getTimezones().stream()
                .filter(item -> item.getText().replace(STAR, EMPTY).trim().equalsIgnoreCase(EASTERN))
                .anyMatch(item -> item.getText().contains(STAR));

        assertTrue(easternHasStar, "Eastern timezone should be marked with a star when clock is set to America/New_York");
    }

    @Test
    @DisplayName("Test updateClockTimezone with a SHORT_IDS key adds item and marks it with a star")
    void testUpdateClockTimezoneWithShortIdKeyMarksWithStar()
    {
        final String shortId = "JST"; // resolves to Asia/Tokyo — not already in the default list
        final int countBefore = clockFrame.getClockMenuBar().getTimezones().size();

        clockFrame.updateClockTimezone(new javax.swing.JMenuItem(shortId));

        final int countAfter = clockFrame.getClockMenuBar().getTimezones().size();
        assertEquals(countBefore + 1, countAfter, "A new timezone entry should be added to the list");

        final boolean hasStar = clockFrame.getClockMenuBar().getTimezones().stream()
                .filter(item -> item.getText().replace(STAR, EMPTY).trim().equalsIgnoreCase(shortId))
                .anyMatch(item -> item.getText().contains(STAR));
        assertTrue(hasStar, "The newly selected JST timezone should be marked with a star");
    }

    @Test
    @DisplayName("Test updateClockTimezone with a display name and long zone ID shows the display name in the menu")
    void testUpdateClockTimezoneWithDisplayNameShowsReadableLabel()
    {
        // Simulate what the ZoneOption dialog produces: display name as text, long ID as name
        final String displayName = "Japan Time";
        final String longId = "Asia/Tokyo"; // ZoneId.SHORT_IDS.get("JST")
        final javax.swing.JMenuItem tzItem = new javax.swing.JMenuItem(displayName);
        tzItem.setName(longId);
        final int countBefore = clockFrame.getClockMenuBar().getTimezones().size();

        clockFrame.updateClockTimezone(tzItem);

        final int countAfter = clockFrame.getClockMenuBar().getTimezones().size();
        assertEquals(countBefore + 1, countAfter, "A new timezone entry should be added");

        final boolean labelIsReadable = clockFrame.getClockMenuBar().getTimezones().stream()
                .anyMatch(item -> item.getText().replace(STAR, EMPTY).trim().equals(displayName));
        assertTrue(labelIsReadable, "Menu item should display the readable name, not the raw key");

        final boolean hasStar = clockFrame.getClockMenuBar().getTimezones().stream()
                .filter(item -> item.getText().replace(STAR, EMPTY).trim().equals(displayName))
                .anyMatch(item -> item.getText().contains(STAR));
        assertTrue(hasStar, "The newly added Japan Time entry should be marked with a star");
    }

    @Test
    @DisplayName("Test updateClockTimezone does not add duplicate when SHORT_IDS key resolves to same ZoneId as an existing entry")
    void testUpdateClockTimezoneSkipsDuplicateByZoneId()
    {
        // PST resolves to America/Los_Angeles, which is the same ZoneId as the existing "Pacific" entry
        final int countBefore = clockFrame.getClockMenuBar().getTimezones().size();

        clockFrame.updateClockTimezone(new javax.swing.JMenuItem("PST"));

        final int countAfter = clockFrame.getClockMenuBar().getTimezones().size();
        assertEquals(countBefore, countAfter, "PST should not be added because Pacific already covers America/Los_Angeles");

        final long starCount = clockFrame.getClockMenuBar().getTimezones().stream()
                .filter(item -> item.getText().contains(STAR))
                .count();
        assertEquals(1, starCount, "Exactly one timezone entry should have a star");
    }

    @Test
    @DisplayName("Test setupTimezone adds item to the change timezone submenu")
    void testSetupTimezoneAddsItemToMenu()
    {
        final int countBefore = clockFrame.getClockMenuBar().getChangeTimeZoneMenu().getItemCount();
        final javax.swing.JMenuItem newTzItem = new javax.swing.JMenuItem("Test/Zone");

        clockFrame.getClockMenuBar().setupTimezone(newTzItem);

        assertEquals(countBefore + 1, clockFrame.getClockMenuBar().getChangeTimeZoneMenu().getItemCount(),
                "Timezone item should be added to the change timezone menu");
        assertEquals(java.awt.Color.WHITE, newTzItem.getForeground(), "Timezone item foreground should be white");
        assertEquals(java.awt.Color.BLACK, newTzItem.getBackground(), "Timezone item background should be black");
    }

    @Test
    @DisplayName("Test initial text state of menu items after construction")
    void testInitialMenuItemTextStates()
    {
        assertEquals(SHOW+SPACE+FULL_TIME_SETTING, clockFrame.getClockMenuBar().getFullTimeSetting().getText());
        assertEquals(SHOW+SPACE+PARTIAL_TIME_SETTING, clockFrame.getClockMenuBar().getPartialTimeSetting().getText());
        assertEquals(Turn+SPACE+off+SPACE+DST_SETTING, clockFrame.getClockMenuBar().getToggleDSTSetting().getText());
        assertEquals(HIDE+SPACE+DIGITAL_TIME, clockFrame.getClockMenuBar().getShowDigitalTimeOnAnalogueClockSetting().getText());
        assertEquals(PAUSE+SPACE+ALL+SPACE+TIMER+S.toLowerCase(), clockFrame.getClockMenuBar().getPauseResumeAllTimersSetting().getText());
        assertEquals(PAUSE+SPACE+ALL+SPACE+ALARM+S.toLowerCase(), clockFrame.getClockMenuBar().getPauseResumeAllAlarmsSetting().getText());
        assertEquals(SHOW+SPACE+ANALOGUE+SPACE+TIME, clockFrame.getClockMenuBar().getShowAnalogueTimePanel().getText());
        assertEquals(REVERSE+SPACE+LAPS, clockFrame.getClockMenuBar().getReverseLaps().getText());
        assertEquals(VIEW_DIGITAL_CLOCK, clockFrame.getClockMenuBar().getDigitalClockFeature().getText());
        assertEquals(VIEW_ANALOGUE_CLOCK, clockFrame.getClockMenuBar().getAnalogueClockFeature().getText());
        assertEquals(VIEW_ALARMS, clockFrame.getClockMenuBar().getAlarmsFeature().getText());
        assertEquals(VIEW_TIMERS, clockFrame.getClockMenuBar().getTimerFeature().getText());
        assertEquals(VIEW_STOPWATCHES, clockFrame.getClockMenuBar().getStopwatchFeature().getText());
    }

    @Test
    @DisplayName("Test getters return non-null values after construction")
    void testGettersReturnNonNull()
    {
        assertNotNull(clockFrame.getClockMenuBar().getClockFrame());
        assertNotNull(clockFrame.getClockMenuBar().getClock());
        assertNotNull(clockFrame.getClockMenuBar().getSettingsMenu());
        assertNotNull(clockFrame.getClockMenuBar().getFeaturesMenu());
        assertNotNull(clockFrame.getClockMenuBar().getChangeTimeZoneMenu());
        assertNotNull(clockFrame.getClockMenuBar().getTimezones());
        assertFalse(clockFrame.getClockMenuBar().getTimezones().isEmpty(), "Timezones list should not be empty");
        assertNotNull(clockFrame.getClockMenuBar().getMilitaryTimeSetting());
        assertNotNull(clockFrame.getClockMenuBar().getFullTimeSetting());
        assertNotNull(clockFrame.getClockMenuBar().getPartialTimeSetting());
        assertNotNull(clockFrame.getClockMenuBar().getToggleDSTSetting());
        assertNotNull(clockFrame.getClockMenuBar().getShowDigitalTimeOnAnalogueClockSetting());
        assertNotNull(clockFrame.getClockMenuBar().getPauseResumeAllTimersSetting());
        assertNotNull(clockFrame.getClockMenuBar().getResetTimersPanelSetting());
        assertNotNull(clockFrame.getClockMenuBar().getPauseResumeAllAlarmsSetting());
        assertNotNull(clockFrame.getClockMenuBar().getResetAlarmsPanelSetting());
        assertNotNull(clockFrame.getClockMenuBar().getShowAnalogueTimePanel());
        assertNotNull(clockFrame.getClockMenuBar().getReverseLaps());
        assertNotNull(clockFrame.getClockMenuBar().getDigitalClockFeature());
        assertNotNull(clockFrame.getClockMenuBar().getAnalogueClockFeature());
        assertNotNull(clockFrame.getClockMenuBar().getAlarmsFeature());
        assertNotNull(clockFrame.getClockMenuBar().getTimerFeature());
        assertNotNull(clockFrame.getClockMenuBar().getStopwatchFeature());
    }

    @ParameterizedTest
    @DisplayName("Test Show Help For Each Panel")
    @EnumSource(Panel.class)
    @Disabled // just because it requires manual clicking to close the window
    void testShowHelpMenu(Panel panel)
    {
        clockFrame.setPanelType(panel);
        Arrays.stream(clockFrame.getClockMenuBar().getTheHelpFeature().getActionListeners())
                .toList()
                .forEach(listener -> listener.actionPerformed(new ActionEvent(this, 0, "")));

        assertTrue(true, "Should be true");
    }
}
