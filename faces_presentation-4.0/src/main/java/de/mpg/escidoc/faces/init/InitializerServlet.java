package de.mpg.escidoc.faces.init;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class InitializerServlet extends HttpServlet
{

    @Override
    public void init() throws ServletException
    {
        super.init();
        //ApplicationBean applicationBean = (ApplicationBean) BeanHelper.getApplicationBean(ApplicationBean.class);
    }
    
}
