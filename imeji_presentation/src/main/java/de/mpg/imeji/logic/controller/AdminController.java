/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.logic.controller;

import de.mpg.imeji.logic.vo.User;

public class AdminController extends ImejiController
{
    public AdminController(User user)
    {
        super(user);
    }
    
    public void cleanGraph()
    {
        //super.cleanGraph();
    }

    @Override
    protected String getSpecificFilter() throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getSpecificQuery() throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }
}
