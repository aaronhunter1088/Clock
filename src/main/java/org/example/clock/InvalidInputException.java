package org.example.clock;

/**
 * This class is used to determine that an error
 * can occurred while the Clock was running. It is
 * a checked exception, meaning the clock will always
 * break, and this exception must be dealt with.
 * "extends Exception says that this exception is a
 * Checked Exception: the compiler forces you to
 * handle these exceptions explicitly. Methods that
 * generate this exception must declare that this
 * exception is thrown.
 * It forces the programmer to deal with the exception."
 * @author michael ball
 * @version 2.7
 */
public class InvalidInputException extends Exception {

	private String message;
	private Throwable cause;

	@Override
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

	public InvalidInputException() { this(null, null); }
	public InvalidInputException(String message) { this(message, null); }
	public InvalidInputException(Throwable cause) { this(null, cause); }
	/**
	 * Main constructor for InvalidInputException
	 * @param message the message provided
	 * @param cause the reason why
	 */
	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
		setMessage(message);
		setCause(cause);
	}
}
