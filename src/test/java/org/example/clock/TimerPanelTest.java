package org.example.clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.event.ActionEvent;

import static org.example.clock.ClockConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TimerPanelTest
{
    static { System.setProperty("appName", TimerPanelTest.class.getSimpleName()); }
    private static final Logger logger = LogManager.getLogger(TimerPanelTest.class);
    Clock clock;
    TimerPanel timer;

    @BeforeClass
    public static void beforeClass()
    {
        logger.info("Starting TimerPanelTest...");
    }

    @Before
    public void beforeEach()
    {
        clock = new Clock(true);
        timer = new TimerPanel(clock);
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    ActionEvent action;

    @Test
    public void validateFirstTextField()
    {
        timer.getHourField().setText(HOUR);
        assertTrue(timer.validateFirstTextField());

        timer.getHourField().setText("13");
        assertTrue(timer.validateFirstTextField());

        timer.getHourField().setText("24");
        assertFalse(timer.validateFirstTextField());

        timer.getHourField().setText("-1");
        assertFalse(timer.validateFirstTextField());

        timer.getHourField().setText(EMPTY);
        assertFalse(timer.validateFirstTextField());
    }

    @Test
    public void validateSecondTextField()
    {
        timer.getMinuteField().setText(MIN);
        assertTrue(timer.validateSecondTextField());

        timer.getMinuteField().setText("13");
        assertTrue(timer.validateSecondTextField());

        timer.getMinuteField().setText("60");
        assertFalse(timer.validateSecondTextField());

        timer.getMinuteField().setText("-1");
        assertFalse(timer.validateSecondTextField());

        timer.getMinuteField().setText(EMPTY);
        assertFalse(timer.validateSecondTextField());
    }

    @Test
    public void validateThirdTextField()
    {
        timer.getSecondField().setText(SEC);
        assertTrue(timer.validateThirdTextField());

        timer.getSecondField().setText("13");
        assertTrue(timer.validateThirdTextField());

        timer.getSecondField().setText("60");
        assertFalse(timer.validateThirdTextField());

        timer.getSecondField().setText("59");
        assertTrue(timer.validateThirdTextField());

        timer.getSecondField().setText(EMPTY);
        assertFalse(timer.validateThirdTextField());
    }

    @Test
    public void resetTimerFields()
    {
    }

    @Test
    public void performCountDownWith5Seconds() throws InterruptedException
    {
        timer.getHourField().setText("0");
        timer.getMinuteField().setText("0");
        timer.getSecondField().setText("5");
        timer.getTimerButton().setText(SET);
        timer.run(mock(ActionEvent.class));
        Thread.sleep(5000);
        assertEquals("Expected timer to be done", 0, Integer.parseInt(timer.getSecondField().getText()));
    }

    @Test
    public void pauseCountDown() throws InterruptedException
    {
        timer.getHourField().setText("0");
        timer.getMinuteField().setText("1");
        timer.getSecondField().setText("5");
        timer.getTimerButton().setText(SET);
        timer.run(mock(ActionEvent.class));
        Thread.sleep(5000);
        timer.run(mock(ActionEvent.class));

        assertEquals("After 5 seconds, 1 minute is shown", 1, Integer.parseInt(timer.getMinuteField().getText()));
        assertEquals("After 5 seconds, 0 seconds is shown", 0, Integer.parseInt(timer.getSecondField().getText()));
    }
}