package v1;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


public class Clockv1_Test {
	private Clock clock;
	
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {}
	
	@Test
	public void testDaylightSavingsTimeSpringForwardWorks() throws InterruptedException, ParseException {
		clock = new Clock(1, 59, 59, "March", "Sunday", 10, 2019, Time.AM); // throws ParseException
		
		for (int i = 0; i < 1; i++) { // throws InterruptedException
			clock.tick();
			Thread.sleep(1000);
		}
		
		assertEquals(3, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AM, clock.getTime());
		assertSame("March", clock.getMonth());
		assertSame("Sunday", clock.getDay());
		assertEquals(10, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.daylightSavingsTime);
		
		// validate that we are moving forward now
		for (int i = 0; i < 3600; i++) {
			clock.tick(); // 60 seconds and 60 minutes and one hour
			Thread.sleep(1000);
		}
		
		assertEquals(4, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AM, clock.getTime());
		assertSame("March", clock.getMonth());
		assertSame("Sunday", clock.getDay());
		assertEquals(10, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.daylightSavingsTime);
	}
	
	@Test
	public void testDaylightSavingsTimeFallBackWorksButContinuesNormallyThenOn() throws InterruptedException, ParseException {
		clock = new Clock(1, 59, 59, "November", "Sunday", 3, 2019, Time.AM);
		
		for (int i = 0; i < 1; i++) {
			clock.tick();
			Thread.sleep(1000);
		}
		
		assertEquals(1, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AM, clock.getTime());
		assertSame("November", clock.getMonth());
		assertSame("Sunday", clock.getDay());
		assertEquals(3, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.daylightSavingsTime);
		
		// validate that the clock moves forward
		for (int i = 0; i < 3600; i++) {
			clock.tick(); // 60 seconds and 60 minutes and one hour
			Thread.sleep(1000);
		}
		
		assertEquals(2, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AM, clock.getTime());
		assertSame("November", clock.getMonth());
		assertSame("Sunday", clock.getDay());
		assertEquals(3, clock.getDate());
		assertEquals(2019, clock.getYear());
		assertEquals(false, clock.daylightSavingsTime);
	}
	
	@Test
	public void testUpdateTextArea() throws InterruptedException, ParseException {
		clock = new Clock(11, 59, 59, "September", "Monday", 30, 2019, Time.PM);
		for (int i = 0; i < 1; i++) {
			clock.tick();
			Thread.sleep(1000);
		}
		assertEquals(12, clock.getHours());
		assertEquals(0, clock.getMinutes());
		assertEquals(0, clock.getSeconds());
		assertEquals(Time.AM, clock.getTime());
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

