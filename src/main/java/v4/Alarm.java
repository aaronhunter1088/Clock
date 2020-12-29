package v4;

import static v4.Time.Day;
import static v4.Time.AMPM;

import java.text.ParseException;
import java.util.ArrayList;

@SuppressWarnings("unused")
/* an Alarm is similar to a Clock except
 * it has a list of days that is should
 * use to trigger on or off.
 */
public class Alarm extends Clock
{
    //Alarm specific parameters
    private ArrayList<Day> days;
    private Day currentDay;

    // Getters
    public ArrayList<Day> getDays() { return this.days; }
    public Day getCurrentDay() { return this.currentDay; }

    // Setters
    protected void setDays(ArrayList<Day> days) { this.days = days; }
    protected void setCurrentDay(Day currentDay) { this.currentDay = currentDay; }

    // Constructors
    public Alarm(Clock clock, int hours, boolean isUpdateAlarm) throws ParseException
    {
        super(clock);
        setHours(hours);
        setUpdateAlarm(isUpdateAlarm);
    }
    public Alarm(Clock clock, int hours, int minutes, AMPM time, boolean isUpdateAlarm, ArrayList<Day> days) throws ParseException
    {
        super(clock);
        setHours(hours);
        setMinutes(minutes);
        setAMPM(time);
        setDays(days);
        setUpdateAlarm(isUpdateAlarm);
    }

    // Helper methods
    public void printAlarmStatus()
    { printClockStatus(""); }
    public void printAlarmStatus(String status)
    {
        super.printClockStatus(status);
        System.out.println("days:");
        for(Day day: days)
        { System.out.println("\t"+day); }
    }
    protected void setCurrentDay()
    {
        // alarm knows which days it should go off
        // it also is a clock, and knows that value
    }
}
