/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.faces.util;

import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.RecordsType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import gov.loc.www.zing.srw.service.SRWPort;

import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.schemas.searchresult.x08.SearchResultRecordDocument;
import de.escidoc.schemas.searchresult.x08.SearchResultRecordDocument.SearchResultRecord;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.album.AlbumVO;
import de.mpg.escidoc.faces.container.collection.CollectionVO;
import de.mpg.escidoc.faces.item.ItemVO;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ContainerResultVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class QueryHelper
{

    private static Logger logger = Logger.getLogger(QueryHelper.class);
    
    private List<ItemVO> items = null;
    private List<AlbumVO> albums = null;
    private List<FacesContainerVO> facesContainers = null;
    private XmlTransforming xmlTransforming = null;
    private int totalNumberOfItems = 0;
   
    
    public QueryHelper() throws Exception
    {
        InitialContext context = new InitialContext();
        xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
    }
    
    /**
     * Queries the framework SRW/SRU interface.
     * 
     * @param query SRW query string.
     * @param itemsPerPage Maximum number of items that should be displayed on one page.
     * @param pageNumber Current page number.
     * @param sortedBy Sorting criteria as {@link String}, separated by comma.
     */
    public void executeQuery(String query, int itemsPerPage, int pageNumber, String sortedBy) throws Exception
    {
        SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        executeQueryForItems(query, itemsPerPage, pageNumber, sortedBy, sessionBean.getQueryExtension());
    }
    
    /**
     * @throws URISyntaxException 
     * @throws ServiceException 
     * Queries the framework SRW/SRU interface.
     * 
     * @param query SRW query string.
     * @param itemsPerPage Maximum number of items that should be displayed on one page.
     * @param pageNumber Current page number.
     * @param sortedBy Sorting criteria as {@link String}, separated by comma.
     * @param queryExtension cql part to define general restrictions.
     * @throws  
     */
    public void executeQueryForItems(String query, int itemsPerPage, int pageNumber, String sortedBy, String queryExtension) throws Exception
    {
        logger.info("query : " + concatQuery(queryExtension, query));
        logger.info("itemsPerPage : " + itemsPerPage);
        logger.info("pageNumber : " + pageNumber);
        logger.info("sortedBy : " + sortedBy);
        
        NonNegativeInteger show = new NonNegativeInteger(itemsPerPage + ""); 
        PositiveInteger page = new PositiveInteger(pageNumber + "");
        // creation of the request for the search method
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(concatQuery(queryExtension, query));
        if (sortedBy != null)
        {
            searchRetrieveRequest.setSortKeys(sortedBy);
        }
        searchRetrieveRequest.setStartRecord(page);       
        searchRetrieveRequest.setMaximumRecords(show);        
        searchRetrieveRequest.setRecordPacking("xml");
        // Create XML Source
        SRWPort searchHandler = ServiceLocator.getSearchHandler("escidoc_all");
        SearchRetrieveResponseType searchResult = searchHandler.searchRetrieveOperation(searchRetrieveRequest);    
        logger.info(searchRetrieveRequest.toString()); 
        
        try
        {
            RecordsType recordsType = searchResult.getRecords();
            items = new ArrayList<ItemVO>();
            totalNumberOfItems = 0;
            if (recordsType != null)
            {
                RecordType[] records = recordsType.getRecord();
    
                for (RecordType record : records)
                {
                    MessageElement[] messages = record.getRecordData().get_any();
                    for (MessageElement messageElement : messages)
                    {
                        String itemXml = messageElement.getAsString();
//                        logger.debug("XML: " + itemXml);
                        try
                        {
                            // Parse Search result document
                            SearchResultRecordDocument document = SearchResultRecordDocument.Factory.parse(itemXml);
                            SearchResultRecord srr = document.getSearchResultRecord();
                            // Initialize a cursor
                            XmlCursor cursor = srr.newCursor();
                            // Set XPath
                            String nsuri = PropertyReader.getProperty("xsd.soap.item.item");
                            String namespace = "declare namespace escidocItem='" + nsuri + "';"; 
                            // Read xml to XPath
                            cursor.selectPath(namespace + "./escidocItem:item");
                            cursor.toNextSelection();
                            
                            ItemDocument itemDoc;
                            
                            //TODO to remove
                            try{
                            // Create the item document
                            itemDoc = ItemDocument.Factory.parse(cursor.xmlText());
                            }catch(Exception e){
                            	cursor.toLastChild();
                            	cursor.toFirstChild();
                            	itemDoc = ItemDocument.Factory.parse(cursor.xmlText());
                            }
                            
                            
                            cursor.dispose(); 

                            // Store results in item list.
                            items.add(new ItemVO(itemDoc));
                        }
                        catch (Exception e)
                        {
                            logger.error("Error unmarshmalling item", e);
                        }
                    }
                }
                totalNumberOfItems = searchResult.getNumberOfRecords().intValue();
            }
            
            // Delete list from component tree to let it be refreshed.
            UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent("Start:listpanel:list");
            
            if (component != null)
            {
                component.getParent().getChildren().remove(component);
            }
            
            // If the is a diagnostics, check if it has to do with the sorting 
            if (searchResult.getDiagnostics() != null)
            {
                for (int i = 0; i < searchResult.getDiagnostics().getDiagnostic().length; i++)
                {
                    DiagnosticType diagnostic = searchResult.getDiagnostics().getDiagnostic(i);
                    SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
                    String[] details = diagnostic.getDetails().split("\"");
                    if (details.length == 3 
                            && "1/java.lang.RuntimeException: field ".equals(details[0])
                            && " does not appear to be indexed".equals(details[2]))
                    {
                        sb.setMessage("Sorting disabled! One of the sorting parameter (" 
                                            + details[1]
                                            +  ") is not valid, please change your sorting parameter");
                        executeQueryForItems(query, itemsPerPage, pageNumber, "", queryExtension);
                    }    
                } 
            }
            
            
        } catch (Exception e) {
            logger.error("No result for this request", e);// TODO: handle exception
        }
    }
    
    /**
     * 
     * @param query
     * @param itemsPerPage
     * @param pageNumber
     * @param sortedBy
     * @param index
     * @throws ServiceException
     * @throws URISyntaxException
     * @throws RemoteException
     */
    public void executeQueryForContainers(String query, int itemsPerPage, int pageNumber, String sortedBy, String index) throws ServiceException, URISyntaxException, RemoteException
    {
//        NonNegativeInteger show = new NonNegativeInteger(itemsPerPage + "");
//        PositiveInteger page = new PositiveInteger(pageNumber + "");
//        // creation of the request for the search method
//        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
//        searchRetrieveRequest.setVersion("1.1");
//        searchRetrieveRequest.setQuery(query);
//        if (sortedBy != null)
//        {
//            searchRetrieveRequest.setSortKeys(sortedBy);
//        }
//        searchRetrieveRequest.setStartRecord(page);
//        searchRetrieveRequest.setMaximumRecords(show);
//        searchRetrieveRequest.setRecordPacking("xml");
//        // Create XML Source
//        SRWPort searchHandler = ServiceLocator.getSearchHandler(index);
//        SearchRetrieveResponseType searchResult = searchHandler.searchRetrieveOperation(searchRetrieveRequest);
//        try
//        {
//            RecordsType recordsType = searchResult.getRecords();
//            containers = new ArrayList<FacesContainerVO>();
//            totalNumberOfItems = 0;
//            if (recordsType != null)
//            {
//                RecordType[] records = recordsType.getRecord();
//    
//                for (RecordType record : records)
//                {
//                    MessageElement[] messages = record.getRecordData().get_any();
//                    for (MessageElement messageElement : messages)
//                    {
//                        String containerXml = messageElement.getAsString();
//                        //logger.debug("XML: " + itemXml);
//                        
//                        ContainerResultVO containerResultVO = (ContainerResultVO)xmlTransforming.transformToSearchResult(containerXml);
//                        containers.add(new FacesContainerVO(containerResultVO));
//                    }
//                }
//                totalNumberOfItems = searchResult.getNumberOfRecords().intValue();
//            }
//            // Delete list from component tree to let it be refreshed.
//            UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent("Start:listpanel:list");
//            if (component != null)
//            {
//                component.getParent().getChildren().remove(component);
//            }
//        } catch (Exception e) {
//            logger.error("No result for this request", e);// TODO: handle exception
//        }
    }
    
    /**
     * Execute query for {@link AlbumVO}
     * @param query
     * @param itemsPerPage
     * @param pageNumber
     * @param sortedBy
     * @param index
     * @throws ServiceException
     * @throws URISyntaxException
     * @throws RemoteException
     * @deprecated use executeQueryForFacesContainers instead
     */
    public void executeQueryForAlbums(String query, int itemsPerPage, int pageNumber, String sortedBy, String index) throws ServiceException, URISyntaxException, RemoteException
    {
        NonNegativeInteger show = new NonNegativeInteger(itemsPerPage + "");
        PositiveInteger page = new PositiveInteger(pageNumber + "");
        // creation of the request for the search method
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(query);
        if (sortedBy != null)
        {
            searchRetrieveRequest.setSortKeys(sortedBy);
        }
        searchRetrieveRequest.setStartRecord(page);
        searchRetrieveRequest.setMaximumRecords(show);
        searchRetrieveRequest.setRecordPacking("xml");
        // Create XML Source
        SRWPort searchHandler = ServiceLocator.getSearchHandler(index);
        SearchRetrieveResponseType searchResult = searchHandler.searchRetrieveOperation(searchRetrieveRequest);
        try
        {
            RecordsType recordsType = searchResult.getRecords();
            albums = new ArrayList<AlbumVO>();
            totalNumberOfItems = 0;
            if (recordsType != null)
            {
                RecordType[] records = recordsType.getRecord();
    
                for (RecordType record : records)
                {
                    MessageElement[] messages = record.getRecordData().get_any();
                    for (MessageElement messageElement : messages)
                    {
                        String containerXml = messageElement.getAsString();
                        //logger.debug("XML: " + itemXml);
                        
                        ContainerResultVO containerResultVO = (ContainerResultVO)xmlTransforming.transformToSearchResult(containerXml);
                        albums.add(new AlbumVO(containerResultVO));
                    }
                }
                totalNumberOfItems = searchResult.getNumberOfRecords().intValue();
            }
            // Delete list from component tree to let it be refreshed.
            UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent("Start:listpanel:list");
            if (component != null)
            {
                component.getParent().getChildren().remove(component);
            }
        } catch (Exception e) {
            logger.error("No result for this request", e);// TODO: handle exception
        }
    }
    
    public void executeQueryForFacesContainers(String query, int itemsPerPage, int pageNumber, String sortedBy, String index) throws ServiceException, URISyntaxException, RemoteException
    {
        NonNegativeInteger show = new NonNegativeInteger(itemsPerPage + "");
        PositiveInteger page = new PositiveInteger(pageNumber + "");
        // creation of the request for the search method
        SearchRetrieveRequestType searchRetrieveRequest = new SearchRetrieveRequestType();
        searchRetrieveRequest.setVersion("1.1");
        searchRetrieveRequest.setQuery(query);
        if (sortedBy != null)
        {
            searchRetrieveRequest.setSortKeys(sortedBy);
        }
        searchRetrieveRequest.setStartRecord(page);
        searchRetrieveRequest.setMaximumRecords(show);
        searchRetrieveRequest.setRecordPacking("xml");
        // Create XML Source
        SRWPort searchHandler = ServiceLocator.getSearchHandler(index);
        SearchRetrieveResponseType searchResult = searchHandler.searchRetrieveOperation(searchRetrieveRequest);
        try
        {
            RecordsType recordsType = searchResult.getRecords();
            facesContainers = new ArrayList<FacesContainerVO>();
            totalNumberOfItems = 0;
            if (recordsType != null)
            {
                RecordType[] records = recordsType.getRecord();
    
                for (RecordType record : records)
                {
                    MessageElement[] messages = record.getRecordData().get_any();
                    for (MessageElement messageElement : messages)
                    {
                        String containerXml = messageElement.getAsString();
                        logger.info("XML: " + containerXml);
                        
                        ContainerResultVO containerResultVO = (ContainerResultVO)xmlTransforming.transformToSearchResult(containerXml);
                        facesContainers.add(new FacesContainerVO(containerResultVO));
                    }
                }
                totalNumberOfItems = searchResult.getNumberOfRecords().intValue();
            }
            // Delete list from component tree to let it be refreshed.
            UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent("Start:listpanel:list");
            if (component != null)
            {
                component.getParent().getChildren().remove(component);
            }
        } catch (Exception e) {
            logger.error("No result for this request", e);// TODO: handle exception
        }
    }
    
    /**
     * Create the query to browse the album.
     * @param albumId: the id of the album
     * @return The query.
     */
    public String createContainerQuery(FacesContainerVO container)
    {
        String query = "";
        if (container != null)
        {
            if (container.getMembers().size() > 0) 
            {
                query = "escidoc.objid any \"";
                for (int i = 0; i < container.getMembers().size(); i++)
                {
                    query += container.getMembers().get(i).getObjectId() ;
                    if (i < container.getMembers().size() - 1)
                    {
                        query += "  ";
                    }
                    else
                    {
                        query += "\"";
                    }
                }
            }
            // case of the album has no pictures within
            if (container.getMembers().size() == 0)
            {
                query = "";
                query += "(escidoc.objid=000)";
                totalNumberOfItems = 0;
            }
        }
        return query;
    }
    
    public String createCollectionQuery(FacesContainerVO collection){
    	String query = "";
    	if(collection != null){
    		String collectionID = collection.getLatestVersion().getObjectId();
    		query = "(escidoc.content-relation="+collectionID+")";
    	}
	
	
	
	
    	return query;
    }

    public static String concatQuery(String query1, String query2)
    {
        if (query1 == null || "".equals(query1))
        {
            return query2;
        }
        else if (query2 == null || "".equals(query2))
        {
            return query1;
        }
        else
        {
            return "(" + query1 + ") and (" + query2 + ")";
        }
    }

    public List<ItemVO> getItems()
    {
        return items;
    }

    public void setItems(List<ItemVO> items)
    {
        this.items = items;
    }

    public int getTotalNumberOfItems()
    {
        return totalNumberOfItems;
    }

    public void setTotalNumberOfItems(int totalNumberOfItems)
    {
        this.totalNumberOfItems = totalNumberOfItems;
    }

    public ItemVO getFirstItem()
    {
        if (items != null && items.size() > 0)
        {
            return items.get(0);
        }
        else
        {
            return null;
        }
    }
    /*
     * Create the query for the multiple sorting and save it in the the sessionBean 
     */
    public String createSortingQuery(String sort1, String order1, String sort2, String order2, String sort3, String order3)
    {
        SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        String sortedBy = "";
        if (sort1 != null)
        {
            sortedBy += "sort." + addSortParameter(sort1);
            //sessionBean.setSort1(sort1);
            sessionBean.getSortList().set(0, sort1);
        }
        else
        {
            //sortedBy += "escidoc.emotion";
        }
        if (order1 != null)
        {
            sortedBy +=",," + addOrderParameter(order1);
            //sessionBean.setOrder1(order1);
            sessionBean.getOrderList().set(0, order1);
        }
        else 
        {
            //sortedBy +=",,1";
            
        }
        if (sort2 != null)
        {
            sortedBy += " sort." + addSortParameter(sort2);
            //sessionBean.setSort2(sort2);
            sessionBean.getSortList().set(1, sort2);;
        }
        else 
        {
            //sortedBy += " escidoc.identifier";
            
        }
        if (order2 != null)
        {
            sortedBy +=",," + addOrderParameter(order2);
            //sessionBean.setOrder2(order2);
            sessionBean.getOrderList().set(1, order2);
        }
        else 
        {
            //sortedBy +=",,1";
        }
        if (sort3 != null)
        {
            sortedBy += " sort." + addSortParameter(sort3);
            //sessionBean.setSort3(sort3);
            sessionBean.getSortList().set(2, sort3);
        }
        else
        {
            //sortedBy += " escidoc.picture-group";
        }
        if (order3 != null)
        {
            sortedBy +=",," + addOrderParameter(order3);
            //sessionBean.setOrder3(order3);
            sessionBean.getOrderList().set(2, order3);
        }
        else 
        {
           // sortedBy +=",,1";
        }
        
        return sortedBy;
    }
    
    public String addSortParameter(String sort)
    {
        String query = "";
        if (sort.equals("pictureset"))
        {
           return "picture-group";
        }
        if (sort.equals("personid"))
        {
            return "identifier";
        }
        // new cases
       /* if (sort.equals("personid"))
        {
            return "identifier";
        }
        if (sort.equals("pictureset"))
        {
            return "picture-group";
        }*/
        else
        {
           return sort.toLowerCase();
        }
    }
    
    public String addOrderParameter(String order)
    {
        if (order.equals("asc"))
        {
            return "1";
        }
        else
        {
            return "0";
        }
    }

    /**
     * @deprecated
     * @return
     */
    public List<AlbumVO> getAlbums()
    {
        return albums;
    }

    /**
     * @deprecated
     * @param albums
     */
    public void setAlbums(List<AlbumVO> albums)
    {
        this.albums = albums;
    }
    
    /**
     * 
     * @return
     */
    public List<FacesContainerVO> getFacesContainers()
    {
        return facesContainers;
    }

    public void setFacesContainers(List<FacesContainerVO> facesContainers)
    {
        this.facesContainers = facesContainers;
    }
}
