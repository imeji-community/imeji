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
        item, user, prop, creator, editor, created, modified, status, grant, grant_type, grant_for, filename, visibility, mds, col, prof, type, cont_md, cont_title, cont_description, cont_person, cont_person_family, cont_person_given, cont_person_name, cont_person_org, cont_person_org_name, profile, md, statement, all, text, number, date, time, title, longitude, latitude, license, link, label, citation, citation_style, cone, person, person_family, person_given, person_id, person_role, person_org, person_org_title, person_org_id, person_org_description, person_org_city, person_org_country;
    }

    private String name;
    private String namespace;
    private SearchIndex parent;
    private List<SearchIndex> children = new ArrayList<SearchIndex>();
    private boolean listType = false;

    /**
     * Construct a new {@link SearchIndex} with a name and a namespace
     * 
     * @param name
     * @param namespace
     */
    public SearchIndex(String name, String namespace)
    {
        this.name = name;
        this.namespace = namespace;
    }

    /**
     * Construct a new {@link SearchIndex} with a name and a namespace and parent {@link SearchIndex}
     * 
     * @param name
     * @param namespace
     * @param parent
     */
    public SearchIndex(String name, String namespace, SearchIndex parent)
    {
        this(name, namespace);
        this.parent = parent;
        if (parent != null && !parent.getChildren().contains(this))
        {
            parent.getChildren().add(this);
        }
    }

    /**
     * Construct a new {@link SearchIndex} for a list element with a namespace and parent {@link SearchIndex}
     * 
     * @param namespace
     * @param parent
     * @param listType
     */
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
