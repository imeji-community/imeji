package de.mpg.escidoc.faces.container.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.escidoc.schemas.container.x08.ContainerDocument;
import de.escidoc.schemas.container.x08.ContainerDocument.Container;
import de.escidoc.schemas.containerlist.x08.ContainerListDocument;
import de.escidoc.schemas.containerlist.x08.ContainerListDocument.ContainerList;
import de.escidoc.www.services.om.ContainerHandler;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.FacesContainerController;
import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.album.AlbumSession;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO.HandlerType;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.QueryHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ContainerVOListWrapper;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class FacesContainerListController 
{
    //private XmlTransforming xmlTransforming = null;
    
    public FacesContainerListController()
    {
        InitialContext context;
        try
        {
            context = new InitialContext();
           
        }
        catch (NamingException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Retrieve an albumList.
     * @param list
     * @param userHandle
     * @return
     */
    public FacesContainerListVO retrieve(FacesContainerListVO list, String userHandle) throws Exception
    {
        if (HandlerType.SEARCH.equals(list.getHandler())) 
        {
            return search(list, userHandle);
        }
        else if (HandlerType.FILTER.equals(list.getHandler()))
        {
            return filter(list, userHandle);
        }
        
        return null;
    }
    
    /**
     * Retrieve a list of albums with filters.
     * @param filter
     * @param userHandle
     * @return
     * @throws Exception
     */
    public FacesContainerListVO filter(FacesContainerListVO list, String userHandle) throws Exception
    {       
        AlbumSession albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);  
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        InitialContext context = new InitialContext();
        
        
        ContainerHandler cth = null;
        
        if (userHandle != null) 
        {
			cth = ServiceLocator.getContainerHandler(userHandle);
		}
        else
        {
        	cth = ServiceLocator.getContainerHandler();
        }
        
        
        String containerListXml = cth.retrieveContainers(list.getParameters().getParametersAsFilter());      
        System.out.println(containerListXml);
        ContainerListDocument containerListDoc = ContainerListDocument.Factory.parse(containerListXml);
        List<ContainerVO> containerList = new ArrayList<ContainerVO>();
        for(Container cont : containerListDoc.getContainerList().getContainerArray())
        {
        	ContainerDocument containerDoc = ContainerDocument.Factory.parse(cont.getDomNode());
        	containerList.add(FacesContainerController.transformToContainerVO(containerDoc.xmlText()));
        }
        
        
        list = actualizeListWithNewList(list, containerList);
        
        FacesContainerListVO containerListVO = new FacesContainerListVO(list);
        
        containerListVO.setSize(Integer.parseInt(containerListDoc.getContainerList().getNumberOfRecords().getStringValue()));
        
        return containerListVO;
    }
    
    /**
     * Retrieve a list of album with the search.
     * @param parameters
     * @param userHandle
     * @return
     */
    public FacesContainerListVO search(FacesContainerListVO list, String userHandle) throws Exception 
    {
        QueryHelper queryHelper  = new QueryHelper();
        queryHelper.executeQueryForFacesContainers(
                    list.getParameters().getParametersAsSearchQuery()
                    , list.getParameters().getShow()
                    , (list.getParameters().getPage()-1) * list.getParameters().getShow() + 1 
                    , list.getParameters().getParametersAsSortingQuery()
                    , "escidoc_all");
        
        list.setList(queryHelper.getFacesContainers());  
        
        FacesContainerListVO albumListVO = new FacesContainerListVO(list);
        albumListVO.setSize(queryHelper.getTotalNumberOfItems());
                
        return albumListVO;
    }
    
    /**
     * Actualize the old list with the new list.
     * Keep the local variables stored (like selected).
     * Set the new sorting order.
     * @param oldList the {@link AlbumListVO} currently managed.
     * @param list the {@link ContainerVO} newly retrieved from the FW (with new sorting).
     * @return
     */
    public FacesContainerListVO actualizeListWithNewList(FacesContainerListVO oldList, List<? extends ContainerVO> list)
    {
        Map<String, FacesContainerVO> map = new HashMap<String, FacesContainerVO>();
        
        for (int i = 0; i < list.size(); i++)
        {
            map.put(list.get(i).getVersion().getObjectId(), new FacesContainerVO(list.get(i)));
            //System.out.println(list.get(i).getMetadataSets().get(0).getTitle());
        }
        
        for (int i = 0; i < oldList.getList().size(); i++)
        {
            if (map.containsKey(oldList.getList().get(i).getVersion().getObjectId()))
            {
                map.get(oldList.getList().get(i).getVersion().getObjectId()).setSelected(oldList.getList().get(i).isSelected());
            }
        }
        
        oldList.getList().clear();
        
        for (int i = 0; i <list.size(); i++)
        {
           oldList.getList().add( map.get(list.get(i).getVersion().getObjectId()));
        }
        
        return oldList;
    }
    
    /**
     * Concats two lists, but keeps parameters.
     * @param list the {@link AlbumListVO} currently managed.
     * @param addList the {@link ContainerVO} newly retrieved from the FW (with new sorting).
     * @return
     */
    public FacesContainerListVO addToList(FacesContainerListVO list, List<? extends ContainerVO> addList)
    {
        Map<String, FacesContainerVO> map = new HashMap<String, FacesContainerVO>();
        
        for (int i = 0; i < addList.size(); i++)
        {
            map.put(addList.get(i).getVersion().getObjectId(), new FacesContainerVO(addList.get(i)));
            //System.out.println(list.get(i).getMetadataSets().get(0).getTitle());
        }
        
        for (int i = 0; i < list.getList().size(); i++)
        {
            if (map.containsKey(list.getList().get(i).getVersion().getObjectId()))
            {
                map.get(list.getList().get(i).getVersion().getObjectId()).setSelected(list.getList().get(i).isSelected());
            }
        }
        
        for (int i = 0; i < addList.size(); i++)
        {
            list.getList().add( map.get(addList.get(i).getVersion().getObjectId()));
        }
        
        return list;
    }
}
