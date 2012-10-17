/**
 * 
 */
package de.fub.imeji.ingest.core.zuse.beans;


import java.util.ArrayList;

import de.fub.imeji.ingest.core.zuse.metadata.terms.ZuseDCTerms;


/**
 * @author hnguyen
 *
 */
public class ZuseDCTermsBean {
	private ArrayList<String> labelTree;
	private ZuseDCTerms dcterms;

	public ZuseDCTermsBean(ArrayList<String> labelTree, ZuseDCTerms dcterms) {
		this.setLabelTree(labelTree);
		this.setDcterms(dcterms);
	}

	/**
	 * @param labelTree the labelTree to set
	 */
	public void setLabelTree(ArrayList<String> labelTree) {
		this.labelTree = labelTree;
	}

	/**
	 * @return the labelTree
	 */
	public ArrayList<String> getLabelTree() {
		return labelTree;
	}
	
	/**
	 * 
	 * @return
	 */
	public ZuseDCTerms getDcterms() {
		return dcterms;
	}

	/**
	 * 
	 * @param dcterms
	 */
	public void setDcterms(ZuseDCTerms dcterms) {
		this.dcterms = dcterms;
	}
}
