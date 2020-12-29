package v4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClockTest {

    @Test
    public void test() {}

//    Clock clock = spy(Clock.class);
//    @Rule public MockitoRule mockito = MockitoJUnit.rule().strictness(Strictness.LENIENT);
//    @Test
//    public void tickUpdatesClockValuesWhenTimeIs420() throws InvalidInputException {
//        //clock = new Clock(4, 20, 0, Time.Month.NOVEMBER, Time.Day.SUNDAY, 29, 2020, Time.AMPM.AM);
//        doReturn(0).when(clock).getSeconds();
//        doReturn(false).when(clock).isDateChanged(); // called 3 times
//        doReturn(NOVEMBER).when(clock).getMonth();
//        doReturn(29).when(clock).getDate();
//        doReturn(AM).when(clock).getAMPM();
//        doReturn(false).when(clock).isShowMilitaryTime();
//        doReturn(false).when(clock).isDaylightSavingsTime();
//        doReturn("04:20:00 AM").when(clock).getTimeAsStr();
//        doReturn(Calendar.getInstance()).when(clock).getCalendar();
//        doReturn(4).when(clock).convertTimeMonthToInt(clock.month);
//        doReturn(false).when(clock).isTodayDaylightSavingsTime();
//
//        for(int i=1; i<2; i++)
//        {
//            clock.tick();
//        }
//    }
}