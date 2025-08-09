package clock.entity;

import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link Timer} class
 *
 * @author Michael Ball
 * @version 2.0
 */
class TimerTest
{
    private static final Logger logger = LogManager.getLogger(TimerTest.class);

    private Clock clock;
    private Timer timer1, timer2;
    private List<Timer> timers = new ArrayList<>();

    @Mock
    ActionEvent actionEvent;

    @BeforeAll
    static void beforeClass()
    { logger.info("Starting {}...", TimerTest.class.getSimpleName()); }

    @BeforeEach
    void beforeEach()
    {
        clock = new Clock(true);
    }

    @AfterEach
    void afterEach()
    {}

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", TimerTest.class.getSimpleName()); }

    @Test
    @DisplayName("Create a Timer")
    void testCreatingATimer()
    {
        assertEquals("00:00:00", new clock.entity.Timer().toString());
    }

    @Test
    @DisplayName("Create a 1 hour Timer")
    void testCreateA1HourTimer()
    {
        timer1 = new clock.entity.Timer(1, 0, 0);

        assertEquals("01:00:00", timer1.toString(), "Strings don't match");
        assertSame(1, timer1.getHours());
        assertSame(0, timer1.getMinutes());
        assertSame(0, timer1.getSeconds());
        assertFalse(timer1.isTimerGoingOff());
        assertFalse(timer1.isPaused());
    }

    @Test
    @DisplayName("Create a 5 min Timer")
    void testCreatingA5MinuteTimer()
    {
        timer1 = new clock.entity.Timer(0, 5, 0, clock);
        timer2 = new clock.entity.Timer(0, 5, 0, "Two", clock);

        assertEquals("00:05:00", timer1.toString(), "Strings don't match");
        assertSame(0, timer1.getHours());
        assertSame(5, timer1.getMinutes());
        assertSame(0, timer1.getSeconds());
        assertFalse(timer1.isTimerGoingOff());
        assertFalse(timer1.isPaused());

        assertEquals("(Two) 00:05:00", timer2.toString(), "Strings don't match");
        assertSame(0, timer2.getHours());
        assertSame(5, timer2.getMinutes());
        assertSame(0, timer2.getSeconds());
        assertFalse(timer2.isTimerGoingOff());
        assertFalse(timer2.isPaused());
    }

    @Test
    @DisplayName("Create 2 Timers, Pause Both, Resume 1")
    void testCreateTwoTimers()
    {
        timer1 = new Timer(0, 4, 0, clock);
        timer2 = new Timer(0, 5, 0, clock);
        timers.add(timer1);
        timers.add(timer2);

        timer1.startTimer();
        sleep(1000); // timer1 now at 3:59, timer2 "doesn't exist yet"
        timer2.startTimer();
        sleep(1000); // timer1 now at 3:58, timer2 at 4:59

        timer1.pauseTimer(); // timer1 paused, timer2 at 4:58

        sleep(3000);

        timer2.pauseTimer();

        assertSame(3, timer1.getCountDown().getMinute());

        assertSame(4, timer2.getCountDown().getMinute());

        timer1.resumeTimer();

        assertFalse(timer1.isPaused(), "Timer1 should not be paused after resuming");
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 24, 25, 100 })
    @DisplayName("Create Invalid Timer by Hour")
    void testCreateInvalidTimerByHour(int hours)
    {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> new Timer(hours, 0, 0, clock));
        assertEquals("Hours must be between 0 and 12", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 100 })
    @DisplayName("Create Invalid Timer by Minute")
    void testCreateInvalidTimerByMinute(int minutes)
    {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> new Timer(1, minutes, 0, clock));
        assertEquals("Minutes must be between 0 and 59", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 100 })
    @DisplayName("Create Invalid Timer by Second")
    void testCreateInvalidTimerBySecond(int seconds)
    {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> new Timer(1, 1, seconds, clock));
        assertEquals("Seconds must be between 0 and 59", exception.getMessage());
    }

    @Test
    @DisplayName("Timer with name reaches zero")
    void testTimerReachesZero()
    {
        timer1 = new Timer(0, 0, 5, "Test Timer", clock);
        timer1.startTimer();
        sleep(6000); // wait for the timer to reach zero

        assertTrue(timer1.isTimerGoingOff(), "Timer should be going off");
        assertEquals("(Test Timer) 00:00:00", timer1.toString(), "Timer did not reach zero as expected");
    }

    @Test
    @DisplayName("Timer with 'no name' reaches zero")
    void testTimerWithNoNameReachesZero()
    {
        timer1 = new Timer(0, 0, 5, clock);
        timer1.startTimer();
        sleep(6000); // wait for the timer to reach zero

        assertTrue(timer1.isTimerGoingOff(), "Timer should be going off");
        assertEquals("00:00:00", timer1.toString(), "Timer did not reach zero as expected");
    }

    @Test
    @DisplayName("Timer with tabbed over 'no name' reaches zero")
    void testTimerWithTabbedOverNoNameReachesZero()
    {
        timer1 = new Timer(0, 0, 5, "Timer "+(Timer.timersCounter+1), clock);
        timer1.startTimer();
        sleep(6000); // wait for the timer to reach zero

        assertTrue(timer1.isTimerGoingOff(), "Timer should be going off");
        String expecting = "(Timer " + Timer.timersCounter + ") 00:00:00";
        assertEquals(expecting, timer1.toString(), "Timer did not match expecting");
    }

    @Test
    @DisplayName("Timer at zero is reset")
    void testTimerReachesZeroThenIsReset()
    {
        timer1 = new Timer(0, 0, 5, "Test Timer", clock);
        timer1.startTimer();
        sleep(6000); // wait for the timer to reach zero

        assertTrue(timer1.isTimerGoingOff(), "Timer should be going off");

        timer1.resetTimer();

        assertFalse(timer1.isTimerGoingOff(), "Timer should not be going off after reset");
        assertFalse(timer1.isHasBeenStarted(), "Timer should not have been started");
        assertFalse(timer1.isHasBeenTriggered(), "Timer should not have been triggered");
    }

    @Test
    @DisplayName("Timer is stopped midway")
    void testTimerStoppedMidway()
    {
        timer1 = new Timer(0, 0, 10, "Test Timer", clock);
        timer1.startTimer();
        sleep(3000); // wait for 3 seconds

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertTrue(timer1.isHasBeenStarted(), "Timer should have been started");
            assertFalse(timer1.isHasBeenTriggered(), "Timer should not have been triggered yet");
        });

        timer1.stopTimer();

        javax.swing.SwingUtilities.invokeLater(() -> {
            assertFalse(timer1.isHasBeenStarted(), "Timer should not be started after stopping");
            assertFalse(timer1.isHasBeenTriggered(), "Timer should not have been triggered after stopping");
            assertEquals("(Test Timer) 00:00:07", timer1.toString(), "Timer did not stop at the expected time");
        });
    }

    // TODO: Move to TimerPanelTest
