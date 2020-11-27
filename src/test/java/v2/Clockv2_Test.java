package v2;

import java.text.ParseException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import v2.Clockv2;
import v2.Time.*;

import static org.junit.jupiter.api.Assertions.*;


public class Clockv2_Test {
	private Clockv2 clock;
	
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {}
	
	@Test
	public void testDaylightSavingsTimeSpringForwardWorks() throws InterruptedException, ParseException {
		clock = new Clockv2(1, 59, 59, v2.Time.Month.MARCH, v2.Time.Day.SUNDAY.strValue, 10, 2019, v2.Time.AMPM.AM); // throws ParseException
		
		for (int i = 0; i < 1; i++) { // throws InterruptedException
			clock.tick();
			Thread.sleep(1000);
		}
		
		assertEquals(3, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AMPM.AM, clock.getAmpm());
		assertSame("March", clock.getMonth());
		assertSame("Sunday", clock.getDay());
		assertEquals(10, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.daylightSavingsTime);
		
		// validate that we are moving forward now
		for (int i = 0; i < 1; i++) {
			clock.tick(60, 60, 1); // 60 seconds and 60 minutes and one hour
			Thread.sleep(1000);
		}
		
		assertEquals(4, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(v2.Time.AMPM.AM, clock.getAmpm());
		assertSame("March", clock.getMonth());
		assertSame("Sunday", clock.getDay());
		assertEquals(10, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.daylightSavingsTime);
	}
	
	@Test
	public void testDaylightSavingsTimeFallBackWorksButContinuesNormallyThenOn() throws InterruptedException, ParseException {
		clock = new Clockv2(1, 59, 59, Time.Month.NOVEMBER, Time.Day.SUNDAY.strValue, 3, 2019, Time.AMPM.AM);
		
		for (int i = 0; i < 1; i++) {
			clock.tick();
			Thread.sleep(1000);
		}
		
		assertEquals(1, clock.getHours()); // should be 1, testing daylight savings time
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AMPM.AM, clock.getAmpm());
		assertSame(Time.Month.NOVEMBER.strValue, clock.getMonth());
		assertSame(Day.SUNDAY.strValue, clock.getDay());
		assertEquals(3, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.daylightSavingsTime);
		
		// validate that the clock moves forward
		for (int i = 0; i < 1; i++) {
			clock.tick(60, 60, 1); // 60 seconds and 60 minutes and one hour
			Thread.sleep(1000);
		}
		
		assertEquals(2, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AMPM.AM, clock.getAmpm());
		assertSame("November", clock.getMonth());
		assertSame("Sunday", clock.getDay());
		assertEquals(3, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.daylightSavingsTime);
	}
	
	@Test
	public void testUpdateTextArea() throws InterruptedException, ParseException {
		clock = new Clockv2(11, 59, 59, Time.Month.SEPTEMBER, "Monday", 30, 2019, Time.AMPM.PM);
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
		assertEquals(true, Clockv2.isALeapYear(2000));
		assertEquals(false, Clockv2.isALeapYear(2019));
	}
}

