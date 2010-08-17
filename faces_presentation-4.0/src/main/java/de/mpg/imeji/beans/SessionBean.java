package de.mpg.imeji.beans;

import de.mpg.jena.vo.User;

public class SessionBean
{
    private User user = null;
    
    public SessionBean()
    {
	// TODO Auto-generated constructor stub
    }

    /**
     * @return the user
     */
    public User getUser()
    {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    
}
