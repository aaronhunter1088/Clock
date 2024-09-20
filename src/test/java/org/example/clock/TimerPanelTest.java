package org.example.clock;

import org.junit.Test;
import org.mockito.Mock;

import java.awt.event.ActionEvent;
import java.text.ParseException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TimerPanelTest {
    Clock clock = new Clock();
    TimerPanel timer = new TimerPanel(clock);

    @Mock
    ActionEvent action;

    public TimerPanelTest() throws ParseException, InvalidInputException {
    }

    @Test
    public void validateFirstTextField()
    {
        timer.getJTextField1().setText("Hour");
        assertTrue(timer.validateFirstTextField());

        timer.getJTextField1().setText("13");
        assertTrue(timer.validateFirstTextField());

        timer.getJTextField1().setText("24");
        assertFalse(timer.validateFirstTextField());

        timer.getJTextField1().setText("-1");
        assertFalse(timer.validateFirstTextField());

        timer.getJTextField1().setText("");
        assertFalse(timer.validateFirstTextField());
    }

    @Test
    public void validateSecondTextField()
    {
        timer.getJTextField2().setText("Min");
        assertTrue(timer.validateSecondTextField());

        timer.getJTextField2().setText("13");
        assertTrue(timer.validateSecondTextField());

        timer.getJTextField2().setText("60");
        assertFalse(timer.validateSecondTextField());

        timer.getJTextField2().setText("-1");
        assertFalse(timer.validateSecondTextField());

        timer.getJTextField2().setText("");
        assertFalse(timer.validateSecondTextField());
    }

    @Test
    public void validateThirdTextField()
    {
        timer.getJTextField3().setText("Sec");
        assertTrue(timer.validateThirdTextField());

        timer.getJTextField3().setText("13");
        assertTrue(timer.validateThirdTextField());

        timer.getJTextField3().setText("60");
        assertFalse(timer.validateThirdTextField());

        timer.getJTextField3().setText("59");
        assertTrue(timer.validateThirdTextField());

        timer.getJTextField3().setText("");
        assertFalse(timer.validateThirdTextField());
    }

    @Test
    public void resetTimerFields() {
    }

    @Test
    public void performCountDownWith5Seconds() throws InterruptedException
    {
        timer.getJTextField1().setText("0");
        timer.getJTextField2().setText("0");
        timer.getJTextField3().setText("5");
        timer.getTimerButton().setText("Set");
        timer.run(mock(ActionEvent.class));
        Thread.sleep(5000);
        assertEquals("Expected timer to be done", 0, Integer.parseInt(timer.getJTextField3().getText()));
    }

    @Test
    public void pauseCountDown() throws InterruptedException {
        timer.getJTextField1().setText("0");
        timer.getJTextField2().setText("1");
        timer.getJTextField3().setText("5");
        timer.getTimerButton().setText("Set");
        timer.run(mock(ActionEvent.class));
        Thread.sleep(5000);
        timer.run(mock(ActionEvent.class));

        assertEquals("After 5 seconds, 1 minute is shown", 1, Integer.parseInt(timer.getJTextField2().getText()));
        assertEquals("After 5 seconds, 0 seconds is shown", 0, Integer.parseInt(timer.getJTextField3().getText()));
    }
}