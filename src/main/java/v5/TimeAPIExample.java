package v5;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

public class TimeAPIExample
{
    public static void main(String[] args)
    {
        LocalDate date = LocalDate.now();
        System.out.println("Date: " + date);
        // set the date by LocalDate.of()
        LocalDate presetDate = LocalDate.of(2025, 1, 5);
        System.out.println("Preset Date: " + presetDate);
        // get the year, month, date from the date
        int year = date.getYear();
        int month = date.getMonthValue();
        Month aMonth = date.getMonth();
        int day = date.getDayOfMonth();
        System.out.println("Year: " + year);
        System.out.println("Month Value: " + month);
        System.out.println("Month: " + aMonth);
        System.out.println("Day of Month: " + day);

        for(Month m : java.time.Month.values())
        {
            System.out.println(m);
        }

        for(DayOfWeek d : java.time.DayOfWeek.values())
        {
            System.out.println(d);
        }

        LocalTime time = LocalTime.now();
        System.out.println("Time:  " + time);

        LocalTime presetTime = LocalTime.of(11, 21, 35);
        System.out.println("Preset Time:  " + presetTime);
    }
}
