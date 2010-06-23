package de.mpg.escidoc.faces.album;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.escidoc.www.services.om.ContainerHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.PidTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO.State;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * 
 * @author saquet
 *
 */
public class AlbumController
{
    /**
     * The album controlled.
     */
    private AlbumVO album;
    
    private XmlTransforming xmlTransforming = null;
    private String APPLICATION_URL = null;
    
    public AlbumController()
    {
        try
        {
            // Class initialization
            InitialContext context = new InitialContext();
            xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
            
            album = new AlbumVO();
        }
        catch (Exception e) 
        {
            throw new RuntimeException(e);
        }
    }
    
    public AlbumController(AlbumVO album)
    {
        try
        {
            // Class initialization
            InitialContext context = new InitialContext();
            xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
            
            // Variable initialization
            this.album = album;
            APPLICATION_URL = PropertyReader.getProperty("escidoc.faces.instance.url");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Retrieve an album.
     * @param id
     * @param userHandle
     * @return
     * @throws Exception
     */
    public AlbumVO retrieve(String id, String userHandle) throws Exception
    {

//            AlbumListParameters parameters = new AlbumListParameters("pending", SortParameterType.LAST_MODIFICATION_DATE, OrderParameterType.ASCENDING, 10, 0, null);
//            parameters.setId(new ArrayList<String>());
//            parameters.getId().add(id);
//            String list = ServiceLocator.getContainerHandler(userHandle).retrieveContainers(parameters.getParametersAsFilter());
            // System.out.println(list);
            
           ContainerHandler handler = ServiceLocator.getContainerHandler();
            
            if (userHandle != null)
            {
                handler = ServiceLocator.getContainerHandler(userHandle);
            }
            
            String albumXml= handler.retrieve(id);
            
            ContainerVO containerVo = xmlTransforming.transformToContainer(albumXml);
            
            album = new AlbumVO(containerVo);

        return album;
    }
    
    /**
     * Publish a pending album.
     * Step1: assign object pid.
     * Step2: assign version pid.
     * Step3: submit album.
     * Step4: release album.
     * @param album
     * @param userHandle
     * @return
     * @throws Exception
     */
    public AlbumVO publish(AlbumVO album, String userHandle) throws Exception
    {
        String modificationDate = JiBXHelper.serializeDate(album.getModificationDate());
        String paramXml = null;
        
        // Assign object pid
        PidTaskParamVO paramAssignation = 
                new PidTaskParamVO(album.getLatestVersion().getModificationDate()
                                        , ServiceLocator.getFrameworkUrl() 
                                            + "/ir/container/" 
                                            + album.getLatestVersion().getObjectId());
        
        paramXml = xmlTransforming.transformToPidTaskParam(paramAssignation);
        
        modificationDate = ServiceLocator
            .getContainerHandler(userHandle)
                    .assignObjectPid(album.getLatestVersion().getObjectId(), paramXml);
        
        album = retrieve(album.getVersion().getObjectId(), userHandle);
        
        modificationDate = JiBXHelper.serializeDate(album.getModificationDate());
        
        // Assign version pid
        PidTaskParamVO paramVersion = 
            new PidTaskParamVO(JiBXHelper.deserializeDate(modificationDate)
                                    , APPLICATION_URL + "album/" + album.getVersion().getObjectIdAndVersion());
        
        paramXml = xmlTransforming.transformToPidTaskParam(paramVersion);
        
        modificationDate = ServiceLocator
            .getContainerHandler(userHandle)
                .assignVersionPid( album.getLatestVersion().getObjectId(), paramXml);
        
        album = retrieve(album.getVersion().getObjectId(), userHandle);
        
        modificationDate = JiBXHelper.serializeDate(album.getModificationDate());
        
        // Submit album
        paramXml = "<param last-modification-date=\""
            + JiBXHelper.serializeDate(album.getModificationDate())
            + "\"><comment>submit to publish</comment></param>";

            modificationDate = ServiceLocator.getContainerHandler(userHandle).submit(album.getVersion().getObjectId(), paramXml);
        
        album = retrieve(album.getVersion().getObjectId(), userHandle);
        modificationDate = JiBXHelper.serializeDate(album.getModificationDate());
        
        // Release the Album
        paramXml = "<param last-modification-date=\""
                    + modificationDate
                    + "\"><comment>Publication of the album</comment></param>";
  
        ServiceLocator.getContainerHandler(userHandle).release(
                    album.getVersion().getObjectId(), paramXml);
        
        album = retrieve(album.getVersion().getObjectId(), userHandle);
        
        return album;
    }
    
    /**
     * Create an album on the FW.
     * @param album
     * @param userHandle
     * @throws Exception
     */
    public AlbumVO create(AlbumVO album, String userHandle) throws Exception
    {
    	ContainerVO ct = new ContainerVO(album);
    	ct.getMetadataSets().set(0, new MdsPublicationVO(album.getMdRecord()));
    	// Album serialization
        String albumXml = xmlTransforming.transformToContainer(ct);
        // Create Album on FW
        albumXml = ServiceLocator.getContainerHandler(userHandle).create(albumXml);
        // Album deserialization
        ContainerVO containerVO = xmlTransforming.transformToContainer(albumXml);
        // Create album
        album = new AlbumVO(containerVO);
        
        return album;
    }
    
    
    /**
     * Edit an album
     * @param album : album to update
     * @param userHandle
     */
    public void edit(AlbumVO album, String userHandle) throws Exception
    {        
    	ContainerVO ct = new ContainerVO(album);
    	ct.getMetadataSets().set(0, new MdsPublicationVO(album.getMdRecord()));
    	// Serialize
        String albumXml = xmlTransforming.transformToContainer(ct);
        
        // Call FW's update
        ServiceLocator.getContainerHandler(
                userHandle).update(
                        album.getLatestVersion().getObjectId(), albumXml);
    }
    
    /**
     * Delete an album if status is not released or withdraw.
     * @param album
     * @param userHandle
     * @return true if album has been deleted
     * @throws Exception 
     */
    public boolean delete(AlbumVO album, String userHandle) throws Exception
    {
        if (State.PENDING.equals(album.getVersion().getState())
                || State.SUBMITTED.equals(album.getVersion().getState()))
        {
            removeAllImages(album, userHandle);
            
            ServiceLocator
                .getContainerHandler(userHandle)
                    .delete(album.getLatestVersion().getObjectId());
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Withdraw an an album if its status is released.
     * @param album
     * @param userHandle
     * @return true if album has been withdrawn
     */
    public boolean withdraw(AlbumVO album, String message, String userHandle) throws Exception
    {
        if (State.RELEASED.equals(album.getVersion().getState()))
        {
            String param = "<param last-modification-date=\""
                + JiBXHelper.serializeDate(album.getLatestVersion().getModificationDate())
                + "\"><comment>" 
                + message
                + "</comment></param>";
        
            ServiceLocator
                .getContainerHandler(userHandle)
                    .withdraw(album.getLatestVersion().getObjectId(), param);

            return true;
        }
        
        return false;
    }
    
    /**
     *  Submit an album.
     * The album must be in state pending.
     * The album must have PID's, please call the method assignPIDs before.
     */
    public void submit(String userHandle)
    {
        String param = "<param last-modification-date=\""
            + JiBXHelper.serializeDate(album.getModificationDate())
            + "\"><comment>submit to publish</comment></param>";
        
        try
        {
            ServiceLocator.getContainerHandler(userHandle).submit(
                album.getVersion().getObjectId(), param);
        }
        catch (Exception e) 
        {
            throw new RuntimeException("Error submitting album", e);
        }
    }
    
    /**
     * Call Export
     * @param albumVO
     * @param userHandle
     */
    public void export(AlbumVO album, ExportManager export)
    {
    	try 
    	{
			export.doExport(album);
		} 
    	catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
    }
    
    /**
     * WORKAROUND METHOD???? updateWorkAround()
     */
    
    /**
     * Adds an item as a member to an album if this item is not already a member of the album.
     * The method is synchronized to avoid optimistic lock exception from FW.
     * @param ItemVO 
     * @throws NamingException 
     * @throws IOException 
     */
    public AlbumVO addMember(AlbumVO album, String item, String userHandle) throws Exception
    {
        List<String> itemList = Arrays.asList(item);
        return this.addMembers(album, itemList, userHandle);
    }
    
    /**
     * Adds items as members to an album if this item is not already a member of the album.
     * The method is synchronized to avoid optimistic lock exception from FW.
     * @param itemList 
     * @throws IOException 
     */
    public AlbumVO addMembers(AlbumVO album, List<String> itemList, String userHandle) throws Exception
    {     
        String param ="<param last-modification-date=\"";
        param += JiBXHelper.serializeDate(album.getVersion().getModificationDate());
        param += "\">";
        for (int i = 0; i < itemList.size(); i++)
        {
            if (album.getMembersId()
                    .indexOf(itemList.get(i)) == -1)
            {
                // Create the list of items to be added
                param += "<id>";
                param += itemList.get(i);
                param += "</id>";
            }
        }
        param += "</param>";
        
        // Add the pictures
        ServiceLocator
            .getContainerHandler(userHandle)
                .addMembers(album.getVersion().getObjectId(), param);
        
        return retrieve(album.getVersion().getObjectId(), userHandle);
    }
    
    /**
     * Removes an item from an album
     * The method is synchronized to avoid optimistic lock exception from FW.
     * @param ItemVO 
     * @throws NamingException 
     * @throws IOException 
     */
    public synchronized AlbumVO removeMember(AlbumVO album, String item, String userHandle) throws Exception
    {
        List<String> itemList = Arrays.asList(item);
        return removeMembers(album, itemList, userHandle);
    }
    
    /**
     * Removes a list of items from an album
     * The method is synchronized to avoid optimistic lock exception from FW.
     * @param List<ItemVO>
     * @throws IOException 
     */
    public synchronized AlbumVO removeMembers(AlbumVO album, List<String> itemList, String userHandle) throws Exception
    {
        String param ="<param last-modification-date=\"";
        param += JiBXHelper.serializeDate(album.getVersion().getModificationDate());
        param += "\">";
        
        for (int i = 0; i < itemList.size(); i++)
        {
            if (album.getMembersId()
                    .indexOf(itemList.get(i)) != -1)
            {
                param += "<id>";
                param += itemList.get(i);
                param += "</id>";
            }
        }
        param += "</param>";
        
        try
        {
            ServiceLocator
                .getContainerHandler(userHandle)
                     .removeMembers(album.getLatestVersion().getObjectId(), param);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        return retrieve(album.getVersion().getObjectId(), userHandle);
    }
    
    /**
     * Delete all members of an album.
     * @param album
     * @param userHandle
     * @throws Exception
     */
    public void removeAllImages(AlbumVO album, String userHandle) throws Exception
    {
        // Set delete method parameter
        String paramXml = 
                "<param last-modification-date=\"" +
                    JiBXHelper.serializeDate(album.getLatestVersion().getModificationDate())
                + "\">" +
                "<filter name=\"http://purl.org/dc/elements/1.1/identifier\">";

        for (int i = 0; i <  album.getMembers().size(); i++)
        {
            paramXml += "<id>" +  album.getMembersId().get(i) + "</id>";
        }
        
        paramXml += "</filter></param>";
        
        // Remove all the members of the album
        ServiceLocator
            .getContainerHandler(userHandle)
                .removeMembers(album.getLatestVersion().getObjectId(), paramXml);
    }
    
    /**
     * Retrieves a list of all images identifiers of the album.
     * @return List<String>
     */
    public List<String> getImages()
    {
        List<String> images = new ArrayList<String>();
        for (int i = 0; i < this.album.getMembers().size(); i++)
        {
            images.add(this.album.getMembers().get(i).getObjectId());
        }        
        return images;
    }
    
    /**
     * Returns the number of images in an album.
     * @return number of images as int
     */
    public int getAlbumSize()
    {
        return this.album.getMembers().size();
    }
}
