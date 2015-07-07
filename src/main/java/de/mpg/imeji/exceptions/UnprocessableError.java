package de.mpg.imeji.exceptions;

public class UnprocessableError extends ImejiException {
  /**
	 * 
	 */
  private static final long serialVersionUID = -2949658202758865427L;

  public UnprocessableError(String message) {
    super(message);
  }

  public UnprocessableError(String message, Throwable e) {
    super(message, e);
  }
}
