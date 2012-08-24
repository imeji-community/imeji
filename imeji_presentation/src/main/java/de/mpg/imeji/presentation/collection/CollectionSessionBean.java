/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.collection;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;

public class CollectionSessionBean
{
    private CollectionImeji active = null;
    private MetadataProfile profile = null;
    private String selectedMenu = "SORTING";
    private String filter = "all";
    private List<Metadata> metadataTypes = null;

    public CollectionSessionBean()
    {
        try
        {
            init();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initializing collection session:", e);
        }
    }

    public void init() throws Exception
    {
        active = new CollectionImeji();
        profile = new MetadataProfile();
        metadataTypes = new ArrayList<Metadata>();
        for (Metadata.Types t : Metadata.Types.values())
        {
            metadataTypes.add(MetadataFactory.createMetadata(t));
        }
    }
    
    /**
     * @return the active
     */
    public CollectionImeji getActive()
    {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(CollectionImeji active)
    {
        this.active = active;
    }

    /**
     * @return the selectedMenu
     */
    public String getSelectedMenu()
    {
        return selectedMenu;
    }

    /**
     * @param selectedMenu the selectedMenu to set
     */
    public void setSelectedMenu(String selectedMenu)
    {
        this.selectedMenu = selectedMenu;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter(String filter)
    {
        this.filter = filter;
    }

    public void setMetadataTypes(List<Metadata> metadataTypes)
    {
        this.metadataTypes = metadataTypes;
    }

    public List<Metadata> getMetadataTypes()
    {
        return metadataTypes;
    }

    public MetadataProfile getProfile()
    {
        return profile;
    }

    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }
    
}
