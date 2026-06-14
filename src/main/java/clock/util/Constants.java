package clock.util;

import java.time.DayOfWeek;
import java.util.List;

import static java.time.DayOfWeek.*;

/**
 * Constants
 * <p>
 * Alphabetized for easy reference in each section.
 * @author michael ball
 * @version since 2.0
 */
public class Constants {

    private Constants() {}

    // General
    /** Represents "All" — used in labels referring to every item in a collection. */
    public static final String ALL = "All";
    /** Represents "AM" — used in standard time display. */
    public static final String AM = "AM";
    /** Represents "AND" — used in composed display strings. */
    public static final String AND = "AND";
    /** Represents "Analogue" — label for the analogue clock panel. */
    public static final String ANALOGUE = "Analogue";
    /** Represents "are" — used in composed message strings. */
    public static final String are = "are";
    /** Represents "Button" — used as a UI component label suffix. */
    public static final String BUTTON = "Button";
    /** Represents "Change" — used on buttons that trigger value changes. */
    public static final String CHANGE = "Change";
    /** Represents ":" — colon character used in time formatting. */
    public static final String COLON = ":";
    /** Represents "," — comma character used in date and list formatting. */
    public static final String COMMA = ",";
    /** Represents "Clock" — general label for the clock application. */
    public static final String CLOCK = "Clock";
    /** Represents "Date" — label for date display components. */
    public static final String DATE = "Date";
    /** Represents "Digital" — label for the digital clock panel. */
    public static final String DIGITAL = "Digital";
    /** Represents "digital time" — setting name for toggling digital time display. */
    public static final String DIGITAL_TIME = "digital time";
    /** Represents "daylight savings time" — setting name for the DST toggle. */
    public static final String DST_SETTING = "daylight savings time";
    /** Represents the numeral "8" — used in numeric input or display contexts. */
    public static final String EIGHT = "8";
    /** Represents the numeral "11" — used in numeric input or display contexts. */
    public static final String ELEVEN = "11";
    /** Represents an empty string — used as a blank/default value. */
    public static final String EMPTY = "";
    /** Label for the time zone selection dialog. */
    public static final String SELECT_ZONE_ID = "Select Time Zone";
    /** Represents "TextField" — used as a UI component type label. */
    public static final String TEXT_FIELD = "TextField";
    /** Represents the numeral "5" — used in numeric input or display contexts. */
    public static final String FIVE = "5";
    /** Represents the numeral "4" — used in numeric input or display contexts. */
    public static final String FOUR = "4";
    /** Represents "full date" — setting name for showing the full date. */
    public static final String FULL_TIME_SETTING = "full date";
    /** Represents "going off!" — message displayed when an alarm triggers. */
    public static final String going_off = "going off!";
    /** Represents "Hide" — label for buttons that hide a UI component. */
    public static final String HIDE = "Hide";
    /** Represents "Hours" — label for the hours input field. */
    public static final String Hours = "Hours";
    /** Represents "is" — used in composed message strings. */
    public static final String is = "is";
    /** Represents "Label" — used as a UI component type label. */
    public static final String LABEL = "Label";
    /** Represents midnight in military time format ("0000 hours 00"). */
    public static final String MIDNIGHT_MILITARY_TIME = "0000 hours 00";
    /** Represents midnight in standard time format ("12:00:00 AM"). */
    public static final String MIDNIGHT_STANDARD_TIME = "12:00:00 AM";
    /** Represents "military time" — setting name for the military time toggle. */
    public static final String MILITARY_TIME_SETTING = "military time";
    /** Represents "Minutes" — label for the minutes input field. */
    public static final String Minutes = "Minutes";
    /** Represents "Name" — label for name input fields. */
    public static final String NAME = "Name";
    /** Represents a newline character — used in composed multi-line strings. */
    public static final String NEWLINE = "\n";
    /** Represents the numeral "9" — used in numeric input or display contexts. */
    public static final String NINE = "9";
    /** Represents "No" — used in confirmation dialogs and composed labels. */
    public static final String NO = "No";
    /** Represents "of" — used in composed display strings. */
    public static final String of = "of";
    /** Represents "off" — used to describe a disabled or inactive state. */
    public static final String off = "off";
    /** Represents "on" — used to describe an enabled or active state. */
    public static final String on = "on";
    /** Represents the numeral "1" — used in numeric input or display contexts. */
    public static final String ONE = "1";
    /** Represents "Panel" — used as a UI component type label. */
    public static final String PANEL = "Panel";
    /** Represents "partial date" — setting name for showing a partial date. */
    public static final String PARTIAL_TIME_SETTING = "partial date";
    /** Represents "PM" — used in standard time display. */
    public static final String PM = "PM";
    /** Represents "Seconds" — label for the seconds input field. */
    public static final String Seconds = "Seconds";
    /** Represents the numeral "7" — used in numeric input or display contexts. */
    public static final String SEVEN = "7";
    /** Represents the numeral "6" — used in numeric input or display contexts. */
    public static final String SIX = "6";
    /** Represents "Show" — label for buttons that reveal a UI component. */
    public static final String SHOW = "Show";
    /** Represents a single space character — used in composed strings. */
    public static final String SPACE = " ";
    /** Represents "standard time" — setting name for the standard time toggle. */
    public static final String STANDARD_TIME_SETTING = "standard time";
    /** Represents "*" — star/asterisk character used as a visual indicator. */
    public static final String STAR = "*";
    /** Represents "Stopwatch" — label for a single stopwatch item. */
    public static final String STOPWATCH = "Stopwatch";
    /** Represents "Stopwatches" — label for the stopwatches panel. */
    public static final String STOPWATCHES = "Stopwatches";
    /** Represents "/" — slash character used in date formatting. */
    public static final String SLASH = "/";
    /** Represents the numeral "10" — used in numeric input or display contexts. */
    public static final String TEN = "10";
    /** Represents "Test" — used as a label in test-related contexts. */
    public static final String TEST = "Test";
    /** Represents the numeral "3" — used in numeric input or display contexts. */
    public static final String THREE = "3";
    /** Represents "Turn" — used in composed labels such as "Turn on/off". */
    public static final String Turn = "Turn";
    /** Represents the numeral "12" — used in numeric input or display contexts. */
    public static final String TWELVE = "12";
    /** Represents the numeral "2" — used in numeric input or display contexts. */
    public static final String TWO = "2";

