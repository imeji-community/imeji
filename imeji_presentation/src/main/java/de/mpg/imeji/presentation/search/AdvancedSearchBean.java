/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.search.util.SearchIndexInitializer;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.filter.FiltersSession;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

public class AdvancedSearchBean
{
    private SearchFormular formular = null;
    // Menus
    private List<SelectItem> collectionsMenu;
    private List<SelectItem> operatorsMenu;
    private SessionBean session;
    private static Logger logger = Logger.getLogger(AdvancedSearchBean.class);

    public AdvancedSearchBean()
    {
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        operatorsMenu = new ArrayList<SelectItem>();
        operatorsMenu.add(new SelectItem(LOGICAL_RELATIONS.AND, session.getLabel("and")));
        operatorsMenu.add(new SelectItem(LOGICAL_RELATIONS.OR, session.getLabel("or")));
    }

    public void newSearch()
    {
        getNewSearch();
    }
    
    public String getNewSearch()
    {
        try
        {
            String query = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("q");
            initFormular(URLQueryTransformer.parseStringQuery(query));
        }
        catch (Exception e)
        {
            logger.error("Error initializing advanced search", e);
            BeanHelper.error("Error initializing advanced search");
        }
        return "";
    }

    public void initFormular(SearchQuery searchQuery) throws Exception
    {
        Map<String, CollectionImeji> cols = loadCollections();
        Map<String, MetadataProfile> profs = loadProfilesAndInitCollectionsMenu(cols.values());
        ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class)).init1((new ArrayList<MetadataProfile>(profs
                .values())));
        formular = new SearchFormular(searchQuery, cols, profs);
        if (formular.getGroups().size() == 0)
        {
            formular.addSearchGroup(0);
        }
    }

    public void initFormular() throws Exception
    {
        initFormular(new SearchQuery());
    }

    private Map<String, CollectionImeji> loadCollections()
    {
        CollectionController cc = new CollectionController(session.getUser());
        Map<String, CollectionImeji> map = new HashMap<String, CollectionImeji>();
        for (String uri : cc.search(new SearchQuery(), null, -1, 0).getResults())
        {
            CollectionImeji c = ObjectLoader.loadCollectionLazy(URI.create(uri), session.getUser());
            map.put(uri, c);
        }
        return map;
    }

    private Map<String, MetadataProfile> loadProfilesAndInitCollectionsMenu(Collection<CollectionImeji> collections)
    {
        collectionsMenu = new ArrayList<SelectItem>();
        collectionsMenu.add(new SelectItem(null, session.getLabel("select_collection")));
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
        FiltersSession filtersSession = (FiltersSession)BeanHelper.getSessionBean(FiltersSession.class);
        filtersSession.getFilters().clear();
        goToResultPage();
        return "";
    }

    public void goToResultPage()
    {
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        try
        {
            String encodedQuery = URLEncoder.encode(
                    URLQueryTransformer.transform2URL(formular.getFormularAsSearchQuery()), "UTF-8");
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(navigation.getBrowseUrl() + "?q=" + encodedQuery);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void changeGroup()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        formular.changeSearchGroup(gPos);
    }

    public void addGroup()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        formular.addSearchGroup(gPos);
    }

    public void removeGroup()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        formular.removeSearchGroup(gPos);
        if (formular.getGroups().size() == 0)
        {
            formular.addSearchGroup(0);
        }
    }

    public void changeElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.changeElement(gPos, elPos, false);
    }

    public void updateElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.changeElement(gPos, elPos, true);
    }

    public void addElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.addElement(gPos, elPos);
    }

    public void removeElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.removeElement(gPos, elPos);
        if (formular.getGroups().get(gPos).getElements().size() == 0)
        {
            formular.removeSearchGroup(gPos);
            formular.addSearchGroup(gPos);
        }
    }

    public String getSimpleQuery()
    {
        return URLQueryTransformer.searchQuery2PrettyQuery(formular.getFormularAsSearchQuery());
    }

    public List<SelectItem> getCollectionsMenu()
    {
        return collectionsMenu;
    }

    public void setCollectionsMenu(List<SelectItem> collectionsMenu)
    {
        this.collectionsMenu = collectionsMenu;
    }

    public SearchFormular getFormular()
    {
        return formular;
    }

    public void setFormular(SearchFormular formular)
    {
        this.formular = formular;
    }

    public List<SelectItem> getOperatorsMenu()
    {
        return operatorsMenu;
    }

    public void setOperatorsMenu(List<SelectItem> operatorsMenu)
    {
        this.operatorsMenu = operatorsMenu;
    }
}
