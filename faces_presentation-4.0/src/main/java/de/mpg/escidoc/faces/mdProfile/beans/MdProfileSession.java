package de.mpg.escidoc.faces.mdProfile.beans;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.faces.mdProfile.MdProfileVO;
import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.MetadataBean;

public class MdProfileSession
{
    private List<Metadata> metadataList = null;
    private List<MetadataBean> metadataBeanList = null;
    private MdProfileVO mdProfile = null;
    private String profileName = "";
    
    public MdProfileSession()
    {
	mdProfile = new MdProfileVO();
	metadataList = new ArrayList<Metadata>();
	metadataBeanList = new ArrayList<MetadataBean>();
	
	// WorkAround until cone integration
	Metadata emotion = new Metadata("emotion", "Emotion", "http://purl.org/mpdl/face/emotion");
	Metadata age = new Metadata("age", "Age", "http://purl.org/mpdl/face/age");
	Metadata pictureSet = new Metadata("picture-group", "Picture Set", "http://purl.org/mpdl/face/picture-group");
	Metadata genre = new Metadata("genre", "Genre", "http://purl.org/mpdl/face/genre");
	emotion.setIndex("emotion");
	age.setIndex("age");
	genre.setIndex("genre");
	pictureSet.setIndex("picture-group");
	metadataList.add(emotion);
	metadataList.add(age);
	metadataList.add(pictureSet);
	metadataList.add(genre);
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
    public MdProfileVO getMdProfile()
    {
        return mdProfile;
    }

    /**
     * @param mdProfile the mdProfile to set
     */
    public void setMdProfile(MdProfileVO mdProfile)
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

    /**
     * @return the metadataBeanList
     */
    public List<MetadataBean> getMetadataBeanList()
    {
        return metadataBeanList;
    }

    /**
     * @param metadataBeanList the metadataBeanList to set
     */
    public void setMetadataBeanList(List<MetadataBean> metadataBeanList)
    {
        this.metadataBeanList = metadataBeanList;
    }

    
    
}
