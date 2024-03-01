package org.example.clock;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static java.time.DayOfWeek.*;

@RunWith(MockitoJUnitRunner.class)
public class AlarmPanelTest {

    private static Clock clock;
    private static Alarm alarm;

    @BeforeClass
    public static void setupBeforeClass() throws InterruptedException, ParseException, InvalidInputException
    {
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
    public void testSettingAudioStreamWorks()
    {
        AlarmPanel testAlarmPanel = createAndSetupAlarmPanel();

        assertTrue("Music player should be set", null != testAlarmPanel.getMusicPlayer());
    }

    @Test
    public void testMusicPlayerCanSoundAlarm() throws InterruptedException {
        AlarmPanel testAlarmPanel = createAndSetupAlarmPanel();
        ExecutorService executor = Executors.newCachedThreadPool();
        testAlarmPanel.setCurrentAlarmGoingOff(alarm);
        testAlarmPanel.triggerAlarm(executor);
        assertTrue("Alarm is going off", testAlarmPanel.isAlarmIsGoingOff());
        Thread.sleep(1000);
        testAlarmPanel.stopAlarm();
        assertFalse("Alarm is off", testAlarmPanel.isAlarmIsGoingOff());
        assertNull("Music Player is null", testAlarmPanel.getMusicPlayer());
    }

    @Test
    public void testCheckingWhichCheckBoxesAreCheckedWorks()
    {
        clock.getAlarmPanel().getMondayCheckBox().doClick();
        clock.getAlarmPanel().getTuesdayCheckBox().doClick();
        ArrayList<DayOfWeek> daysChecked = clock.getAlarmPanel().checkWhichCheckBoxesWereChecked();

        Predicate<DayOfWeek> predicate = p -> daysChecked.contains(MONDAY) && daysChecked.contains(TUESDAY);
        assertTrue("DaysChecked list size should be 2", daysChecked.size() == 2);
        assertTrue("daysChecked should contain only MONDAY and TUESDAY", daysChecked.stream().allMatch(predicate));
    }

    @Test
    public void alarmWorksAsExpected() {
        clock.setListOfAlarms(new ArrayList<Alarm>(){{add(alarm);}});
        clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
        assertTrue("Alarm should be going off!", alarm.isAlarmGoingOff());
        assertEquals("This 'alarm' is set as the current alarm going off", alarm, clock.getAlarmPanel().getCurrentAlarmGoingOff());
        assertTrue("Alarm should not be triggered to go off!", alarm.isAlarmGoingOff());
    }

    @Test
    public void testRangeIsCorrect()
    {
        assertFalse(12 <= 0);
        assertFalse(12 > 23);
    }
    // Helper methods
    public AlarmPanel createAndSetupAlarmPanel()
    {
        AlarmPanel testAlarmPanel = clock.getAlarmPanel();
        testAlarmPanel.setAlarm(alarm);
        testAlarmPanel.setupMusicPlayer();
        return testAlarmPanel;
    }
}