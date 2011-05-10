package de.mpg.imeji.search.beans;

import java.io.Serializable;

import de.mpg.imeji.search.AnyFieldCriterion;

public class AnyFieldCriterionBean extends CriterionBean implements Serializable{
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
	
	public boolean clearCriterion()
	{
		anyFieldCriterionVO = new AnyFieldCriterion();
		anyFieldCriterionVO.setSearchString("");
		return true;
	}

}
