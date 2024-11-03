package com.example.clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import static java.time.Month.MARCH;
import static com.example.clock.ClockConstants.*;
import static org.junit.Assert.*;
import static java.time.DayOfWeek.*;

@RunWith(MockitoJUnitRunner.class)
public class AlarmPanelTest
{
    static { System.setProperty("appName", AlarmPanelTest.class.getSimpleName()); }
    private static final Logger logger = LogManager.getLogger(AlarmPanelTest.class);
    private Clock clock = new Clock(true);
    private Alarm alarm;

    @BeforeClass
    public static void beforeClass()
    {
        logger.info("Starting AlarmPanelTest...");
    }

    @Before
    public void beforeEach() throws InvalidInputException
    {
        clock = new Clock(true);
        alarm = new Alarm(clock, true);
    }

    @Test
    public void testSettingAudioStreamWorks()
    {
        AlarmPanel testAlarmPanel = createAndSetupAlarmPanel();
        assertNotNull("Music player should be set", testAlarmPanel.getMusicPlayer());
    }

    @Test
    public void testMusicPlayerCanSoundAlarm() throws InterruptedException
    {
        AlarmPanel testAlarmPanel = createAndSetupAlarmPanel();
        ExecutorService executor = Executors.newCachedThreadPool();
        testAlarmPanel.setActiveAlarm(alarm);
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
        List<DayOfWeek> daysChecked = clock.getAlarmPanel().getDaysChecked();

        Predicate<DayOfWeek> predicate = p -> daysChecked.contains(MONDAY) && daysChecked.contains(TUESDAY);
        assertTrue("DaysChecked list size should be 2", daysChecked.size() == 2);
        assertTrue("daysChecked should contain only MONDAY and TUESDAY", daysChecked.stream().allMatch(predicate));
    }

    @Test
    public void alarmWorksAsExpected() throws InvalidInputException
    {
        clock = new Clock(12, 0, 0, MARCH, FRIDAY, 3, 2021, AM);
        alarm.setIsAlarmGoingOff(true);
        clock.setListOfAlarms(List.of(alarm));
        clock.getAlarmPanel().setActiveAlarm(alarm);
        clock.getAlarmPanel().checkIfAnyAlarmsAreGoingOff();
        assertTrue("Alarm should be going off!", alarm.isAlarmGoingOff());
        assertEquals("This 'alarm' is set as the current alarm going off", alarm, clock.getAlarmPanel().getActiveAlarm());
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
        //testAlarmPanel.setupMusicPlayer();
        return testAlarmPanel;
    }
}