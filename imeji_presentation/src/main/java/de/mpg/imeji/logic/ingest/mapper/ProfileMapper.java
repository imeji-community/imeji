/**
 * 
 */
package de.mpg.imeji.logic.ingest.mapper;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import de.mpg.imeji.logic.vo.MetadataProfile;

/**
 * @author hnguyen
 * 
 */
public class ProfileMapper {

	private class DuplicateProfileObject {
		
		private List<String> duplicateFilenames;
		
		Hashtable<String, MetadataProfile> hashTableFilename;
		
		public DuplicateProfileObject() {
			this.setDuplicateFilenames(new ArrayList<String>());

			this.setHashTableFilename(new Hashtable<String, MetadataProfile>());
		}

		/**
		 * @return the duplicateFilenames
		 */
		public List<String> getDuplicateFilenames() {
			return duplicateFilenames;
		}

		/**
		 * @param duplicateFilenames the duplicateFilenames to set
		 */
		public void setDuplicateFilenames(List<String> duplicateFilenames) {
			this.duplicateFilenames = duplicateFilenames;
		}

		/**
		 * @return the hashTableFilename
		 */
		public Hashtable<String, MetadataProfile> getHashTableFilename() {
			return hashTableFilename;
		}

		/**
		 * @param hashTableFilename the hashTableFilename to set
		 */
		public void setHashTableFilename(Hashtable<String, MetadataProfile> hashTableFilename) {
			this.hashTableFilename = hashTableFilename;
		}
	
	}
	
	private DuplicateProfileObject dupProfiles;
	
	
	/**
	 * @throws URISyntaxException 
	 * 
	 */
	public ProfileMapper(List<MetadataProfile> profileList) {
		this.dupProfiles = this.process(profileList);
	}

	private DuplicateProfileObject process(List<MetadataProfile> profileList) {
		DuplicateProfileObject dupProfiles = new DuplicateProfileObject();		
		
		for (MetadataProfile profile : profileList) {
			MetadataProfile profileAsFilename = dupProfiles.getHashTableFilename().get(profile.getTitle());

			if (profileAsFilename == null) {
				dupProfiles.getHashTableFilename().put(new String(profile.getTitle()), profile);
			} else {
				dupProfiles.getDuplicateFilenames().add(new String(profile.getTitle()));
			}

		}
		
		return dupProfiles;
	}
	
	public List<String> getDuplicateFilenames() {
		return this.dupProfiles.getDuplicateFilenames();
	}
	
	public boolean hasDuplicateFilenames() {
		return !this.getDuplicateFilenames().isEmpty();
	}

	private List<MetadataProfile> getUniqueFilenameListsAsProfileList() {				
		return new ArrayList<MetadataProfile>(this.dupProfiles.hashTableFilename.values());
	}
	
	public List<MetadataProfile> getMappedProfiles() {
		return this.getUniqueFilenameListsAsProfileList();
	}

}
