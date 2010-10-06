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
import de.mpg.jena.util.ComplexTypeHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.ComplexType.ComplexTypes;

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
		
	public String getSearchCriterion() {
		String criterion = "";

		int i = 0;
		for(CollectionCriterion collectionCriterion : collectionCriterionList) 
		{ 
		    String collectionLogicalOperator = "";
		    String collectionQuery = "";
			
			if(!(collectionCriterion.getSelectedCollectionId().equals("")))
			{
			    if(i!=0)
			    {
			        //criterion += " " + collectionCriterion.getLogicOperator();
			        //Always use OR here, doesnt make sense else
			        collectionLogicalOperator += " OR";
			    }
			    collectionQuery += ImejiNamespaces.IMAGE_COLLECTION.name() + "." + Filtertype.URI + "=\"" + collectionCriterion.getSelectedCollectionId() +"\"";
			
			    int j=0;
    			String mdQuery = "";
			    for(MDCriterion mdc : collectionCriterion.getMdCriterionList())
    			{
    				if(!mdc.getMdText().equals("") && mdc.getSelectedMdName()!=null && !mdc.getSelectedMdName().equals("")) 
    				{
    				   
    				    if(j!=0)
    				    {
    				        mdQuery += " " + mdc.getLogicOperator();
    				    }
    				    mdQuery+=" ( ";
				        String ctCriterion = "";
				        
				    
				        ComplexTypes ct = ComplexTypeHelper.getComplexTypesEnum(mdc.getSelectedStatement().getType());  
				        switch (ct)
				        {
				            case TEXT : 
				            {
				                ctCriterion = ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_TEXT.name() + "." + Filtertype.REGEX + "=\"" + mdc.getMdText() + "\"";
				                break;
				            }
				            case DATE : 
                            {
                                try
                                {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
                                    Date date = sdf.parse(mdc.getMdText());
                                    sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
                                    StringBuffer sb = new StringBuffer(sdf.format(date)); 
                                    System.out.println("MYDATE:" + sb.toString());
                                    ctCriterion = ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_DATE.name() + "." + mdc.getDateOperator() + "=\"" + sb.toString() + "\"";
                                }
                                catch (ParseException e)
                                {
                                    BeanHelper.error("Wrong date format");
                                }
                                break;
                            }
				            case  NUMBER : 
                            {
                                ctCriterion =  ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_NUMBER.name() + "." + mdc.getNumberOperator() + "=\"" + mdc.getMdText() + "\"";
                                break;
                            }
				            case  CONE_AUTHOR : 
                            {
                                ctCriterion =  ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_FAMILY_NAME.name()+ "." + Filtertype.REGEX + "=\"" + mdc.getMdText()+ "\"";
                                ctCriterion += " OR " + ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_GIVEN_NAME.name() + "." + Filtertype.REGEX + "=\"" + mdc.getMdText()+ "\"";
                                ctCriterion += " OR " + ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_ORGANIZATION_NAME.name() + "." + Filtertype.REGEX + "=\"" + mdc.getMdText()+ "\"";
                                break;
                            }
				            case  GEOLOCATION : 
                            {
                                ctCriterion =  ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_GEOLOCATION_LONGITUDE.name()+ "." + Filtertype.REGEX + "=\"" + mdc.getMdText()+ "\"";
                                ctCriterion += " OR " + ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_GEOLOCATION_LATITUDE.name() + "." + Filtertype.REGEX + "=\"" + mdc.getMdText()+ "\"";
                                break;
                            }
				        }
    				            
    				        
    				        
				        mdQuery += collectionQuery + " AND " + ImejiNamespaces.IMAGE_METADATA_NAME.name() + "." + Filtertype.EQUALS + "=\"" + mdc.getSelectedMdName() + "\" AND " + ctCriterion;
				        mdQuery +=" )";
    				    j++;
    				}
    			}
    			if(!mdQuery.equals(""))
    			{
    			    criterion += (collectionLogicalOperator + mdQuery);
    			}
    			i++;	
    			}
			
		}
		
		return criterion;
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