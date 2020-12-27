package v3;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class AlarmPanelTest extends Object {

    private static Clock clock;
    private static Clock.Alarm alarm;

    @BeforeClass
    public static void setupBeforeClass() throws InterruptedException, ParseException {
        clock = new Clock();
        clock.setVisible(true);
        clock.getContentPane().setBackground(Color.BLACK);
        clock.setSize(Clock.defaultSize);
        clock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clock.setBounds(200, 200, 700, 300);
        clock.tick();

        alarm = new Clock.Alarm(clock, clock.getHours(), true);
    }
    @Test
    public void setUpdatingAlarm() {
    }

    @Test
    public void convertStringToTimeAMPM() {
    }

    @Test
    public void setupCreatedAlarmsFunctionality() {
    }

    @Test
    public void createAlarm() {
    }

    //TODO: refactor
//    @Test
//    public void alarmWorksAsExpected() throws ParseException {
//        clock.setListOfAlarms(new ArrayList<>(){{add(alarm);}});
//        clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
////        assertTrue("Alarm doesn't match time of clock",
////                spyClock.getAlarmPanel().getAlarm().getTimeAsStr()
////                .equals(clock.getTimeAsStr()));
//        assertTrue("Alarm should be going off!", clock.getAlarmPanel().getAlarm().isAlarmGoingOff());
//        clock.tick();
//        assertTrue("Alarm should not be triggered to go off!", clock.getAlarmPanel().getAlarm().getTimeAsStr().equals(clock.getTimeAsStr()));
//    }

}