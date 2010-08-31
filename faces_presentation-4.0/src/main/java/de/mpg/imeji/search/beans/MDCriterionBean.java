package de.mpg.imeji.search.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.xalan.xsltc.compiler.sym;

import de.mpg.imeji.search.MDCriterion;
import de.mpg.imeji.search.simulator.Simulator;
import de.mpg.jena.controller.SearchCriterion;

public class MDCriterionBean extends CriterionBean {
	public static final String BEAN_NAME = "MDCriterionBean";

	private MDCriterion mdCriterionVO;
	private Simulator s = new Simulator();
	

	public MDCriterionBean(){
		this(new MDCriterion());
	}
	
	public MDCriterionBean(MDCriterion mdCriterionVO) {
		setMdCriterionVO(mdCriterionVO);
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

	public boolean clearCriterion() {
		mdCriterionVO.setMd(null);
		mdCriterionVO.setMdText(null);
		return true;
		

		
	}


}
