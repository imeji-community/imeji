package de.fub.imeji.ingest.core.beans.metadata;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;

import org.jdom2.Element;

public class MetadataProfile implements Cloneable {
	private final String metadataProfileName;
	private Hashtable<String, Element> metaDatas;
	
	public MetadataProfile(String metadataProfileName) {
		this.metadataProfileName = metadataProfileName;
		this.metaDatas = new Hashtable<String, Element>();
	}

	/**
	 * @return the metadataProfileName
	 */
	public String getMetadataProfileName() {
		return metadataProfileName;
	}

	/**
	 * @param metaDatas the metaDatas to set
	 */
	public void setMetaDatas(Hashtable<String, Element> metaDatas) {
		this.metaDatas = metaDatas;
	}

	/**
	 * @return the metaDatas
	 */
	public Hashtable<String, Element> getMetaDatas() {
		if(this.metaDatas == null)
			throw new NoSuchElementException("Metadata not available!");
		return metaDatas;
	}

	/**
	 * 
	 */
	public MetadataProfile clone() {
		try {
			return (MetadataProfile) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * clear the meta data hash table
	 */
	public void clear() {		
		Enumeration<String> mdKeys = this.getMetaDatas().keys();
		while(mdKeys.hasMoreElements()) {
			String mdKey = mdKeys.nextElement();
			Element metadata = this.getMetaDatas().get(mdKey);
			if(metadata.getChildren().isEmpty()) {
				metadata.getContent().clear();
			} else {
				this.clear(metadata);
			}
		}
	}

	/**
	 * 
	 * @param metadata
	 */
	private void clear(Element metadata) {
		List<Element> kids = metadata.getChildren();
		for (Element element : kids) {
			if(element.getChildren().isEmpty())
				element.getContent().clear();
			else {
				this.clear(element);
			}
		}
	}

}
