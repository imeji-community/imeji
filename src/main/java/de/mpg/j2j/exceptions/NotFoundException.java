package de.mpg.j2j.exceptions;

/**
 * j2j Object not found {@link Exception}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class NotFoundException extends Exception
{
    /**
     * Constructor for new {@link NotFoundException}
     * 
     * @param message
     */
    public NotFoundException(String message)
    {
        super(message);
    }

    private static final long serialVersionUID = -6966574805131228401L;
}
