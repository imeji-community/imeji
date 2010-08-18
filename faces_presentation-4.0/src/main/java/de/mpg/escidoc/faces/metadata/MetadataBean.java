package de.mpg.escidoc.faces.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.util.BeanHelper;

/**
 * Bean for Metadata representation in MdProfile formular
 * 
 * @author saquet
 */
public class MetadataBean
{
    private static Logger logger = Logger.getLogger(MetadataBean.class);
    private Metadata current = null;
    private List<Metadata> metadataList = new ArrayList<Metadata>();
    private SessionBean sessionBean = null;
    private List<ConstraintBean> constraints = null;
    private int constraintPosition = 0;

    /**
     * Constructor for a {@link MetadataBean}
     * 
     * @param list
     */
    public MetadataBean(List<Metadata> list)
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        constraints = new ArrayList<ConstraintBean>();
        metadataList.addAll(list);
        if (current == null)
        {
            current = new Metadata(metadataList.get(0));
        }
        for (Metadata m : list)
        {
            for (String str : m.getConstraint())
            {
                constraints.add(new ConstraintBean(str));
            }
        }
    }

    public void menuListener(ValueChangeEvent event)
    {
        if (event != null && event.getOldValue() != event.getNewValue())
        {
            for (Metadata m : metadataList)
            {
                if (m.getIndex().equals(event.getNewValue().toString()))
                {
                    current = new Metadata(m);
                }
            }
        }
    }

    public void valueListener(ValueChangeEvent event)
    {
        if (event != null && event.getOldValue() != event.getNewValue())
        {
            current.setSimpleValue(event.getNewValue().toString());
        }
    }

    public void requiredListener(ValueChangeEvent event)
    {
        if (event != null && event.getOldValue() != event.getNewValue())
        {
            boolean required = Boolean.getBoolean(event.getNewValue().toString());
            if (required)
            {
                this.current.setMinOccurs(1);
            }
            else
            {
                current.setMinOccurs(0);
            }
        }
    }

    public void multipleListener(ValueChangeEvent event)
    {
        if (event != null && event.getOldValue() != event.getNewValue())
        {
            boolean multiple = Boolean.getBoolean(event.getNewValue().toString());
            if (multiple)
            {
                this.current.setMaxOccurs(1000);
            }
            else
            {
                current.setMaxOccurs(1);
            }
        }
    }

    public int getConstraintsSize()
    {
        return this.constraints.size();
    }

    public String addConstraint()
    {
        if (getConstraintPosition() == 0)
        {
            constraints.add(new ConstraintBean(""));
        }
        else
        {
            constraints.add(getConstraintPosition() + 1, new ConstraintBean(""));
        }
        return "";
    }

    public String removeConstraint()
    {
        constraints.remove(getConstraintPosition());
        return "";
    }

    public Metadata getCurrent()
    {
        return current;
    }

    public void setCurrent(Metadata selected)
    {
        this.current = selected;
    }

    public List<Metadata> getMetadataList()
    {
        return metadataList;
    }

    public void setMetadataList(List<Metadata> metadataList)
    {
        this.metadataList = metadataList;
    }

    /**
     * @return the constraints
     */
    public List<ConstraintBean> getConstraints()
    {
        return constraints;
    }

    /**
     * @param constraints the constraints to set
     */
    public void setConstraints(List<ConstraintBean> constraints)
    {
        this.constraints = constraints;
    }

    public void setConstraintPosition(int constraintPosition)
    {
        this.constraintPosition = constraintPosition;
    }

    public int getConstraintPosition()
    {
        return constraintPosition;
    }

    /**
     * JSF Bean for a Constraint
     * 
     * @author saquet
     */
    public class ConstraintBean
    {
        private String constraint = "";

        public ConstraintBean(String constraint)
        {
            this.constraint = constraint;
        }

        public void setValue(String value)
        {
            this.constraint = value;
        }

        public String getValue()
        {
            return constraint;
        }

        public void listener(ValueChangeEvent event)
        {
            if (event != null && event.getOldValue() != event.getNewValue())
            {
                constraint = event.getNewValue().toString();
            }
        }
    }
}
