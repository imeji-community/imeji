package de.mpg.imeji.beans;

import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.AdminController;

public class AdminInterfaceBean
{
    
    private String username;
    private String password;
    
    public String cleanGraph() throws Exception
    {
        checkUsername();
        AdminController ac = new AdminController(null);
        ac.cleanGraph();
        BeanHelper.info("Graph cleaned up");
        return "pretty:";
        
    }
    
    private void checkUsername() throws Exception
    {
        if (this.username.equals("admin") && this.password.equals("noSciDoc"))
        {
            return;
        }
        else
        {
            throw new Exception("Authentication failed");
        }
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }
}
