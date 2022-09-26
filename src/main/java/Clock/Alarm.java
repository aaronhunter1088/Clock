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
public class Alarm extends Clock
{
    private ArrayList<DayOfWeek> days;
    public ArrayList<DayOfWeek> getDays() { return this.days; }
    protected void setDays(ArrayList<DayOfWeek> days) { this.days = days; }

    public Alarm() throws InvalidInputException
    {
        super(new Clock());
    }
    public Alarm(Clock clock, int hours, boolean isUpdateAlarm)
    {
        super(clock);
        setHours(hours);
        setDays(new ArrayList<>(){{add(clock.getDayOfWeek());}});
        setUpdateAlarm(isUpdateAlarm);
    }
    public Alarm(int hours, int minutes, Time time, boolean isUpdateAlarm, ArrayList<DayOfWeek> days) throws InvalidInputException
    {
        super();
        setHours(hours);
        setMinutes(minutes);
        setSeconds(0);
        setAMPM(time);
        setDays(days);
        setUpdateAlarm(isUpdateAlarm);
    }

    public List<String> getDaysShortened()
    {
        List<String> shortenedDays = new ArrayList<>();
        if (getDays().contains(MONDAY) && getDays().contains(TUESDAY) &&
                getDays().contains(WEDNESDAY) && getDays().contains(THURSDAY) &&
                getDays().contains(FRIDAY))
            { shortenedDays.add("Wk "); }
        else if (getDays().contains(SATURDAY) && getDays().contains(SUNDAY)) { shortenedDays.add("Wd"); }
        else {
            shortenedDays.add("Days: ");
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
        return shortenedDays;
    }
}
