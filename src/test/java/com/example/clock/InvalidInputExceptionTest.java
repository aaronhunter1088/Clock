package com.example.clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Month;

import static java.time.Month.*;
import static org.junit.Assert.*;

public class InvalidInputExceptionTest
{
    static { System.setProperty("appName", InvalidInputExceptionTest.class.getSimpleName()); }
    private static final Logger logger = LogManager.getLogger(InvalidInputExceptionTest.class);

    private Month goodMonth;
    private final String INVALID_MONTH = "Invalid month";

    @BeforeClass
    public static void beforeClass() { logger.info("Starting InvalidInputExceptionTest..."); }

    @Before
    public void beforeEach() {
        goodMonth = AUGUST;
    }

    @After
    public void afterEach() {}

    @Test
    public void whenAValidMonthIsGivenReturnAValidInteger()
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
                default -> throw new InvalidInputException("Unknown month");
            };
        } catch (InvalidInputException iie) {
            fail("No exception should have been thrown");
        }
        assertEquals(8, month);
    }

    @Test
    public void testIIEIsCreated()
    {
        InvalidInputException iie = new InvalidInputException();
        assertNotNull(iie);
    }

    @Test
    public void testIIEIsCreatedFromThrowable()
    {
        InvalidInputException iie = new InvalidInputException(new IllegalArgumentException(INVALID_MONTH, new IllegalArgumentException().getCause()));
        assertNotNull(iie.getCause());
    }

    @Test
    public void testIIEIsCreatedFromThrowableWithMessage()
    {
        InvalidInputException iie = new InvalidInputException(INVALID_MONTH, new IllegalArgumentException());
        assertNotNull(iie.getMessage());
        assertNotNull(iie.getCause());
    }

}

