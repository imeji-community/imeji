package de.mpg.imeji.image;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class SelectedBean extends ImagesBean
{
    private int totalNumberOfRecords;
    private SessionBean sb;
    private List<SelectItem> metadata;
    private Collection<Image> images;
    
    public SelectedBean()
    {
       super();
       this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    @Override
    public String getNavigationString()
    {
        return "pretty:selected";
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
        images = new ArrayList<Image>();
        
        List<SearchCriterion> uris = new ArrayList<SearchCriterion>();
        for (URI uri : sb.getSelected())
        {
            uris.add(new SearchCriterion(SearchCriterion.Operator.OR, ImejiNamespaces.ID_URI, uri.toString(), Filtertype.URI));
        }
        try
        {
            totalNumberOfRecords = controller.search(uris, null, -1, offset).size();
            images = controller.search(uris, null, limit, offset);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }        

        initializeMetadata();
        return ImejiFactory.imageListToBeanList(images);
    }
    

    public void initializeMetadata()
    {
        metadata = new ArrayList<SelectItem>();
        CollectionController c = new CollectionController(sb.getUser());
        Map<URI, MetadataProfile> profiles = new HashMap<URI, MetadataProfile>();
        for (Image im : images)
        {
            CollectionImeji coll = c.retrieve(im.getCollection());
            if (profiles.get(coll.getProfile().getId()) == null)
            {
                profiles.put(coll.getProfile().getId(), coll.getProfile());
            }
        }
        for (MetadataProfile mdp : profiles.values())
        {
            for (Statement s : mdp.getStatements())
            {
                metadata.add(new SelectItem(s.getType(), ObjectHelper.getAllowedType(s.getType()).getLabel()));
            }
        }
    }
    
    public List<SelectItem> getMetadata()
    {
        for(SelectItem si : metadata)
        {
            System.out.println(si.getLabel() + " : " + si.getValue());
        }
        return metadata;
    }

    public void setMetadata(List<SelectItem> metadata)
    {
        this.metadata = metadata;
    }

}
