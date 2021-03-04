package v4;

import org.junit.Test;
import org.mockito.Mock;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TimerPanelTest extends Object
{
    TimerPanel timer = new TimerPanel();

    @Mock
    ActionEvent action;

    public TimerPanelTest() throws ParseException {
    }

    @Test
    public void validateFirstTextField() {
    }

    @Test
    public void validateSecondTextField() {
    }

    @Test
    public void validateThirdTextField() {
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
        timer.startOrPauseTimer(action);
        Thread.sleep(5000);
        assertEquals("Expected timer to be done", 0, Integer.parseInt(timer.getJTextField3().getText()));
    }

    @Test
    public void pauseCountDown() throws InterruptedException {
        timer.getJTextField1().setText("0");
        timer.getJTextField2().setText("1");
        timer.getJTextField3().setText("5");
        timer.getTimerButton().setText("Set");
        timer.startOrPauseTimer(action);
        Thread.sleep(5000);
        timer.startOrPauseTimer(action);

        assertEquals("After 5 seconds, 1 minute is shown", 1, Integer.parseInt(timer.getJTextField2().getText()));
        assertEquals("After 5 seconds, 0 seconds is shown", 0, Integer.parseInt(timer.getJTextField3().getText()));
    }
}