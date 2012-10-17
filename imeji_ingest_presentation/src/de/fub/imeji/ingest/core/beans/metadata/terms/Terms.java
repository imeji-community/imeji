package de.fub.imeji.ingest.core.beans.metadata.terms;

import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Namespace;

public class Terms {
	private static long id = 0;
	private String termLabel;
	private Namespace termNamespace;
	private List<Attribute> attributes;	
		
	public static Namespace XSI_NAMESPACE = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
	
	
	public Terms(String label, Namespace namespace, List<Attribute> attributes) {
		this.setTermLabel(label);
		this.setTermNamespace(namespace);
		this.setAttributes(attributes);
	}

	public static long getId() {
		long currentId = id++;
		return currentId;
	}

	/**
	 * @param termLabel the termLabel to set
	 */
	public void setTermLabel(String termLabel) {
		this.termLabel = termLabel;
	}

	/**
	 * @return the termLabel
	 */
	public String getTermLabel() {
		return termLabel;
	}

	/**
	 * @param termNamespace the termNamespace to set
	 */
	public void setTermNamespace(Namespace termNamespace) {
		this.termNamespace = termNamespace;
	}

	/**
	 * @return the termNamespace
	 */
	public Namespace getTermNamespace() {
		return termNamespace;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the attributes
	 */
	public List<Attribute> getAttributes() {
		return attributes;
	}
}
