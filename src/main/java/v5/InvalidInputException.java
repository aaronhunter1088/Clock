package v5;

@SuppressWarnings("unused")
/** This class is used to determine that an error
 * can occurred while the Clock was running. It is
 * a checked exception, meaning the clock will always
 * break, and this exception must be dealt with.
 *
 * "extends Exception says that this exception is a
 * Checked Exception: the compiler forces you to
 * handle these exceptions explicitly. Methods that
 * generate this exception must declare that this
 * exception s thrown.
 * It forces the programmer to deal with the exception."
 *
 * @author michael ball
 * @version 2.5
 */
public class InvalidInputException extends Exception {

	private static final long serialVersionUID = 1L;
	private String message;
	private Throwable cause;

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public InvalidInputException() { super(); }
	
	public InvalidInputException(String message) {
		super(message);
		setMessage(message);
	}
	
	public InvalidInputException(Throwable cause) {
		super(cause);
		setCause(cause);
	}
	
	public InvalidInputException(String message, Throwable cause) {
		super(message, cause);
		setMessage(message);
		setCause(cause);
	}
}
