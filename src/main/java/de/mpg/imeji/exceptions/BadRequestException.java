package de.mpg.imeji.exceptions;

import java.io.Serializable;

public class BadRequestException extends ImejiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3721639144291667847L;

	public BadRequestException(String message)
    {
        super(message);
    }
	

}
