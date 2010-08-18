package de.mpg.imeji.vo;

import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Person;

public class CollectionVO extends CollectionImeji
{
    private MdProfileVO profile = null;
    private boolean selected = false;
    
    public CollectionVO()
    {
	profile = new MdProfileVO();
    }

    public String getIdAsString()
    {
	return this.getId().getPath();
    }

    /**
     * Return the number of images
     */
    public int getSize()
    {
        return  this.getImages().size();
    }

    /**
     * @return the profile
     */
    public MdProfileVO getProfile()
    {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(MdProfileVO profile)
    {
        this.profile = profile;
    }

    /**
     * @return the selected
     */
    public boolean isSelected()
    {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
    
    public String getPersonString()
    {
        String personString = "";
       for(Person p:  getMetadata().getPersons())
       {
           personString+=p.getFamilyName() +", " +p.getGivenName();
           
       }
       return personString;
    }
    
    
       
    
}
