package de.mpg.imeji.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.metadata.util.SuggestBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.MetadataProfile;

public class CollectionCriterion extends Criterion implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private MetadataProfile selectedProfile;
	private String selectedCollectionId;
	private List<MDCriterion> mdCriterionList;
    private Collection<CollectionImeji> collections;
    private int mdPosition;
	
	public Collection<CollectionImeji> getCollections() 
	{
		return collections;
	}

	public void setCollections(Collection<CollectionImeji> collections) 
	{
		this.collections = collections;
	}

	public CollectionCriterion(List<CollectionImeji> collections)
	{
		this.collections = collections; 
	}
	
    public void collectionChanged(ValueChangeEvent event)
    {	
       if(event.getNewValue() != null && !event.getNewValue().toString().equals(getSelectedCollectionId()))
       {
    	   String collId = (String)event.getNewValue();
           for(CollectionImeji coll : collections)
           {
               if(coll.getId().toString().equals(collId))
               {
                   	setSelectedCollectionId(collId);
                   	SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	       			ProfileController pc = new ProfileController(sb.getUser());
	       			try 
	       			{
	       				setSelectedProfile(pc.retrieve(coll.getProfile()));
	       				((SuggestBean)BeanHelper.getSessionBean(SuggestBean.class)).init(getSelectedProfile());
	       			} 
	       			catch (Exception e) 
	       			{
	       	            BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error_reading_profile") + ": " + coll.getProfile());  
	       			}
               }
           }
           updateMDList();
       }
       if(event.getNewValue() == null)
       {
    	   setMdCriterionList(null);
       }
    }
    
    public void updateMDList()
    {
    	setMdCriterionList(newMdCriterionList());
    }
    
    
    public List<MDCriterion> newMdCriterionList()
    {
    	List<MDCriterion> mdCriterionList = new ArrayList<MDCriterion>();
    	if(getSelectedProfile().getStatements().size()>0)
    	{
    		MDCriterion newMd = new MDCriterion(getSelectedProfile().getStatements());
        	mdCriterionList.add(newMd);
    	}
    	else
    	{
    		 BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("profile_empty"));
    	}
    	return mdCriterionList;
    }
    
	public List<MDCriterion> getMdCriterionList() {
		return mdCriterionList;
	}
	
	public int getMdCriterionListSize() {
        return mdCriterionList.size();
    }


	public void setMdCriterionList(List<MDCriterion> mdCriterionList) {
		this.mdCriterionList = mdCriterionList;
	}

	
	public boolean clearCriterion() 
	{
		setSearchString("");
		//setSelectedProfile(null);
		if (mdCriterionList != null)
		{
			for(int i=0; i< mdCriterionList.size(); i++)
			{
				mdCriterionList.get(i).clearCriterion();
			}
		}
		return true;
	}

    public void setSelectedProfile(MetadataProfile selectedProfile)
    {
        this.selectedProfile = selectedProfile;
    }

    public MetadataProfile getSelectedProfile()
    {
        return selectedProfile;
    }    
    
    public String addMd(){
        List<MDCriterion> mds = getMdCriterionList(); 
        MDCriterion newMd = new MDCriterion(getSelectedProfile().getStatements());
        mds.add(mdPosition +1, newMd);  
        return "";
    }
    
    public int getMdPosition() {
        return mdPosition;
    }

    public void setMdPosition(int mdPosition) {
        this.mdPosition = mdPosition;
    }
    
    public String removeMd(){

            List<MDCriterion> mds = getMdCriterionList();
            mds.remove(mdPosition);         

        return "";
    }

    public void setSelectedCollectionId(String selectedCollectionId)
    {
        this.selectedCollectionId = selectedCollectionId;
    }

    public String getSelectedCollectionId()
    {
        return selectedCollectionId;
    }
}
