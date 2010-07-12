package de.mpg.escidoc.faces.mdProfile.beans;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.faces.mdProfile.beans.MdProfileBean.MetadataBean;
import de.mpg.escidoc.faces.metadata.Metadata;

public class MdProfileSession
{
    private List<Metadata> metadataList = null;
    private List<MetadataBean> mdProfile = null;
    private String profileName = "";
    
    public MdProfileSession()
    {
	mdProfile = new ArrayList<MetadataBean>(); 
	metadataList = new ArrayList<Metadata>();
	
	// WorkAround until cone integration
	Metadata emotion = new Metadata("emotion", "Emotion");
	Metadata age = new Metadata("age", "Age");
	emotion.setIndex("face.emotion");
	age.setIndex("face.age");
	metadataList.add(emotion);
	metadataList.add(age);
	// End workaround
    }

    /**
     * @return the metadataList
     */
    public List<Metadata> getMetadataList()
    {
        return metadataList;
    }

    /**
     * @param metadataList the metadataList to set
     */
    public void setMetadataList(List<Metadata> metadataList)
    {
        this.metadataList = metadataList;
    }

    /**
     * @return the mdProfile
     */
    public List<MetadataBean> getMdProfile()
    {
        return mdProfile;
    }

    /**
     * @param mdProfile the mdProfile to set
     */
    public void setMdProfile(List<MetadataBean> mdProfile)
    {
        this.mdProfile = mdProfile;
    }

    /**
     * @return the name
     */
    public String getProfileName()
    {
        return profileName;
    }

    /**
     * @param name the name to set
     */
    public void setProfileName(String name)
    {
        this.profileName = name;
    }

    
    
}
