package v3;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static v3.Time.AMPM.AM;
import static v3.Time.Month.NOVEMBER;

@RunWith(MockitoJUnitRunner.class)
public class ClockTest extends Object {

    Clock clock = spy(Clock.class);

    @Test
    public void tickUpdatesClockValuesWhenTimeIs420() throws ParseException, InterruptedException, InvalidInputException {
        //clock = new Clock(4, 20, 0, Time.Month.NOVEMBER, Time.Day.SUNDAY, 29, 2020, Time.AMPM.AM);
        doReturn(0).when(clock).getSeconds();
        doReturn(false).when(clock).isDateChanged(); // called 3 times
        doReturn(NOVEMBER).when(clock).getMonth();
        doReturn(29).when(clock).getDate();
        doReturn(AM).when(clock).getAMPM();
        doReturn(false).when(clock).isShowMilitaryTime();
        doReturn(false).when(clock).isDaylightSavingsTime();
        doReturn("04:20:00 AM").when(clock).getTimeAsStr();
        doReturn(Calendar.getInstance()).when(clock).getCalendar();
        doCallRealMethod().when(clock).convertTimeMonthToInt(clock.month);
        doReturn(false).when(clock).isTodayDaylightSavingsTime();

        for(int i=1; i<2; i++)
        {
            clock.tick();
        }

        Mockito.verify(clock, times(1))
            .setDefaultClockValues(0, 0, 0, null, null, 0, 0, null);
    }
}