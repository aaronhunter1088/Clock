package v5;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.util.ArrayList;

import static v5.Time.AMPM;

@SuppressWarnings("unused")
/** An Alarm is similar to a Clock.
 * The differences may continue to grow but
 * as for now, an Alarm knows all the days
 * it should go off, the time at which to
 * go off, and can set the current day based
 * on Clocks time if alarm is going off.
 *
 * @author michael ball
 * @version 2.5
 */
public class Alarm extends Clock
{
    //Alarm specific parameters
    private ArrayList<DayOfWeek> days;
    private DayOfWeek currentDay;

    // Getters
    public ArrayList<DayOfWeek> getDays() { return this.days; }
    public DayOfWeek getCurrentDay() { return this.currentDay; }

    // Setters
    protected void setDays(ArrayList<DayOfWeek> days) { this.days = days; }
    protected void setCurrentDay(DayOfWeek currentDay) { this.currentDay = currentDay; }

    // Constructors
    public Alarm() throws ParseException
    {
        super(new Clock());
    }
    public Alarm(Clock clock, int hours, boolean isUpdateAlarm) throws ParseException
    {
        super(clock);
        setHours(hours);
        setUpdateAlarm(isUpdateAlarm);
    }
    public Alarm(Clock clock, int hours, int minutes, AMPM time, boolean isUpdateAlarm, ArrayList<DayOfWeek> days) throws ParseException
    {
        setHours(hours);
        setMinutes(minutes);
        setSeconds(0);
        setAMPM(time);
        setDays(days);
        setUpdateAlarm(isUpdateAlarm);
    }

    // Helper methods
    public void printAlarmStatus()
    { this.printAlarmStatus(this.getClass(), ""); }
    public void printAlarmStatus(String message)
    { this.printAlarmStatus(this.getClass(), message); }
    public void printAlarmStatus(Class clazz, String status)
    {
        this.printClockStatus(clazz, status);
        System.out.print("days: ");
        if (null != days)
        {
            for(DayOfWeek day: days)
            { System.out.print("\t"+day); }
        }
        else
        {
            System.out.println("\t'No days'");
        }
    }
    protected void setCurrentDay()
    {
        // alarm knows which days it should go off
        // it also is a clock, and knows that value
    }
}
