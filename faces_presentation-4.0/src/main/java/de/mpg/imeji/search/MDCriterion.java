package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.model.SelectItem;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Statement;

public class MDCriterion extends Criterion{
	private String mdName;
	private String mdText;
	private List<SelectItem> mdList;
    private Collection<CollectionImeji> collections;
    private String collectionName;

	public MDCriterion(Collection<CollectionImeji> collections, String collectionName){
		this.collectionName = collectionName;
		this.collections = collections;

	}
	
    // TODO use default mdList ?
    public List<SelectItem> newMdList(){
    	List<SelectItem> newMdList = new ArrayList<SelectItem>();
        try{
        	for (CollectionImeji ci : collections){
        		if (ci.getMetadata().getTitle().equalsIgnoreCase(collectionName)){
        			Collection<Statement> s = ci.getProfile().getStatements();
                    if (s.size() != 0){
                        for (Statement statement : ci.getProfile().getStatements())
                        	newMdList.add(new SelectItem(statement.getName(), statement.getName()));
                    }
                    }
        		}
        }catch (Exception e){
        }		
        return newMdList;
    	
    }
	
	public List<SelectItem> getMdList() {
		return mdList;
	}

	public Collection<CollectionImeji> getCollections() {
		return collections;
	}

	public void setCollections(Collection<CollectionImeji> collections) {
		this.collections = collections;
	}

	public void setMdList(List<SelectItem> mdList) {
		this.mdList = mdList;
	}
	
	public String getMdName() {
		return mdName;
	}
	public void setMdName(String mdName) {
		this.mdName = mdName;
	}
	public String getMdText() {
		return mdText;
	}
	public void setMdText(String mdText) {
		this.mdText = mdText;
	}
	public boolean clearCriterion() {
		setSearchString("");
		setMdName("");
		setMdText("");
		return true;
	}
}