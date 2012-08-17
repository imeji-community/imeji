/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.mdProfile;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.collection.CollectionBean.TabType;
import de.mpg.imeji.presentation.collection.CollectionSessionBean;
import de.mpg.imeji.presentation.mdProfile.wrapper.StatementWrapper;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;
import de.mpg.imeji.presentation.util.UrlHelper;
import de.mpg.j2j.misc.LocalizedString;

public class MdProfileBean
{
    private MetadataProfile profile = null;
    private TabType tab = TabType.PROFILE;
    private CollectionSessionBean collectionSession = null;
    private List<StatementWrapper> statements = null;
    private List<SelectItem> mdTypesMenu = null;
    private String id = null;
    private List<SelectItem> profilesMenu = null;
    private SessionBean sessionBean;
    private String template;
    private ProfileController pc;
    private int statementPosition = 0;
    private int constraintPosition = 0;
    private int labelPosition = 0;

    public MdProfileBean()
    {
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (collectionSession.getProfile() == null)
        {
            collectionSession.setProfile(new MetadataProfile());
        }
        profile = collectionSession.getProfile();
        statements = new ArrayList<StatementWrapper>();
        pc = new ProfileController(sessionBean.getUser());
        initMenus();
    }

    public MdProfileBean(MetadataProfile profile)
    {
        this();
        this.profile = profile;
    }

    public void initMenus()
    {
        mdTypesMenu = new ArrayList<SelectItem>();
        mdTypesMenu.add(new SelectItem(null, "--"));
        for (Metadata.Types t : Metadata.Types.values())
        {
            mdTypesMenu.add(new SelectItem(t.getClazzNamespace(), ((SessionBean)BeanHelper
                    .getSessionBean(SessionBean.class)).getLabel("facet_" + t.name().toLowerCase())));
        }
    }

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
            initBeanObjects(profile);
        }
        return "";
    }

    public void reset()
    {
        profile.getStatements().clear();
        statements.clear();
        collectionSession.setProfile(profile);
    }

    public void initBeanObjects(MetadataProfile mdp)
    {
        statements.clear();
        for (Statement st : mdp.getStatements())
        {
            statements.add(new StatementWrapper(st, mdp.getId()));
        }
    }

    public void loadtemplates()
    {
        profilesMenu = new ArrayList<SelectItem>();
        profilesMenu.add(new SelectItem(null, "Select Template"));
        for (MetadataProfile mdp : pc.search())
        {
            if (mdp.getId().toString() != profile.getId().toString())
            {
                profilesMenu.add(new SelectItem(mdp.getId().toString(), mdp.getTitle()));
            }
        }
    }

    public String changeTemplate() throws Exception
    {
        MetadataProfile tp = ObjectCachedLoader.loadProfile(URI.create(this.template));
        profile.getStatements().clear();
        profile.setStatements(tp.getStatements());
        for (Statement s : profile.getStatements())
        {
            s.setId(URI.create(s.getId().toString().replace(tp.getId().toString(), profile.getId().toString())));
        }
        collectionSession.setProfile(profile);
        initBeanObjects(profile);
        return getNavigationString();
    }

    public void templateListener(ValueChangeEvent event) throws Exception
    {
        if (event != null && event.getNewValue() != event.getOldValue())
        {
            this.template = event.getNewValue().toString();
            MetadataProfile tp = ObjectCachedLoader.loadProfile(URI.create(this.template));
            profile.getStatements().clear();
            profile.setStatements(tp.getStatements());
            collectionSession.setProfile(profile);
            initBeanObjects(profile);
        }
    }

    public void parseID()
    {
        if (this.getId() == null && this.getProfile().getId() != null)
        {
            this.setId(this.getProfile().getId().getPath().split("/")[2]);
        }
    }

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
        return statements.size();
    }

    public void addVocabulary()
    {
        statements.get(getStatementPosition()).setVocabularyString("--");
    }

    public void removeVocabulary()
    {
        statements.get(getStatementPosition()).setVocabularyString(null);
    }

    public void addStatement()
    {
        if (statements.isEmpty())
        {
            statements.add(new StatementWrapper(ImejiFactory.newStatement(), profile.getId()));
        }
        else
        {
            statements.add(getStatementPosition() + 1,
                    new StatementWrapper(ImejiFactory.newStatement(), profile.getId()));
        }
    }

    public void removeStatement()
    {
        statements.remove(getStatementPosition());
    }

    public void addLabel()
    {
        statements.get(getStatementPosition()).getLabels().add(new LocalizedString("", ""));
    }

    public void removeLabel()
    {
        statements.get(getStatementPosition()).getLabels().remove(getLabelPosition());
    }

    public void addConstraint()
    {
        Statement st = ((List<StatementWrapper>)statements).get(getStatementPosition()).getAsStatement();
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

    public void removeConstraint()
    {
        Statement st = ((List<StatementWrapper>)statements).get(getStatementPosition()).getAsStatement();
        ((List<String>)st.getLiteralConstraints()).remove(getConstraintPosition());
        collectionSession.setProfile(profile);
    }

    public int getConstraintPosition()
    {
        return constraintPosition;
    }

    public void setConstraintPosition(int constraintPosition)
    {
        this.constraintPosition = constraintPosition;
    }

    public MetadataProfile getProfile()
    {
        return profile;
    }

    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }

    public TabType getTab()
    {
        return tab;
    }

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

    public List<Statement> getUnwrappedStatements()
    {
        List<Statement> l = new ArrayList<Statement>();
        for (StatementWrapper w : getStatements())
        {
            l.add(w.getAsStatement());
        }
        return l;
    }

    public List<StatementWrapper> getStatements()
    {
        return statements;
    }

    public void setStatements(List<StatementWrapper> statements)
    {
        this.statements = statements;
    }

    public List<SelectItem> getMdTypesMenu()
    {
        return mdTypesMenu;
    }

    public void setMdTypesMenu(List<SelectItem> mdTypesMenu)
    {
        this.mdTypesMenu = mdTypesMenu;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public List<SelectItem> getProfilesMenu()
    {
        return profilesMenu;
    }

    public void setProfilesMenu(List<SelectItem> profilesMenu)
    {
        this.profilesMenu = profilesMenu;
    }

    public String getTemplate()
    {
        return template;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

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
            else if (statementNames.contains(s.getId()))
            {
                BeanHelper.error(sessionBean.getMessage("error_profile_name_not_unique"));
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
            // if (s.getId() != null && s.getId().toString().equals(profile.getId().toString() + "/"))
            // {
            // s.setId(URI.create(profile.getId().toString() + "/"
            // + ((List<LocalizedString>)s.getLabels()).get(0).toString()));
            // }
            i++;
        }
        return true;
    }

    public boolean isEditable()
    {
        Security security = new Security();
        return security.check(OperationsType.UPDATE, sessionBean.getUser(), profile);
    }

    public boolean isVisible()
    {
        Security security = new Security();
        return security.check(OperationsType.READ, sessionBean.getUser(), profile);
    }

    public boolean isDeletable()
    {
        Security security = new Security();
        return security.check(OperationsType.DELETE, sessionBean.getUser(), profile);
    }

    public int getLabelPosition()
    {
        return labelPosition;
    }

    public void setLabelPosition(int labelPosition)
    {
        this.labelPosition = labelPosition;
    }
}
