package de.mpg.imeji.logic.search.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Element of a {@link SearchPair}, defines the index of the searched elements
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchIndex
{
    /**
     * All indexes names, searchable in imeji
     * 
     * @author saquet (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    public static enum names
    {
        ID_URI, MY_IMAGES, PROPERTIES, PROPERTIES_CREATED_BY, PROPERTIES_MODIFIED_BY, PROPERTIES_CREATION_DATE, PROPERTIES_LAST_MODIFICATION_DATE, PROPERTIES_STATUS, PROPERTIES_CREATED_BY_USER_GRANT, PROPERTIES_CREATED_BY_USER_GRANT_TYPE, PROPERTIES_CREATED_BY_USER_GRANT_FOR, IMAGE_FILENAME, IMAGE_VISIBILITY, IMAGE_METADATA_SET, IMAGE_COLLECTION, IMAGE_COLLECTION_PROFILE, IMAGE_METADATA_TYPE_RDF, CONTAINER_METADATA, CONTAINER_METADATA_TITLE, CONTAINER_METADATA_DESCRIPTION, CONTAINER_METADATA_PERSON, CONTAINER_METADATA_PERSON_FAMILY_NAME, CONTAINER_METADATA_PERSON_GIVEN_NAME, CONTAINER_METADATA_PERSON_COMPLETE_NAME, CONTAINER_METADATA_PERSON_ORGANIZATION, CONTAINER_METADATA_PERSON_ORGANIZATION_NAME, COLLECTION_PROFILE, IMAGE_METADATA, IMAGE_METADATA_STATEMENT, FULLTEXT, IMAGE_METADATA_TEXT, IMAGE_METADATA_NUMBER, IMAGE_METADATA_DATE, IMAGE_METADATA_TIME, IMAGE_METADATA_TITLE, IMAGE_METADATA_LONGITUDE, IMAGE_METADATA_LATITUTE, IMAGE_METADATA_LICENSE, IMAGE_METADATA_URI, IMAGE_METADATA_LABEL, IMAGE_METADATA_CITATION, IMAGE_METADATA_CITATIONSTYLE, IMAGE_METADATA_CONEID, IMAGE_METADATA_PERSON, IMAGE_METADATA_PERSON_FAMLILYNAME, IMAGE_METADATA_PERSON_GIVENNAME, IMAGE_METADATA_PERSON_IDENTIFIER, IMAGE_METADATA_PERSON_ROLE, IMAGE_METADATA_PERSON_ORGANIZATION, IMAGE_METADATA_PERSON_ORGANIZATION_TITLE, IMAGE_METADATA_PERSON_ORGANIZATION_IDENTIFIER, IMAGE_METADATA_PERSON_ORGANIZATION_DESCRIPTION, IMAGE_METADATA_PERSON_ORGANIZATION_CITY, IMAGE_METADATA_PERSON_ORGANIZATION_COUNTRY;
    }

    private String name;
    private String namespace;
    private SearchIndex parent;
    private List<SearchIndex> children = new ArrayList<SearchIndex>();
    private boolean listType = false;

    public SearchIndex(String name, String namespace)
    {
        this.name = name;
        this.namespace = namespace;
    }

    public SearchIndex(String name, String namespace, SearchIndex parent)
    {
        this(name, namespace);
        this.parent = parent;
        if (parent != null && !parent.getChildren().contains(this))
        {
            parent.getChildren().add(this);
        }
    }

    public SearchIndex(String namespace, SearchIndex parent, boolean listType)
    {
        this.setNamespace(namespace);
        this.parent = parent;
        this.setListType(listType);
        if (parent != null && !parent.getChildren().contains(this))
        {
            parent.getChildren().add(this);
        }
    }

    public boolean hasParent()
    {
        return getParent() != null;
    }

    public SearchIndex getParent()
    {
        return parent;
    }

    public void setParent(SearchIndex parent)
    {
        this.parent = parent;
    }

    public void setChildren(List<SearchIndex> children)
    {
        this.children = children;
    }

    public List<SearchIndex> getChildren()
    {
        return children;
    }

    public void setListType(boolean listType)
    {
        this.listType = listType;
    }

    public boolean isListType()
    {
        return listType;
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
