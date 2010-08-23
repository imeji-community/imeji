package de.mpg.imeji.mdProfile;

import javax.faces.context.FacesContext;

public class CreateMdProfileBean extends MdProfileBean
{
    public CreateMdProfileBean()
    {
       super();
       if ("1".equals(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reset")))
       {
           this.reset();
       }
    }
    
    public String save()
    {
        System.out.println("CREATE PROFILE");
        return "pretty:";
    }
}
