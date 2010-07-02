package de.mpg.escidoc.faces.album.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.escidoc.www.services.om.ContainerHandler;
import de.mpg.escidoc.faces.album.AlbumVO;
import de.mpg.escidoc.faces.album.beans.AlbumSession;
import de.mpg.escidoc.faces.album.list.AlbumListVO.HandlerType;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.QueryHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ContainerVOListWrapper;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * 
 * @author saquet
 *
 */
public class AlbumsListController
{
    private XmlTransforming xmlTransforming = null;
    
    public AlbumsListController()
    {
        InitialContext context;
        try
        {
            context = new InitialContext();
            xmlTransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
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
    public AlbumListVO retrieve(AlbumListVO list, String userHandle) throws Exception
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
    public AlbumListVO filter(AlbumListVO list, String userHandle) throws Exception
    {       
        AlbumSession albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);  
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        ContainerVOListWrapper wrapper = null;
        InitialContext context = new InitialContext();
        xmlTransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);
        
        ContainerHandler cth = null;
        
        if (userHandle != null) 
        {
			cth = ServiceLocator.getContainerHandler(userHandle);
		}
        else
        {
        	cth = ServiceLocator.getContainerHandler();
        }
        
        // START Workaround for FW Bug
        if (false)
        {
            list.getList().clear();
            
            //Get all albums (used for the sorting)
            AlbumListVO listAll = new AlbumListVO(new ArrayList<AlbumVO>(), list.clone().getParameters(), list.getHandler());
            listAll.getParameters().setPage(1);
            listAll.getParameters().setShow(200);
            listAll.getList().clear();
            AlbumListVO listSorted = new AlbumListVO(new ArrayList<AlbumVO>(), list.clone().getParameters(), list.getHandler());
            listSorted.getList().clear();
          
            String containerListXml = cth.retrieveContainers(listAll.getParameters().getParametersAsFilter());      
            List<? extends ContainerVO> allAlbums = xmlTransforming.transformToContainerList(containerListXml);
            listAll = addToList(listAll, allAlbums);
            
            int page = list.getParameters().getPage();
            int show = list.getParameters().getShow();
            
            list.getParameters().setPage(1);
            list.getParameters().setShow(200);
            
            //Add pending albums from user
            if (sessionBean.getUser() != null) 
            {
            	 list.getParameters().setCreator(sessionBean.getUser().getReference().getObjectId());
                 list.getParameters().setState("pending");
                 containerListXml = cth.retrieveContainers(list.getParameters().getParametersAsFilter());      
                 List<? extends ContainerVO> pendingAlbums = xmlTransforming.transformToContainerList(containerListXml);
                 list = addToList(list, pendingAlbums);
               
                 //Add withdrawn albums from user
                 list.getParameters().setCreator(sessionBean.getUser().getReference().getObjectId());
                 list.getParameters().setState("withdrawn");
                 containerListXml = cth.retrieveContainers(list.getParameters().getParametersAsFilter());      
                 List<? extends ContainerVO> withdrawnAlbums = xmlTransforming.transformToContainerList(containerListXml);
                 list = addToList(list, withdrawnAlbums);
			}
           
            //Add all public albums
            list.getParameters().setCreator(null);
            list.getParameters().setState("released");
            containerListXml = cth.retrieveContainers(list.getParameters().getParametersAsFilter());      
            List<? extends ContainerVO> publicAlbums = xmlTransforming.transformToContainerList(containerListXml);
            list = addToList(list, publicAlbums);
            
            //Check and set the sorting
            for (int i = 0; i < listAll.getList().size(); i++) 
            {
            	for (int j = 0; j < list.getList().size(); j++) 
            	{
					if (list.getList().get(j).getVersion().getObjectId().equals(
							listAll.getList().get(i).getVersion().getObjectId())) 
					{
						listSorted.getList().add(list.getList().get(j));
					}
				}
			}
            
            list.getParameters().setPage(page);
            list.getParameters().setShow(show);
            
            // Remove the album that not should be displayed on this page
            int lastRecord = list.getParameters().getPage() * list.getParameters().getShow();
            int firstRecord =  (list.getParameters().getPage() - 1) * list.getParameters().getShow();
            list.getList().clear();
            list.setList(listSorted.getList());
            list.setSize(list.getList().size());
            
            if (lastRecord <= list.getList().size()) 
            {
            	list.setList(list.getList().subList(firstRecord, lastRecord));
			}
            else
            {
            	list.setList(list.getList().subList(firstRecord,  list.getList().size()));
            }
        }
        else
        {
            String containerListXml = cth.retrieveContainers(list.getParameters().getParametersAsFilter());      
            wrapper = xmlTransforming.transformToContainerListWrapper(containerListXml);
            list = actualizeListWithNewList(list, wrapper.getContainerVOList());
        }
        // END Workaround for FW Bug
        
        AlbumListVO albumListVO = new AlbumListVO(list);
        
        if (albumSession.getFilter().equalsIgnoreCase("all")) 
        {
        	//albumListVO.setSize(list.getList().size());
		}
        else
        {
        	albumListVO.setSize(Integer.parseInt(wrapper.getNumberOfRecords()));
        }
        
        //

              
        return albumListVO;
    }
    
    /**
     * Retrieve a list of album with the search.
     * @param parameters
     * @param userHandle
     * @return
     */
    public AlbumListVO search(AlbumListVO list, String userHandle) throws Exception
    {
        QueryHelper queryHelper  = new QueryHelper();
        queryHelper
            .executeQueryForAlbums(
                    list.getParameters().getParamtersAsSearchQuery()
                    , list.getParameters().getShow()
                    , (list.getParameters().getPage()-1) * list.getParameters().getShow() + 1 
                    , list.getParameters().getParametersAsSortingQuery()
                    , "escidoc_all");
        
        list.setList(queryHelper.getAlbums());  
        
        AlbumListVO albumListVO = new AlbumListVO(list);
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
    public AlbumListVO actualizeListWithNewList(AlbumListVO oldList, List<? extends ContainerVO> list)
    {
        Map<String, AlbumVO> map = new HashMap<String, AlbumVO>();
        
        for (int i = 0; i < list.size(); i++)
        {
            map.put(list.get(i).getVersion().getObjectId(), new AlbumVO(list.get(i)));
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
    public AlbumListVO addToList(AlbumListVO list, List<? extends ContainerVO> addList)
    {
        Map<String, AlbumVO> map = new HashMap<String, AlbumVO>();
        
        for (int i = 0; i < addList.size(); i++)
        {
            map.put(addList.get(i).getVersion().getObjectId(), new AlbumVO(addList.get(i)));
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
