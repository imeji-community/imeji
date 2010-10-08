package example;

import java.util.Collection;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.Uri;

@Namespace("http://semanticbible.org/ns/2006/NTNames#")
public class Human {
	private String name;
	private String description;
	private String uri;
	private Collection<Human> children;
	
	
	@RdfProperty(NTNames.parentOf_PROPERTY)
	public Collection<Human> getChildren() {
		return children;
	}

	public void setChildren(Collection<Human> children) {
		this.children = children;
	}

	public Human(String uri) {
		this.uri = uri;
	}
	
	@Uri
	public String uri() {
		return uri;
	}
	
	@RdfProperty(NTNames.name_en_PROPERTY)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@RdfProperty(NTNames.description_PROPERTY)
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (obj instanceof Human)
			return equals((Human)obj);
		else
			return false;
	}
	
	public boolean equals(Human h) {
		return uri.equals(h.uri);
	}
	
	
	
}
