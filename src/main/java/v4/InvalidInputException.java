package v4;

/*  extends Exception says that this exception is a Checked Exception:
	the compiler forces you to handle these exceptions explicitly. 
	Methods that generate this exception must declare that this exception
	is thrown. 
	It forces the programmer to deal with the exception. 
*/
@SuppressWarnings("unused")
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
