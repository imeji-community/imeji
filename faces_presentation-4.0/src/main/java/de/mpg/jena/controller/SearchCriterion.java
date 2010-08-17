package de.mpg.jena.controller;

public class SearchCriterion {

	enum Operator
	{
		AND,OR
	}

	enum ImejiNamespaces {
		
		PROPERTIES("http://imeji.mpdl.mpg.de/properties"),
		
		PROPERTIES_CREATED_BY("http://imeji.mpdl.mpg.de/createdBy", PROPERTIES),
		PROPERTIES_MODIFIED_BY("http://imeji.mpdl.mpg.de/modifiedBy", PROPERTIES),
		PROPERTIES_CREATION_DATE("http://imeji.mpdl.mpg.de/creationDate", PROPERTIES),
		PROPERTIES_LAST_MODIFICATION_DATE("http://imeji.mpdl.mpg.de/lastModificationDate", PROPERTIES),
		
		IMAGE_METADATA("http://imeji.mpdl.mpg.de/image/metadata"),
		IMAGE_METADATA_NAMESPACE("http://imeji.mpdl.mpg.de/image/elementNamespace", IMAGE_METADATA),
		IMAGE_METADATA_NAME("http://imeji.mpdl.mpg.de/image/name", IMAGE_METADATA),
		IMAGE_METADATA_VALUE("http://imeji.mpdl.mpg.de/image/value", IMAGE_METADATA),
		
		
		CONTAINER_METADATA("http://imeji.mpdl.mpg.de/container/metadata"),
		CONTAINER_METADATA_TITLE("http://purl.org/dc/elements/1.1/title", CONTAINER_METADATA),
		CONTAINER_METADATA_DESCRIPTION("http://purl.org/dc/elements/1.1/description", CONTAINER_METADATA),
		CONTAINER_METADATA_PERSON("http://purl.org/escidoc/metadata/profiles/0.1/person", CONTAINER_METADATA),
		CONTAINER_METADATA_PERSON_FAMILY_NAME("http://purl.org/escidoc/metadata/terms/0.1/family-name", CONTAINER_METADATA_PERSON),
		CONTAINER_METADATA_PERSON_GIVEN_NAME("http://purl.org/escidoc/metadata/terms/0.1/given-name", CONTAINER_METADATA_PERSON),
		CONTAINER_METADATA_PERSON_ORGANIZATION("http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", CONTAINER_METADATA_PERSON),
		CONTAINER_METADATA_PERSON_ORGANIZATION_NAME("http://purl.org/dc/elements/1.1/title", CONTAINER_METADATA_PERSON_ORGANIZATION);
		

		private String ns;
		private ImejiNamespaces parent;
		
		private ImejiNamespaces(String ns) {
			this.ns = ns;
		}
		
		private ImejiNamespaces(String ns, ImejiNamespaces parent) {
			this.ns = ns;
			this.parent = parent;
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
	}
	
	private ImejiNamespaces namespace;
	private String value;
	private Operator operator = Operator.AND;
			
	
	
	
	public SearchCriterion(ImejiNamespaces namespace, String value)
	{
		this.namespace = namespace;
		this.value = value;
		
	}
	
	public SearchCriterion(Operator op, ImejiNamespaces namespace, String value)
	{
		this.namespace = namespace;
		this.value = value;
		this.operator = op;
		
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
}
