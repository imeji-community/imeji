package de.mpg.j2j.exceptions;

public class AlreadyExistsException extends Exception
{
    private static final long serialVersionUID = 4123172628332785494L;

    public AlreadyExistsException(String message)
    {
        super(message);
    }
}
