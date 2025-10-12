package clock.exception;

import java.io.Serial;
import java.io.Serializable;

/**
 * InvalidInputException
 * <p>
 * This class is used to determine that an error
 * has occurred while initializing a Clock, Alarm or
 * a Timer. It is an unchecked exception.
 * It is thrown when the user enters invalid input
 * while creating a Clock, Alarm or Timer.
 * The application will account for this and will
 * report the error to the user as necessary.
 *
 * @author michael ball
 * @version 2.0
 */
public class InvalidInputException extends RuntimeException implements Serializable
{
	@Serial
	private static final long serialVersionUID = 2L;

	private String message;
	private Throwable cause;

	/**
	 * Default constructor for InvalidInputException
	 */
	public InvalidInputException()
	{ this(null, null); }

	/**
	 * Constructor for InvalidInputException with a message
	 * @param message the message provided
	 */
	public InvalidInputException(String message)
	{ this(message, null); }

	/**
	 * Constructor for InvalidInputException with a cause
	 * @param cause the reason why
	 */
	public InvalidInputException(Throwable cause)
	{ this(null, cause); }

	/**
	 * Main constructor for InvalidInputException
	 * @param message the message provided
	 * @param cause the reason why
	 */
	public InvalidInputException(String message, Throwable cause)
	{
		super(message, cause);
		setMessage(message);
		setCause(cause);
	}

	/** Get the message */
	public String getMessage() { return message; }
	/** Get the cause */
	public Throwable getCause() { return cause; }

	/** Set the message */
	public void setMessage(String message) { this.message = message; }
	/** Set the cause */
	public void setCause(Throwable cause) { this.cause = cause; }
}