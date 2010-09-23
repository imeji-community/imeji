package de.mpg.imeji.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Statement;

public class CollectionCriterion extends Criterion implements Serializable{

	private CollectionImeji selectedCollection;
	private String selectedCollectionId;
	private List<MDCriterion> mdCriterionList;
    private Collection<CollectionImeji> collections;
    private int mdPosition;
	
	public Collection<CollectionImeji> getCollections() {
		return collections;
	}

	public void setCollections(Collection<CollectionImeji> collections) {
		this.collections = collections;
	}

	public CollectionCriterion(List<CollectionImeji> collections){
		this.collections = collections;
		if(collections!=null && collections.size()>0)
		{
		    setSelectedCollection(collections.get(0));
		    setMdCriterionList(newMdCriterionList());
	        updateMDList();
		}
		
       

	}
	
    public void collectionChanged(ValueChangeEvent event){

       
        String collId = (String)event.getNewValue();
        for(CollectionImeji coll : collections)
        {
            if(coll.getId().toString().equals(collId))
            {
                setSelectedCollectionId(collId);
                setSelectedCollection(coll);
            }
        }

        updateMDList();
           
    }
    
    public void updateMDList()
    {
        try{
            
            setMdCriterionList(newMdCriterionList());
                
            List<SelectItem> newMdList = new ArrayList<SelectItem>();
            Collection<Statement> s = selectedCollection.getProfile().getStatements();
            if (s.size() != 0){
                for (Statement statement : selectedCollection.getProfile().getStatements())
                    newMdList.add(new SelectItem(statement.getName(), statement.getName()));
            }   
         // TODO use default mdList ?
//                else
////                    newMdList.add(new SelectItem("title", "title"));
            for(int j=0; j< getMdCriterionList().size(); j++)
                getMdCriterionList().get(j).setMdList(newMdList);
                    
                
    }catch (Exception e){
        e.getMessage();
    }
    }
    
    public List<MDCriterion> newMdCriterionList(){
    	MDCriterion newMd = new MDCriterion(getSelectedCollection().getProfile().getStatements());
    	List<MDCriterion> mdCriterionList = new ArrayList<MDCriterion>();
    	mdCriterionList.add(newMd);
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

	
	public boolean clearCriterion() {
		setSearchString("");
		setSelectedCollection(null);
		for(int i=0; i<mdCriterionList.size(); i++)
			mdCriterionList.get(i).clearCriterion();
		return true;
	}

    public void setSelectedCollection(CollectionImeji selectedCollection)
    {
        this.selectedCollection = selectedCollection;
    }

    public CollectionImeji getSelectedCollection()
    {
        return selectedCollection;
    }
    
    public String addMd(){
        List<MDCriterion> mds = getMdCriterionList();
        MDCriterion newMd = new MDCriterion(getSelectedCollection().getProfile().getStatements());
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
        if(mdPosition >0){
            List<MDCriterion> mds = getMdCriterionList();
            mds.remove(mdPosition);         
        }
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
