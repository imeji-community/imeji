package de.mpg.imeji.search.beans;

import de.mpg.imeji.search.AnyFieldCriterion;

public class AnyFieldCriterionBean extends CriterionBean{
	private AnyFieldCriterion anyFieldCriterionVO;
	
	public AnyFieldCriterionBean(){
		this(new AnyFieldCriterion());
	}

	public AnyFieldCriterionBean(AnyFieldCriterion anyFieldCriterionVO) {
		setAnyFieldCriterionVO(anyFieldCriterionVO);
	}

	public AnyFieldCriterion getAnyFieldCriterionVO() {
		return anyFieldCriterionVO;
	}

	public void setAnyFieldCriterionVO(AnyFieldCriterion anyFieldCriterionVO) {
		this.anyFieldCriterionVO = anyFieldCriterionVO;
	}
	
	public boolean clearCriterion(){
		anyFieldCriterionVO = new AnyFieldCriterion();
		anyFieldCriterionVO.setSearchString("");
		return true;
	}

}
