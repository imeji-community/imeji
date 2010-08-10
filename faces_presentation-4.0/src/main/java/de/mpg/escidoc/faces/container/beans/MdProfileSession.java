package de.mpg.escidoc.faces.container.beans;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.faces.mdProfile.MdProfileVO;
import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.MetadataBean;
import de.mpg.escidoc.faces.metadata.MetadataBean.ConstraintBean;
import de.mpg.escidoc.faces.metadata.helper.VocabularyHelper;

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
	//Get vocabularies
	metadataList = VocabularyHelper.getDcTermsVocabulary();
	metadataList.addAll(VocabularyHelper.getEtermsVocabulary());
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
    	updateMdProfile();
        return mdProfile;
    }

    private void updateMdProfile() 
    {
    	mdProfile = new MdProfileVO();
    	int i = 0;
    	for (MetadataBean m : getMetadataBeanList())
    	{
    	   // profile.getMetadataList().add(new Metadata(m.getCurrent().getName(), m.getCurrent().getIndex(), m.getCurrent().getNamespace()));
    		mdProfile.getMetadataList().add(new Metadata(m.getCurrent()));
    		mdProfile.getMetadataList().get(i).getConstraint().clear();
    	    
    	    for (ConstraintBean c : m.getConstraints())
    	    {
    		if (!"".equals(c.getValue()))
    		{
    			mdProfile.getMetadataList().get(i).getConstraint().add(c.getValue());
    		} 
    	    }
    	    i++;
    	}
		
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
