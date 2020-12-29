package v4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AlarmPanelTest extends Object {

    private static Clock clock;
    private static Alarm alarm;

    @BeforeClass
    public static void setupBeforeClass() throws InterruptedException, ParseException {
        clock = new Clock();
        clock.setVisible(true);
        clock.getContentPane().setBackground(Color.BLACK);
        clock.setSize(Clock.defaultSize);
        clock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clock.setBounds(200, 200, 700, 300);
        clock.tick();

        alarm = new Alarm(clock, clock.getHours(), true);
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

    @Test
    public void alarmWorksAsExpected() {
        clock.setListOfAlarms(new ArrayList<>(){{add(alarm);}});
        clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
        assertTrue("Alarm should be going off!", alarm.isAlarmGoingOff());
        assertEquals("This 'alarm' is set as the current alarm going off", alarm, clock.getAlarmPanel().getCurrentAlarmGoingOff());
        assertTrue("Alarm should not be triggered to go off!", alarm.isAlarmGoingOff());
    }

}