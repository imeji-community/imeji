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

	@Override
	public ArrayList<SearchCriterion> createSearchCriterion() {
		ArrayList<SearchCriterion> criterions = new ArrayList<SearchCriterion>();

		return null;
	}
	
}