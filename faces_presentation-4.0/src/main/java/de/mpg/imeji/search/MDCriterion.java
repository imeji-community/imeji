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
    	System.out.println("collectionName = " +collectionName); 
    	if(collectionName == null || collectionName.equalsIgnoreCase("--") || collectionName == "")
    		newMdList.add(new SelectItem("title", "title"));
        try{
        	for (CollectionImeji ci : collections){
        		if (ci.getMetadata().getTitle().equalsIgnoreCase(collectionName)){
        			Collection<Statement> s = ci.getProfile().getStatements();
                    if (s.size() != 0){
                        for (Statement statement : ci.getProfile().getStatements())
                        	newMdList.add(new SelectItem(statement.getName(), statement.getName()));
                    }
                    else
                       // TODO use default mdList ?
                       newMdList.add(new SelectItem("title", "title"));
                    }
        		}
        }catch (Exception e){
        	newMdList.add(new SelectItem("title", "title"));
        }		
        return newMdList;
    	
    }
	
	public List<SelectItem> getMdList() {
		return mdList;
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
	
	public ArrayList<String> createSearchCriterion() {
		ArrayList<String> criterions = new ArrayList<String>();
	
		if(isSearchStringEmpty() == true)
			return criterions;
		else{
			String criterion = new String();
			criterion += "md=" + getMdName() +"&mdText=" + getMdText();
			System.err.println(criterion);
			criterions.add(criterion);
		}
		return  criterions;
	}
	
}