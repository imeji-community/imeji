/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import de.mpg.imeji.logic.concurrency.locks.Lock;
import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.metadata.editors.SimpleImageEditor;
import de.mpg.imeji.presentation.metadata.util.MetadataHelper;
import de.mpg.imeji.presentation.metadata.util.SuggestBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.UrlHelper;

public class SingleEditBean implements Serializable
{
    private Item item = null;
    private MetadataProfile profile = null;
    private SimpleImageEditor editor = null;
    private Map<URI, Boolean> valuesMap = new HashMap<URI, Boolean>();
    private String toggleState = "displayMd";
    private int mdPosition = 0;
    private String pageUrl = "";

    public SingleEditBean(Item im, MetadataProfile profile, String pageUrl)
    {
        item = im;
        this.profile = profile;
        this.pageUrl = pageUrl;
        init();
    }

    public String getCheckToggleState()
    {
        toggleState = "displayMd";
        if (UrlHelper.getParameterBoolean("edit"))
        {
            showEditor();
        }
        return "";
    }

    public void init()
    {
        for (Statement st : profile.getStatements())
        {
            valuesMap.put(st.getId(), false);
        }
        for (Metadata md : item.getMetadataSet().getMetadata())
        {
            valuesMap.put(md.getStatement(), true);
        }
        for (Statement st : profile.getStatements())
        {
            if (!valuesMap.get(st.getId()))
            {
                item.getMetadataSet().getMetadata().add(MetadataFactory.createMetadata(st));
            }
            valuesMap.put(st.getId(), true);
        }
        List<Item> imAsList = new ArrayList<Item>();
        imAsList.add(item);
        editor = new SimpleImageEditor(imAsList, profile, null);
        ((SuggestBean)BeanHelper.getSessionBean(SuggestBean.class)).init(profile);
    }

    public String save() throws Exception
    {
        cleanImageMetadata();
        editor.getImages().clear();
        editor.getImages().add(item);
        editor.save();
        reloadPage();
        cancel();
        return "";
    }

    public String cancel() throws Exception
    {
        this.toggleState = "displayMd";
        if (editor != null && !editor.getImages().isEmpty())
            item = editor.getImages().get(0);
        {
            cleanImageMetadata();
        }
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        Locks.unLock(new Lock(this.item.getId().toString(), sb.getUser().getEmail()));
        reloadImage();
        return "";
    }

    private void reloadPage() throws IOException
    {
        FacesContext.getCurrentInstance().getExternalContext().redirect(pageUrl + "?init=1");
    }

    private void reloadImage()
    {
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        ItemController itemController = new ItemController(sessionBean.getUser());
        try
        {
            item = itemController.retrieve(item.getId());
        }
        catch (Exception e)
        {
            BeanHelper.error("Error reload image" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cleanImageMetadata()
    {
        for (int i = 0; i < item.getMetadataSet().getMetadata().size(); i++)
        {
            if (MetadataHelper.isEmpty(((List<Metadata>)item.getMetadataSet().getMetadata()).get(i)))
            {
                ((List<Metadata>)item.getMetadataSet().getMetadata()).remove(i);
                i--;
            }
        }
    }

    public String showEditor()
    {
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        Security security = new Security();
        if (security.check(OperationsType.UPDATE, sb.getUser(), item))
        {
            this.toggleState = "editMd";
            try
            {
                Locks.lock(new Lock(item.getId().toString(), sb.getUser().getEmail()));
            }
            catch (Exception e)
            {
                BeanHelper.error(sb.getMessage("error_editor_image_locked"));
            }
            init();
        }
        else
        {
            BeanHelper.error(sb.getMessage("error_editor_not_allowed"));
        }
        return "";
    }

    public String addMetadata()
    {
        editor.addMetadata(0, mdPosition);
        this.item = editor.getImages().get(0);
        init();
        return "";
    }

    public String removeMetadata()
    {
        editor.removeMetadata(0, mdPosition);
        this.item = editor.getImages().get(0);
        init();
        return "";
    }

    public SimpleImageEditor getEditor()
    {
        return editor;
    }

    public void setEditor(SimpleImageEditor editor)
    {
        this.editor = editor;
    }

    public Item getImage()
    {
        return item;
    }

    public void setImage(Item item)
    {
        this.item = item;
    }

    public MetadataProfile getProfile()
    {
        return profile;
    }

    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }

    public Map<URI, Boolean> getValuesMap()
    {
        return valuesMap;
    }

    public void setValuesMap(Map<URI, Boolean> valuesMap)
    {
        this.valuesMap = valuesMap;
    }

    public int getMdPosition()
    {
        return mdPosition;
    }

    public void setMdPosition(int mdPosition)
    {
        this.mdPosition = mdPosition;
    }

    public String getToggleState()
    {
        return toggleState;
    }

    public void setToggleState(String toggleState)
    {
        this.toggleState = toggleState;
    }
}
