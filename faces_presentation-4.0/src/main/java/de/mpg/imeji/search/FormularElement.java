package de.mpg.imeji.search;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.complextypes.util.ComplexTypeHelper;

public class FormularElement 
{
	private String searchValue;
	private Filtertype filter;
	private Operator operator;
	private String namespace;
	private URI statementType;
	private List<SelectItem> filtersMenu;
	
	private Logger logger = Logger.getLogger(FormularElement.class);

	public FormularElement() 
	{
		this.operator = Operator.OR;
		this.filter = Filtertype.EQUALS;
	}
	
	public FormularElement(List<SearchCriterion> scList, MetadataProfile profile)
	{
		this();
		
		for (SearchCriterion sc : scList)
		{
			if (ImejiNamespaces.IMAGE_METADATA_NAMESPACE.equals(sc.getNamespace()))
			{
				this.namespace = sc.getValue();
			}
			else
			{
				this.searchValue = sc.getValue();
				this.filter = sc.getFilterType();
			}
		}
		
		initStatementType(profile, namespace);
		initFiltersMenu();
	}

	public void initFiltersMenu()
	{
		filtersMenu = new ArrayList<SelectItem>();
		switch (ComplexTypeHelper.getComplexType(statementType)) 
		{
		case DATE:
			filtersMenu.add(new SelectItem(Filtertype.EQUALS_DATE, "="));
			filtersMenu.add(new SelectItem(Filtertype.GREATER_DATE, ">="));
			filtersMenu.add(new SelectItem(Filtertype.LESSER_DATE, "<="));
			break;
		case NUMBER:
			filtersMenu.add(new SelectItem(Filtertype.EQUALS_NUMBER, "="));
			filtersMenu.add(new SelectItem(Filtertype.GREATER_NUMBER, ">="));
			filtersMenu.add(new SelectItem(Filtertype.LESSER_NUMBER, "<="));
			break;
		default:
			filter = Filtertype.REGEX;
			filtersMenu = null;
		}
	}

	public List<SearchCriterion> getAsSCList()
	{
		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
		
		if (searchValue == null || "".equals(searchValue.trim()))
		{
			return scList;
		}
		
		if(statementType == null) throw new RuntimeException("Statement Type of formular element " + namespace + " is null!");

		switch (ComplexTypeHelper.getComplexType(statementType)) 
		{
		case DATE:
			SearchCriterion scDate = new SearchCriterion(operator,ImejiNamespaces.IMAGE_METADATA_DATE, searchValue, filter);
			scList.add(scDate);
			break;
		case GEOLOCATION:
			SearchCriterion scLat = new SearchCriterion(operator, ImejiNamespaces.IMAGE_METADATA_GEOLOCATION_LATITUDE, searchValue, filter);
			SearchCriterion scLong = new SearchCriterion(operator, ImejiNamespaces.IMAGE_METADATA_GEOLOCATION_LONGITUDE, searchValue, filter);
			scList.add(scLat);
			scList.add(scLong);
			break;
		case LICENSE:
			SearchCriterion scLic = new SearchCriterion(operator, ImejiNamespaces.IMAGE_METADATA_TYPE_URI, searchValue, filter);
			scList.add(scLic);
			break;
		case NUMBER:
			SearchCriterion scNum = new SearchCriterion(operator, ImejiNamespaces.IMAGE_METADATA_NUMBER, searchValue, filter);
			scList.add(scNum);
			break;
		case PERSON:
			SearchCriterion scFam = new SearchCriterion(operator, ImejiNamespaces.IMAGE_METADATA_PERSON_FAMILY_NAME, searchValue, filter);
			SearchCriterion scGiv = new SearchCriterion(operator, ImejiNamespaces.IMAGE_METADATA_PERSON_GIVEN_NAME, searchValue, filter);
			SearchCriterion scOrg = new SearchCriterion(operator, ImejiNamespaces.IMAGE_METADATA_PERSON_ORGANIZATION_NAME, searchValue, filter);
			scList.add(scFam);
			scList.add(scGiv);
			scList.add(scOrg);
			break;
		case PUBLICATION:
			SearchCriterion scPub = new SearchCriterion(operator, ImejiNamespaces.IMAGE_METADATA_TYPE_URI, searchValue, filter);
			scList.add(scPub);
			break;
		case TEXT:
			SearchCriterion scText = new SearchCriterion(operator, ImejiNamespaces.IMAGE_METADATA_TEXT, searchValue, filter);
			scList.add(scText);
			break;
		case URI:
			SearchCriterion scURI = new SearchCriterion(operator, ImejiNamespaces.IMAGE_METADATA_TYPE_URI, searchValue, filter);
			scList.add(scURI);
			break;
		}
		
		scList.add(new SearchCriterion(Operator.AND,ImejiNamespaces.IMAGE_METADATA_NAMESPACE, namespace, Filtertype.URI));

		return scList;
	}
	
	public void initStatementType(MetadataProfile p, String namespace)
	{
		for (Statement st : p.getStatements())
		{
			if (st.getName().toString().equals(namespace))
			{
				statementType = st.getType();
			}
		}
	}


	public String getSearchValue()
	{
		return searchValue;
	}
	public void setSearchValue(String searchValue) 
	{
		this.searchValue = searchValue;
	}

	public String getNamespace() 
	{
		return namespace;
	}

	public void setNamespace(String namespace) 
	{
		this.namespace = namespace;
	}

	public URI getStatementType() 
	{
		return statementType;
	}

	public void setStatementType(URI statementType) 
	{
		this.statementType = statementType;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Filtertype getFilter() {
		return filter;
	}

	public void setFilter(Filtertype filter) {
		this.filter = filter;
	}

	public List<SelectItem> getFiltersMenu() {
		return filtersMenu;
	}

	public void setFiltersMenu(List<SelectItem> filtersMenu) {
		this.filtersMenu = filtersMenu;
	}



}
