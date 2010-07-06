package de.mpg.escidoc.faces.container.album;

import java.util.List;

import de.mpg.escidoc.faces.container.FacesContainerVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListParameters;
import de.mpg.escidoc.faces.container.list.FacesContainerListVO.HandlerType;
import de.mpg.escidoc.faces.metadata.MdsFacesContainerVO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.framework.PropertyReader;

public class AlbumVO extends FacesContainerVO
{
    /**
     * Default constructor
     */
    public AlbumVO()
    {
        super();
        init();
    }
    
    /**
     * Constructor of an album from a {@link ContainerVO} 
     * @param ct
     */
    public AlbumVO(ContainerVO ct)
    {
        super(ct);
        
        if (ct.getStatusComment() != null) 
        {
        	this.setStatusComment(ct.getStatusComment());
		}
        
        try
        {
            super.setMdRecord(new MdsFacesContainerVO((MdsPublicationVO) ct.getMetadataSets().get(0)));
        }
        catch (Exception e)
        {
            // This is in case the md record is not compatible due to wrong transformation of existing album during migration.
        	super.setMdRecord(new MdsFacesContainerVO());
        	super.getMdRecord().setTitle(ct.getMetadataSets().get(0).getTitle());
            super.getMdRecord().getAbstracts().add(new TextVO("Album with old md-record schema."));
        }
    }
    
    private void init()
    {
        try
        {
            this.setContext(new ContextRO(PropertyReader.getProperty("escidoc.faces.context.id")));
            this.setContentModel(PropertyReader.getProperty("escidoc.faces.container.content-model.id"));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
