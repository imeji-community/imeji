/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.xml.bind.DatatypeConverter;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.ByteArray;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;

import com.sun.mail.smtp.DigestMD5;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.filter.FiltersSession;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Java bean for the advanced search page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AdvancedSearchBean
{
    private SearchForm formular = null;
    // Menus
    private List<SelectItem> collectionsMenu;
    private List<SelectItem> operatorsMenu;
    /**
     * True if the query got an error (for instance wrong date format). Then the message is written in red
     */
    private boolean errorQuery = false;
    private SessionBean session;
    private static Logger logger = Logger.getLogger(AdvancedSearchBean.class);

    /**
     * Constructor for the {@link AdvancedSearchBean}
     */
    public AdvancedSearchBean()
    {
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        operatorsMenu = new ArrayList<SelectItem>();
        operatorsMenu.add(new SelectItem(LOGICAL_RELATIONS.AND, session.getLabel("and")));
        operatorsMenu.add(new SelectItem(LOGICAL_RELATIONS.OR, session.getLabel("or")));
    }

    /**
     * Called when the page is called per get request. Read the query in the url and initialize the form with it
     * 
     * @return
     */
    public String getNewSearch()
    {
        try
        {
            String query = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("q");
            if (!UrlHelper.getParameterBoolean("error"))
            {
                errorQuery = false;
                initForm(URLQueryTransformer.parseStringQuery(query));
            }
        }
        catch (Exception e)
        {
            logger.error("Error initializing advanced search", e);
            BeanHelper.error("Error initializing advanced search");
        }
        return "";
    }

    /**
     * Initialized the search form with the {@link SearchQuery}
     * 
     * @param searchQuery
     * @throws Exception
     */
    public void initForm(SearchQuery searchQuery) throws Exception
    {
        Map<String, CollectionImeji> cols = loadCollections();
        Map<String, MetadataProfile> profs = loadProfilesAndInitCollectionsMenu(cols.values());
        ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class)).init1((new ArrayList<MetadataProfile>(profs
                .values())));
        formular = new SearchForm(searchQuery, cols, profs);
        if (formular.getGroups().size() == 0)
        {
            formular.addSearchGroup(0);
        }
    }

    /**
     * Reset the Search form with empty values
     * 
     * @throws Exception
     */
    public String reset() throws Exception
    {
        initForm(new SearchQuery());
        return "";
    }

    /**
     * Load all {@link CollectionImeji} which are searchable for the current {@link User}
     * 
     * @return
     */
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

    /**
     * Load the {@link MetadataProfile} of the {@link Collection} and init the collectionmenu
     * 
     * @param collections
     * @return
     */
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

    /**
     * Method called when form is submitted
     * 
     * @return
     * @throws IOException
     */
    public String search() throws IOException
    {
        FiltersSession filtersSession = (FiltersSession)BeanHelper.getSessionBean(FiltersSession.class);
        filtersSession.getFilters().clear();
        goToResultPage();
        return "";
    }

    /**
     * Redirect to the search result page
     * 
     * @throws IOException
     */
    public void goToResultPage() throws IOException
    {
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        try
        {
            errorQuery = false;
            FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .redirect(
                            navigation.getBrowseUrl() + "?q="
                                    + URLQueryTransformer.transform2UTF8URL(formular.getFormularAsSearchQuery()));
        }
        catch (Exception e)
        {
            errorQuery = true;
            FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getSearchUrl() + "?error=1");
        }
    }

    /**
     * Change the {@link SearchGroup}
     */
    public void changeGroup()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        formular.changeSearchGroup(gPos);
    }

    /**
     * Add a new {@link SearchGroupForm}
     */
    public void addGroup()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        formular.addSearchGroup(gPos);
    }

    /**
     * Remove a {@link SearchGroupForm}
     */
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

    /**
     * Change a {@link SearchMetadataForm}. The search value is removed
     */
    public void changeElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.changeElement(gPos, elPos, false);
    }

    /**
     * Update a {@link SearchMetadataForm}. The search value is keeped
     */
    public void updateElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.changeElement(gPos, elPos, true);
    }

    /**
     * Add a new {@link SearchMetadataForm}
     */
    public void addElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.addElement(gPos, elPos);
    }

    /**
     * Remove a new {@link SearchMetadataForm}
     */
    public void removeElement()
    {
        int gPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("gPos"));
        int elPos = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("elPos"));
        formular.removeElement(gPos, elPos);
        if (formular.getGroups().get(gPos).getSearchElementForms().size() == 0)
        {
            formular.removeSearchGroup(gPos);
            formular.addSearchGroup(gPos);
        }
    }

    /**
     * Return the current {@link SearchQuery} in the form as a user friendly query
     * 
     * @return
     */
    public String getSimpleQuery()
    {
        try
        {
            errorQuery = false;
            return URLQueryTransformer.searchQuery2PrettyQuery(formular.getFormularAsSearchQuery());
        }
        catch (Exception e)
        {
            errorQuery = true;
            if ("Wrong date format".equals(e.getMessage()))
                return session.getMessage("error_date_format");
            return e.getMessage();
        }
    }

    /**
     * Getter
     * 
     * @return
     */
    public List<SelectItem> getCollectionsMenu()
    {
        return collectionsMenu;
    }

    /**
     * Setter
     * 
     * @param collectionsMenu
     */
    public void setCollectionsMenu(List<SelectItem> collectionsMenu)
    {
        this.collectionsMenu = collectionsMenu;
    }

    /**
     * Getter
     * 
     * @return
     */
    public SearchForm getFormular()
    {
        return formular;
    }

    /**
     * stter
     * 
     * @param formular
     */
    public void setFormular(SearchForm formular)
    {
        this.formular = formular;
    }

    /**
     * Getter
     * 
     * @return
     */
    public List<SelectItem> getOperatorsMenu()
    {
        return operatorsMenu;
    }

    /**
     * Setter
     * 
     * @param operatorsMenu
     */
    public void setOperatorsMenu(List<SelectItem> operatorsMenu)
    {
        this.operatorsMenu = operatorsMenu;
    }

    /**
     * @return the errorQuery
     */
    public boolean getErrorQuery()
    {
        return errorQuery;
    }

    /**
     * @param errorQuery the errorQuery to set
     */
    public void setErrorQuery(boolean errorQuery)
    {
        this.errorQuery = errorQuery;
    }
}
