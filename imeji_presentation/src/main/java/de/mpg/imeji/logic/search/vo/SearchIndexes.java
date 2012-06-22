package de.mpg.imeji.logic.search.vo;

import java.util.ArrayList;
import java.util.List;

public enum SearchIndexes
{
    ID_URI("http://imeji.org/terms/id"), MY_IMAGES("http://imeji.org/terms/"), PROPERTIES(
            "http://imeji.org/terms/properties"), PROPERTIES_CREATED_BY("http://imeji.org/terms/createdBy", PROPERTIES), PROPERTIES_CREATED_BY_USER_GRANT(
            "http://xmlns.com/foaf/0.1/grants", PROPERTIES_CREATED_BY), PROPERTIES_CREATED_BY_USER_GRANT_TYPE(
            "http://imeji.org/terms/grantType", PROPERTIES_CREATED_BY_USER_GRANT), PROPERTIES_CREATED_BY_USER_GRANT_FOR(
            "http://imeji.org/terms/grantFor", PROPERTIES_CREATED_BY_USER_GRANT), PROPERTIES_MODIFIED_BY(
            "http://imeji.org/terms/modifiedBy", PROPERTIES), PROPERTIES_CREATION_DATE(
            "http://purl.org/dc/terms/created", PROPERTIES), PROPERTIES_LAST_MODIFICATION_DATE(
            "http://purl.org/dc/terms/modified", PROPERTIES), PROPERTIES_STATUS("http://imeji.org/terms/status",
            PROPERTIES), IMAGE_FILENAME("http://imeji.org/terms/filename"), IMAGE_VISIBILITY(
            "http://imeji.org/terms/visibility"), IMAGE_METADATA_SET("http://imeji.org/terms/metadataSet"), IMAGE_METADATA(
            "http://imeji.org/terms/metadata", IMAGE_METADATA_SET), IMAGE_METADATA_STATEMENT(
            "http://imeji.org/terms/statement", IMAGE_METADATA), IMAGE_METADATA_SEARCH(
            "http://imeji.org/terms/searchValue", IMAGE_METADATA), IMAGE_METADATA_NAME(
            "http://imeji.org/terms/metadata/name", IMAGE_METADATA), IMAGE_METADATA_TYPE_RDF(
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", IMAGE_METADATA), IMAGE_METADATA_TYPE(
            "http://imeji.org/terms/complexTypes", IMAGE_METADATA), IMAGE_METADATA_TYPE_URI(
            "http://imeji.org/terms/complexTypes/URI", IMAGE_METADATA_TYPE), IMAGE_METADATA_PERSON(
            "http://imeji.org/terms/metadata/person", IMAGE_METADATA), IMAGE_METADATA_PERSON_FAMILY_NAME(
            "http://purl.org/escidoc/metadata/terms/0.1/family-name", IMAGE_METADATA_PERSON), IMAGE_METADATA_PERSON_GIVEN_NAME(
            "http://purl.org/escidoc/metadata/terms/0.1/given-name", IMAGE_METADATA_PERSON), IMAGE_METADATA_PERSON_ORGANIZATION(
            "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", IMAGE_METADATA_PERSON, true), IMAGE_METADATA_PERSON_ORGANIZATION_NAME(
            "http://purl.org/dc/elements/1.1/title", IMAGE_METADATA_PERSON_ORGANIZATION), IMAGE_METADATA_GEOLOCATION_LONGITUDE(
            "http://imeji.org/terms/metadata/longitude", IMAGE_METADATA), IMAGE_METADATA_GEOLOCATION_LATITUDE(
            "http://imeji.org/terms/metadata/latitude", IMAGE_METADATA), IMAGE_METADATA_TEXT(
            "http://imeji.org/terms/metadata/text", IMAGE_METADATA), IMAGE_METADATA_DATE(
            "http://imeji.org/terms/metadata/dateTime", IMAGE_METADATA), IMAGE_METADATA_NUMBER(
            "http://imeji.org/terms/metadata/number", IMAGE_METADATA), IMAGE_COLLECTION(
            "http://imeji.org/terms/collection"), IMAGE_COLLECTION_PROFILE("http://imeji.org/terms/mdprofile",
            IMAGE_COLLECTION), CONTAINER_METADATA("http://imeji.org/terms/container/metadata"), CONTAINER_METADATA_TITLE(
            "http://purl.org/dc/elements/1.1/title", CONTAINER_METADATA), CONTAINER_METADATA_DESCRIPTION(
            "http://purl.org/dc/elements/1.1/description", CONTAINER_METADATA), CONTAINER_METADATA_PERSON(
            "http://purl.org/escidoc/metadata/terms/0.1/creator", CONTAINER_METADATA, true), CONTAINER_METADATA_PERSON_FAMILY_NAME(
            "http://purl.org/escidoc/metadata/terms/0.1/family-name", CONTAINER_METADATA_PERSON), CONTAINER_METADATA_PERSON_COMPLETE_NAME(
            "http://purl.org/escidoc/metadata/terms/0.1/complete-name", CONTAINER_METADATA_PERSON), CONTAINER_METADATA_PERSON_GIVEN_NAME(
            "http://purl.org/escidoc/metadata/terms/0.1/given-name", CONTAINER_METADATA_PERSON), CONTAINER_METADATA_PERSON_ORGANIZATION(
            "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", CONTAINER_METADATA_PERSON, true), CONTAINER_METADATA_PERSON_ORGANIZATION_NAME(
            "http://purl.org/dc/elements/1.1/title", CONTAINER_METADATA_PERSON_ORGANIZATION), COLLECTION_PROFILE(
            "http://imeji.org/terms/mdprofile");
    private String ns;
    private SearchIndexes parent;
    private List<SearchIndexes> children = new ArrayList<SearchIndexes>();
    private boolean listType = false;;

    private SearchIndexes(String ns)
    {
        this.ns = ns;
    }

    private SearchIndexes(String ns, SearchIndexes parent)
    {
        this.ns = ns;
        this.parent = parent;
        if (parent != null && !parent.getChildren().contains(this))
        {
            parent.getChildren().add(this);
        }
    }

    private SearchIndexes(String ns, SearchIndexes parent, boolean listType)
    {
        this.ns = ns;
        this.parent = parent;
        this.setListType(listType);
        if (parent != null && !parent.getChildren().contains(this))
        {
            parent.getChildren().add(this);
        }
    }

    public void setNs(String ns)
    {
        this.ns = ns;
    }

    public String getNs()
    {
        return ns;
    }

    public SearchIndexes getParent()
    {
        return parent;
    }

    public void setParent(SearchIndexes parent)
    {
        this.parent = parent;
    }

    public void setChildren(List<SearchIndexes> children)
    {
        this.children = children;
    }

    public List<SearchIndexes> getChildren()
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
}