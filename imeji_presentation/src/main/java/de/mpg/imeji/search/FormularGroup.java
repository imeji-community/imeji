/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.lang.MetadataLabels;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class FormularGroup 
{
	private List<FormularElement> elements;
	private String collectionId;
	private List<SelectItem> statementMenu;

	public FormularGroup()
	{
		elements = new ArrayList<FormularElement>();
		statementMenu = new ArrayList<SelectItem>();
	}

	public FormularGroup(List<SearchCriterion> scList, MetadataProfile profile)
	{
		this();
		this.collectionId = SearchFormularHelper.getCollectionId(scList);

		for (SearchCriterion sc : scList)
		{
			if (!ImejiNamespaces.IMAGE_COLLECTION.equals(sc.getNamespace()))
			{
				for (SearchCriterion sc1 : sc.getChildren())
				{
					FormularElement element = new FormularElement(sc1.getChildren(), profile);
					element.setOperator(sc1.getOperator());
					elements.add(element);
				}
			}
		}
		initStatementsMenu(profile);
	}

	public List<SearchCriterion> getAsSCList()
	{
		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();

		scList.add(new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_COLLECTION, collectionId, Filtertype.URI));

		List<SearchCriterion> scEls = new ArrayList<SearchCriterion>();

		for (FormularElement e : elements)
		{
			List<SearchCriterion> subList = e.getAsSCList();
			if (subList.size() > 0)
			{
				SearchCriterion scElement = new SearchCriterion(e.getOperator(), e.getAsSCList());
				scEls.add(scElement);
			}
		}

		if (scEls.size() == 0) 
		{
			return new ArrayList<SearchCriterion>();
		}

		scList.add(new SearchCriterion(Operator.AND, scEls));

		return scList;
	}

	public void initStatementsMenu(MetadataProfile p)
	{
		for (Statement st : p.getStatements())
		{
			String stName = ((MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class)).getInternationalizedLabels().get(st.getName());
			statementMenu.add(new SelectItem(st.getName().toString(), stName));
		}
	}

	public List<FormularElement> getElements() 
	{
		return elements;
	}

	public void setElements(List<FormularElement> elements) 
	{
		this.elements = elements;
	}

	public String getCollectionId() 
	{
		return collectionId;
	}

	public void setCollectionId(String collectionId) 
	{
		this.collectionId = collectionId;
	}

	public List<SelectItem> getStatementMenu() 
	{
		return statementMenu;
	}

	public void setStatementMenu(List<SelectItem> statementMenu) 
	{
		this.statementMenu = statementMenu;
	}

}
