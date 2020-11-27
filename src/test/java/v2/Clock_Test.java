package v2;

import java.text.ParseException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class Clock_Test {
	private Clock clock;
	
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {}
	
	@Test
	public void testDaylightSavingsTimeSpringForwardWorks() throws InterruptedException, ParseException {
		clock = new Clock(1, 59, 59, Time.Month.MARCH, Time.Day.SUNDAY, 10, 2019, Time.AMPM.AM); // throws ParseException
		
		for (int i = 0; i < 1; i++) { // throws InterruptedException
			clock.tick();
			Thread.sleep(1000);
		}
		
		assertEquals(3, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AMPM.AM, clock.getAmpm());
		assertSame(Time.Month.MARCH, clock.getMonth());
		assertSame(Time.Day.SUNDAY, clock.getDay());
		assertEquals(10, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.isDaylightSavingsTime());
		
		// validate that we are moving forward now
		for (int i = 0; i < 1; i++) {
			clock.tick(60, 60, 1); // 60 seconds and 60 minutes and one hour
			Thread.sleep(1000);
		}
		
		assertEquals(4, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(v2.Time.AMPM.AM, clock.getAmpm());
		assertSame(Time.Month.MARCH, clock.getMonth());
		assertSame(Time.Day.SUNDAY, clock.getDay());
		assertEquals(10, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.isDaylightSavingsTime());
	}
	
	@Test
	public void testDaylightSavingsTimeFallBackWorksButContinuesNormallyThenOn() throws InterruptedException, ParseException {
		clock = new Clock(1, 59, 59, Time.Month.NOVEMBER, Time.Day.SUNDAY, 3, 2019, Time.AMPM.AM);
		
		for (int i = 0; i < 1; i++) {
			clock.tick();
			Thread.sleep(1000);
		}
		
		assertEquals(1, clock.getHours()); // should be 1, testing daylight savings time
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AMPM.AM, clock.getAmpm());
		assertSame(Time.Month.NOVEMBER, clock.getMonth());
		assertSame(Time.Day.SUNDAY, clock.getDay());
		assertEquals(3, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.isDaylightSavingsTime());
		
		// validate that the clock moves forward
		for (int i = 0; i < 1; i++) {
			clock.tick(60, 60, 1); // 60 seconds and 60 minutes and one hour
			Thread.sleep(1000);
		}
		
		assertEquals(2, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AMPM.AM, clock.getAmpm());
		assertSame(Time.Month.NOVEMBER, clock.getMonth());
		assertSame(Time.Day.SUNDAY, clock.getDay());
		assertEquals(3, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.isDaylightSavingsTime());
	}
	
	@Test
	public void testUpdateTextArea() throws InterruptedException, ParseException {
		clock = new Clock(11, 59, 59, Time.Month.SEPTEMBER, Time.Day.MONDAY, 30, 2019, Time.AMPM.PM);
		for (int i = 0; i < 1; i++) {
			clock.tick();
			Thread.sleep(1000);
		}
		assertEquals(12, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AMPM.AM, clock.getAmpm());
		assertSame("October", clock.getMonth());
		assertSame("Tuesday", clock.getDay());
		assertEquals(1, clock.getDate());
		assertEquals(2019, clock.getYear());
	}
	
	@Test
	public void testIsALeapYear() {
		assertEquals(true, Clock.isALeapYear(2000));
		assertEquals(false, Clock.isALeapYear(2019));
	}
}

