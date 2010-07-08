package de.mpg.escidoc.faces.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.container.album.AlbumVO;
import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.util.BeanHelper;

public class SearchSession
{
    private Map<String, Map<String, Boolean>> checkBox= null;
    private Map<String, List<String>> textfield = null;
    private Map<String, Metadata> mdMAp = null;
    private AlbumVO collectionVO = null;
    private Navigation navigation = null;
    private int addedFields = 0;

    public SearchSession()
    {
        checkBox = new HashMap<String, Map<String,Boolean>>();
        textfield = new HashMap<String, List<String>>();
        mdMAp = new HashMap<String, Metadata>();
        collectionVO = new AlbumVO();
        this.addedFields = 0;
    }

    /**
     * Set the  search components needing a session. Used for "Revise search". 
     * @param Metadata the metadata the store.
     */
    public void saveSearchParameter(Metadata metadata)
    {
    	if ("checkbox".equals(metadata.getGuiComponent()))
        {
            Map<String, Boolean> metadataValueMap = new HashMap<String, Boolean>();;
            
            for (int i = 0; i < metadata.getValue().size(); i++)
            {
                metadataValueMap.put( metadata.getValue().get(i), true);
            }
            
            checkBox.put(metadata.getIndex(), metadataValueMap);
            mdMAp.put(metadata.getIndex(), metadata);
        }
        
        if ("text-field".equals(metadata.getGuiComponent()))
        {
            textfield.put(metadata.getIndex(), metadata.getValue());
            mdMAp.put(metadata.getIndex(), metadata);
        }
        
        if ("range".equals(metadata.getGuiComponent())) 
        {
             mdMAp.put(metadata.getIndex(), metadata);
		}
    }
    
    /**
     * Listen the values of the text fields and update it.
     * @param event
     */
    public void textFieldListener(ValueChangeEvent event)
    {
        String value = event.getNewValue().toString();
        String index = event.getComponent().getAttributes().get("index").toString();
        
        if (event.getComponent().getAttributes().get("position") != null)
        {
            int position = Integer.parseInt(event.getComponent().getAttributes().get("position").toString());
            textfield.get(index).set(position, value);
        }
        else if (event.getComponent().getAttributes().get("position") == null 
                && value != null && index != null)
        {
            textfield.get(index).set(textfield.get(index).size() - 1, value);
        } 
    }
    
    /**
     * Listen the values of the check boxes.
     * @param event
     */
    public void checkBoxListener(ValueChangeEvent event)
    {
        Boolean value = ((Boolean)event.getNewValue()).booleanValue();
        String index = event.getComponent().getAttributes().get("index").toString();
        String constraint = event.getComponent().getAttributes().get("constraint").toString();
        
        checkBox.get(index).put(constraint, value);
    }
    
    /**
     * Add a text field to the formular.
     * @param event
     * @throws IOException
     */
    public void addTextField(ActionEvent event) throws IOException
    {
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        // Get the author which should get a new affiliation
        Object index = event.getComponent().getAttributes().get("index");
        
        this.getTextfield().get(index).add("");
        this.addedFields ++;
        
        FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getSearchUrl() + "?action=revise");
    }
    
    /**
     * Remove a text field to the formular.
     * @param event
     * @throws IOException
     */
    public void removeTextField(ActionEvent event) throws IOException
    {
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        // Get the author which should get a new affiliation
        Object index = event.getComponent().getAttributes().get("index");
        Object value = event.getComponent().getAttributes().get("value");
        
        for (int i = 0; i <  this.getTextfield().get(index).size(); i++)
        {
            if ( this.getTextfield().get(index).get(i).equals(value))
            {
                this.getTextfield().get(index).remove(i);
            }
        }
        
        this.addedFields --;
        FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getSearchUrl() + "?action=revise");
    }
    
       
    public Map<String, List<String>> getTextfield()
    {
        return textfield;
    }

    public void setTextfield(Map<String, List<String>> textfield)
    {
        this.textfield = textfield;
    }

    public Map<String, Map<String, Boolean>> getCheckBox()
    {
        return checkBox;
    }

    public void setCheckBox(Map<String, Map<String, Boolean>> checkBox)
    {
        this.checkBox = checkBox;
    }

    public Map<String, Metadata> getMdMap()
    {
        return mdMAp;
    }

    public void setMdMAp(Map<String, Metadata> mdMap)
    {
        this.mdMAp = mdMap;
    }

	public AlbumVO getCollectionVO() 
	{
		return collectionVO;
	}

	public void setCollectionVO(AlbumVO collectionVO) 
	{
		this.collectionVO = collectionVO;
	}
    
    public int getAddedFields()
    {
        return addedFields;
    }

    public void setAddedFields(int addedFields)
    {
        this.addedFields = addedFields;
    }
    
}
