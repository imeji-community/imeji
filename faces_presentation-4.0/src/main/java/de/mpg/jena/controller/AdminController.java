package de.mpg.jena.controller;

import de.mpg.jena.vo.User;

public class AdminController extends ImejiController
{
    public AdminController(User user)
    {
        super(user);
    }
    
    public void cleanGraph()
    {
        super.cleanGraph();
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
