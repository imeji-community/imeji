/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.beans;

import de.mpg.imeji.logic.controller.AdminController;
import de.mpg.imeji.presentation.util.BeanHelper;

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
