package clock.panel;

import clock.entity.Alarm;
import clock.entity.Clock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.event.ActionEvent;
import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Stream;

import static clock.entity.Panel.PANEL_ALARM;
import static clock.util.Constants.*;
import static java.time.DayOfWeek.*;
import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link AlarmPanel} class
 * This class has interactions that require
 * you to close windows in order for tests
 * to proceed.
 * @author michael ball
 * @version since 2.9
 */
class AlarmPanelTest
{
    private static final Logger logger = LogManager.getLogger(AlarmPanelTest.class);

    Clock clock;

    AlarmPanel alarmPanel;

    @BeforeAll
    static void beforeClass()
    {
        logger.info("Starting AlarmPanelTest...");
    }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock(11, 30, 0, JANUARY, WEDNESDAY, 1, 2025, AM); // 11:30 AM
        alarmPanel = new AlarmPanel(new ClockFrame(clock));
        alarmPanel.getClockFrame().changePanels(PANEL_ALARM);
    }

    @AfterEach
    void afterEach()
    {
        alarmPanel.getClockFrame().stop();
        alarmPanel.getClockFrame().dispose();
    }

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", AlarmPanelTest.class.getSimpleName()); }

    @Test
    @DisplayName("Alarm Panel starts with PM in dropdown when clock is in PM")
    void testAlarmPanelCreatedWithClockInPMStartsWithPMSelection()
    {
        clock = new Clock(12, 0, 0, JANUARY, WEDNESDAY, 1, 2025, PM); // 12:00 PM
        alarmPanel = new AlarmPanel(new ClockFrame(clock));
        assertEquals(PM ,alarmPanel.getAmpmDropDown().getSelectedItem(), "AlarmPanel should start with PM selected when clock is in PM");
    }

    @ParameterizedTest
    @DisplayName("Valid hours into hours text field returns expecting")
    @CsvSource({
        "0, true", // Valid: 0 is a valid hour, will convert to 12 in 12-hour format (not in the validate method though), stays 0 in 24-hour format
        "1, true", // Valid: 1 is a valid hour
        "2, true", // Valid: 2 is a valid hour
        "3, true", // Valid: 3 is a valid hour
        "4, true", // Valid: 4 is a valid hour
        "5, true", // Valid: 5 is a valid hour
        "6, true", // Valid: 6 is a valid hour
        "7, true", // Valid: 7 is a valid hour
        "8, true", // Valid: 8 is a valid hour
        "9, true", // Valid: 9 is a valid hour
        "10, true",// Valid: 10 is a valid hour
        "11, true",// Valid: 11 is a valid hour
        "12, true", // Valid: 12 is a valid hour

        "'', false", // Invalid: empty string is not a valid hour
        "-1, false", // Invalid: -1 is not a valid hour
        "13, false", // Invalid: 13+ is a valid hour in 24-hour format, but not in 12-hour format
        "24, false", // Invalid hour
        "g, false", // Invalid: g is not a valid hour
    })
    void testValidHoursTextField(String input, boolean expecting)
    {
        // set up
        alarmPanel.getMinutesTextField().setText(FIVE); // give it a valid minute so we can test hours independently
        // test
        alarmPanel.getHoursTextField().setText(input);
        assertEquals(expecting, alarmPanel.validateHoursTextField(), "Hours text field should be " + expecting + " for input: " + input);
    }

    @ParameterizedTest
    @DisplayName("Valid minute into minutes text field returns expecting")
    @CsvSource({
            "0, true", /* Valid: 0 is a valid minute */ "1, true", // Valid: 1 is a valid minute
            "2, true", /* Valid: 2 is a valid minute */ "3, true", // Valid: 3 is a valid minute
            "4, true", /* Valid: 4 is a valid minute */ "5, true", // Valid: 5 is a valid minute
            "6, true", /* Valid: 6 is a valid minute */ "7, true", // Valid: 7 is a valid minute
            "8, true", /* Valid: 8 is a valid minute */ "9, true", // Valid: 9 is a valid minute
            "10, true", /* Valid: 10 is a valid minute */ "11, true", // Valid: 11 is a valid minute
            "12, true", /* Valid: 12 is a valid minute */ "13, true", // Valid: 13 is a valid minute
            "14, true", /* Valid: 14 is a valid minute */ "15, true", // Valid: 15 is a valid minute
            "16, true", /* Valid: 16 is a valid minute */ "17, true", // Valid: 17 is a valid minute
            "18, true", /* Valid: 18 is a valid minute */ "19, true", // Valid: 19 is a valid minute
            "20, true", /* Valid: 20 is a valid minute */ "21, true", // Valid: 21 is a valid minute
            "22, true", /* Valid: 22 is a valid minute */ "23, true", // Valid: 23 is a valid minute
            "24, true", /* Valid: 24 is a valid minute */ "25, true", // Valid: 25 is a valid minute
            "26, true", /* Valid: 26 is a valid minute */ "27, true", // Valid: 27 is a valid minute
            "28, true", /* Valid: 28 is a valid minute */ "29, true", // Valid: 29 is a valid minute
            "30, true", /* Valid: 30 is a valid minute */ "31, true", // Valid: 31 is a valid minute
            "32, true", /* Valid: 32 is a valid minute */ "33, true", // Valid: 33 is a valid minute
            "34, true", /* Valid: 34 is a valid minute */ "35, true", // Valid: 35 is a valid minute
            "36, true", /* Valid: 36 is a valid minute */ "37, true", // Valid: 37 is a valid minute
            "38, true", /* Valid: 38 is a valid minute */ "39, true", // Valid: 39 is a valid minute
            "40, true", /* Valid: 40 is a valid minute */ "41, true", // Valid: 41 is a valid minute
            "42, true", /* Valid: 42 is a valid minute */ "43, true", // Valid: 43 is a valid minute
            "44, true", /* Valid: 44 is a valid minute */ "45, true", // Valid: 45 is a valid minute
            "46, true", /* Valid: 46 is a valid minute */ "47, true", // Valid: 47 is a valid minute
            "48, true", /* Valid: 48 is a valid minute */ "49, true", // Valid: 49 is a valid minute
            "50, true", /* Valid: 50 is a valid minute */ "51, true", // Valid: 51 is a valid minute
            "52, true", /* Valid: 52 is a valid minute */ "53, true", // Valid: 53 is a valid minute
            "54, true", /* Valid: 54 is a valid minute */ "55, true", // Valid: 55 is a valid minute
            "56, true", /* Valid: 56 is a valid minute */ "57, true", // Valid: 57 is a valid minute
            "58, true", /* Valid: 58 is a valid minute */ "59, true", // Valid: 59 is a valid minute

            "'', false", // empty string is not a valid minute
            "-1, false", // -1 is not a valid minute
            "-2, false", // -2 is not a valid minute
            "g, false", // g is not a valid minute
    })
    void testValidMinutesTextField(String input, boolean expecting)
    {
        // set up
        alarmPanel.getHoursTextField().setText(ONE); // give it a valid hour so we can test minutes independently
        // test
        alarmPanel.getMinutesTextField().setText(input);
        assertEquals(expecting, alarmPanel.validateMinutesTextField(), "Minutes text field should be " + expecting + " for input: " + input);
    }

    @ParameterizedTest
    @CsvSource({
            "'', '', ''",
            "' ', '', ''"
    })
    @DisplayName("Alarm Panel does not allow empty text fields")
    void testEmptyHourAndMinutesFieldsIsOk(String name, String minutes, String hours)
    {
        // set up
        alarmPanel.getNameTextField().setText(name);
        alarmPanel.getMinutesTextField().setText(minutes);
        alarmPanel.getHoursTextField().setText(hours);

        // assert
        assertFalse(alarmPanel.validateNameTextField(), "Should not allow empty name field");
        assertFalse(alarmPanel.validateHoursTextField(), "Should not allow empty hour field");
        assertFalse(alarmPanel.validateMinutesTextField(), "Should not allow empty minute field");
    }

    @ParameterizedTest
    @DisplayName("Test validate fields when we click Set Alarm")
    @CsvSource({
            "'name', '5', '5', true", // Valid: name given, HR:5, MIN:5
            "'ALARM1', '5', '5', true", // Valid: name field empty, name auto populates, HR:5, MIN:5

            "'name', '', '', false", // Valid: all fields empty
            "'', '', '', false", // Valid: all fields empty including name
            "'name', '60', '5', false", // Invalid: hours out of range
            "'name', '5', '60', false", // Invalid: minutes out of range
            "'name', '-1', '5', false", // Invalid: negative minutes
            "'name', '5', '-1', false", // Invalid: negative hours
            "'name', '', '5', false", // Valid: minutes field empty
            "'name', '5', '', false", // Valid: minutes field empty
    })
    void testValidateAllInputsCreatesAlarm(String name, String hours, String minutes, boolean expecting)
    {
        // set up
        alarmPanel.getNameTextField().setText(name);
        alarmPanel.getHoursTextField().setText(hours);
        alarmPanel.getMinutesTextField().setText(minutes);
        alarmPanel.getMondayCheckBox().setSelected(true); // need one day selected...

        // test
        boolean result = alarmPanel.validateAllInputs();

        // assert
        assertEquals(expecting, result, "expecting " + expecting + " for input: '" + name + "', " + minutes + ", " + hours);
    }

    @ParameterizedTest
    @DisplayName("Test validate checkboxes returns expected result")
    @MethodSource("provideDaysForValidateCheckboxes")
    void testValidateCheckboxes(List<DayOfWeek> days, boolean expecting)
    {
        boolean result = alarmPanel.validateTheCheckBoxes(days);
        assertEquals(expecting, result, "Checkbox validation should return " + expecting + " for days: " + days);
    }
    private static Stream<Arguments> provideDaysForValidateCheckboxes()
    {
        return Stream.of(
                Arguments.of(List.of(DayOfWeek.MONDAY), true),
                Arguments.of(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY), true),
                Arguments.of(List.of(), false) // No days selected
        );
    }

    @ParameterizedTest
    @CsvSource({
            "'0', '0', 'false'", // both are zero
            "'0', '5', 'true'", // both are not zero
            "'5', '0', 'true'", // both are not zero
            "'1', '5', 'true'", // both are not zero
    })
    @DisplayName("Test all are not zero")
    void testAreAllNotZero(String hours, String minutes, boolean expecting)
    {
        // set up
        alarmPanel.getHoursTextField().setText(hours);
        alarmPanel.getMinutesTextField().setText(minutes);

        // test
        assertEquals(expecting, alarmPanel.areAllNotZeroes(), "Should return " + expecting + " for hours: " + hours + ", minutes: " + minutes);
    }

    @ParameterizedTest
    @CsvSource({
            "'', '', 'true'", // both are blank
            "'0', '', 'false'", // both are not blank
            "'', '0', 'false'", // both are not blank
            "'1', '5', 'false'", // both are not blank
    })
    @DisplayName("Test all are blank")
    void testAreAllBlank(String hours, String minutes, boolean expecting)
    {
        // set up
        alarmPanel.getHoursTextField().setText(hours);
        alarmPanel.getMinutesTextField().setText(minutes);

        // test
        assertEquals(expecting, alarmPanel.areAllBlank(), "Should return " + expecting + " for hours: " + hours + ", minutes: " + minutes);
    }

    @ParameterizedTest
    @DisplayName("Test Set Alarm button clicked")
    @MethodSource("provideSetAlarmButtonClickedTestCases")
    @Disabled()
    void testSetAlarmButtonClicked(String name, String hours, String minutes,
                                   String ampm, List<DayOfWeek> days,
                                   boolean expecting)
    {
        // set up
        Alarm existingAlarm1 = new Alarm("Alarm1", 5, 5, AM, List.of(MONDAY), false, clock);
        Alarm existingAlarm2 = new Alarm("", 5, 5, AM, List.of(MONDAY), false, clock);
        alarmPanel.getClock().getListOfAlarms().add(existingAlarm1);
        alarmPanel.getClock().getListOfAlarms().add(existingAlarm2);
        alarmPanel.getNameTextField().setText(name);
        alarmPanel.getHoursTextField().setText(hours);
        alarmPanel.getMinutesTextField().setText(minutes);
        alarmPanel.getMondayCheckBox().setSelected(true); // need one day selected...
        ActionEvent actionEvent = new ActionEvent(alarmPanel, 0, "");

        // test
        alarmPanel.setAlarm(actionEvent);
        boolean result = alarmPanel.getClock().getListOfAlarms().size() == 1;
        // assert
        assertEquals(expecting, result, "expecting " + expecting + " for input: " + name + ", " + minutes + ", " + hours);
    }
    private static Stream<Arguments> provideSetAlarmButtonClickedTestCases() {
        return Stream.of(
                Arguments.of("Alarm1", "5", "5", AM, List.of(MONDAY), false), // Duplicate alarm of Existing Alarm1
                Arguments.of("", "5", "5", AM, List.of(MONDAY), false), // Duplicate alarm of Existing Alarm2
                Arguments.of("Alarm 2", "", "", AM, List.of(), false), // Invalid: all fields empty
                Arguments.of("", "", "", AM, List.of(), false), // Invalid: all fields empty including name
                Arguments.of("Alarm 3", "60", "5", AM, List.of(MONDAY), false), // Invalid: hours out of range
                Arguments.of("Alarm 4", "5", "60", AM, List.of(MONDAY), false), // Invalid: minutes out of range
                Arguments.of("Alarm 5", "-1", "5", AM, List.of(MONDAY), false), // Invalid: negative minutes
                Arguments.of("Alarm 6", "5", "-1", AM, List.of(MONDAY), false), // Invalid: negative hours
                Arguments.of("Alarm 7", "", "5", AM, List.of(MONDAY), false), // Valid: minutes field empty
                Arguments.of("Alarm 8", "5", "", AM, List.of(MONDAY), false) // Valid: minutes field empty
        );
    }

    @ParameterizedTest
    @DisplayName("Test Checkboxes Are Set When Editing Alarm")
    @CsvSource({
            "true, true, true, true, true, true, true, true, true", // All checkboxes should be selected
            "false, false, false, false, false, false, false, false, false", // All checkboxes should be deselected
            "true, false, true, false, true, false, false, false, false" // Mixed selection
    })
    void testCheckboxesAreSetWhenEditingAlarm(boolean monday, boolean tuesday, boolean wednesday,
                                              boolean thursday, boolean friday, boolean saturday,
                                              boolean sunday, boolean weekdays, boolean weekends)
    {
        // set up
        alarmPanel.getMondayCheckBox().setSelected(monday);
        alarmPanel.getTuesdayCheckBox().setSelected(tuesday);
        alarmPanel.getWednesdayCheckBox().setSelected(wednesday);
        alarmPanel.getThursdayCheckBox().setSelected(thursday);
        alarmPanel.getFridayCheckBox().setSelected(friday);
        alarmPanel.getSaturdayCheckBox().setSelected(saturday);
        alarmPanel.getSundayCheckBox().setSelected(sunday);
        alarmPanel.getWeekdaysCheckBox().setSelected(weekdays);
        alarmPanel.getWeekendsCheckBox().setSelected(weekends);

        // test
        assertEquals(monday, alarmPanel.getMondayCheckBox().isSelected(), "Monday checkbox should be " + monday);
        assertEquals(tuesday, alarmPanel.getTuesdayCheckBox().isSelected(), "Tuesday checkbox should be " + tuesday);
        assertEquals(wednesday, alarmPanel.getWednesdayCheckBox().isSelected(), "Wednesday checkbox should be " + wednesday);
        assertEquals(thursday, alarmPanel.getThursdayCheckBox().isSelected(), "Thursday checkbox should be " + thursday);
        assertEquals(friday, alarmPanel.getFridayCheckBox().isSelected(), "Friday checkbox should be " + friday);
        assertEquals(saturday, alarmPanel.getSaturdayCheckBox().isSelected(), "Saturday checkbox should be " + saturday);
    }

    // ─────────────────────────────────────────────────────────────────
    // getDaysChecked
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getDaysChecked returns empty list when no checkboxes are selected")
    void testGetDaysCheckedNoneSelected()
    {
        final List<DayOfWeek> days = alarmPanel.getDaysChecked();
        assertTrue(days.isEmpty(), "getDaysChecked should return an empty list when nothing is selected");
    }

    @ParameterizedTest
    @DisplayName("getDaysChecked returns correct day when individual checkbox is selected")
    @MethodSource("provideIndividualDayCheckboxCases")
    void testGetDaysCheckedIndividualCheckbox(DayOfWeek day, boolean monday, boolean tuesday,
                                              boolean wednesday, boolean thursday, boolean friday,
                                              boolean saturday, boolean sunday)
    {
        alarmPanel.getMondayCheckBox().setSelected(monday);
        alarmPanel.getTuesdayCheckBox().setSelected(tuesday);
        alarmPanel.getWednesdayCheckBox().setSelected(wednesday);
        alarmPanel.getThursdayCheckBox().setSelected(thursday);
        alarmPanel.getFridayCheckBox().setSelected(friday);
        alarmPanel.getSaturdayCheckBox().setSelected(saturday);
        alarmPanel.getSundayCheckBox().setSelected(sunday);

        final List<DayOfWeek> result = alarmPanel.getDaysChecked();
        assertEquals(1, result.size(), "Should have exactly one day selected");
        assertTrue(result.contains(day), "Selected day should be " + day);
    }
    private static Stream<Arguments> provideIndividualDayCheckboxCases()
    {
        return Stream.of(
                Arguments.of(MONDAY,    true,  false, false, false, false, false, false),
                Arguments.of(TUESDAY,   false, true,  false, false, false, false, false),
                Arguments.of(WEDNESDAY, false, false, true,  false, false, false, false),
                Arguments.of(THURSDAY,  false, false, false, true,  false, false, false),
                Arguments.of(FRIDAY,    false, false, false, false, true,  false, false),
                Arguments.of(SATURDAY,  false, false, false, false, false, true,  false),
                Arguments.of(SUNDAY,    false, false, false, false, false, false, true)
        );
    }

    @Test
    @DisplayName("getDaysChecked returns all 7 days when all checkboxes are selected")
    void testGetDaysCheckedAllSelected()
    {
        alarmPanel.getMondayCheckBox().setSelected(true);
        alarmPanel.getTuesdayCheckBox().setSelected(true);
        alarmPanel.getWednesdayCheckBox().setSelected(true);
        alarmPanel.getThursdayCheckBox().setSelected(true);
        alarmPanel.getFridayCheckBox().setSelected(true);
        alarmPanel.getSaturdayCheckBox().setSelected(true);
        alarmPanel.getSundayCheckBox().setSelected(true);

        final List<DayOfWeek> result = alarmPanel.getDaysChecked();
        assertEquals(7, result.size(), "All 7 days should be returned");
    }

    // ─────────────────────────────────────────────────────────────────
    // setCheckBoxesIfWasSelected
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("setCheckBoxesIfWasSelected selects weekdays checkbox when all weekdays are present")
    void testSetCheckBoxesIfWasSelectedWeekdays()
    {
        final Alarm alarm = new Alarm("WkAlarm", 9, 0, AM,
                List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY), false, clock);
        alarmPanel.setCheckBoxesIfWasSelected(alarm);

        assertTrue(alarmPanel.getMondayCheckBox().isSelected());
        assertTrue(alarmPanel.getTuesdayCheckBox().isSelected());
        assertTrue(alarmPanel.getWednesdayCheckBox().isSelected());
        assertTrue(alarmPanel.getThursdayCheckBox().isSelected());
        assertTrue(alarmPanel.getFridayCheckBox().isSelected());
        assertTrue(alarmPanel.getWeekdaysCheckBox().isSelected(), "Weekdays checkbox should be selected");
        assertFalse(alarmPanel.getWeekendsCheckBox().isSelected(), "Weekends checkbox should not be selected");
    }

    @Test
    @DisplayName("setCheckBoxesIfWasSelected selects weekends checkbox when Saturday and Sunday are present")
    void testSetCheckBoxesIfWasSelectedWeekends()
    {
        final Alarm alarm = new Alarm("WkdAlarm", 10, 0, AM,
                List.of(SATURDAY, SUNDAY), false, clock);
        alarmPanel.setCheckBoxesIfWasSelected(alarm);

        assertTrue(alarmPanel.getSaturdayCheckBox().isSelected());
        assertTrue(alarmPanel.getSundayCheckBox().isSelected());
        assertTrue(alarmPanel.getWeekendsCheckBox().isSelected(), "Weekends checkbox should be selected");
        assertFalse(alarmPanel.getWeekdaysCheckBox().isSelected(), "Weekdays checkbox should not be selected");
    }

    @Test
    @DisplayName("setCheckBoxesIfWasSelected selects only the days from the alarm")
    void testSetCheckBoxesIfWasSelectedPartialDays()
    {
        final Alarm alarm = new Alarm("PartialAlarm", 8, 30, AM,
                List.of(MONDAY, WEDNESDAY, FRIDAY), false, clock);
        alarmPanel.setCheckBoxesIfWasSelected(alarm);

        assertTrue(alarmPanel.getMondayCheckBox().isSelected());
        assertFalse(alarmPanel.getTuesdayCheckBox().isSelected());
        assertTrue(alarmPanel.getWednesdayCheckBox().isSelected());
        assertFalse(alarmPanel.getThursdayCheckBox().isSelected());
        assertTrue(alarmPanel.getFridayCheckBox().isSelected());
        assertFalse(alarmPanel.getSaturdayCheckBox().isSelected());
        assertFalse(alarmPanel.getSundayCheckBox().isSelected());
    }

    // ─────────────────────────────────────────────────────────────────
    // Weekday / Weekend checkbox toggle via doClick
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Clicking weekdays checkbox selects all weekday checkboxes")
    void testWeekdaysCheckboxSelectsWeekdays()
    {
        alarmPanel.getWeekdaysCheckBox().doClick();   // triggers its action listener
        assertTrue(alarmPanel.getMondayCheckBox().isSelected());
        assertTrue(alarmPanel.getTuesdayCheckBox().isSelected());
        assertTrue(alarmPanel.getWednesdayCheckBox().isSelected());
        assertTrue(alarmPanel.getThursdayCheckBox().isSelected());
        assertTrue(alarmPanel.getFridayCheckBox().isSelected());
    }

    @Test
    @DisplayName("Clicking weekdays checkbox twice deselects all weekday checkboxes")
    void testWeekdaysCheckboxDeselectsWeekdays()
    {
        alarmPanel.getWeekdaysCheckBox().doClick(); // select
        alarmPanel.getWeekdaysCheckBox().doClick(); // deselect
        assertFalse(alarmPanel.getMondayCheckBox().isSelected());
        assertFalse(alarmPanel.getTuesdayCheckBox().isSelected());
        assertFalse(alarmPanel.getWednesdayCheckBox().isSelected());
        assertFalse(alarmPanel.getThursdayCheckBox().isSelected());
        assertFalse(alarmPanel.getFridayCheckBox().isSelected());
    }

    @Test
    @DisplayName("Clicking weekends checkbox selects Saturday and Sunday")
    void testWeekendsCheckboxSelectsWeekends()
    {
        alarmPanel.getWeekendsCheckBox().doClick();
        assertTrue(alarmPanel.getSaturdayCheckBox().isSelected());
        assertTrue(alarmPanel.getSundayCheckBox().isSelected());
    }

    @Test
    @DisplayName("Clicking weekends checkbox twice deselects Saturday and Sunday")
    void testWeekendsCheckboxDeselectsWeekends()
    {
        alarmPanel.getWeekendsCheckBox().doClick(); // select
        alarmPanel.getWeekendsCheckBox().doClick(); // deselect
        assertFalse(alarmPanel.getSaturdayCheckBox().isSelected());
        assertFalse(alarmPanel.getSundayCheckBox().isSelected());
    }

    // ─────────────────────────────────────────────────────────────────
    // resetAlarmPanel
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("resetAlarmPanel clears all alarms from the clock")
    void testResetAlarmPanelClearsAlarms()
    {
        alarmPanel.getClock().getListOfAlarms()
                .add(new Alarm("A1", 7, 0, AM, List.of(MONDAY), false, clock));
        alarmPanel.getClock().getListOfAlarms()
                .add(new Alarm("A2", 8, 0, AM, List.of(TUESDAY), false, clock));

        alarmPanel.resetAlarmPanel();
        assertTrue(alarmPanel.getClock().getListOfAlarms().isEmpty(),
                "All alarms should be cleared after resetAlarmPanel");
    }

    @Test
    @DisplayName("resetAlarmPanel resets all text fields to empty")
    void testResetAlarmPanelClearsTextFields()
    {
        alarmPanel.getNameTextField().setText("Test");
        alarmPanel.getHoursTextField().setText("9");
        alarmPanel.getMinutesTextField().setText("30");

        alarmPanel.resetAlarmPanel();
        assertTrue(alarmPanel.getNameTextField().getText().isEmpty(),
                "Name field should be empty after reset");
        assertTrue(alarmPanel.getHoursTextField().getText().isEmpty(),
                "Hours field should be empty after reset");
        assertTrue(alarmPanel.getMinutesTextField().getText().isEmpty(),
                "Minutes field should be empty after reset");
    }

    @Test
    @DisplayName("resetAlarmPanel deselects all checkboxes")
    void testResetAlarmPanelDeselectsCheckboxes()
    {
        alarmPanel.getMondayCheckBox().setSelected(true);
        alarmPanel.getWeekendsCheckBox().setSelected(true);

        alarmPanel.resetAlarmPanel();
        assertFalse(alarmPanel.getMondayCheckBox().isSelected());
        assertFalse(alarmPanel.getWeekendsCheckBox().isSelected());
    }

    // ─────────────────────────────────────────────────────────────────
    // getAlarmsTableData / getAlarmsTableColumnNames
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAlarmsTableData returns empty array when no alarms exist")
    void testGetAlarmsTableDataEmpty()
    {
        final Object[][] data = alarmPanel.getAlarmsTableData();
        assertEquals(0, data.length, "Table data should be empty when no alarms are set");
    }

    @Test
    @DisplayName("getAlarmsTableData returns one row per alarm")
    void testGetAlarmsTableDataWithAlarms()
    {
        alarmPanel.getClock().getListOfAlarms()
                .add(new Alarm("A1", 7, 0, AM, List.of(MONDAY), false, clock));
        alarmPanel.getClock().getListOfAlarms()
                .add(new Alarm("A2", 8, 15, AM, List.of(TUESDAY, WEDNESDAY), false, clock));

        final Object[][] data = alarmPanel.getAlarmsTableData();
        assertEquals(2, data.length, "Table data should have one row per alarm");
    }

    @Test
    @DisplayName("getAlarmsTableColumnNames returns a non-empty array")
    void testGetAlarmsTableColumnNames()
    {
        final String[] names = alarmPanel.getAlarmsTableColumnNames();
        assertNotNull(names, "Column names should not be null");
        assertTrue(names.length > 0, "Column names array should not be empty");
    }
}