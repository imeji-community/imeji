/**
 * 
 */
package de.fub.imeji.ingest.core.beans.metadata;

/**
 * @author hnguyen
 *
 */
public class MetaDataEntity {
	
	public static final String LANGUAGE = "lang";
	public static final String LABEL = "label";
	public static final String ID = "id";
	
	private MetadataProfile metaDataProfile;
	
	public MetaDataEntity(MetadataProfile mdp) {
		this.metaDataProfile = mdp;
	}

	/**
	 * @param metaDataProfile the metaDataProfile to set
	 */
	public void setMetaDataProfile(MetadataProfile metaDataProfile) {
		this.metaDataProfile = metaDataProfile;
	}

	/**
	 * @return the metaDataProfile
	 */
	public MetadataProfile getMetaDataProfile() {
		return metaDataProfile;
	}
}