//    @Test
//    @DisplayName("Create 2 Timers Using GUI")
//    void testCreateTwoTimersUsingGUI() throws InterruptedException, InvocationTargetException
//    {
//        AtomicReference<clock.entity.Timer> timer1 = new AtomicReference<>(new clock.entity.Timer(0, 4, 0, clock));
//        AtomicReference<clock.entity.Timer> timer2 = new AtomicReference<>(new clock.entity.Timer(0, 5, 0, clock));
//
//        openTimerPanel();
//
//        SwingUtilities.invokeLater(() -> {
//            clock.getTimerPanel().getHourField().grabFocus();
//            clock.getTimerPanel().getHourField().setText(Integer.toString(timer1.get().getHours()));
//            //sleep(2000);
//            clock.getTimerPanel().getMinuteField().grabFocus();
//            clock.getTimerPanel().getMinuteField().setText(Integer.toString(timer1.get().getMinutes()));
//            //sleep(2000);
//            clock.getTimerPanel().getSecondField().grabFocus();
//            clock.getTimerPanel().getTimerButton().setEnabled(clock.getTimerPanel().validTextFields());
//            //sleep(2000);
//            clock.getTimerPanel().getTimerButton().doClick();
//
//            clock.getTimerPanel().getHourField().grabFocus();
//            clock.getTimerPanel().getHourField().setText(Integer.toString(timer2.get().getHours()));
//            //sleep(2000);
//            clock.getTimerPanel2().getMinuteField().grabFocus();
//            clock.getTimerPanel2().getMinuteField().setText(Integer.toString(timer2.get().getMinutes()));
//            //sleep(2000);
//            clock.getTimerPanel2().getSecondField().grabFocus();
//            clock.getTimerPanel2().getTimerButton().setEnabled(clock.getTimerPanel2().validTextFields());
//            //sleep(2000);
//            clock.getTimerPanel2().getTimerButton().doClick();
//
//            try {
//                new Thread().sleep(1000); // timer1 now at 3:58, timer2 at 4:59
//                clock.getTimerPanel2().getActiveTimers().get(0).pauseTimer(); // timer1 paused, timer2 at 4:58
//                new Thread().sleep(3000);
//
//                timer1.set(clock.getTimerPanel2().getActiveTimers().get(0));
//                timer2.set(clock.getTimerPanel2().getActiveTimers().get(1));
//
//                assertSame(0, timer1.get().getHours());
//                assertSame(3, timer1.get().getMinutes());
//                assertSame(58, timer1.get().getSeconds());
//
//                assertSame(0, timer2.get().getHours());
//                assertSame(4, timer2.get().getMinutes());
//                assertSame(55, timer2.get().getSeconds());
//            }
//            catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }

    // Helper methods
    private void sleep(int time)
    {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
