package v4;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

import static java.lang.Thread.sleep;

public class Main
{
    protected static final Dimension defaultSize = new Dimension(700, 300);
    public static void main(String[] args) throws ParseException, InterruptedException
    {
        Clock clock = new Clock();
        clock.setVisible(true);
        clock.getContentPane().setBackground(Color.BLACK);
        clock.setSize(defaultSize);
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
