package v1;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class InvalidInputExceptionTest extends Exception {

	private static final long serialVersionUID = 1L;
	private Time goodMonth, badMonth;

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	public void setUp() throws Exception {
		goodMonth = Time.AUGUST;
		badMonth = Time.ERR;
		
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
	
	@SuppressWarnings("unused")
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

}
