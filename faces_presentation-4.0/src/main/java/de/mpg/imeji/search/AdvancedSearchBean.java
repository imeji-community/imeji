package de.mpg.imeji.search;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.lang.MetadataLabels;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ObjectLoader;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.MetadataProfile;

public class AdvancedSearchBean implements Serializable
{
	private SearchFormular formular = null;

	// Menus
	private List<SelectItem> collectionsMenu = new ArrayList<SelectItem>();
	private List<SelectItem> operatorsMenu = new ArrayList<SelectItem>();

	private SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private static Logger logger = Logger.getLogger(AdvancedSearchBean.class);
	

	public AdvancedSearchBean() 
	{
		operatorsMenu.add(new SelectItem(Operator.AND, session.getLabel("and")));
		operatorsMenu.add(new SelectItem(Operator.OR, session.getLabel("or")));
		operatorsMenu.add(new SelectItem(Operator.NOTOR, session.getLabel("not")));
	}

	public String getNewSearch()
	{
		try 
		{
			String query = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("q");
			initFormular(URLQueryTransformer.transform2SCList(query));
		} 
		catch (Exception e) 
		{
			logger.error("Error initializing advanced search", e);
			BeanHelper.error("Error initializing advanced search");
		}
		return "";
	}

	public void initFormular(List<SearchCriterion> scList) throws Exception
	{
		Map<String, CollectionImeji> cols = loadCollections();
		Map<String, MetadataProfile> profs = loadProfiles(cols.values());
		((MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class)).init1((new ArrayList<MetadataProfile>(profs.values())));

		formular = new SearchFormular(scList, cols, profs);
		if (formular.getGroups().size() == 0)
		{
			formular.addSearchGroup(0);
		}
	}
	
	public void initFormular() throws Exception
	{
		initFormular(new ArrayList<SearchCriterion>());
	}

	private Map<String, CollectionImeji> loadCollections()
	{
		CollectionController cc = new CollectionController(session.getUser());
		Map<String, CollectionImeji> map = new HashMap<String, CollectionImeji>();

		for (String uri : cc.search(new ArrayList<SearchCriterion>(), null, -1, 0).getResults())
		{
			CollectionImeji c = ObjectLoader.loadCollection(URI.create(uri), session.getUser());
			
			map.put(uri, c);
		}

		return map;
	}

	private Map<String, MetadataProfile> loadProfiles(Collection<CollectionImeji> collections)
	{		
		collectionsMenu = new ArrayList<SelectItem>();
		collectionsMenu.add(new SelectItem(null, "Select collection"));
		
		Map<String, MetadataProfile> map = new HashMap<String, MetadataProfile>();
		
		for (CollectionImeji c : collections)
		{
			MetadataProfile p = ObjectLoader.loadProfile(c.getProfile(), session.getUser());
			
			if (p.getStatements().size() > 0)
			{
				map.put(c.getId().toString(), p);
				collectionsMenu.add(new SelectItem(c.getId().toString(), c.getMetadata().getTitle()));
			}
		}
		return map;
	}

	public String search()
	{
		ImagesBean bean = (ImagesBean)BeanHelper.getSessionBean(ImagesBean.class);
		List<SearchCriterion> scList = formular.getFormularAsSCList();
		bean.setQuery(URLQueryTransformer.transform2URL(scList));
		bean.setScList(scList);
		bean.getFacets().getFacets().clear();
		
		if (bean.getQuery() == null || "".equals(bean.getQuery().trim()))
		{
			return "";
		}
		
		return "pretty:images";
	}

	public void changeGroup()
	{
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gPos"));
		formular.changeSearchGroup(gPos);
	}

	public void addGroup()
	{
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gPos"));
		formular.addSearchGroup(gPos);
	}
	
	public void removeGroup()
	{
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gPos"));
		formular.removeSearchGroup(gPos);
		if (formular.getGroups().size() == 0)
		{
			formular.addSearchGroup(0);
		}
	}

	public void changeElement()
	{
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gPos"));
		int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("elPos"));
		formular.changeElement(gPos, elPos, false);
	}
	
	public void updateElement()
	{
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gPos"));
		int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("elPos"));
		formular.changeElement(gPos, elPos, true);
	}

	public void addElement()
	{
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gPos"));
		int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("elPos"));
		formular.addElement(gPos, elPos);
	}
	
	public void removeElement()
	{
		int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gPos"));
		int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("elPos"));
		formular.removeElement(gPos, elPos);
		
		if(formular.getGroups().get(gPos).getElements().size() == 0)
		{
			formular.removeSearchGroup(gPos);
			formular.addSearchGroup(gPos);
		}
	}

	public String getSimpleQuery()
	{
		return URLQueryTransformer.transform2SimpleQuery(formular.getFormularAsSCList());
	}
	
	public List<SelectItem> getCollectionsMenu() {
		return collectionsMenu;
	}

	public void setCollectionsMenu(List<SelectItem> collectionsMenu) {
		this.collectionsMenu = collectionsMenu;
	}

	public SearchFormular getFormular() {
		return formular;
	}

	public void setFormular(SearchFormular formular) {
		this.formular = formular;
	}

	public List<SelectItem> getOperatorsMenu() {
		return operatorsMenu;
	}

	public void setOperatorsMenu(List<SelectItem> operatorsMenu) {
		this.operatorsMenu = operatorsMenu;
	}


}
