/**
 * 
 */
package de.mpg.imeji.logic.ingest.vo;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;


/**
 * @author hnguyen
 *
 */
@XmlRootElement(name="ingestProfile")
public class IngestProfile {

	
	private MetadataProfiles mdProfiles;
	private Items items;
	
	/**
	 * 
	 */
	public IngestProfile() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the mdProfile
	 */
	public MetadataProfiles getMdProfiles() {
		return mdProfiles;
	}

	/**
	 * @param mdProfile the mdProfile to set
	 */
	public void setMdProfile(MetadataProfiles mdProfiles) {
		this.mdProfiles = mdProfiles;
	}

	/**
	 * @return the items
	 */	
	public Items getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(Items items) {
		this.items = items;
	}
}
