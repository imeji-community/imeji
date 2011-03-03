package de.mpg.jena.sparql.query;

import java.util.ArrayList;
import java.util.List;

public class QueryElement 
{
	private String name;
	private String nameSpace;
	private String value;
	private boolean optional = true;
	private QueryElement parent;
	private List<QueryElement>  childs = null;
	
	public QueryElement(String name, String nameSpace, QueryElement parent, boolean optional)
	{
		this.name = name;
		this.nameSpace = nameSpace;
		this.parent = parent;
		this.optional = optional;
		this.childs = new ArrayList<QueryElement>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameSpace() {
		return nameSpace;
	}
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public QueryElement getParent() {
		return parent;
	}
	public void setParent(QueryElement parent) {
		this.parent = parent;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public List<QueryElement> getChilds() {
		return childs;
	}

	public void setChilds(List<QueryElement> childs) {
		this.childs = childs;
	}

	
	
}
