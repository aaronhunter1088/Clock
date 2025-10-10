package clock.entity;

import clock.panel.ClockFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.event.ActionEvent;
import java.time.DayOfWeek;
import java.util.List;

import static clock.util.Constants.*;
import static java.time.DayOfWeek.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link ClockMenuBar} class
 *
 * @author Michael Ball
 * @version 2.9
 */
public class ClockMenuBarTests
{
    private static final Logger logger = LogManager.getLogger(ClockMenuBarTests.class);

    private ClockFrame clockFrame;

    @Mock
    private ActionEvent actionEvent;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting {}...", ClockMenuBarTests.class.getSimpleName());
    }

    @BeforeEach
    void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        clockFrame = new ClockFrame();
    }

    @AfterEach
    void afterEach()
    {}

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", ClockMenuBarTests.class.getSimpleName()); }

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
        "true, 'Resume All Timers'",
        "false, 'Pause All Timers'"
    })
    void testTogglePauseResumeAllTimers(boolean paused, String settingText)
    {
        Timer timer = new clock.entity.Timer(0, 0, 1, NAME, clockFrame.getClock());
        timer.setPaused(paused);
        clockFrame.getClock().getListOfTimers().add(timer);
        clockFrame.getClockMenuBar().togglePauseResumeAllTimersSetting(actionEvent);
        // we need to have the setting set to the opposite setting
        if (!paused) {
            clockFrame.getClockMenuBar().togglePauseResumeAllTimersSetting(actionEvent);
        }
        clockFrame.getClock().getListOfTimers().forEach(t -> {
            assertEquals(paused, t.isPaused(), "Expecting " + paused);
        });
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
}
