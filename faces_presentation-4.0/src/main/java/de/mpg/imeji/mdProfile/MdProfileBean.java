package de.mpg.imeji.mdProfile;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.collection.CollectionSessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.MdProfileVO;
import de.mpg.imeji.vo.StatementVO;

public class MdProfileBean
{
    private MdProfileVO mdProfile = null;
    private List<StatementBean> statements = null;
    private List<SelectItem> vocabulary = null;
    private int statementPosition = 0;
    
    private CollectionSessionBean collectionSession = null;

    public MdProfileBean()
    {
        mdProfile = new MdProfileVO();
        statements = new ArrayList<StatementBean>();
        collectionSession = (CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class);
    }

    public void init()
    {
        for (StatementVO st : collectionSession.getMdVocabulary())
        {
            vocabulary.add(new SelectItem(st, st.getLabel()));
        }
    }
    
    public String addStatement()
    {
        StatementVO st = new StatementVO();
        st.setName("");
        StatementBean stBean = new StatementBean(st);
        if (getStatementPosition() == 0)
        {
            statements.add(stBean);
        }
        else
        {
            statements.add(getStatementPosition() + 1, stBean);
        }
        return "";
    }
    
    public String removeStatement()
    {
        statements.remove(getStatementPosition());
        return "";
    }

    /**
     * @return the mdProfile
     */
    public MdProfileVO getMdProfile()
    {
        return mdProfile;
    }

    /**
     * @param mdProfile the mdProfile to set
     */
    public void setMdProfile(MdProfileVO mdProfile)
    {
        this.mdProfile = mdProfile;
    }

    /**
     * @return the metadataBeans
     */
    public List<StatementBean> getStatements()
    {
        return statements;
    }

    /**
     * @param metadataBeans the metadataBeans to set
     */
    public void setStatements(List<StatementBean> statements)
    {
        this.statements = statements;
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
