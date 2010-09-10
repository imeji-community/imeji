package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.model.SelectItem;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.vo.CollectionImeji;

public class CollectionCriterionController {
	
	private List<CollectionCriterion> collectionCriterionList;
	private int collectionPosition;
	private int mdPosition;
	
    private SessionBean sb;
    private CollectionController controller;
	private List<SelectItem> collectionList;
    private Collection<CollectionImeji> collections;	
//    private ArrayList<CollectionImeji> collectionImejiList;
	
	public CollectionCriterionController(){
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        controller = new CollectionController(sb.getUser());
        collectionList = new ArrayList<SelectItem>();
        collectionList.add(new SelectItem(null, "--"));
        collections = new ArrayList<CollectionImeji>();
        try{
            collections = controller.search(new ArrayList<SearchCriterion>(), null, -1, 0);
            for (CollectionImeji ci : collections)
            {
                collectionList.add(new SelectItem(ci.getMetadata().getTitle(), ci.getMetadata().getTitle()));
            }
        }catch (Exception e){
        }  
        
        
		collectionCriterionList = new ArrayList<CollectionCriterion>();
//		collectionImejiList =  new ArrayList<CollectionImeji>();
		//insure there is at one collection with meta data in the list
//		collectionImejiList.add(new CollectionImeji());
		CollectionCriterion newCollection = new CollectionCriterion(collections);
		newCollection .setCollectionName("");
		newCollection .setCollectionList(collectionList);
		newCollection .setMdCriterionList(newCollection.newMdCriterionList());
		collectionCriterionList.add(newCollection);
		
	}
	 

    
    


	
	public String addCollection() {
//		collectionImejiList.add(new CollectionImeji());
		CollectionCriterion newCollection = new CollectionCriterion(collections);
		newCollection .setCollectionName("");
		newCollection .setCollectionList(collectionList);
		newCollection .setMdCriterionList(newCollection.newMdCriterionList());
		collectionCriterionList.add(collectionPosition+1,newCollection);
		return getNavigationString();
	}
	
	public String removeCollection(){
		if(collectionPosition > 0)
			collectionCriterionList.remove(collectionPosition);
		return getNavigationString();
	}
	
	public String addMd(){
		ArrayList<MDCriterion> mds = (ArrayList<MDCriterion>)collectionCriterionList.get(collectionPosition).getMdCriterionList();
    	MDCriterion newMd = new MDCriterion(collections, collectionCriterionList.get(collectionPosition).getCollectionName());
    	newMd.setMdName("");
    	newMd.setMdList(newMd.newMdList());
    	newMd.setMdText("");	
		mds.add(mdPosition +1, newMd);	
		return getNavigationString();
	}
	
	public String removeMd(){
		if(mdPosition >0){
			ArrayList<MDCriterion> mds = (ArrayList<MDCriterion>)collectionCriterionList.get(collectionPosition).getMdCriterionList();
			mds.remove(mdPosition);			
		}
		return getNavigationString();
	}
	  


	public int getCollectionPosition() {
		return collectionPosition;
	}

	public void setCollectionPosition(int collectionPosition) {
		this.collectionPosition = collectionPosition;
	}

	public int getMdPosition() {
		return mdPosition;
	}

	public void setMdPosition(int mdPosition) {
		this.mdPosition = mdPosition;
	}

	public CollectionCriterionController(List<CollectionCriterion> collectionCriterion){
		setCollectionCriterionList(collectionCriterion);
	}
	 

	
	public List<CollectionCriterion> getCollectionCriterionList(){
		return collectionCriterionList;
	}
	
    public void setCollectionCriterionList(List<CollectionCriterion> collectionCriterionList){
        this.collectionCriterionList = collectionCriterionList;
    }

	public void clearAllForms() {
        for (CollectionCriterion vo : collectionCriterionList)
            vo.clearCriterion();
    }
	

		
	public List<String> getSearchCriterion() {
		List<String> criterions = new ArrayList<String>();

		for(int i=0; i<collectionCriterionList.size(); i++){
			String criterion = new String();
			criterion += "("+collectionCriterionList.get(i).getLogicOperator()+"("+ "imeji.collection=" + collectionCriterionList.get(i).getCollectionName() ;
			for(int j=0;j<collectionCriterionList.get(i).getMdCriterionList().size(); j++){
				criterion += "(" + collectionCriterionList.get(i).getMdCriterionList().get(j).getLogicOperator()+"(" +"imeji.mdName = " + collectionCriterionList.get(i).getMdCriterionList().get(j).getMdName()+"imeji.mdText = " + collectionCriterionList.get(i).getMdCriterionList().get(j).getMdText()+")"+")";
			}
			criterion +=")"+")";

			System.err.println(criterion);
			criterions.add(criterion);			
		}
		return criterions;
	}
	
    protected String getNavigationString()
    {
        return "pretty:";
    }
    

	
}