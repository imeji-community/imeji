/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.mdProfile;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.collection.CollectionBean.TabType;
import de.mpg.imeji.presentation.collection.CollectionSessionBean;
import de.mpg.imeji.presentation.mdProfile.wrapper.StatementWrapper;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.UrlHelper;
import de.mpg.j2j.misc.LocalizedString;

/**
 * Bean for {@link MetadataProfile} view pages
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class MdProfileBean
{
    private MetadataProfile profile = null;
    private TabType tab = TabType.PROFILE;
    private CollectionSessionBean collectionSession = null;
    private List<StatementWrapper> wrappers = null;
    private List<SelectItem> mdTypesMenu = null;
    private String id = null;
    private List<SelectItem> profilesMenu = null;
    private SessionBean sessionBean;
    private String template;
    private int statementPosition = 0;
    private int constraintPosition = 0;
    private int labelPosition = 0;

    /**
     * initialize a default {@link MdProfileBean}
     */
    public MdProfileBean()
    {
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (collectionSession.getProfile() == null)
        {
            collectionSession.setProfile(new MetadataProfile());
        }
        profile = collectionSession.getProfile();
        wrappers = new ArrayList<StatementWrapper>();
        initMenus();
    }

    /**
     * Method called on the html page to trigger the initialization of the bean
     * 
     * @return
     */
    public String getInit()
    {
        parseID();
        initMenus();
        if (UrlHelper.getParameterBoolean("reset"))
        {
            reset();
        }
        if (UrlHelper.getParameterBoolean("init"))
        {
            loadtemplates();
            initStatementWrappers(profile);
        }
        return "";
    }

    /**
     * Initialize the menus of the page
     */
    public void initMenus()
    {
        mdTypesMenu = new ArrayList<SelectItem>();
        mdTypesMenu.add(new SelectItem(null, sessionBean.getLabel("select")));
        for (Metadata.Types t : Metadata.Types.values())
        {
            mdTypesMenu.add(new SelectItem(t.getClazzNamespace(), ((SessionBean)BeanHelper
                    .getSessionBean(SessionBean.class)).getLabel("facet_" + t.name().toLowerCase())));
        }
    }

    /**
     * Reset to an empty {@link MetadataProfile}
     */
    public void reset()
    {
        profile.getStatements().clear();
        wrappers.clear();
        collectionSession.setProfile(profile);
    }

    /**
     * Initialize the {@link StatementWrapper} {@link List}
     * 
     * @param mdp
     */
    private void initStatementWrappers(MetadataProfile mdp)
    {
        wrappers.clear();
        for (Statement st : mdp.getStatements())
        {
            wrappers.add(new StatementWrapper(st, mdp.getId()));
        }
    }

    /**
     * Comparator of {@link MetadataProfile} names, to sort a {@link List} of {@link MetadataProfile} according to their
     * name
     * 
     * @author saquet (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    static class profilesLabelComparator implements Comparator<Object>
    {
        @Override
        public int compare(Object o1, Object o2)
        {
            SelectItem profile1 = (SelectItem)o1;
            SelectItem profile2 = (SelectItem)o2;
            String profile1Label = profile1.getLabel();
            String profile1Labe2 = profile2.getLabel();
            return profile1Label.compareTo(profile1Labe2);
        }
    }

    /**
     * Load the templates (i.e. the {@link MetadataProfile} that can be used by the {@link User}), and add it the the
     * menu (sorted by name)
     */
    public void loadtemplates()
    {
        profilesMenu = new ArrayList<SelectItem>();
        try
        {
            ProfileController pc = new ProfileController();
            for (MetadataProfile mdp : pc.search(sessionBean.getUser()))
            {
                if (mdp.getId().toString().equals(profile.getId().toString()))
                {
                    profilesMenu.add(new SelectItem(mdp.getId().toString(), mdp.getTitle()));
                }
            }
            // sort profilesMenu
            Collections.sort(profilesMenu, new profilesLabelComparator());
            // add title to first position
            profilesMenu.add(0, new SelectItem(null, sessionBean.getLabel("profile_select_template")));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_profile_template_load"));
        }
    }

    /**
     * Change the template, when the user select one
     * 
     * @return
     * @throws Exception
     */
    public String changeTemplate() throws Exception
    {
        profile.getStatements().clear();
        MetadataProfile tp = ObjectLoader.loadProfile(URI.create(this.template), sessionBean.getUser());
        if (!tp.getStatements().isEmpty())
        {
            profile.setStatements(tp.getStatements());
        }
        else
        {
            profile.getStatements().add(ImejiFactory.newStatement());
        }
        for (Statement s : profile.getStatements())
        {
            s.setId(URI.create("http://imeji.org/statement/" + UUID.randomUUID()));
        }
        collectionSession.setProfile(profile);
        initStatementWrappers(profile);
        return getNavigationString();
    }

    /**
     * Check all profile elements, and return true if all are valid. Error messages are logged for the user to help him
     * to find why is profile not valid
     * 
     * @param profile
     * @return
     */
    public boolean validateProfile(MetadataProfile profile)
    {
        List<String> statementNames = new ArrayList<String>();
        if (profile.getStatements() == null)
        {
            BeanHelper.error(sessionBean.getLabel("profile_empty"));
            return false;
        }
        int i = 0;
        for (Statement s : profile.getStatements())
        {
            for (LocalizedString ls : s.getLabels())
            {
                if (ls.getLang() == null)
                {
                    BeanHelper.error(sessionBean.getMessage("error_profile_label_no_lang"));
                    return false;
                }
            }
            if (s.getType() == null)
            {
                BeanHelper.error(sessionBean.getMessage("error_profile_select_metadata_type"));
                return false;
            }
            else if (s.getId() == null || !s.getId().isAbsolute())
            {
                BeanHelper.error(s.getId() + " " + sessionBean.getMessage("error_profile_name_not_valid"));
                return false;
            }
            else if (s.getLabels().isEmpty() || "".equals(((List<LocalizedString>)s.getLabels()).get(0).toString()))
            {
                BeanHelper.error(sessionBean.getMessage("error_profile_name_required"));
                return false;
            }
            else
            {
                statementNames.add(s.getId().toString());
            }
            s.setPos(i);
            i++;
        }
        return true;
    }

    /**
     * Listener for the template value
     * 
     * @param event
     * @throws Exception
     */
    public void templateListener(ValueChangeEvent event) throws Exception
    {
        if (event != null && event.getNewValue() != event.getOldValue())
        {
            this.template = event.getNewValue().toString();
            MetadataProfile tp = ObjectCachedLoader.loadProfile(URI.create(this.template));
            profile.getStatements().clear();
            profile.setStatements(tp.getStatements());
            collectionSession.setProfile(profile);
            initStatementWrappers(profile);
        }
    }

    /**
     * Parse the id defined in the url
     */
    public void parseID()
    {
        if (this.getId() == null && this.getProfile().getId() != null)
        {
            this.setId(this.getProfile().getId().getPath().split("/")[2]);
        }
    }

    /**
     * Return the id of the profile encoded in utf-8
     * 
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getEncodedId() throws UnsupportedEncodingException
    {
        if (profile != null && profile.getId() != null)
        {
            return URLEncoder.encode(profile.getId().toString(), "UTF-8");
        }
        else
            return "";
    }


    protected String getNavigationString()
    {
        return "pretty:";
    }

    public int getSize()
    {
        return wrappers.size();
    }

    /**
     * Move a statement up in statement list
     */
    public void moveUp()
    {
        Collections.swap(wrappers, getStatementPosition(), getStatementPosition() + 1);
    }

    /**
     * Move a statement down in statement list
     */
    public void moveDown()
    {
        Collections.swap(wrappers, getStatementPosition() + 1, getStatementPosition());
    }

    /**
     * add a vocabulary according to the position of the clicked button
     */
    public void addVocabulary()
    {
        wrappers.get(getStatementPosition()).setVocabularyString("--");
    }

    /**
     * remove a vocabulary
     */
    public void removeVocabulary()
    {
        wrappers.get(getStatementPosition()).setVocabularyString(null);
    }

    /**
     * Called by add statement button. Add a new statement to the profile. The position of the new statement is defined
     * by the button position
     */
    public void addStatement()
    {
        if (wrappers.isEmpty())
        {
            wrappers.add(new StatementWrapper(ImejiFactory.newStatement(), profile.getId()));
        }
        else
        {
            wrappers.add(getStatementPosition() + 1, new StatementWrapper(ImejiFactory.newStatement(), profile.getId()));
        }
    }

    /**
     * Called by remove statement button. If the statement is not used by an imeji item, remove it, according to the
     * position of the button. If the statement is used, display a warning message in a panel
     */
    public void removeStatement()
    {
        if (!wrappers.get(getStatementPosition()).isUsedByAtLeastOnItem())
        {
            wrappers.remove(getStatementPosition());
        }
        else
        {
            wrappers.get(getStatementPosition()).setShowRemoveWarning(true);
        }
    }

    /**
     * Remove a statement even if it is used by a an item
     */
    public void forceRemoveStatement()
    {
        wrappers.remove(getStatementPosition());
    }

    /**
     * Close the panel with warning information
     */
    public void closeRemoveWarning()
    {
        wrappers.get(getStatementPosition()).setShowRemoveWarning(false);
    }

    /**
     * called by add label button
     */
    public void addLabel()
    {
        wrappers.get(getStatementPosition()).getStatement().getLabels().add(new LocalizedString("", ""));
    }

    /**
     * Called by remove label button
     */
    public void removeLabel()
    {
        ((List<LocalizedString>)wrappers.get(getStatementPosition()).getStatement().getLabels())
                .remove(getLabelPosition());
    }

    /**
     * Called by add constraint button
     */
    public void addConstraint()
    {
        Statement st = wrappers.get(getStatementPosition()).getAsStatement();
        if (getConstraintPosition() >= st.getLiteralConstraints().size())
        {
            ((List<String>)st.getLiteralConstraints()).add("");
        }
        else
        {
            ((List<String>)st.getLiteralConstraints()).add(getConstraintPosition() + 1, "");
        }
        collectionSession.setProfile(profile);
    }

    /**
     * Called by remove constraint button
     */
    public void removeConstraint()
    {
        Statement st = wrappers.get(getStatementPosition()).getAsStatement();
        ((List<String>)st.getLiteralConstraints()).remove(getConstraintPosition());
        collectionSession.setProfile(profile);
    }

    /**
     * getter
     * 
     * @return
     */
    public int getConstraintPosition()
    {
        return constraintPosition;
    }

    /**
     * setter
     * 
     * @param constraintPosition
     */
    public void setConstraintPosition(int constraintPosition)
    {
        this.constraintPosition = constraintPosition;
    }

    /**
     * getter
     * 
     * @return
     */
    public MetadataProfile getProfile()
    {
        return profile;
    }

    /**
     * setter
     * 
     * @param profile
     */
    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }

    /**
     * getter
     * 
     * @return
     */
    public TabType getTab()
    {
        return tab;
    }

    /**
     * setter
     * 
     * @param tab
     */
    public void setTab(TabType tab)
    {
        this.tab = tab;
    }

    /**
     * @return the statementPosition
     */
    public int getStatementPosition()
    {
        return statementPosition;
    }

    /**
     * @param statementPosition the statementPosition to set
     */
    public void setStatementPosition(int statementPosition)
    {
        this.statementPosition = statementPosition;
    }

    /**
     * return the {@link List} of {@link StatementWrapper} as a {@link List} of {@link Statement}
     * 
     * @return
     */
    public List<Statement> getUnwrappedStatements()
    {
        List<Statement> l = new ArrayList<Statement>();
        for (StatementWrapper w : getWrappers())
        {
            l.add(w.getAsStatement());
        }
        return l;
    }

    /**
     * getter
     * 
     * @return
     */
    public List<StatementWrapper> getWrappers()
    {
        return wrappers;
    }

    /**
     * setter
     * 
     * @param statements
     */
    public void setWrappers(List<StatementWrapper> wrappers)
    {
        this.wrappers = wrappers;
    }

    /**
     * getter
     * 
     * @return
     */
    public List<SelectItem> getMdTypesMenu()
    {
        return mdTypesMenu;
    }

    /**
     * setter
     * 
     * @param mdTypesMenu
     */
    public void setMdTypesMenu(List<SelectItem> mdTypesMenu)
    {
        this.mdTypesMenu = mdTypesMenu;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getId()
    {
        return id;
    }

    /**
     * setter
     * 
     * @param id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * getter
     * 
     * @return
     */
    public List<SelectItem> getProfilesMenu()
    {
        return profilesMenu;
    }

    /**
     * setter
     * 
     * @param profilesMenu
     */
    public void setProfilesMenu(List<SelectItem> profilesMenu)
    {
        this.profilesMenu = profilesMenu;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getTemplate()
    {
        return template;
    }

    /**
     * setter
     * 
     * @param template
     */
    public void setTemplate(String template)
    {
        this.template = template;
    }

    /**
     * getter
     * 
     * @return
     */
    public boolean isEditable()
    {
        Security security = new Security();
        return security.check(OperationsType.UPDATE, sessionBean.getUser(), profile);
    }

    /**
     * setter
     * 
     * @return
     */
    public boolean isVisible()
    {
        Security security = new Security();
        return security.check(OperationsType.READ, sessionBean.getUser(), profile);
    }

    /**
     * getter
     * 
     * @return
     */
    public boolean isDeletable()
    {
        Security security = new Security();
        return security.check(OperationsType.DELETE, sessionBean.getUser(), profile);
    }

    /**
     * getter
     * 
     * @return
     */
    public int getLabelPosition()
    {
        return labelPosition;
    }

    /**
     * setter
     * 
     * @param labelPosition
     */
    public void setLabelPosition(int labelPosition)
    {
        this.labelPosition = labelPosition;
    }
}
