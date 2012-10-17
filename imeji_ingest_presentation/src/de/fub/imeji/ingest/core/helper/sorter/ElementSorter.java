/**
 * 
 */
package de.fub.imeji.ingest.core.helper.sorter;


import java.util.Comparator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;

import de.fub.imeji.ingest.core.beans.metadata.MetaDataEntity;

/**
 * @author hnguyen
 *
 */
public class ElementSorter implements Comparator<Element> {

	public int compare(Element o1, Element o2) {
		List<Attribute> attrs1 = o1.getAttributes();
		List<Attribute> attrs2 = o2.getAttributes();
		
		int value1 = Integer.MIN_VALUE;
		int value2 = Integer.MIN_VALUE;
		
		for (Attribute attribute : attrs1) {
			if(attribute.getName().equalsIgnoreCase(MetaDataEntity.ID)) {
				value1 = Integer.parseInt(attribute.getValue());
			}
		}
		
		for (Attribute attribute : attrs2) {
			if(attribute.getName().equalsIgnoreCase(MetaDataEntity.ID)) {
				value2 = Integer.parseInt(attribute.getValue());
			}
		}
		
		if(value1 < value2) {
			return -1;
		} else if(value1 > value2) {
			return 1;
		}
		
		return 0;
	}
}
