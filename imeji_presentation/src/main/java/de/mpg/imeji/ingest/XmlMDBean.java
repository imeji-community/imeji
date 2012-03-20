package de.mpg.imeji.ingest;

import java.util.Hashtable;

/**
 * A generic meta data class using for the xml ingest. 
 * Maps xml components into a hash table (thread safety).
 * All values are represented in a generic type of string. 
 * @author hnguyen
 *
 */
public class XmlMDBean {
	
	/**
	 * The project name
	 */
	private String projectName;
	
	/**
	 * The profile name
	 */
	private String profileName;
	
	/**
	 * The meta data table
	 */
	private Hashtable<String,String> metadatas;

	/**
	 * Standard contructor creating a hashtable
	 */
	public XmlMDBean(String projectName, String profileName) {
		this.setProjectName(projectName);
		this.setProfileName(profileName);
		this.metadatas = new Hashtable<String, String>();
	}
	
	/**
	 * Adds a component to the hash table.
	 * @param tag, the name of a meta data,
	 * @param value, the value of the meta data represented as string type.
	 */
	public void add(String tag, String value) {		
		this.metadatas.put(tag, value);		
	}
	
	/**
	 * @return the metadatas
	 */
	public Hashtable<String, String> getMetadatas() {
		return metadatas;
	}
	
	/**
	 * Method getting the value of the meta data.
	 * @param tag, the name of the meta data.
	 */
	public String getValueOfTag(String tag) {
		return this.metadatas.get(tag);
	}

	/**
	 * Sets the project name
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * Gets the project name
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}
	
	/**
	 * 
	 * @return the amount of meta data
	 */
	public int getSize() {
		return this.metadatas.size();
	}

	/**
	 * Sets the meta data profile name
	 * @param profileName
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	/**
	 * Gets the meta data profile name
	 * @return
	 */
	public String getProfileName() {
		return profileName;
	}
}
