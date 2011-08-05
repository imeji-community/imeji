package de.mpg.imeji.mdProfile;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionBean.TabType;
import de.mpg.imeji.collection.CollectionSessionBean;
import de.mpg.imeji.mdProfile.wrapper.StatementWrapper;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.imeji.util.LocalizedStringHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

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
    private int labelPosition=0;

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
      	
      	for (ComplexTypes t : ComplexTypes.values())
    	{
    		mdTypesMenu.add(
    				new SelectItem(t.getURI()
    						, ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("facet_" + t.name().toLowerCase())));
    	}    
    }
    
    public String getInit()
    {
    	parseID();
    	
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
        MetadataProfile tp = pc.retrieve(URI.create(this.template));
        profile.getStatements().clear();
        profile.setStatements(tp.getStatements());
        collectionSession.setProfile(profile);
        initBeanObjects(profile);
        return getNavigationString();
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
        if(profile != null && profile.getId() != null)
        {
        	return URLEncoder.encode(profile.getId().toString(), "UTF-8");
        }
        else return "";
    }

    protected String getNavigationString()
    {
        return "pretty:";
    }

    public String addVocabulary()
    {
    	statements.get(getStatementPosition()).setVocabularyString("--");
        return getNavigationString();
    }

    public String removeVocabulary()
    {
    	statements.get(getStatementPosition()).setVocabularyString(null);
        return getNavigationString();
    }

    public String addStatement()
    {
    	if (statements.isEmpty())
    	{
    		statements.add(new StatementWrapper(ImejiFactory.newStatement(), profile.getId()));
    	}
    	else
    	{
    		statements.add(getStatementPosition() + 1, new StatementWrapper(ImejiFactory.newStatement(), profile.getId()));
    	}
        return getNavigationString();
    }

    public String removeStatement()
    {
    	statements.remove(getStatementPosition());
        return getNavigationString();
    }
    
    public String addLabel()
    {
    	statements.get(getStatementPosition()).getLabels().add(new LocalizedStringHelper(new LocalizedString("", "eng")));
    	return  getNavigationString();
    }
    
    public String removeLabel()
    {
    	statements.get(getStatementPosition()).getLabels().remove(getLabelPosition());
    	return  getNavigationString();
    }

    public String addConstraint()
    {
        Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
        if (getConstraintPosition() >= st.getLiteralConstraints().size())
            ((List<LocalizedString>)st.getLiteralConstraints()).add(new LocalizedString("", "eng"));
        else
            ((List<LocalizedString>)st.getLiteralConstraints()).add(getConstraintPosition() + 1, new LocalizedString("","eng"));
        collectionSession.setProfile(profile);
        return getNavigationString();
    }

    public String removeConstraint()
    {
        Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
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

    public boolean validateProfile(MetadataProfile profile)
    {
        List<String> statementNames = new ArrayList<String>();
        if (profile.getStatements() == null)
        {
        	BeanHelper.error("Empty profile");
        	return false;
        }
        int i=0;
        
        for (StatementWrapper wrapper : statements)
        {
        	Statement s = wrapper.getAsStatement();
        	
        	if (s.getType() == null)
        	{
        		BeanHelper.error("Please select a type for each metadata.");
            	return false;
        	}
        	else if(s.getName() == null || !s.getName().isAbsolute())
            {
            	BeanHelper.error(s.getName()+" is not a valid name!");
            	return false;
            }
            else if (statementNames.contains(s.getName()))
            {
                BeanHelper.error("Names must be unique!");
                return false;
            }
            else if (s.getName() == null || s.getName().toString().equals(profile.getId().toString() + "/"))
            {
                BeanHelper.error("Names are required!");
                return false;
            }
            else
            {
                statementNames.add(s.getName().toString());
            }
        	s.setPos(i);
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


	public int getLabelPosition() {
		return labelPosition;
	}


	public void setLabelPosition(int labelPosition) {
		this.labelPosition = labelPosition;
	}
	
	
}
