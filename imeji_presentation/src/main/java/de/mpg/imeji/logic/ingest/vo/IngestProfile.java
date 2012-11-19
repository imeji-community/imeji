/**
 * 
 */
package de.mpg.imeji.logic.ingest.vo;

import javax.xml.bind.annotation.XmlRootElement;


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
