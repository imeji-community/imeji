package de.mpg.imeji.exceptions;

public class ImejiException extends Exception  {
	private static final long serialVersionUID = -1024323233094119992L;

		public ImejiException(String message)
	    {
	        super(message);
	        
	    }
		
		public ImejiException(String message, Throwable e) {
			super(message, e);
		}

}
