package de.mpg.imeji.mdProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import thewebsemantic.LocalizedString;

import de.mpg.imeji.collection.CollectionSessionBean;
import de.mpg.imeji.collection.CollectionBean.TabType;
import de.mpg.imeji.mdProfile.wrapper.StatementWrapper;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.md.ComplexType;

public class MdProfileBean
{
    private MetadataProfile profile = null;
    private int statementPosition = 0;
    private TabType tab = TabType.PROFILE;
    private CollectionSessionBean collectionSession = null;
    private int constraintPosition;
    private List<StatementWrapper> statements = null;
    private List<SelectItem> mdTypesMenu = null;

    public MdProfileBean()
    {
        profile = new MetadataProfile();
        statements = new ArrayList<StatementWrapper>();
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
        mdTypesMenu = new ArrayList<SelectItem>();
        for (ComplexType mdt : collectionSession.getMetadataTypes())
        {
            mdTypesMenu.add(new SelectItem(mdt.getType(), mdt.getType().getLabel()));
        }
    }

    public void reset()
    {
        profile.setDescription("");
        profile.setTitle("");
        profile.getStatements().clear();
        collectionSession.getActive().setProfile(profile);
    }

    public String init()
    {
        if (collectionSession.getActive() == null)
        {
            collectionSession.setActive(ImejiFactory.newCollection());
        }
        profile = collectionSession.getActive().getProfile();
        for (Statement st : profile.getStatements())
        {
            statements.add(new StatementWrapper(st));
        }
        return "";
    }

    public String addStatement()
    {
        Statement st = ImejiFactory.newStatement();
        if (getStatementPosition() == 0)
        {
            profile.getStatements().add(st);
        }
        else
        {
            ((List<Statement>)profile.getStatements()).add(getStatementPosition() + 1, st);
        }
        collectionSession.getActive().setProfile(profile);
        return "pretty:createProfile";
    }

    public String removeStatement()
    {
        statements.remove(getStatementPosition());
        collectionSession.getActive().setProfile(profile);
        return "pretty:createProfile";
    }

    public String addConstraint()
    {
        Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
        if (getConstraintPosition() == 0)
        {
            ((List<LocalizedString>)st.getLiteralConstraints()).add(new LocalizedString("", ""));
        }
        else
        {
            ((List<LocalizedString>)st.getLiteralConstraints()).add(getConstraintPosition(),
                    new LocalizedString("", ""));
        }
        collectionSession.getActive().setProfile(profile);
        return "pretty:createProfile";
    }

    public String removeConstraint()
    {
        Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
        if (getConstraintPosition() != 0)
            ((List<LocalizedString>)st.getLiteralConstraints()).remove(getConstraintPosition());
        collectionSession.getActive().setProfile(profile);
        return "pretty:createProfile";
    }

    public int getConstraintsSize()
    {
        Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
        return st.getLiteralConstraints().size();
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
    
}