    // Menu options
    /** Label for the Features menu in the menu bar. */
    public static final String FEATURES = "Features";
    /** Label for the Settings menu in the menu bar. */
    public static final String SETTINGS = "Settings";
    /** Label for the Help menu in the menu bar. */
    public static final String HELP = "Help";
    // Inner menu options
    /** Label for the time zones sub-menu item. */
    public static final String TIME_ZONES = "time zones";

    /** Display name for the Alaska time zone. */
    public static final String ALASKA = "Alaska";
    /** Display name for the Central time zone. */
    public static final String CENTRAL = "Central";
    /** Display name for the Eastern time zone. */
    public static final String EASTERN = "Eastern";
    /** Display name for the Hawaii time zone. */
    public static final String HAWAII = "Hawaii";
    /** Display name for the Mountain time zone. */
    public static final String MOUNTAIN = "Mountain";
    /** Display name for the Pacific time zone. */
    public static final String PACIFIC = "Pacific";

    // ZoneIds
    /** IANA zone ID for the Alaska time zone (America/Anchorage). */
    public static final String AMERICA_ANCHORAGE = "America/Anchorage";
    /** IANA zone ID for the Central time zone (America/Chicago). */
    public static final String AMERICA_CHICAGO = "America/Chicago";
    /** IANA zone ID for the Pacific time zone (America/Los_Angeles). */
    public static final String AMERICA_LOS_ANGELES = "America/Los_Angeles";
    /** IANA zone ID for the Eastern time zone (America/New_York). */
    public static final String AMERICA_NEW_YORK = "America/New_York";
    /** IANA zone ID for the Hawaii time zone (Pacific/Honolulu). */
    public static final String PACIFIC_HONOLULU = "Pacific/Honolulu";
    /** IANA zone ID for the Mountain time zone (America/Denver). */
    public static final String AMERICA_DENVER = "America/Denver";

