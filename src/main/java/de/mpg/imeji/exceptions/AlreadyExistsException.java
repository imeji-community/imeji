package de.mpg.imeji.exceptions;

import com.hp.hpl.jena.Jena;

public class AlreadyExistsException extends ImejiException
{
    private static final long serialVersionUID = 4123172628332785494L;

    /**
     * Constructor for a new {@link AlreadyExistsException}
     * 
     * @param message
     */
    public AlreadyExistsException(String message)
    {
        super(message);
    }
    
}
