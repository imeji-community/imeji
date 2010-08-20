//package de.mpg.imeji.mdProfile;
//
//import javax.faces.event.ValueChangeEvent;
//
//import de.mpg.imeji.vo.StatementVO;
//
//public class StatementBean
//{
//    private StatementVO statement = null;
//    private int constraintPosition = 0;
//    
//    public StatementBean(StatementVO st)
//    {
//        this.statement = st;
//    }
//    
//    public void valueListener(ValueChangeEvent event)
//    {
//        if (event != null && event.getOldValue() != event.getNewValue())
//        {
//            statement.setValue(event.getNewValue().toString());
//        }
//    }
//    
//    public void requiredListener(ValueChangeEvent event)
//    {
//        if (event != null && event.getOldValue() != event.getNewValue())
//        {
//            statement.setRequired(Boolean.getBoolean(event.getNewValue().toString()));
//        }
//    }
//
//    public void multipleListener(ValueChangeEvent event)
//    {
//        if (event != null && event.getOldValue() != event.getNewValue())
//        {
//            statement.setMultiple(Boolean.getBoolean(event.getNewValue().toString()));
//        }
//    }
//
//    public int getConstraintsSize()
//    {
//        return statement.getConstraints().size();
//    }
//
//    public String addConstraint()
//    {
//        if (getConstraintPosition() == 0)
//        {
//            statement.getConstraints().add("");
//        }
//        else
//        {
//            statement.getConstraints().add(getConstraintPosition() + 1, "");
//        }
//        return "";
//    }
//
//    public String removeConstraint()
//    {
//        statement.getConstraints().remove(getConstraintPosition());
//        return "";
//    }
//
//    /**
//     * @return the statement
//     */
//    public StatementVO getStatement()
//    {
//        return statement;
//    }
//
//    /**
//     * @param statement the statement to set
//     */
//    public void setStatement(StatementVO statement)
//    {
//        this.statement = statement;
//    }
//    
//    public void setConstraintPosition(int constraintPosition)
//    {
//        this.constraintPosition = constraintPosition;
//    }
//
//    public int getConstraintPosition()
//    {
//        return constraintPosition;
//    }
//
//    
//}
