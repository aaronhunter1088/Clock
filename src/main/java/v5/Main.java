package v5;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

import static java.lang.Thread.sleep;

public class Main
{
    public static void main(String[] args) throws ParseException
    {
        Clock clock = new Clock();
        //Clock clock = new Clock(11, 59, 50, Time.Month.FEBRUARY, Time.Day.SUNDAY, 21, 2021, Time.AMPM.PM);
        clock.setVisible(true);
        clock.getContentPane().setBackground(Color.BLACK);
        //clock.setSize(new Dimension(700, 300));
        clock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clock.setBounds(200, 200, 700, 300);
        try
        {
            while (true)
            {
                clock.tick();
                // check alarms
                sleep(250);
                clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
                clock.getTimerPanel().checkIfTimerHasConcluded();
                if (clock.getTimerPanel().isTimerHasConcluded()) {
                    clock.getTimerPanel().setTimerHasConcluded(false);
                }
                sleep(750);
            }
        }
        catch (Exception e)
        {
            for(StackTraceElement ste : e.getStackTrace())
            { System.err.println(ste); }
        }
    }
}
