package de.mpg.imeji.search.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.search.MDCriterion;
import de.mpg.jena.controller.SearchCriterion;

public class MDCriterionBean extends CriterionBean {
	public static final String BEAN_NAME = "MDCriterionBean";
	private List<SelectItem> mdList;
	private MDCriterion mdCriterionVO;
	

	public MDCriterionBean(){
		this(new MDCriterion());
	}
	
	public MDCriterionBean(MDCriterion mdCriterionVO) {
		setMdCriterionVO(mdCriterionVO);
	}

	public List<SelectItem> getMdList() {
		mdList = new ArrayList<SelectItem>();
		//TODO: remove static mdprofile list
		mdList.add(new SelectItem("Author","Author"));
		mdList.add(new SelectItem("Genre","Genre"));
		mdList.add(new SelectItem("Datum","Datum"));
		mdList.add(new SelectItem("Country","Country"));
		
		return mdList;
	}
	public void setMdList(List<SelectItem> mdList) {
		this.mdList = mdList;
	}
	
	public MDCriterion getMdCriterionVO() {
		return mdCriterionVO;
	}

	public void setMdCriterionVO(MDCriterion mdCriterionVO) {
		this.mdCriterionVO = mdCriterionVO;
	}

	
	public ArrayList<SearchCriterion> createSearchCriterion(){
		return null;
		
	}

	public void clearCriterion() {
		mdCriterionVO.setMd(null);
		mdCriterionVO.setMdText(null);

		
	}


}
