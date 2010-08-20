package de.mpg.imeji.mdProfile;

import java.util.List;

import javax.faces.event.ValueChangeEvent;

import thewebsemantic.LocalizedString;

import de.mpg.imeji.collection.CollectionSessionBean;
import de.mpg.imeji.collection.CollectionBean.TabType;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class MdProfileBean
{
    private MetadataProfile profile = null;
    private int statementPosition = 0;
    private TabType tab = TabType.PROFILE;
    private CollectionSessionBean collectionSession = null;
    private int constraintPosition;

    public MdProfileBean()
    {
        profile = new MetadataProfile();
        Statement s;
    }

    public void init()
    {
    }

    public String addStatement()
    {
        Statement st = ImejiFactory.newStatement();
        if (getStatementPosition() == 0)
        {
            ((List<Statement>)profile.getStatements()).add(st);
        }
        else
        {
            ((List<Statement>)profile.getStatements()).add(getStatementPosition() + 1, st);
        }
        return "";
    }

    public String removeStatement()
    {
        ((List<Statement>)profile.getStatements()).remove(getStatementPosition());
        return "";
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
        return "";
    }

    public String removeConstraint()
    {
        Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
        ((List<LocalizedString>)st.getLiteralConstraints()).remove(getConstraintPosition());
        return "";
    }

    public void requiredListener(ValueChangeEvent event)
    {
        if (event != null && event.getOldValue() != event.getNewValue())
        {
            Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
            if (Boolean.getBoolean(event.getNewValue().toString()))
            {
                st.setMinOccurs("1");
            }
            else
            {
                st.setMinOccurs("0");
            }
        }
    }

    public void multipleListener(ValueChangeEvent event)
    {
        if (event != null && event.getOldValue() != event.getNewValue())
        {
            Statement st = ((List<Statement>)profile.getStatements()).get(getStatementPosition());
            
            if (Boolean.getBoolean(event.getNewValue().toString()))
            {
                st.setMaxOccurs("unbounded");
            }
            else
            {
                st.setMaxOccurs("1");
            }
        }
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
}
