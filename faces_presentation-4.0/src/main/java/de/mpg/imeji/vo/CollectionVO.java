package de.mpg.imeji.vo;

import de.mpg.jena.vo.CollectionImeji;

public class CollectionVO extends CollectionImeji
{
    private MdProfileVO profile = null;
    
    public CollectionVO()
    {
	profile = new MdProfileVO();
    }
    
    public CollectionVO(CollectionImeji ci)
    {
	this.setId(ci.getId());
	this.setImages(ci.getImages());
	this.setMetadata(ci.getMetadata());
	this.setMetadataDSP(ci.getMetadataDSP());
	this.setMetadataSchema(ci.getMetadataSchema());
	this.setProperties(ci.getProperties());
    }
    
    public String getIdAsString()
    {
	return this.getId().getPath();
    }

    /**
     * @return the size
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
        
}
