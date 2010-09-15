package de.mpg.jena.controller;

import java.util.ArrayList;
import java.util.List;

public class SearchCriterion {

	public enum Operator
	{
		AND,OR
	}


	public enum Filtertype
	{
	    REGEX, URI, EQUALS;
	}

   
	
	public enum ImejiNamespaces {

	    ID_URI(""),

		PROPERTIES("http://imeji.mpdl.mpg.de/properties"),
		
		PROPERTIES_CREATED_BY("http://imeji.mpdl.mpg.de/createdBy", PROPERTIES),
		PROPERTIES_CREATED_BY_USER_GRANT("http://xmlns.com/foaf/0.1/grants", PROPERTIES_CREATED_BY),
		PROPERTIES_CREATED_BY_USER_GRANT_TYPE("http://imeji.mpdl.mpg.de/grantType", PROPERTIES_CREATED_BY_USER_GRANT),
		PROPERTIES_CREATED_BY_USER_GRANT_FOR("http://imeji.mpdl.mpg.de/grantFor", PROPERTIES_CREATED_BY_USER_GRANT),
		
		PROPERTIES_MODIFIED_BY("http://imeji.mpdl.mpg.de/modifiedBy", PROPERTIES),
		PROPERTIES_CREATION_DATE("http://imeji.mpdl.mpg.de/creationDate", PROPERTIES),
		PROPERTIES_LAST_MODIFICATION_DATE("http://imeji.mpdl.mpg.de/lastModificationDate", PROPERTIES),
		PROPERTIES_STATUS("http://imeji.mpdl.mpg.de/status", PROPERTIES),
		
		IMAGE_METADATA("http://imeji.mpdl.mpg.de/image/metadata"),
		IMAGE_METADATA_NAMESPACE("http://imeji.mpdl.mpg.de/image/metadata/elementNamespace", IMAGE_METADATA),
		IMAGE_METADATA_NAME("http://imeji.mpdl.mpg.de/image/metadata/name", IMAGE_METADATA),
		IMAGE_METADATA_COMPLEXTYPE("http://purl.org/dc/terms/type", IMAGE_METADATA),
		IMAGE_METADATA_COMPLEXTYPE_ENUMTYPE("http://purl.org/dc/terms/type", IMAGE_METADATA),
		IMAGE_METADATA_COMPLEXTYPE_PERSON("http://imeji.mpdl.mpg.de/metadata/person", IMAGE_METADATA_COMPLEXTYPE),
		IMAGE_METADATA_COMPLEXTYPE_PERSON_FAMILY_NAME("http://purl.org/escidoc/metadata/terms/0.1/family-name", IMAGE_METADATA_COMPLEXTYPE_PERSON),
		IMAGE_METADATA_COMPLEXTYPE_PERSON_GIVEN_NAME("http://purl.org/escidoc/metadata/terms/0.1/given-name", IMAGE_METADATA_COMPLEXTYPE_PERSON),
		IMAGE_METADATA_COMPLEXTYPE_PERSON_ORGANIZATION("http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", IMAGE_METADATA_COMPLEXTYPE_PERSON),
		IMAGE_METADATA_COMPLEXTYPE_PERSON_ORGANIZATION_NAME("http://purl.org/dc/elements/1.1/title", IMAGE_METADATA_COMPLEXTYPE_PERSON_ORGANIZATION),
		
		IMAGE_METADATA_COMPLEXTYPE_TEXT("http://imeji.mpdl.mpg.de/metadata/text", IMAGE_METADATA_COMPLEXTYPE),
		IMAGE_METADATA_COMPLEXTYPE_DATE("http://imeji.mpdl.mpg.de/metadata/date", IMAGE_METADATA_COMPLEXTYPE),
		IMAGE_METADATA_COMPLEXTYPE_NUMBER("http://imeji.mpdl.mpg.de/metadata/double", IMAGE_METADATA_COMPLEXTYPE),
	    
		IMAGE_COLLECTION("http://imeji.mpdl.mpg.de/collection"),

		CONTAINER_METADATA("http://imeji.mpdl.mpg.de/container/metadata"),
		CONTAINER_METADATA_TITLE("http://purl.org/dc/elements/1.1/title", CONTAINER_METADATA),
		CONTAINER_METADATA_DESCRIPTION("http://purl.org/dc/elements/1.1/description", CONTAINER_METADATA),
		CONTAINER_METADATA_PERSON("http://purl.org/escidoc/metadata/terms/0.1/creator", CONTAINER_METADATA),
		CONTAINER_METADATA_PERSON_FAMILY_NAME("http://purl.org/escidoc/metadata/terms/0.1/family-name", CONTAINER_METADATA_PERSON),
		CONTAINER_METADATA_PERSON_GIVEN_NAME("http://purl.org/escidoc/metadata/terms/0.1/given-name", CONTAINER_METADATA_PERSON),
		CONTAINER_METADATA_PERSON_ORGANIZATION("http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", CONTAINER_METADATA_PERSON),
		CONTAINER_METADATA_PERSON_ORGANIZATION_NAME("http://purl.org/dc/elements/1.1/title", CONTAINER_METADATA_PERSON_ORGANIZATION);
		

		private String ns;
		private ImejiNamespaces parent;
		private List<ImejiNamespaces> children = new ArrayList<ImejiNamespaces>();
		
		private ImejiNamespaces(String ns) {
			this.ns = ns;
		}
		
		private ImejiNamespaces(String ns, ImejiNamespaces parent) {
			this.ns = ns;
			this.parent = parent;
			if(parent!=null && !parent.getChildren().contains(this))
			{
			    parent.getChildren().add(this);
			}
		}
		
		

		public void setNs(String ns) {
			this.ns = ns;
		}

		public String getNs() {
			return ns;
		}

		public ImejiNamespaces getParent() {
			return parent;
		}

		public void setParent(ImejiNamespaces parent) {
			this.parent = parent;
		}

        public void setChildren(List<ImejiNamespaces> children)
        {
            this.children = children;
        }

        public List<ImejiNamespaces> getChildren()
        {
            return children;
        }
		
		

       
	}
	
	private ImejiNamespaces namespace;
	private String value;
	private Operator operator = Operator.AND;	
	private Filtertype filterType = Filtertype.REGEX;
	private List<SearchCriterion> children = new ArrayList<SearchCriterion>();
	
	
	public SearchCriterion(ImejiNamespaces namespace, String value)
	{
		this.namespace = namespace;
		this.value = value;
		
	}
	
	public SearchCriterion(Operator op, ImejiNamespaces namespace, String value, Filtertype filterType)
	{
		this.namespace = namespace;
		this.value = value;
		this.operator = op;
		this.filterType = filterType;
		
	}
	
	public SearchCriterion(Operator op,  List<SearchCriterion> children)
	{
        this.operator = op;
	    this.children = children;
        
    }

	public ImejiNamespaces getNamespace() {
		return namespace;
	}

	public void setNamespace(ImejiNamespaces namespace) {
		this.namespace = namespace;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

  

    public void setFilterType(Filtertype filterType)
    {
        this.filterType = filterType;
    }

    public Filtertype getFilterType()
    {
        return filterType;
    }

    public void setChildren(List<SearchCriterion> children)
    {
        this.children = children;
    }

    public List<SearchCriterion> getChildren()
    {
        return children;
    }
    
    
}
