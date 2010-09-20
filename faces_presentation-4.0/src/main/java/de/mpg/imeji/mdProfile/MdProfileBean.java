package de.mpg.imeji.mdProfile;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionSessionBean;
import de.mpg.imeji.collection.CollectionBean.TabType;
import de.mpg.imeji.mdProfile.wrapper.StatementWrapper;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class MdProfileBean
{
    private MetadataProfile profile = null;
    private int statementPosition = 0;
    private TabType tab = TabType.PROFILE;
    private CollectionSessionBean collectionSession = null;
    private int constraintPosition;
    private List<StatementWrapper> statements = null;
    private List<SelectItem> mdTypesMenu = null;
    private String id = null;
    private List<SelectItem> profilesMenu = null;
    private SessionBean sessionBean;
    private String template;
    private ProfileController pc;

    public MdProfileBean()
    {
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        pc = new ProfileController(sessionBean.getUser());
        if (collectionSession.getProfile() == null)
            collectionSession.setProfile(new MetadataProfile());
        profile = collectionSession.getProfile();
        profilesMenu = new ArrayList<SelectItem>();
        statements = new ArrayList<StatementWrapper>();
        mdTypesMenu = new ArrayList<SelectItem>();
        for (ComplexType mdt : collectionSession.getMetadataTypes())
            mdTypesMenu.add(new SelectItem(mdt.getEnumType().name(), mdt.getEnumType().getLabel()));
        if (this.getId() == null && this.getProfile().getId() != null)
            this.setId(this.getProfile().getId().getPath().split("/")[2]);
    }
    
    public MdProfileBean(MetadataProfile profile)
    {
        this.profile = profile;
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        pc = new ProfileController(sessionBean.getUser());
        profilesMenu = new ArrayList<SelectItem>();
        statements = new ArrayList<StatementWrapper>();
        mdTypesMenu = new ArrayList<SelectItem>();
        for (ComplexType mdt : collectionSession.getMetadataTypes())
            mdTypesMenu.add(new SelectItem(mdt.getEnumType().name(), mdt.getEnumType().getLabel()));
        if (this.getId() == null && this.getProfile().getId() != null)
            this.setId(this.getProfile().getId().getPath().split("/")[2]);
    }

    public void reset()
    {
        profile.getStatements().clear();
        statements.clear();
        collectionSession.setProfile(profile);
    }

    public void init()
    {
        if (UrlHelper.getParameterBoolean("reset")) 
            reset();
        loadtemplates();
        setStatementWrappers(profile);
    }

    public void setStatementWrappers(MetadataProfile mdp)
    {
        statements.clear();
        for (Statement st : mdp.getStatements())
            statements.add(new StatementWrapper(st));
    }

    public void loadtemplates()
    {
        profilesMenu.add(new SelectItem(null, "Select Template"));
        for (MetadataProfile mdp : pc.retrieveAll())
        {
            if (mdp.getId().toString() != profile.getId().toString())
                profilesMenu.add(new SelectItem(mdp.getId().toString(), mdp.getTitle()));
        }
    }

    public void templateListener(ValueChangeEvent event) throws Exception
    {
        if (event != null && event.getNewValue() != event.getOldValue())
        {
            this.template = event.getNewValue().toString();
            MetadataProfile tp = pc.retrieve(URI.create(this.template));
            profile.getStatements().clear();
            profile.setStatements(tp.getStatements());
            collectionSession.setProfile(profile);
            setStatementWrappers(profile);
        }
    }

    protected String getNavigationString()
    {
        return "pretty:";
    }

    public String addStatement()
    {
        Statement st = ImejiFactory.newStatement();
        if (getStatementPosition() == 0)
            profile.getStatements().add(st);
        else
            ((List<Statement>)profile.getStatements()).add(getStatementPosition() + 1, st);
        collectionSession.setProfile(profile);
        return getNavigationString();
    }

    public String removeStatement()
    {
        ((List<Statement>)profile.getStatements()).remove(getStatementPosition());
        collectionSession.setProfile(profile);
        return getNavigationString();
    }

    public String addConstraint()
    {
        Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
        if (getConstraintPosition() == 0)
        {
            ((List<LocalizedString>)st.getLiteralConstraints()).add(new LocalizedString("", "eng"));
        }
        else
        {
            ((List<LocalizedString>)st.getLiteralConstraints()).add(getConstraintPosition(), new LocalizedString("",
                    "eng"));
        }
        collectionSession.setProfile(profile);
        return getNavigationString();
    }

    public String removeConstraint()
    {
        Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
        if (getConstraintPosition() != 0)
            ((List<LocalizedString>)st.getLiteralConstraints()).remove(getConstraintPosition());
        collectionSession.setProfile(profile);
        return getNavigationString();
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
    
    public boolean validateProfile()
    {
        List<String> statementNames = new ArrayList<String>();
        for (Statement s : profile.getStatements())
        {
            if(statementNames.contains(s.getName()))
            {
                BeanHelper.error("Names must be unique!");
                return false;
                
            }
            else if(s.getName()==null || s.getName().equals(""))
            {
                BeanHelper.error("Names are required!");
                return false;
            }
            else
            {
                statementNames.add(s.getName());
            }
            
        }
        return true;
    }
}
