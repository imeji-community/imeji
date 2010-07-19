package de.mpg.escidoc.faces.metadata.schema;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.schema.SchemaElement.SchemaElementType;
import de.mpg.escidoc.faces.metadata.schema.SchemaMarkup.SchemaMarkupType;

public class SimpleSchema
{
    private List<SchemaElement> elements = null;
    private String name = null;
    private String namespace = null;
    
    private SchemaMarkup rootMarkup = null;
    private SchemaElement firstElement = null;
    
    public SimpleSchema(String name, String namespace, List<Metadata> metadataList)
    {
	this.name = name;
	this.namespace = namespace;
	this.elements = new ArrayList<SchemaElement>();
	
	List<XmlAttribute> rootAttributes = new ArrayList<XmlAttribute>();
	rootAttributes.add(new XmlAttribute(SchemaMarkup.PREFIX, SchemaMarkup.NAMESPACE, "xmlns"));
	
	if (elements != null)
	{
	    int i = 0;
	    for (Metadata m : metadataList)
	    {
		elements.add(new SchemaElement(m, "ns" + i));
		rootAttributes.add(new XmlAttribute("ns" + i, m.getNamespace(), "xmlns"));
		i++;
	    }
	}
	
	rootMarkup = new SchemaMarkup(SchemaMarkupType.ELEMENT, rootAttributes);
	
	Metadata firstElementMetadata = new Metadata(name, name, "http://faces.mpdl.mpg.de/escidoc:123");
	firstElementMetadata.setMaxOccurs(1);
	firstElementMetadata.setMinOccurs(1);
	firstElement = new SchemaElement(firstElementMetadata, null);
	
	firstElement.setChilds(elements);
    }

    /**
     * @return the xsd
     */
    public String getXsd()
    {
	String xsd = rootMarkup.asStartTag();
	
	firstElement.getImportElement();
	
	for (SchemaElement se : elements)
	{
	    xsd += se.getImportElement();
	}
	
	xsd += firstElement.getElementWithComplexType();
	
	xsd += firstElement.getComplexTypeElement();

	return xsd + rootMarkup.asEndTag();
    }

    
    public void addElement(SchemaElement e)
    {
	
    }
    
    public void removeElement(SchemaElement e)
    {
	
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    
    
}
