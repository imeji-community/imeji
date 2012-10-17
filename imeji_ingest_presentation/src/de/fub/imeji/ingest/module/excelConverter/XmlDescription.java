/**
 * 
 */
package de.fub.imeji.ingest.module.excelConverter;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

import de.fub.imeji.ingest.core.beans.metadata.terms.DCTerms;
import de.fub.imeji.ingest.core.beans.metadata.terms.IMEJITerms;
import de.fub.imeji.ingest.core.beans.metadata.terms.RDFTerms;
import de.fub.imeji.ingest.core.zuse.metadata.terms.ZuseDCTerms;

/**
 * @author hnguyen
 *
 */
public class XmlDescription extends Element{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7796620969516127077L;
	
	public static String descriptionTag = "Description";
	private static String label = "label";
	private String parentName = "";
	
	public XmlDescription() {
		this(XmlDescription.descriptionTag,RDFTerms.RDF_NAMESPACE);
	}
	
	public XmlDescription(List<Attribute> attributes) {
		this();
		this.setAttributes(attributes);
	}
	
	public XmlDescription(String descriptionTag, Namespace ns) {
		super(descriptionTag, ns);
	}
	
	public void setType(String typeNsUri) {
		Element type = new Element("type",DCTerms.DCTERMS_NAMESPACE);
						
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute attribute = new Attribute("resource", typeNsUri, RDFTerms.RDF_NAMESPACE);
		
		attributes.add(attribute);
		
		type.setAttributes(attributes);
		
		this.addContent(type);
		
	}
	
	/**
	 * 
	 * @param languageTag
	 * @param value
	 */
	public void addLabel(String lang, String value) {
		
		Element label = new Element("label",RDFTerms.RDFS_NAMESPACE);
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute attribute = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
		attributes.add(attribute);

		label.setAttributes(attributes);
		label.setText(value);
		
		this.addContent(label);
	}
	
	/**
	 * @param originLabelName the originLabelName to set
	 */
	public void setOriginLabelName(String originLabelName, Namespace ns) {
		List<Element> children = this.getChildren();
		for (Element element : children) {
			if(element.getName().equalsIgnoreCase(XmlDescription.label) && !element.hasAttributes()) {				
				element.setText(originLabelName);
				element.setNamespace(ns);
				return;
			}
		}
		
		Element label = new Element("label",ns);
		label.setText(originLabelName);		
		
		this.addContent(label);
	}

	/**
	 * @return the originLabelName
	 */
	public String getOriginLabelName() {
		List<Element> children = this.getChildren();
		for (Element element : children) {
			if(element.getName().equalsIgnoreCase(XmlDescription.label)) {
				String e = element.getName(); 
				String n = element.getText();
				return element.getText();				
			}
		}
		
		return null;
	}

	/**
	 * 
	 * @param isDescriptionTag
	 * @param value
	 */
	public void setDescription(boolean value, Namespace ns) {
		Element isDescription = new Element("isDescription",IMEJITerms.IMEJI_NAMESPACE);
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute attribute = new Attribute("datatype", ns.getURI(), RDFTerms.RDF_NAMESPACE);
		attributes.add(attribute);
		isDescription.setAttributes(attributes);
		
		isDescription.setText(new Boolean(value).toString());
		
		this.addContent(isDescription);
		
	}

	/**
	 * 
	 * @param minOccursTag
	 * @param value
	 */
	public void setMinOccurs(String value, Namespace ns) {
		Element minOccurs = new Element("minOccurs",IMEJITerms.IMEJI_NAMESPACE);
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute attribute = new Attribute("datatype", ns.getURI(), RDFTerms.RDF_NAMESPACE);
		attributes.add(attribute);		
		minOccurs.setAttributes(attributes);
		
		minOccurs.setText(value);
		
		this.addContent(minOccurs);
		
	}

	/**
	 * 
	 * @param maxOcccursTag
	 * @param value
	 */
	public void setMaxOccurs(String value, Namespace ns) {
		Element maxOccurs = new Element("maxOccurs",IMEJITerms.IMEJI_NAMESPACE);
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute attribute = new Attribute("datatype", ns.getURI(), RDFTerms.RDF_NAMESPACE);
		attributes.add(attribute);
		maxOccurs.setAttributes(attributes);
		
		maxOccurs.setText(value);
		this.addContent(maxOccurs);
		
	}

	/**
	 * 
	 * @param parentTag
	 * @param xstm
	 */
	public void setParentNode(XmlStatement xstm) {
		
		Element parent = new Element("hasParent",IMEJITerms.IMEJI_NAMESPACE);		
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute attribute = new Attribute("resource", xstm.getNamespaceID(), RDFTerms.RDF_NAMESPACE);
		attributes.add(attribute);
		parent.setAttributes(attributes);
		
		this.addContent(parent);
	}

	/**
	 * @param parentName the parentName to set
	 */
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	/**
	 * @return the parentName
	 */
	public String getParentName() {
		return parentName;
	}
	
	
}
