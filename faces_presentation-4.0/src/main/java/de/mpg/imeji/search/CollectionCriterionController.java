package de.mpg.imeji.search;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.complextypes.util.ComplexTypeHelper;

public class CollectionCriterionController implements Serializable {
	
	private List<CollectionCriterion> collectionCriterionList;
	private int collectionPosition;
    private SessionBean sb;

	private List<SelectItem> collectionList;
	private List<CollectionImeji> collections;	
 
	public CollectionCriterionController(){
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        try {
			getCollectionList(); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		collectionCriterionList = new ArrayList<CollectionCriterion>(); 
		CollectionCriterion newCollection = new CollectionCriterion((collections));
		collectionCriterionList.add(newCollection);
		
	}     
	
	public void getUserCollecitons() throws Exception{
        collections = new ArrayList<CollectionImeji>();
        CollectionController cc = new CollectionController(sb.getUser());
		collections = (List<CollectionImeji>)cc.search(new ArrayList<SearchCriterion>(), null, -1, 0);
		if(collectionCriterionList != null){
			for(int i=0; i<collectionCriterionList.size(); i++){
				collectionCriterionList.get(i).setCollections(collections);
				/*
				for(int j=0; j<collectionCriterionList.get(i).getMdCriterionList().size(); j++)
					collectionCriterionList.get(i).getMdCriterionList().get(j).setCollections(collections);
					*/
			}
		}
	}
	
	public List<SelectItem> getCollectionList() throws Exception {
		getUserCollecitons();
        collectionList = new ArrayList<SelectItem>();
        for (CollectionImeji ci : collections)
        {
            collectionList.add(new SelectItem(ci.getId().toString(), ci.getMetadata().getTitle()));
        }
		return collectionList;
	}
	
	public void setCollectionList(List<SelectItem> collectionList) {
		this.collectionList = collectionList;
	}
	
	public String addCollection() {
		CollectionCriterion newCollection = new CollectionCriterion(collections);
		collectionCriterionList.add(collectionPosition+1,newCollection);
		return "";
	}
	
	public String removeCollection(){

			collectionCriterionList.remove(collectionPosition);
		return "";
	}

	public int getCollectionPosition() {
		return collectionPosition;
	}

	public void setCollectionPosition(int collectionPosition) {
		this.collectionPosition = collectionPosition;
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
		
	public String getSearchCriterion() 
	{
		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();

		for(CollectionCriterion collectionCriterion : collectionCriterionList) 
		{ 
		    SearchCriterion subSC = new SearchCriterion(Operator.AND, new ArrayList<SearchCriterion>());
		    
			if(!(collectionCriterion.getSelectedCollectionId().equals("")))
			{    
			    SearchCriterion collSC = new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_COLLECTION, collectionCriterion.getSelectedCollectionId(), Filtertype.URI);
			    scList.add(collSC);

			    for(MDCriterion mdc : collectionCriterion.getMdCriterionList())
    			{
    				if(!mdc.getMdText().equals("") && mdc.getSelectedMdName()!=null && !mdc.getSelectedMdName().equals("")) 
    				{ 
    					Operator op  = Operator.AND;
    					if (mdc.getLogicOperator() != null)
    					{
    						 op  = Operator.valueOf(mdc.getLogicOperator());
    					}
    					
    					//Create Search criterion
    					SearchCriterion mdSC = new SearchCriterion(op, new ArrayList<SearchCriterion>());
    					
    					// Add metadata type
    					mdSC.getChildren().add(new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_NAMESPACE, mdc.getSelectedStatement().getName().toString(), Filtertype.URI));
    					
    					// Add metadata value
    					switch (ComplexTypeHelper.getComplexType(mdc.getSelectedStatement().getType()))
 				        {
 				        	case DATE:
 				        		String dop = mdc.getDateOperator();
 				        		Filtertype f = Filtertype.EQUALS_DATE;
 				        		if ("<".equals(dop)) f = Filtertype.LESSER_DATE;
 				        		else if (">".equals(dop)) f = Filtertype.GREATER_DATE;
 				        		mdSC.getChildren().add(new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_DATE, mdc.getMdText(), f));
 				        		break;
 				        	case GEOLOCATION:
 				        		//TODO to create a new searchcriterion with the 2 values as childs
 				        		mdSC.getChildren().add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_GEOLOCATION_LONGITUDE, mdc.getMdText(), Filtertype.REGEX));
 				        		mdSC.getChildren().add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_GEOLOCATION_LATITUDE, mdc.getMdText(), Filtertype.REGEX));
 				        		break;
 				        	case TEXT:
 				        		mdSC.getChildren().add(new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_TEXT, mdc.getMdText(), Filtertype.REGEX));
 				        		break;
 				        	case LICENSE:
 				        		//TODO
 				        		break;
 				        	case NUMBER:
 				        		String nop = mdc.getNumberOperator();
 				        		Filtertype f1 = Filtertype.EQUALS_NUMBER;
 				        		if ("<".equals(nop)) f1 = Filtertype.LESSER_NUMBER;
 				        		else if (">".equals(nop)) f1 = Filtertype.GREATER_NUMBER;
 				        		mdSC.getChildren().add(new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_NUMBER, mdc.getMdText(), f1));
 				        		break;
 				        	case PERSON:
 				        		mdSC.getChildren().add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_PERSON_FAMILY_NAME, mdc.getMdText(),  Filtertype.REGEX));
 				        		mdSC.getChildren().add(new SearchCriterion(Operator.OR,  ImejiNamespaces.IMAGE_METADATA_PERSON_GIVEN_NAME, mdc.getMdText(), Filtertype.REGEX));
 				        		mdSC.getChildren().add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_PERSON_ORGANIZATION_NAME, mdc.getMdText(),  Filtertype.REGEX));
 				        		break;
 				        	case PUBLICATION:
 				        		//TODO
 				        		break;
 				        	case URI:
 				        		//TODO
 				        		break;
 				        }
    				    subSC.getChildren().add(mdSC);
    				}
    			}
    		}
		    scList.add(subSC);
		}
		String query = URLQueryTransformer.transform2URL(scList);
		return query;
	}
	
    protected String getNavigationString(){
        return "pretty:";
    }

    public void setCollections(List<CollectionImeji> collections)
    {
        this.collections = collections;
    }

    public List<CollectionImeji> getCollections()
    {
        return collections;
    }
    
    public int getSize()
    {
        return collectionCriterionList.size();
    }
	
}