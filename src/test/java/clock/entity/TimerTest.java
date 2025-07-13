package clock.entity;

import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static org.junit.jupiter.api.Assertions.*;

class TimerTest
{
    static { System.setProperty("appName", TimerTest.class.getSimpleName()); }
    private static final Logger logger = LogManager.getLogger(TimerTest.class);

    private Clock clock;

    @Mock
    ActionEvent actionEvent;

    @BeforeAll
    static void beforeAll()
    { logger.info("Starting TimerTest..."); }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock();
        clock.setTestingClock(true);
        clock.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @AfterEach
    void afterEach()
    {
        if (clock != null) {
            logger.info("Test complete. Closing the clock...");
            // Create a WindowEvent with WINDOW_CLOSING event type
            WindowEvent windowClosing = new WindowEvent(clock, WindowEvent.WINDOW_CLOSING);

            // Dispatch the event to the JFrame instance
            clock.dispatchEvent(windowClosing);

            // Ensure the clock is no longer visible
            assertFalse(clock.isVisible());

            // Dispose of the JFrame to release resources
            clock.dispose();
        }
    }

    @AfterAll
    static void afterAll()
    { logger.info("Concluding TimerTest."); }

    @Test
    @DisplayName("Create a Timer")
    void testCreatingATimer() throws InvalidInputException
    {
        assertNotEquals(null, new clock.entity.Timer());
        assertEquals("00:00:00", new clock.entity.Timer().toString());
    }

    @Test
    @DisplayName("Create a 5 min Timer")
    void testCreatingA5MinuteTimer() throws InvalidInputException
    {
        clock.entity.Timer timer = new clock.entity.Timer(0, 5, 0, clock);

        assertEquals("00:05:00", timer.toString(), "Strings don't match");
        assertSame(0, timer.getHour());
        assertSame(5, timer.getMinute());
        assertSame(0, timer.getSecond());
        assertFalse(timer.isTimerGoingOff());
        assertTrue(timer.isPaused());
    }

    @Test
    @DisplayName("Create 2 Timers")
    void testCreateTwoTimers() throws InvalidInputException, InterruptedException
    {
        clock.entity.Timer timer1 = new Timer(0, 4, 0, clock);
        clock.entity.Timer timer2 = new clock.entity.Timer(0, 5, 0, clock);

        clock.getTimerPanel2().startTimer(timer1);
        clock.getTimerPanel2().startTimer(timer2); // timer1 now at 3:59, timer2 at 5:00
        // count down from 10 to 0 but don't do Thread.sleep
        sleep(1000); // timer1 now at 3:58, timer2 at 4:59

        timer1.pauseTimer(); // timer1 paused, timer2 at 4:58

        sleep(3000);

        assertSame(0, timer1.getHour());
        assertSame(3, timer1.getMinute());
        assertSame(58, timer1.getSecond());

        assertSame(0, timer2.getHour());
        assertSame(4, timer2.getMinute());
        assertSame(55, timer2.getSecond());
    }

    @Test
    @DisplayName("Create 2 Timers Using GUI")
    void testCreateTwoTimersUsingGUI() throws InvalidInputException, InterruptedException, InvocationTargetException
    {
        AtomicReference<clock.entity.Timer> timer1 = new AtomicReference<>(new clock.entity.Timer(0, 4, 0, clock));
        AtomicReference<clock.entity.Timer> timer2 = new AtomicReference<>(new clock.entity.Timer(0, 5, 0, clock));

        openTimerPanel();

        SwingUtilities.invokeLater(() -> {
            clock.getTimerPanel2().getHourField().grabFocus();
            clock.getTimerPanel2().getHourField().setText(Integer.toString(timer1.get().getHour()));
            //sleep(2000);
            clock.getTimerPanel2().getMinuteField().grabFocus();
            clock.getTimerPanel2().getMinuteField().setText(Integer.toString(timer1.get().getMinute()));
            //sleep(2000);
            clock.getTimerPanel2().getSecondField().grabFocus();
            clock.getTimerPanel2().getTimerButton().setEnabled(clock.getTimerPanel2().validTextFields());
            //sleep(2000);
            clock.getTimerPanel2().getTimerButton().doClick();

            clock.getTimerPanel2().getHourField().grabFocus();
            clock.getTimerPanel2().getHourField().setText(Integer.toString(timer2.get().getHour()));
            //sleep(2000);
            clock.getTimerPanel2().getMinuteField().grabFocus();
            clock.getTimerPanel2().getMinuteField().setText(Integer.toString(timer2.get().getMinute()));
            //sleep(2000);
            clock.getTimerPanel2().getSecondField().grabFocus();
            clock.getTimerPanel2().getTimerButton().setEnabled(clock.getTimerPanel2().validTextFields());
            //sleep(2000);
            clock.getTimerPanel2().getTimerButton().doClick();

            try {
                new Thread().sleep(1000); // timer1 now at 3:58, timer2 at 4:59
                clock.getTimerPanel2().getActiveTimers().get(0).pauseTimer(); // timer1 paused, timer2 at 4:58
                new Thread().sleep(3000);

                timer1.set(clock.getTimerPanel2().getActiveTimers().get(0));
                timer2.set(clock.getTimerPanel2().getActiveTimers().get(1));

                assertSame(0, timer1.get().getHour());
                assertSame(3, timer1.get().getMinute());
                assertSame(58, timer1.get().getSecond());

                assertSame(0, timer2.get().getHour());
                assertSame(4, timer2.get().getMinute());
                assertSame(55, timer2.get().getSecond());
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Helper methods
    private void tick(int times)
    {
        for (int i=0; i<times; i++) {
            clock.ticking();
        }
    }

    private void openTimerPanel() throws InterruptedException
    {
        clock.getClockMenuBar().getFeaturesMenu().getItem(3).doClick(); // click on Timer
        new Thread().sleep(1000);
    }

    private void sleep(int time)
    {
        try { new Thread().sleep(time); }
        catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
