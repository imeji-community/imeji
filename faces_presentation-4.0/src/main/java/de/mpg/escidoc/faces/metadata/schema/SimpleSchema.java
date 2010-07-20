package de.mpg.escidoc.faces.metadata.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.schema.SchemaElement.SchemaElementType;
import de.mpg.escidoc.faces.metadata.schema.SchemaMarkup.SchemaMarkupType;

public class SimpleSchema
{
    private List<SchemaElement> elements = null;
    private String name = null;
    private String namespace= null;
    private Map<String, String> namespaces = new HashMap<String, String>();
    
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
		if (!namespaces.containsKey(m.getNamespace()))
		{
		    elements.add(new SchemaElement(m, "ns" + i));
		    rootAttributes.add(new XmlAttribute("ns" + i, m.getNamespace(), "xmlns"));
		    namespaces.put(m.getNamespace(), "ns" + i);
		    i++;
		}
		else
		{
		    elements.add(new SchemaElement(m, namespaces.get(m.getNamespace())));
		}
	    }
	}
	
	rootMarkup = new SchemaMarkup(SchemaMarkupType.SCHEMA, rootAttributes);
	
	Metadata firstElementMetadata = new Metadata(name, name, "http://faces.mpdl.mpg.de/escidoc:123");
	firstElement = new SchemaElement(firstElementMetadata, null);
	
	firstElement.setChilds(elements);
    }

    /**
     * @return the xsd
     */
    public String getXsd()
    {
	String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	xsd += rootMarkup.asStartTag();
	
	firstElement.getImportElement();
	
	List<String> importElements = new ArrayList<String>();
	for (SchemaElement se : elements)
	{
	    if (!importElements.contains(se.getImportElement()))
	    {
		 xsd += se.getImportElement();
		 importElements.add(se.getImportElement());
	    }
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
