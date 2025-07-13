package clock.contract;

/**
 * Constants for Clock application
 *
 * @author Michael Ball
*  @version 2.8
 */
public interface ClockConstants
{
    String HIDE = "Hide";
    String SHOW = "Show";
    String SPACE = " ";
    String EMPTY = "";
    String DIGITAL_TIME = "digital time";
    String STANDARD_TIME_SETTING = "standard time";
    String MILITARY_TIME_SETTING = "military time";
    String FULL_TIME_SETTING = "full date";
    String PARTIAL_TIME_SETTING = "partial date";
    String DST_SETTING = "daylight savings time";
    String Turn = "Turn";
    String on = "on";
    String off = "off";
    String STAR = "*";
    String SLASH = "/";
    String COLON = ":";
    String COMMA = ",";
    String NEWLINE = "\n";
    String Hours = "Hours";
    String Minutes = "Minutes";
    String Seconds = "Seconds";
    String AM = "AM";
    String PM = "PM";
    String S = "S";
    String No_Alarms = "No Alarms";
    String No_Timers = "No Timers";
    String is = "is";
    String are = "are";
    String going_off = "going off!";
    String MIDNIGHT_STANDARD_TIME = "12:00:00 AM";
    String MIDNIGHT_MILITARY_TIME = "2400 hours 00"; // check on 0000 hours 00

    // Time zones
    String TIME_ZONES = "time zones";
    String SETTINGS = "Settings";
    String FEATURES = "Features";

    String HAWAII = "Hawaii";
    String ALASKA = "Alaska";
    String PACIFIC = "Pacific";
    String CENTRAL = "Central";
    String EASTERN = "Eastern";

    // ZoneIds
    String PACIFIC_HONOLULU = "Pacific/Honolulu";
    String AMERICA_ANCHORAGE = "America/Anchorage";
    String AMERICA_LOS_ANGELES = "America/Los_Angeles";
    String AMERICA_CHICAGO = "America/Chicago";
    String AMERICA_NEW_YORK = "America/New_York";
    //String

    // Timer
    String SET = "Set";
    String TIMER = "Timer";
    String VIEW_TIMER = "View Timer";
    String RESUME_TIMER = "Resume Timer";
    String PAUSE_TIMER = "Pause Timer";
    String RESET = "Reset";
    String STOP = "Stop";
    String COMPLETE = "Complete";
    String HOUR = "Hour";
    String MIN = "Min";
    String SEC = "Sec";
    String ZERO = "0";
    String TIMER_HOUR_ERROR = "0 <= "+HOUR+" > 24";
    String TIMER_MIN_ERROR = "0 <= "+MIN+" > 60";
    String TIMER_SEC_ERROR = "0 <= "+SEC+" > 60";
    String CURRENT_TIMERS = "Current Timers";

    // Alarm
    String SET_ALARMS = "Set Alarms";
    String VIEW_ALARMS = "View Alarms";
    String ALARM = "Alarm";
    String ADDED = "Added";
    String AMPM = "AM/PM";
    String CURRENT_ALARMS = "Current Alarms";
    String WEEK = "WK";
    String WEEKEND = "WKD";

    String VIEW_DIGITAL_CLOCK = "View Digital Clock";
    String VIEW_ANALOGUE_CLOCK = "View Analogue Clock";
}