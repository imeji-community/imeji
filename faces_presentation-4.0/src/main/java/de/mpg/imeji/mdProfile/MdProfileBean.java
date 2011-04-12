package de.mpg.imeji.mdProfile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.richfaces.json.JSONCollection;
import org.richfaces.json.JSONException;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionBean.TabType;
import de.mpg.imeji.collection.CollectionSessionBean;
import de.mpg.imeji.mdProfile.wrapper.StatementWrapper;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
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
    }
       

    public MdProfileBean(MetadataProfile profile)
    {
        this();
    	this.profile = profile;
    }

    public void reset()
    {
        profile.getStatements().clear();
        statements.clear();
        collectionSession.setProfile(profile);
    }
    
    public String getInit()
    {
    	mdTypesMenu = new ArrayList<SelectItem>();
    	for (ComplexTypes t : ComplexTypes.values())
    	{
    		mdTypesMenu.add(new SelectItem(t.getURI(), t.name()));
    	}    
        if (this.getId() == null && this.getProfile().getId() != null)
        {
        	this.setId(this.getProfile().getId().getPath().split("/")[2]);
        }
    	if (UrlHelper.getParameterBoolean("reset")) reset();
        loadtemplates();
        setStatementWrappers(profile);
        return "";
    }

    public void setStatementWrappers(MetadataProfile mdp)
    {
        statements.clear();
        for (Statement st : mdp.getStatements())
        {
        	statements.add(new StatementWrapper(st, mdp.getId()));
        }
    }

    public void loadtemplates()
    {
        profilesMenu.add(new SelectItem(null, "Select Template"));
//        for (MetadataProfile mdp : pc.retrieveAll())
//        {
//            if (mdp.getId().toString() != profile.getId().toString())
//            {
//                profilesMenu.add(new SelectItem(mdp.getId().toString(), mdp.getTitle()));
//            }
//        }
    }

    public String changeTemplate() throws Exception
    {
        MetadataProfile tp = pc.retrieve(URI.create(this.template));
        profile.getStatements().clear();
        profile.setStatements(tp.getStatements());
        collectionSession.setProfile(profile);
        setStatementWrappers(profile);
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
            setStatementWrappers(profile);
        }
    }

    public String getEncodedId() throws UnsupportedEncodingException
    {
        if(profile != null && profile.getId() != null)
            return URLEncoder.encode(profile.getId().toString(), "UTF-8");
        else return "";
    }

    protected String getNavigationString()
    {
        return "pretty:";
    }

    public boolean isVocabulary(String uri)
    {
        try
        {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(uri + "?format=json&n=2&m=full&q=e");
            client.executeMethod(method);
            if (HttpServletResponse.SC_OK != method.getStatusCode())
                throw new HttpException();
            new JSONCollection(method.getResponseBodyAsString());
            return true;
        }
        catch (JSONException e)
        {
            BeanHelper.error(uri + " is not a valid JSON source");
        }
        catch (HttpException e)
        {
            BeanHelper.error(uri + " is not a valid URL");
        }
        catch (IOException e)
        {
            BeanHelper.error(uri + " is not a valid URL");
        }
        return false;
    }

    public String checkVocabulary()
    {
        Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
        if (isVocabulary(st.getVocabulary().toString()))
            BeanHelper.info(st.getVocabulary().toString() + " is valid");
        return "pretty:";
    }

    public String addVocabulary() throws URISyntaxException
    {
        Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
        st.setVocabulary(new URI(ProfileHelper.getDefaultVocabulary(st.getType())));
        collectionSession.setProfile(profile);
        return getNavigationString();
    }

    public String removeVocabulary() throws URISyntaxException
    {
        Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
        st.setVocabulary(null);
        collectionSession.setProfile(profile);
        return getNavigationString();
    }

    public String addStatement()
    {
        Statement st = ImejiFactory.newStatement();
        if (profile.getStatements().size() == 0)
        {
            profile.getStatements().add(st);
        }
        else
        {
            st.setPos(getStatementPosition() + 1);
            ((List<Statement>)profile.getStatements()).add(getStatementPosition() + 1, st);
        }
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
        if (getConstraintPosition() == 0 || getConstraintPosition() >= st.getLiteralConstraints().size())
            ((List<LocalizedString>)st.getLiteralConstraints()).add(new LocalizedString("", "eng"));
        else
            ((List<LocalizedString>)st.getLiteralConstraints()).add(getConstraintPosition(), new LocalizedString("","eng"));
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
        for (Statement s : profile.getStatements())
        {
        	if(s.getName() == null || !s.getName().isAbsolute())
            {
            	BeanHelper.error(s.getName()+" is not a valid name!");
            	return false;
            }
            else if (statementNames.contains(s.getName()))
            {
                BeanHelper.error("Names must be unique!");
                return false;
            }
            else if (s.getName() == null || s.getName().equals(""))
            {
                BeanHelper.error("Names are required!");
                return false;
            }
            else if (s.getVocabulary() != null && !isVocabulary(s.getVocabulary().toString()))
            {
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
}
