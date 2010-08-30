package de.mpg.imeji.search.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.search.MDCriterion;
import de.mpg.imeji.search.simulator.Simulator;
import de.mpg.jena.controller.SearchCriterion;

public class MDCriterionBean extends CriterionBean {
	public static final String BEAN_NAME = "MDCriterionBean";
	private List<SelectItem> mdList = null;
	private MDCriterion mdCriterionVO;
	

	public MDCriterionBean(){
		this(new MDCriterion());
	}
	
	public MDCriterionBean(MDCriterion mdCriterionVO) {
		setMdCriterionVO(mdCriterionVO);
	}

	public List<SelectItem> getMdList(){
		mdList = new ArrayList<SelectItem>();
		//TODO: remove static mdprofile list
		Simulator s = new Simulator();
		try{
			for(int i=0; i<s.getSelectedCollection().getMdList().size(); i++)
				mdList.add(new SelectItem(s.getSelectedCollection().getMdList().get(i).getValue(),s.getSelectedCollection().getMdList().get(i).getLabel()));
		}catch(Exception e){
			for(int i=0; i<s.getDefaultCollection().getMdList().size(); i++)
				mdList.add(new SelectItem(s.getDefaultCollection().getMdList().get(i).getValue(),s.getDefaultCollection().getMdList().get(i).getLabel()));

			}
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

	public boolean clearCriterion() {
		mdCriterionVO.setMd(null);
		mdCriterionVO.setMdText(null);
		return true;
		

		
	}


}
