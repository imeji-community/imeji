package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Statement;

public class CollectionCriterion extends Criterion{
	private Collection<SelectItem> collectionList;
	private String collectionName;
	private List<MDCriterion> mdCriterionList;
    private Collection<CollectionImeji> collections;
	
	public Collection<CollectionImeji> getCollections() {
		return collections;
	}

	public void setCollections(Collection<CollectionImeji> collections) {
		this.collections = collections;
	}

	public CollectionCriterion(Collection<CollectionImeji> collections){
		this.collections = collections;

	}
	
    public void collectionChanged(ValueChangeEvent event){
        try{
            String coTitle = event.getNewValue().toString();
 
            for (CollectionImeji ci : collections){
                if (ci.getMetadata().getTitle().equalsIgnoreCase(coTitle)){
                    List<SelectItem> newMdList = new ArrayList<SelectItem>();
                    Collection<Statement> s = ci.getProfile().getStatements();
                    if (s.size() != 0){
                    	for (Statement statement : ci.getProfile().getStatements())
                    		newMdList.add(new SelectItem(statement.getName(), statement.getName()));
                    }   
                 // TODO use default mdList ?
//                    else
////                    	newMdList.add(new SelectItem("title", "title"));
                    for(int j=0; j< getMdCriterionList().size(); j++)
                    	getMdCriterionList().get(j).setMdList(newMdList);
                    }
                }
        }catch (Exception e){
        	e.getMessage();
        }
    }
    
    public List<MDCriterion> newMdCriterionList(){
    	MDCriterion newMd = new MDCriterion(collections, collectionName);
    	newMd.setMdName("");
    	newMd.setMdList(newMd.newMdList());
    	newMd.setMdText("");
    	List<MDCriterion> mdCriterionList = new ArrayList<MDCriterion>();
    	mdCriterionList.add(newMd);
    	return mdCriterionList;
    }
    
	public Collection<SelectItem> getCollectionList() {
		return collectionList;
	}

	public void setCollectionList(Collection<SelectItem> collectionList) {
		this.collectionList = collectionList;
	}

	public List<MDCriterion> getMdCriterionList() {
		return mdCriterionList;
	}

	public void setMdCriterionList(List<MDCriterion> mdCriterionList) {
		this.mdCriterionList = mdCriterionList;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	public boolean clearCriterion() {
		setSearchString("");
		setCollectionName("");
		for(int i=0; i<mdCriterionList.size(); i++)
			mdCriterionList.get(i).clearCriterion();
		return true;
	}
}
