/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.faces.context.FacesContext;

import org.apache.axis.utils.ArrayUtil;

import com.hp.hpl.jena.ontology.Profile;

import de.mpg.imeji.logic.concurrency.locks.Lock;
import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.metadata.editors.MetadataEditor;
import de.mpg.imeji.presentation.metadata.editors.SimpleImageEditor;
import de.mpg.imeji.presentation.metadata.util.MetadataHelper;
import de.mpg.imeji.presentation.metadata.util.SuggestBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ProfileHelper;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Metadata Editor for the detail item page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SingleEditBean
{
    private Item item = null;
    private MetadataProfile profile = null;
    private SimpleImageEditor editor = null;
    private String toggleState = "displayMd";
    private int mdPosition = 0;
    private String pageUrl = "";
    private List<SuperMetadataBean> metadataList;

    /**
     * Constructor
     * 
     * @param im
     * @param profile
     * @param pageUrl
     */
    public SingleEditBean(Item im, MetadataProfile profile, String pageUrl)
    {
        item = im;
        this.profile = profile;
        this.pageUrl = pageUrl;
        init();
    }

    /**
     * Check in the url if the editor should be automatically shown
     * 
     * @return
     */
    public String getCheckToggleState()
    {
        toggleState = "displayMd";
        if (UrlHelper.getParameterBoolean("edit"))
        {
            showEditor();
        }
        return "";
    }

    /**
     * Initialize the page
     */
    public void init()
    {
        prepareMetadataSetForEditor();
        editor = new SimpleImageEditor(item, profile, null);
        ((SuggestBean)BeanHelper.getSessionBean(SuggestBean.class)).init(profile);
        metadataList = new ArrayList<SuperMetadataBean>();
        metadataList.addAll(editor.getItems().get(0).getMetadata());
    }

    /**
     * Prepare the {@link MetadataSet}of the current {@link Item} for the {@link MetadataEditor}, i.e, add emtpy
     * {@link Metadata} if none is defined for one {@link Statement}
     */
    private void prepareMetadataSetForEditor()
    {
        item.getMetadataSet().sortMetadata();
        item.getMetadataSet().setMetadata(createListOfMetadataWithExistingValuesAndEmtpyValues());
    }

    /**
     * Create a new {@link List} of {@link Metadata} with the {@link Metadata} of the current {@link Item} plus, if
     * missing according to the {@link MetadataProfile}, new emtpy {@link Metadata}
     * 
     * @return
     */
    private List<Metadata> createListOfMetadataWithExistingValuesAndEmtpyValues()
    {
        List<Metadata> l = new ArrayList<Metadata>();
        // add the existing Metadata to the list, and if they is a missing metadata, add a new emtpy one
        for (Metadata md : item.getMetadataSet().getMetadata())
        {
            if (l.isEmpty() && !isFirstStatement(md.getStatement()))
            {
                // Add all metadata that should be before the first existing metadata
                l.addAll(createMetadataBetween(null, md.getStatement()));
            }
            else if (!l.isEmpty() && !isNextStatement(l.get(l.size() - 1).getStatement(), md.getStatement())
                    && !isbefore(md.getStatement(), l.get(l.size() - 1).getStatement()))
            {
                // Add all metadata that should be before the next metadata in the list
                l.addAll(createMetadataBetween(l.get(l.size() - 1).getStatement(), md.getStatement()));
            }
            else
            {
                // Add the existing metadata
                l.add(md);
            }
        }
        URI lastStatement = null;
        if (!l.isEmpty())
            lastStatement = l.get(l.size() - 1).getStatement();
        // add all no created metadata after the last metadata
        l.addAll(createMetadataBetween(lastStatement, null));
        return setPositionToMetadata(l);
    }

    /**
     * Create new {@link Metadata} for the {@link Statement} which are ordered betwenn from and to according to the
     * {@link MetadataProfile}
     * 
     * @param from
     * @param to
     * @return
     */
    private List<Metadata> createMetadataBetween(URI from, URI to)
    {
        List<Metadata> l = new ArrayList<Metadata>();
        int fromPosition = 0;
        if (from != null)
            fromPosition = ProfileHelper.getStatement(from, profile).getPos();
        int toPosition = profile.getStatements().size();
        if (to != null)
            toPosition = ProfileHelper.getStatement(to, profile).getPos();
        for (Statement st : profile.getStatements())
        {
            if (st.getPos() > fromPosition && st.getPos() < toPosition)
            {
                l.add(MetadataFactory.createMetadata(st));
            }
        }
        return l;
    }

    /**
     * True if the {@link Statement} with the give {@link URI} is the first in the current {@link MetadataProfile}
     * 
     * @param st
     * @return
     */
    private boolean isFirstStatement(URI st)
    {
        return ProfileHelper.getStatement(st, profile).getPos() == 0;
    }

    /**
     * True if the {@link Statement} st2 is next to st1 according to the order in the current {@link MetadataProfile}
     * 
     * @param st1
     * @param st2
     * @return
     */
    private boolean isNextStatement(URI st1, URI st2)
    {
        return ProfileHelper.getStatement(st1, profile).getPos() + 1 == ProfileHelper.getStatement(st2, profile)
                .getPos();
    }

    /**
     * True if st1 is before than st2 according to the order in the current {@link MetadataProfile}
     * 
     * @param st1
     * @param st2
     * @return
     */
    private boolean isbefore(URI st1, URI st2)
    {
        return ProfileHelper.getStatement(st1, profile).getPos() < ProfileHelper.getStatement(st2, profile).getPos();
    }

    /**
     * /** Set the position of the {@link Metadata} according to their current order
     * 
     * @param mds
     * @return
     */
    private List<Metadata> setPositionToMetadata(List<Metadata> l)
    {
        int pos = 0;
        for (Metadata md : l)
        {
            md.setPos(pos);
            pos++;
            System.out.println(md.asFulltext());
        }
        return l;
    }

    /**
     * Save the {@link Item} with its {@link Metadata}
     * 
     * @return
     * @throws Exception
     */
    public String save() throws Exception
    {
        copySuperMetadatatoItem();
        cleanImageMetadata();
        editor.getItems().clear();
        editor.getItems().add(new EditorItemBean(item, profile));
        editor.save();
        reloadPage();
        cancel();
        return "";
    }

    /**
     * Transform all {@link SuperMetadataBean} to {@link Metadata} which are going to be saved
     */
    private void copySuperMetadatatoItem()
    {
        item.getMetadataSet().getMetadata().clear();
        for (SuperMetadataBean smb : metadataList)
        {
            item.getMetadataSet().getMetadata().add(smb.asMetadata());
        }
    }

    /**
     * Cancel the editing, and reset original values
     * 
     * @return
     * @throws Exception
     */
    public String cancel() throws Exception
    {
        this.toggleState = "displayMd";
        if (editor != null && !editor.getItems().isEmpty())
        {
            item = editor.getItems().get(0).asItem();
        }
        cleanImageMetadata();
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        Locks.unLock(new Lock(this.item.getId().toString(), sb.getUser().getEmail()));
        reloadImage();
        return "";
    }

    /**
     * Reload the current page
     * 
     * @throws IOException
     */
    private void reloadPage() throws IOException
    {
        FacesContext.getCurrentInstance().getExternalContext().redirect(pageUrl + "?init=1");
    }

    /**
     * Reload the current image
     */
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

    /**
     * Remove Metadata which are emtpy to avoid to display then
     */
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

    /**
     * Show the metadata editor
     * 
     * @return
     */
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
            // init();
        }
        else
        {
            BeanHelper.error(sb.getMessage("error_editor_not_allowed"));
        }
        return "";
    }

    /**
     * Add a metadata after the metadata on which the metadata has been clicked
     * 
     * @return
     */
    public String addMetadata()
    {
        editor.addMetadata(0, mdPosition);
        item = editor.getItems().get(0).asItem();
        init();
        return "";
    }

    /**
     * Remove the metadata on which the metadata has been clicked
     * 
     * @return
     */
    public String removeMetadata()
    {
        editor.removeMetadata(0, mdPosition);
        item = editor.getItems().get(0).asItem();
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

    public List<SuperMetadataBean> getMetadataList()
    {
        return metadataList;
    }

    public void setMetadataList(List<SuperMetadataBean> metadataList)
    {
        this.metadataList = metadataList;
    }
}
