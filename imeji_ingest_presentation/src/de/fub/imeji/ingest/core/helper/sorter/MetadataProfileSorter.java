/**
 * 
 */
package de.fub.imeji.ingest.core.helper.sorter;


import java.util.Comparator;
import java.util.Enumeration;

import de.fub.imeji.ingest.core.beans.metadata.MetadataProfile;

/**
 * @author hnguyen
 *
 */
public class MetadataProfileSorter implements Comparator<MetadataProfile> {
	private String label;
	
	public MetadataProfileSorter(String label) {
		this.setLabel(label);
	}	
	
	public int compare(MetadataProfile o1, MetadataProfile o2) {
		
		
		Enumeration<String> mdKeys1 = o1.getMetaDatas().keys();
		Enumeration<String> mdKeys2 = o2.getMetaDatas().keys();		
		
		
		String val1 = "0";
		String val2 = "0";
		
		while(mdKeys1.hasMoreElements()) {
			String mdKey1 = mdKeys1.nextElement();
			
			if(mdKey1.contains(this.getLabel())) {
				val1 = o1.getMetaDatas().get(mdKey1).getValue();
				break;
			}			
		}
		
		while(mdKeys2.hasMoreElements()) {
			String mdKey2 = mdKeys2.nextElement();
			
			if(mdKey2.contains(this.getLabel())) {
				val2 = o2.getMetaDatas().get(mdKey2).getValue();
				break;
			}			
		}
		

		return val1.compareTo(val2);

	}



	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}



	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

}
