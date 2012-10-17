package de.fub.imeji.ingest.module.excelConverter;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Attribute;

import de.fub.imeji.ingest.core.beans.metadata.terms.RDFTerms;

public class XmlStatement extends Element{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4696004798869640228L;
	
	private static String statementTag = "statement";
	
	private static Namespace ns = Namespace.getNamespace("http://zuse.zib.de/imeji/statement/");
	private int statementID;
	
	private XmlDescription description;
	
	/**
	 * 
	 */
	public XmlStatement() {
		this(statementTag, ns);
	}
	
	/**
	 * 
	 */
	public XmlStatement(Namespace namespace) {
		this(statementTag, namespace);
	}
	
	/**
	 * 
	 * @param tag
	 * @param namespace
	 */
	public XmlStatement(String tag, Namespace namespace) {
		super(statementTag, ns);
		this.description = new XmlDescription();
		this.addContent(this.description);
	}

	
	/**
	 * 
	 * @param statementID
	 */
	public XmlStatement(int statementID) {
		this();
		
	}
	
	/**
	 * 
	 * @param statementID
	 */
	public XmlStatement(int statementID, Namespace namespace) {
		this();
		this.statementID = statementID;
		this.setNamespace(namespace);
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute attribute = new Attribute("about", ns.getURI()+this.statementID, RDFTerms.RDF_NAMESPACE);
		attributes.add(attribute);
		this.description.setAttributes(attributes);
	}
	
	/**
	 * 
	 * @param tag
	 * @param namespace
	 * @param statementID
	 */
	public XmlStatement(String tag, Namespace namespace, int statementID) {
		this(tag, namespace);
		this.statementID = statementID;
	}
	
	public String getNamespaceID() {
		return ns.getURI() + new Integer(this.statementID).toString();
	}

	public void setOriginLabelName(String signature_original,
			Namespace namespace) {
		this.description.setOriginLabelName(signature_original, namespace);
		
	}

	public void addLabel(String string, String signature) {
		this.description.addLabel(string, signature);
	}

	public void setType(String string) {
		this.description.setType(string);
	}

	public void setMinOccurs(String string, Namespace namespace) {
		this.description.setMinOccurs(string, namespace);
	}

	public void setMaxOccurs(String string, Namespace namespace) {
		this.description.setMaxOccurs(string, namespace);
	}

	public String getOriginLabelName() {
		
		return this.description.getOriginLabelName();
	}

	public void setParentNode(XmlStatement xmlStatement) {
		this.description.setParentNode(xmlStatement);
		
	}

	public void setDescription(boolean value, Namespace namespace) {
		this.description.setDescription(value ,namespace);
	}

	/**
	 * @param parentName the parentName to set
	 */
	public void setParentName(String parentName) {
		this.description.setParentName(parentName);
	}

	/**
	 * @return the parentName
	 */
	public String getParentName() {
		return this.description.getParentName();
	}
}
