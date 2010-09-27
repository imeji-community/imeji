package de.mpg.imeji.image;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.collections.KeyValue;

import de.mpg.imeji.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.facet.FacetsBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;

public class ImagesBean extends BasePaginatorListSessionBean<ImageBean>
{  
    private String objectClass;
    private int totalNumberOfRecords;
    private SessionBean sb;
    private List<SelectItem> sortMenu;
    private String selectedSortCriterion;
    private String selectedSortOrder;
    private FacetsBean facets;
    
    private String query;

    public ImagesBean()
    {
        super();
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        initMenus();
    }

    private void initMenus()
    {
        sortMenu = new ArrayList<SelectItem>();
        sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_CREATION_DATE, ImejiNamespaces.PROPERTIES_CREATION_DATE
                .name()));
        sortMenu.add(new SelectItem(ImejiNamespaces.IMAGE_COLLECTION, ImejiNamespaces.IMAGE_COLLECTION.name()));
        //
        sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE,
                ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE.name()));
        selectedSortCriterion = ImejiNamespaces.PROPERTIES_CREATION_DATE.name();
        selectedSortOrder = SortOrder.DESCENDING.name();
    }

    @Override
    public String getNavigationString()
    {
        return "pretty:images";
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return totalNumberOfRecords;
    }

    @Override
    public List<ImageBean> retrieveList(int offset, int limit)
    {
        ImageController controller = new ImageController(sb.getUser());
        Collection<Image> images = new ArrayList<Image>();
        try
        {
            SortCriterion sortCriterion = new SortCriterion();
            sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
            sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
            
            
           
            
            List<List<SearchCriterion>> scList = new ArrayList<List<SearchCriterion>>();
            try
            {
                scList = transformQuery(query);
            }
            catch (Exception e)
            {
                BeanHelper.error("Invalid search query!");
            }
            totalNumberOfRecords = controller.searchAdvanced(scList, null, -1, 0).size(); 
            images = controller.searchAdvanced(scList, sortCriterion, limit, offset);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        this.setFacets(new FacetsBean((List<Image>)images));
        return ImejiFactory.imageListToBeanList(images);
    }

    public List<SelectItem> getSortMenu()
    {
        return sortMenu;
    }

    public void setSortMenu(List<SelectItem> sortMenu)
    {
        this.sortMenu = sortMenu;
    }

    public String getSelectedSortCriterion()
    {
        return selectedSortCriterion;
    }

    public void setSelectedSortCriterion(String selectedSortCriterion)
    {
        this.selectedSortCriterion = selectedSortCriterion;
    }

    public String getSelectedSortOrder()
    {
        return selectedSortOrder;
    }

    public void setSelectedSortOrder(String selectedSortOrder)
    {
        this.selectedSortOrder = selectedSortOrder;
    }

    public String toggleSortOrder()
    {
        if (selectedSortOrder.equals("DESCENDING"))
        {
            selectedSortOrder = "ASCENDING";
        }
        else
        {
            selectedSortOrder = "DESCENDING";
        }
        return getNavigationString();
    }

    public String getObjectClass()
    {
        return objectClass;
    }

    public void setObjectClass(String objectClass)
    {
        this.objectClass = objectClass;
    }

    public FacetsBean getFacets()
    {
        return facets;
    }

    public void setFacets(FacetsBean facets)
    {
        this.facets = facets;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getQuery()
    {
        return query;
    }
    
    public static List<List<SearchCriterion>> transformQuery(String query) throws Exception
    {
        boolean inverse=false;
        List<List<SearchCriterion>> scList = new ArrayList<List<SearchCriterion>>(); 
        
        if(query!=null && !query.trim().equals(""))
        {
            
        
            StringReader reader = new StringReader(query);
            
            int bracketsOpened = 0;
            int bracketsClosed = 0;
            String substring = "";
            String lastOperator="";
            List<SearchCriterion> currentSubList = new ArrayList<SearchCriterion>();
            
            int c = 0;
            
            while((c=reader.read())!=-1)
            {
                substring += (char)c;   
                
                if(c=='(')
                {
                    bracketsOpened++;
                    if(bracketsOpened-bracketsClosed == 1)
                    {
                        currentSubList = new ArrayList<SearchCriterion>();
                        substring = "";
                    }
                }
                else if(c==')')
                {
                    bracketsClosed++; 
                    if(bracketsOpened - bracketsClosed == 0)
                    {
                        scList.add(currentSubList);
                        substring="";
                    }
                }
                
                if(substring.equals("AND ") || substring.equals("OR "))
                {
                    lastOperator = substring.trim();
                    substring = "";
                }
                else if(substring.trim().equals("INVERSE"))
                {
                    inverse=true;
                    substring ="";
                }
                else if (substring.matches("\\s*[^\\s]+=[^\\s]+\\s+"))
                {
                    String[] keyValue = substring.split("=");
                    
                    String[] nsFilter = keyValue[0].split("\\.");
                    String value = keyValue[1].trim(); 
                    
                    if(nsFilter[0].trim().equals("ANY_METADATA"))
                    {
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_TEXT, value , Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_NUMBER, value, Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_FAMILY_NAME, value, Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_GIVEN_NAME, value, Filtertype.REGEX));
                        currentSubList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_ORGANIZATION_NAME, value, Filtertype.REGEX));
                    }
                    else
                    {
                        ImejiNamespaces ns = ImejiNamespaces.valueOf(nsFilter[0].trim());
                        Filtertype filter = Filtertype.valueOf(nsFilter[1].trim());
                       
                        Operator op = Operator.AND;
    
                        if(!lastOperator.equals(""))
                        {
                            op = Operator.valueOf(lastOperator.trim());
                        }
                       
                        //Operator op = Operator.valueOf(lastOperator.trim());
                        SearchCriterion sc = new SearchCriterion(op,ns, value, filter);
                        sc.setInverse(inverse);
                        currentSubList.add(sc);
                    }
                   
                    substring = "";
                    
                    
                }
                
                
    
            }
            if(bracketsClosed == 0)
            {
                scList.add(currentSubList);
            }
            if(bracketsOpened!= bracketsClosed)
            {
                throw new Exception("Invalid query!");
            }
        }
        
        /*
        List<List<SearchCriterion>> scList = new ArrayList<List<SearchCriterion>>(); 
        
        if(query!=null && !query.equals(""))
        {
            
            //QUICK SEARCH
            if(query.trim().startsWith("ANY_METADATA"))
            {
                String[] queryParts = query.split("=");
                List<SearchCriterion> scList1 = new ArrayList<SearchCriterion>();
                scList1.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_TEXT, queryParts[1] , Filtertype.REGEX));
                scList1.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_NUMBER, queryParts[1], Filtertype.REGEX));
                scList1.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_FAMILY_NAME, queryParts[1], Filtertype.REGEX));
                scList1.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_GIVEN_NAME, queryParts[1], Filtertype.REGEX));
                scList1.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_ORGANIZATION_NAME, queryParts[1], Filtertype.REGEX));
                scList.add(scList1);
            }
            
            //ADVANCED SEARCH
            else
            {
                
                
                //remove first and last bracket
                query = query.substring(3); 
                query = query.substring(0, query.length()-2);
                
                System.out.println(query);
                String[] parts = query.split(" \\) AND \\(");
                
                List<String> andPartsList = new ArrayList<String>();
                List<String> orPartsList = new ArrayList<String>();
                
                for(String andPart :parts)
                {
                    String[] orParts = andPart.split(" \\) OR \\(");
                    if(orParts.length > 1)
                    {
                        orPartsList.addAll(Arrays.asList(orParts));
                    }
                    else
                    {
                        andPartsList.add(andPart);
                    }
                }
                
                for(String andPart: andPartsList)
                {
                    List<SearchCriterion> scList1 = new ArrayList<SearchCriterion>();
                    String[] subAndParts = andPart.split(" AND ");
                    for(String subAndPart : subAndParts)
                    {
                        String[] keyValue= subAndPart.split("=");
                        String[] nsAndFilter = keyValue[0].split("\\.");
                        ImejiNamespaces ns = ImejiNamespaces.valueOf(nsAndFilter[0].trim());
                        Filtertype filter = Filtertype.valueOf(nsAndFilter[1].trim());
                        
                        String value = keyValue[1].trim();
                        scList1.add(new SearchCriterion(Operator.AND, ns, value, filter));
                    }
                    scList.add(scList1);
        
                }
                
                for(String orPart: orPartsList)
                {
                    List<SearchCriterion> scList2 = new ArrayList<SearchCriterion>();
                    String[] subAndParts = orPart.split(" AND ");
                    int i = 0;
                    for(String subAndPart : subAndParts)
                    {
                        String[] keyValue= subAndPart.split("=");
                        String[] nsAndFilter = keyValue[0].split("\\.");
                        ImejiNamespaces ns = ImejiNamespaces.valueOf(nsAndFilter[0].trim());
                        Filtertype filter = Filtertype.valueOf(nsAndFilter[1].trim());
                        String value = keyValue[1].trim();
                        if(i==0)
                        {
                            scList2.add(new SearchCriterion(Operator.OR, ns, value, filter));
                        }
                        else
                        {
                            scList2.add(new SearchCriterion(Operator.AND, ns, value, filter));
                        }
                       
                    }
                    scList.add(scList2);
                    
                    
                }
            }
        }
        */
        return scList;
    }
    
    
}
