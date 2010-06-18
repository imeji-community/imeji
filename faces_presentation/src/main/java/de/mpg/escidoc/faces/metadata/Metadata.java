package de.mpg.escidoc.faces.metadata;

import java.util.ArrayList;
import java.util.List;

import org.purl.escidoc.schemas.genericMetadata.metadata.x01.StatementType;
import org.purl.escidoc.schemas.genericMetadata.metadata.x01.DescriptionDocument.Description;
import org.purl.escidoc.schemas.genericMetadata.metadata.x01.impl.StatementTypeImpl;
import org.purl.escidoc.schemas.genericMetadata.metadata.x01.impl.DescriptionDocumentImpl.DescriptionImpl;

/**
 * A generic metadata
 * @name The name of the metadata
 * @constraint A List of the different value allowed for this metadata.
 * @value The list of value of a metadata. If the metadata has constraint, the value has to be in it.
 * @author Bastien Saquet.
 *
 */
public class Metadata
{
    /**
     * The name of the metadata
     */
    private String name = null;
    
    /**
     * The list of value of the metadata
     */
    private List<String> value = null;
    
    /**
     * The type of the metadata (i.e. parent name)
     */
    private String group = null;
    
    /**
     * The eSciDoc index of the metadata
     */
    private String index = null;
    
    /**
     * The complete node of the metadata.
     */
    private StatementType node;
    
    /**
     * The label to be displayed to be displayed.
     */
    private String label = null;
    
    // From here to be checked!!!
    /**
     * The list of constraint of the metadata
     */
    private List<String> constraint = null;
    
    /**
     * The gui component of the metadata
     */
    private String guiComponent = null;
    
    private String simpleValue = null;
    private String max, min;

    /**
     * Constructor for a Metadata. 
     * @param node Description or StatementType.
     */
    public Metadata(Object node)
    {   
        if (DescriptionImpl.class.equals(node.getClass()))
        {
            this.node = StatementType.Factory.newInstance();    
            this.node.setStatementArray(((Description)node).getStatementArray());
            this.node.setId(((Description)node).getResourceClass());
        }
        
        if (StatementTypeImpl.class.equals(node.getClass()))
        {
            this.node = (((StatementType)node));
        }
        
        if (this.node != null)
        {
            initVariables();
        }
    }
        
    /**
     * Constructor of a Metadata.
     * @param metadata : Metadata.
     */
    public Metadata(Metadata metadata)
    {
        this(metadata.getNode());
        this.simpleValue = metadata.getSimpleValue();
        this.value = metadata.getValue();
        this.index = metadata.getIndex();
        this.group = metadata.getGroup();
        this.guiComponent = metadata.getGuiComponent();
        this.constraint = metadata.getConstraint();
        this.max = metadata.max;
        this.min = metadata.min;
    }
    
    private void initVariables()
    {
        // Initialize name
        if (this.node.getId() != null)
        {
            name = this.node.getId();
            index = this.node.getId();
        }

        // Initialize simple value
        if (this.node.getValueArray().length > 0 )
        {
            simpleValue = this.node.getValueArray(0).toString();
        }
        
        // Initialize label
        if (this.node.getLabel() != null)
        {
            label = this.node.getLabel().getStringValue();
        }
                
        // Initialize constraints.
        constraint = new ArrayList<String>();
        
        for (int i = 0; i < this.node.sizeOfConstraintArray(); i++)
        {
            for (int j = 0; j < this.node.getConstraintArray(i).sizeOfLiteralOptionArray(); j++)
            {
                constraint.add(i, this.node.getConstraintArray(i).getLiteralOptionArray(j).getDomNode().getFirstChild().getNodeValue());
            }
        }
        
        if (this.node.getGuiComponent() != null)
        {
            guiComponent = this.node.getGuiComponent().getStringValue();
        }
        
        // Initialize values.
        value = new ArrayList<String>();
        
        for (int i = 0; i < this.node.sizeOfValueArray(); i++)
        {
            value.add(i, this.node.getValueArray(i));
        }
        
        min = "";
        max = "";
    }
    
    /**
     * Clone method
     */
    public Metadata clone()
    {
        Metadata clone = new Metadata(this);
        
        return clone;
    }
   

    /**
     * The list of the constraint of the metadata.
     * @return The list of the name of the constraint as String.
     */
    public List<String> getConstraint()
    {
        return constraint;
    }
    
    public int getValueSize()
    {
        return value.size();
    }

    /**
     * The list of the value of the Metadata. The value have  to respect the constraint.
     * @return The list of the value.
     */
    public List<String> getValue()
    {
        return value;
    }
    
    /**
     * The list of the value of the metadata. The value have to respect the constrain.
     * @param value
     */
    public void setValue(List<String> value)
    {
        this.value = value;
    }
    
    /**
     * Add a value to the list of value of this metadata.
     * @param newValue 
     * @return true if the new value has been added.
     */
    public boolean addValue(String newValue)
    {
        if (!value.contains(newValue))
        {
            if (constraint.size()>0)
            {
                if (constraint.contains(newValue))
                {
                    value.add(newValue);
                }
            }
            else
            {
                value.add(newValue);
            }
            
            return true;
        }
        
        return false;
    }
    
    public void resetAllValue()
    {
        min = null;
        max = null;
        simpleValue = null;
        value = new ArrayList<String>();
    }

    /**
     * Get the name of the metadata
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of the metadata.
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    public String getMax()
    {
        return max;
    }

    public void setMax(String max)
    {
        this.max = max;
    }

    public String getMin()
    {
        return min;
    }

    public void setMin(String min)
    {
        this.min = min;
    }

    /**
     * Get the group the metadata belongs (i.e, the name of his parent )
     * @return
     */
    public String getGroup()
    {
        return group;
    }

    /**
     * Set the group the metadata belongs.
     * @param group
     */
    public void setGroup(String group)
    {
        this.group = group;
    }

    public String getSimpleValue()
    {
        return simpleValue;
    }

    public void setSimpleValue(String simpleValue)
    {
        this.simpleValue = simpleValue;
    }

    /**
     * Get the eSciDoc search index of the metadata.
     * @return String
     */
    public String getIndex()
    {
        return index;
    }

    /**
     * Set the eSciDoc search index of the metadata.
     * @param index
     */
    public void setIndex(String index)
    {
        this.index = index;
    }

    /**
     * Get the complete node of the Metadata
     * @return StatementType.
     */
    public StatementType getNode()
    {
        return node;
    }

    /**
     * Set the complete node of the metadata.
     * @param node
     */
    public void setNode(StatementType node)
    {
        this.node = node;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getGuiComponent()
    {
        return guiComponent;
    }

    public void setGuiComponent(String guiComponent)
    {
        this.guiComponent = guiComponent;
    }


    
}
