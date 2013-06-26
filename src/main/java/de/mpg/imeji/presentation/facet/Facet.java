/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.facet;

import java.net.URI;

import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * The Facet used by the Faceted search in the browse item page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Facet
{
    private URI uri;
    private String label;
    private int count;
    private FacetType type;
    private URI metadataURI;
    private String internationalizedLabel;

    /**
     * The type of the {@link Facet}. Depends on which page is displayed the {@link Facet}
     * 
     * @author saquet (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    public enum FacetType
    {
        TECHNICAL, COLLECTION, SEARCH;
    }

    /**
     * Constructor for a {@link Facet}
     * 
     * @param uri
     * @param label
     * @param count
     * @param type
     * @param metadataURI
     */
    public Facet(URI uri, String label, int count, FacetType type, URI metadataURI)
    {
        this.count = count;
        this.label = label;
        this.uri = uri;
        this.type = type;
        this.metadataURI = metadataURI;
    }

    /**
     * Initialized the internationalized label according to the {@link MetadataProfile}
     */
    private void initInternationalLabel()
    {
        if (FacetType.TECHNICAL.name().equals(type.name()))
        {
            internationalizedLabel = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("facet_"
                    + label.toLowerCase());
        }
        else if (FacetType.COLLECTION.name().equals(type.name()))
        {
            internationalizedLabel = ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class))
                    .getInternationalizedLabels().get(metadataURI);
            if (isNotDefine())
            {
                internationalizedLabel = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                        .getLabel("facet_not_define");
            }
        }
        else if (FacetType.SEARCH.name().equals(type.name()))
        {
            internationalizedLabel = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("search");
        }
        if (internationalizedLabel == null
                || (label != null && internationalizedLabel.equals("facet_" + label.toLowerCase())))
        {
            internationalizedLabel = label;
        }
    }

    /**
     * Getter
     * 
     * @return
     */
    public URI getUri()
    {
        return uri;
    }

    /**
     * Setter
     * 
     * @param uri
     */
    public void setUri(URI uri)
    {
        this.uri = uri;
    }

    /**
     * Getter
     * 
     * @return
     */
    public String getinternationalizedLabel()
    {
        initInternationalLabel();
        return internationalizedLabel;
    }

    /**
     * @return
     */
    public String getNotDefineType()
    {
        return ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class)).getInternationalizedLabels().get(
                metadataURI);
    }
    /**
     * True if the current {@link Facet} is a negation facet
     * 
     * @return
     */
    public boolean isNotDefine()
    {
        if (label == null)
            return false;
        return (label.toLowerCase().startsWith("no "));
    }

    /**
     * Getter
     * 
     * @return
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Setter
     * 
     * @param label
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Getter
     * 
     * @return
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Setter
     * 
     * @param count
     */
    public void setCount(int count)
    {
        this.count = count;
    }

    /**
     * Getter
     * 
     * @return
     */
    public FacetType getType()
    {
        return type;
    }

    /**
     * Setter
     * 
     * @param type
     */
    public void setType(FacetType type)
    {
        this.type = type;
    }

    /**
     * Getter
     * 
     * @return
     */
    public URI getMetadataURI()
    {
        return metadataURI;
    }

    /**
     * Setter
     * 
     * @param metadataURI
     */
    public void setMetadataURI(URI metadataURI)
    {
        this.metadataURI = metadataURI;
    }
}
