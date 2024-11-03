package com.example.clock;

/**
 * This class is used to determine that an error
 * has occurred while initializing Clock. It is
 * a checked exception, meaning the clock will always
 * break, and this exception must be dealt with.
 * "extends Exception says that this exception is a
 * Checked Exception: the compiler forces you to
 * handle these exceptions explicitly. Methods that
 * generate this exception must declare that this
 * exception is thrown.
 * It forces the programmer to deal with the exception."
 *
 * Example: Supplying a negative minutes value or a value
 * greater than 59 will throw this exception. The rule of
 * thumb being that a proper minute value is between 0 and 59,
 * and once it becomes 60, it rolls back to 0, and increases
 * the hour by 1. During initialization, none of the logic
 * which determines whether or not to increase any hour
 * value (in this example) executes.
 * @author michael ball
*  @version 2.8
 */
public class InvalidInputException extends Exception
{
	private String message;
	private Throwable cause;

	/**
	 * Default constructor for InvalidInputException
	 */
	InvalidInputException()
	{ this(null, null); }

	/**
	 * Constructor for InvalidInputException with a message
	 * @param message the message provided
	 */
	InvalidInputException(String message)
	{ this(message, null); }

	/**
	 * Constructor for InvalidInputException with a cause
	 * @param cause the reason why
	 */
	InvalidInputException(Throwable cause)
	{ this(null, cause); }

	/**
	 * Main constructor for InvalidInputException
	 * @param message the message provided
	 * @param cause the reason why
	 */
	InvalidInputException(String message, Throwable cause)
	{
		super(message, cause);
		setMessage(message);
		setCause(cause);
	}

	public String getMessage() {
		return message;
	}
	public Throwable getCause() {
		return cause;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public void setCause(Throwable cause) {
		this.cause = cause;
	}
}