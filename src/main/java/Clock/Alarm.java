package Clock;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import Clock.Time.*;
import static java.time.DayOfWeek.*;

/**
 * An Alarm is similar to a Clock.
 * The differences may continue to grow but
 * as for now, an Alarm knows all the days
 * it should go off, the time at which to
 * go off, and can set the current day based
 * on Clocks time if alarm is going off.
 *
 * @author michael ball
 * @version 2.5
 */
public class Alarm
{
    protected int minutes;
    protected String minutesAsStr;
    protected int hours;
    protected String hoursAsStr;
    protected Time ampm;
    private ArrayList<DayOfWeek> days;
    protected boolean alarmGoingOff;
    protected boolean updatingAlarm;
    protected Clock clock;

    public Alarm() throws InvalidInputException
    {
        super();
        setHours(0);
        setMinutes(0);
        //setAMPM();
        //setDays(null);
        setIsAlarmUpdating(false);
    }
    public Alarm(Clock clock, int hours, boolean isUpdateAlarm)
    {
        //super(clock);
        setClock(clock);
        setHours(hours);
        setDays(new ArrayList<>(){{add(clock.getDayOfWeek());}});
        setIsAlarmUpdating(isUpdateAlarm);
    }
    public Alarm(int hours, int minutes, Time time, boolean isUpdateAlarm, ArrayList<DayOfWeek> days, Clock clock) throws InvalidInputException
    {
        //super();
        setClock(clock);
        setHours(hours);
        setMinutes(minutes);
        setAMPM(time);
        setDays(days);
        setIsAlarmUpdating(isUpdateAlarm);
    }

    //@Override
    public void setHours(int hours) {
        this.hours = hours;
        if (hours < 10) {
            this.hoursAsStr = "0" + this.hours;
        } else
            this.hoursAsStr = String.valueOf(this.hours);
    }
    //@Override
    public int getHours() { return this.hours; }
    public String getHoursAsStr() { return this.hoursAsStr; }

    //@Override
    public void setMinutes(int minutes) {
        this.minutes = minutes;
        if (minutes < 10) {
            this.minutesAsStr = "0" + this.minutes;
        } else {
            this.minutesAsStr = String.valueOf(this.minutes);
        }
    }
    //@Override
    public int getMinutes() { return this.minutes; }
    public String getMinutesAsStr() { return this.minutesAsStr; }

    //@Override
    protected void setAMPM(Time ampm) {
        this.ampm = ampm;
    }
    //@Override
    public Time getAMPM() { return this.ampm; }

    protected String getAlarmAsString() {
        return this.hoursAsStr + ":" + this.minutesAsStr + " " + this.ampm;
    }

    public ArrayList<DayOfWeek> getDays() { return this.days; }

    public List<String> getDaysShortened()
    {
        List<String> shortenedDays = new ArrayList<>();
        shortenedDays.add("Days: ");
        if (getDays().contains(MONDAY) && getDays().contains(TUESDAY) &&
                getDays().contains(WEDNESDAY) && getDays().contains(THURSDAY) &&
                getDays().contains(FRIDAY))
            { shortenedDays.add("Weekdays "); }
        else if (getDays().contains(SATURDAY) && getDays().contains(SUNDAY)) { shortenedDays.add("Weekend"); }
        else {
            for(DayOfWeek day : getDays())
            {
                if (day == MONDAY) { shortenedDays.add("M "); }
                if (day == TUESDAY) { shortenedDays.add("T "); }
                if (day == WEDNESDAY) { shortenedDays.add("W "); }
                if (day == THURSDAY) { shortenedDays.add("Th "); }
                if (day == FRIDAY) { shortenedDays.add("F "); }
                if (day == SATURDAY) { shortenedDays.add("S "); }
                if (day == SUNDAY) { shortenedDays.add("Su "); }
            }
        }
        shortenedDays.add("\n------");
        return shortenedDays;
    }

    protected void setIsAlarmGoingOff(boolean alarmGoingOff) { this.alarmGoingOff = alarmGoingOff; }
    protected void setIsAlarmUpdating(boolean updatingAlarm) { this.updatingAlarm = updatingAlarm; }

    protected void setClock(Clock clock) { this.clock = clock; }
    public Clock getClock() { return this.clock; }

    public boolean isAlarmGoingOff() { return alarmGoingOff; }
    public boolean isUpdatingAlarm() { return updatingAlarm; }

    protected void setDays(ArrayList<DayOfWeek> days) { this.days = days; }
}