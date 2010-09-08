package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.List;

import de.mpg.jena.controller.SearchCriterion;

public class CollectionCriterion extends Criterion{
	private String collection;
    private MDCriterionController mdCriterionController = null;
	
	public CollectionCriterion(){
        mdCriterionController = new MDCriterionController();		
	}
	
    public MDCriterionController getMdCriterionController()
    {
        return mdCriterionController;
    }

    public void setMdCriterionController(MDCriterionController mdCriterionController)
    {
        this.mdCriterionController = mdCriterionController;
    }
	
	@Override
	public ArrayList<String> createSearchCriterion() {
		ArrayList<String> criterions = new ArrayList<String>();
		if(isSearchStringEmpty() == true)
			return criterions;
		else{
			String criterion = new String();
//			for(int i=0; i<mdList.size(); i++){
//				criterion += "Collection=" + getCollection()+ "&MD=" + getMdList().get(i).getMd() + "&MDText=" + getMdList().get(i).getMdText();
//				criterions.add(criterion);
//			}
		}
		return criterions;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}
}
