package de.mpg.escidoc.faces.mdProfile.beans;

import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.jce.provider.JCEBlockCipher.IDEA;

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
	Metadata emotion = new Metadata("emotion", "Emotion", "http://purl.org/escidoc/metadata/terms/0.1/");
	Metadata age = new Metadata("age", "Age", "http://purl.org/escidoc/metadata/terms/0.1/");
	Metadata pictureSet = new Metadata("picture-group", "Picture Set", "http://purl.org/escidoc/metadata/terms/0.1/");
	Metadata genre = new Metadata("genre", "Genre", "http://purl.org/escidoc/metadata/terms/0.1/");
	Metadata identifier = new Metadata("identifier", "Identifier", "http://purl.org/dc/elements/1.1/");
	emotion.setIndex("emotion");
	age.setIndex("age");
	genre.setIndex("genre");
	pictureSet.setIndex("picture-group");
	identifier.setIndex("identifier");
	emotion.setSchemaLocation("../../metadata/0.1/escidoctypes.xsd");
	age.setSchemaLocation("../../metadata/0.1/escidoctypes.xsd");
	genre.setSchemaLocation("../../metadata/0.1/escidoctypes.xsd");
	pictureSet.setSchemaLocation("../../metadata/0.1/escidoctypes.xsd");
	identifier.setSchemaLocation("http://dublincore.org/schemas/xmls/qdc/2008/02/11/dcterms.xsd");
	metadataList.add(emotion);
	metadataList.add(age);
	metadataList.add(pictureSet);
	metadataList.add(genre);
	metadataList.add(identifier);
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
