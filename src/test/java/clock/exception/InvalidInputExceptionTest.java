package clock.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.time.Month;

import static java.time.Month.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link InvalidInputException} class
 *
 * @author michael ball
 * @version since 2.0
 */
class InvalidInputExceptionTest
{
    private static final Logger logger = LogManager.getLogger(InvalidInputExceptionTest.class);

    private Month goodMonth;
    private final String INVALID_MONTH = "Invalid month";

    @BeforeAll
    static void beforeClass() { logger.info("Starting InvalidInputExceptionTest..."); }

    @BeforeEach
    void beforeEach() {
        goodMonth = AUGUST;
    }

    @AfterEach
    void afterEach() {}

    @AfterAll
    static void afterAll() { logger.info("Concluding {}", InvalidInputExceptionTest.class.getSimpleName()); }

    @Test
    void whenAValidMonthIsGivenReturnAValidInteger()
    {
        int month = 0;
        try {
            month = switch (goodMonth) {
                case JANUARY -> 1;
                case FEBRUARY -> 2;
                case MARCH -> 3;
                case APRIL -> 4;
                case MAY -> 5;
                case JUNE -> 6;
                case JULY -> 7;
                case AUGUST -> 8;
                case SEPTEMBER -> 9;
                case OCTOBER -> 10;
                case NOVEMBER -> 11;
                case DECEMBER -> 12;
            };
        } catch (IllegalArgumentException iae) {
            fail("No exception should have been thrown");
        }
        assertEquals(8, month);
    }

    @Test
    void testIIEIsCreated()
    {
        InvalidInputException iie = new InvalidInputException();
        assertNotNull(iie);
    }

    @Test
    void testIIEIsCreatedFromThrowable()
    {
        InvalidInputException iie = new InvalidInputException(new IllegalArgumentException(INVALID_MONTH, new IllegalArgumentException().getCause()));
        assertNotNull(iie.getCause());
    }

    @Test
    void testIIEIsCreatedFromThrowableWithMessage()
    {
        InvalidInputException iie = new InvalidInputException(INVALID_MONTH, new IllegalArgumentException());
        assertNotNull(iie.getMessage());
        assertNotNull(iie.getCause());
    }

}

