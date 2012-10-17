/**
 * 
 */
package de.fub.imeji.ingest.module.excelConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes.Name;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

import de.fub.imeji.ingest.core.beans.metadata.terms.DCTerms;
import de.fub.imeji.ingest.core.beans.metadata.terms.RDFTerms;

/**
 * @author hnguyen
 *
 */
public class XmlProfile extends Element{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5295086376141158407L;
	
	private static String profileTag = "profile";
	private static Namespace ns = RDFTerms.RDF_NAMESPACE;
	
	public XmlProfile() {
		this(profileTag, ns);
	}
	/**
	 * 
	 * @param string
	 * @param namespace
	 */
	public XmlProfile(String string, Namespace namespace) {
		super(string,namespace);
	}
	
	/**
	 * 
	 * @param descriptionText
	 */
	public void setDescription(String descriptionText) {
		List<Namespace> tagNSS = new ArrayList<Namespace>();
		tagNSS.add(RDFTerms.RDF_NAMESPACE);
		this.addTag("description",DCTerms.DCTERMS_ELEMENT_NAMESPACE,tagNSS,"datatype","http://www.w3.org/2001/XMLSchema#string",RDFTerms.RDF_NAMESPACE,descriptionText);
	}
	
	/**
	 * 
	 * @param titleText
	 */
	public void setTitle(String titleText) {		
		List<Namespace> tagNSS = new ArrayList<Namespace>();
		tagNSS.add(RDFTerms.RDF_NAMESPACE);		
		this.addTag("title",DCTerms.DCTERMS_ELEMENT_NAMESPACE,tagNSS,"datatype","http://www.w3.org/2001/XMLSchema#string",RDFTerms.RDF_NAMESPACE,titleText);
	}
		
	public void addTag(String tagName, Namespace tagNS, List<Namespace> tagNSS, String typeName, String typeURL, Namespace typeNS, String textContent) {
		Element tag = new Element(tagName, tagNS);
		
		for (Namespace namespace : tagNSS) {
			tag.addNamespaceDeclaration(namespace);
		}
	
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute attribute = new Attribute(typeName, typeURL, typeNS);
		attributes.add(attribute);
		
		tag.setAttributes(attributes);
		tag.setText(textContent);
		
		this.addContent(tag);
	}
}
