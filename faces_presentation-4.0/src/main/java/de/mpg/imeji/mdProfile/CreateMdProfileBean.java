package de.mpg.imeji.mdProfile;

public class CreateMdProfileBean extends MdProfileBean
{
    public CreateMdProfileBean()
    {
       super();
    }
    
    public String save()
    {
        System.out.println("CREATE PROFILE");
        return "pretty:";
    }
}