    // Alarm
    /** Label for a single alarm item. */
    public static final String ALARM = "Alarm";
    /** Title for alarm-related error dialogs. */
    public static final String ALARM_ERROR = "Alarm Error";
    /** Label for the AM/PM toggle in alarm input. */
    public static final String AMPM = "AM/PM";
    /** Label for the days selection component in alarm input. */
    public static final String DAYS = "Days";
    /** Abbreviation for Monday used in day-of-week selectors. */
    public static final String M = "M";
    /** Abbreviation for Tuesday used in day-of-week selectors. */
    public static final String T = "T";
    /** Abbreviation for Wednesday used in day-of-week selectors. */
    public static final String W = "W";
    /** Abbreviation for Thursday used in day-of-week selectors. */
    public static final String TH = "Th";
    /** Abbreviation for Friday used in day-of-week selectors. */
    public static final String F = "F";
    /** Abbreviation for Saturday used in day-of-week selectors. */
    public static final String S = "S";
    /** Abbreviation for Sunday used in day-of-week selectors. */
    public static final String SU = "Su";
    /** Label for the "every day" recurrence option in alarm scheduling. */
    public static final String EVERY_DAY = "Every Day";
    /** Label displayed in the alarm panel when no alarms exist. */
    public static final String No_Alarms = NO + SPACE + ALARM + S.toLowerCase();
    /** Status label shown when an alarm is in snooze/sleeping state. */
    public static final String SLEEPING = "Sleeping .zZ";
    /** Label for the snooze button on alarm dialogs. */
    public static final String SNOOZE = "Snooze";
    /** Label for the stop button used to dismiss an active alarm. */
    public static final String STOP = "Stop";
    /** Label for the menu item that navigates to the alarm panel. */
    public static final String VIEW_ALARMS = "View Alarms";
    /** Abbreviation for a weekly recurrence option in alarm scheduling. */
    public static final String WEEK = "WK";
    /** Label for the weekdays recurrence option in alarm scheduling. */
    public static final String WEEKDAYS = "Weekdays";
    /** Abbreviation for the weekend recurrence option in alarm scheduling. */
    public static final String WEEKEND = "WKD";
    /** Label for the weekends recurrence option in alarm scheduling. */
    public static final String WEEKENDS = "Weekends";
    /** Label for the menu item that navigates to the digital clock panel. */
    public static final String VIEW_DIGITAL_CLOCK = "View Digital Clock";
    /** Label for the menu item that navigates to the analogue clock panel. */
    public static final String VIEW_ANALOGUE_CLOCK = "View Analogue Clock";
    /** Ordered list of weekday {@link DayOfWeek} values (Monday–Friday). */
    public static final List<DayOfWeek> WEEKDAYS_LIST = List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY);

    // Timer
    /** Label for the countdown display in timer panels. */
    public static final String COUNTDOWN = "Countdown";
    /** Label for the edit action button in timer and alarm panels. */
    public static final String EDIT = "Edit";
    /** Label for the hour input field in timer panels. */
    public static final String HOUR = "Hour";
    /** Abbreviation for minutes used in timer input labels. */
    public static final String MIN = "Min";
    /** Label for the pause button used to pause timers and alarms. */
    public static final String PAUSE = "Pause";
    /** Label for the remove button used to delete a timer or alarm. */
    public static final String REMOVE = "Remove";
    /** Label for the reset button used to reset a timer or panel. */
    public static final String RESET = "Reset";
    /** Label for the resume button used to resume paused timers and alarms. */
    public static final String RESUME = "Resume";
    /** Abbreviation for seconds used in timer input labels. */
    public static final String SEC = "Sec";
    /** Label for the set button used to confirm a timer or alarm value. */
    public static final String SET = "Set";
    /** General label for time display components. */
    public static final String TIME = "Time";
    /** Label for a single timer item. */
    public static final String TIMER = "Timer";
    /** Title for timer-related error dialogs. */
    public static final String TIMER_ERROR = "Timer Error";
    /** Label for the menu item that navigates to the timers panel. */
    public static final String VIEW_TIMERS = "View Timers";
    /** Label for the menu item that navigates to the stopwatches panel. */
    public static final String VIEW_STOPWATCHES = "View Stopwatches";
    /** Label for the menu item that opens the help view. */
    public static final String VIEW_HELP = "View Help";
    /** Represents the numeral "0" — used as the default/zero value in inputs. */
    public static final String ZERO = "0";
    /** Label displayed in the timer panel when no timers exist. */
    public static final String No_Timers = NO + SPACE + TIMER + S.toLowerCase();

    // Stopwatch
    /** Label for the elapsed time display in stopwatch panels. */
    public static final String ELAPSED = "Elapsed";
    /** Label for a single lap entry in stopwatch panels. */
    public static final String LAP = "Lap";
    /** Prefix symbol for a lap number display (e.g., "Lap #1"). */
    public static final String LAP_SYM = "Lap #";
    /** Label for the laps section or column in stopwatch panels. */
    public static final String LAPS = "Laps";
    /** Label indicating the number of laps recorded on a stopwatch. */
    public static final String RECORDED = "Recorded";
    /** Label for the restore button in stopwatch panels. */
    public static final String RESTORE = "Restore";
    /** Label for the reverse button that reverses lap order display. */
    public static final String REVERSE = "Reverse";
    /** Label for the select button used to choose a stopwatch. */
    public static final String SELECT = "Select";
    /** Label for the start button used to start a stopwatch or timer. */
    public static final String START = "Start";
    /** Format string for parsing stopwatch time values; produces "mm:ss:SSS". */
    public static final String STOPWATCH_PARSE_FORMAT = "%02d:%02d:%03d";
    /** Format string for displaying stopwatch readings; produces "mm:ss.SSS". */
    public static final String STOPWATCH_READING_FORMAT = "%02d:%02d.%03d";
    /** Label for the button that switches to the "view all" stopwatches mode. */
    public static final String VIEW_ALL = "View All";
}
