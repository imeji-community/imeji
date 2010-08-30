package de.mpg.imeji.search;

import java.util.ArrayList;

import de.mpg.jena.controller.SearchCriterion;

public class MDCriterion extends Criterion{
	private String md;
	private String mdText;
	
	public MDCriterion(){
		
	}
	
	public String getMd() {
		return md;
	}
	public void setMd(String md) {
		this.md = md;
	}
	public String getMdText() {
		return mdText;
	}
	public void setMdText(String mdText) {
		this.mdText = mdText;
	}

	public ArrayList<String> createSearchCriterion() {
		ArrayList<String> criterions = new ArrayList<String>();
	
		if(isSearchStringEmpty() == true)
			return criterions;
		else{
			String criterion = new String();
			criterion += "MD=" + getMd() +"&MDText=" + getMdText();
			System.err.println(criterion);
			criterions.add(criterion);
		}

		return  criterions;
	}
	
}