package v3;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidInputExceptionTest extends Exception {

	private static final long serialVersionUID = 1L;
	private Time.Month goodMonth, badMonth;
	private final String INVALID_MONTH = "Invalid month";

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	public void setUp() {
		goodMonth = Time.Month.AUGUST;
		badMonth = Time.Month.ERR;
	}

	@Test
	public void whenAValidMonthIsGivenReturnAValidInteger() {
		int month = 0;
        try {
        	switch (goodMonth) {
	            case JANUARY: month = 1; break;
	            case FEBRUARY: month = 2; break;
	            case MARCH: month = 3; break;
	            case APRIL: month = 4; break;
	            case MAY: month = 5; break;
	            case JUNE: month = 6; break;
	            case JULY: month = 7; break;
	            case AUGUST: month = 8; break;
	            case SEPTEMBER: month = 9; break;
	            case OCTOBER: month = 10; break;
	            case NOVEMBER: month = 11; break;
	            case DECEMBER: month = 12; break;
	            default: throw new InvalidInputException("Unknown month");
	        }
        } catch (InvalidInputException iie) {
        	fail("No exception should have been thrown");
        }
        assertTrue(month == 8);
	}

	@Test
	public void whenAnInvalidMonthIsGivenReturnAnInvalidInputException() {
		int month = 0;
        try {
        	switch (badMonth) {
	            case JANUARY: month = 1; break;
	            case FEBRUARY: month = 2; break;
	            case MARCH: month = 3; break;
	            case APRIL: month = 4; break;
	            case MAY: month = 5; break;
	            case JUNE: month = 6; break;
	            case JULY: month = 7; break;
	            case AUGUST: month = 8; break;
	            case SEPTEMBER: month = 9; break;
	            case OCTOBER: month = 10; break;
	            case NOVEMBER: month = 11; break;
	            case DECEMBER: month = 12; break;
	            default: throw new InvalidInputException("Unknown month");
	        }
        } catch (InvalidInputException iie) {
        	assertTrue("Unknown month".equals(iie.getMessage()));
        }
        
	}

	@Test
	public void testIIEIsCreated() {
		InvalidInputException iie = new InvalidInputException();
		assertNotNull(iie);
	}

	@Test
	public void test() {
		InvalidInputException iie = new InvalidInputException(new IllegalArgumentException(INVALID_MONTH, new IllegalArgumentException().getCause()));
		assertNotNull(iie.getCause());
	}

	@Test
	public void testIIEIsCreatedWithMessageAndCause() {
		InvalidInputException iie = new InvalidInputException(INVALID_MONTH, new IllegalArgumentException());
		assertNotNull(iie.getMessage());
		assertNotNull(iie.getCause());
	}

}
