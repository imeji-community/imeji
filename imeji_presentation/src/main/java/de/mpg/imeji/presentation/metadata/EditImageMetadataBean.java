/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.concurrency.locks.Lock;
import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.collection.CollectionImagesBean;
import de.mpg.imeji.presentation.history.HistorySession;
import de.mpg.imeji.presentation.image.ImagesBean;
import de.mpg.imeji.presentation.image.SelectedBean;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.metadata.editors.MetadataEditor;
import de.mpg.imeji.presentation.metadata.editors.MetadataMultipleEditor;
import de.mpg.imeji.presentation.metadata.util.MetadataHelper;
import de.mpg.imeji.presentation.metadata.util.SuggestBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * Bean for batch and multiple metadata editor
 * 
 * @author saquet
 */
public class EditImageMetadataBean implements Serializable
{
    // objects
    private ImagesBean imagesBean;
    private MetadataEditor editor = null;
    private MetadataProfile profile = null;
    private Statement statement = null;
    private Metadata metadata = null;
    // menus
    private List<SelectItem> statementMenu = null;
    private String selectedStatementName = null;
    private List<SelectItem> modeRadio = null;
    private String selectedMode = "basic";
    // other
    private int mdPosition;
    private int imagePosition;
    private String editType = "selected";
    private boolean isProfileWithStatements = true;
    private int lockedImages = 0;
    private SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    private static Logger logger = Logger.getLogger(EditImageMetadataBean.class);

    /**
     * Constructor
     */
    public EditImageMetadataBean()
    {
        statementMenu = new ArrayList<SelectItem>();
        modeRadio = new ArrayList<SelectItem>();
    }

    /**
     * Initialize the complete page
     * 
     * @return
     */
    public String getInit()
    {
        try
        {
            List<Item> items = initImages();
            initProfileAndStatement(items);
            initStatementsMenu();
            initEditor(items);
            ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class)).init(profile);
        }
        catch (Exception e)
        {
            BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error") + " " + e);
            logger.error("Error init Edit page", e);
        }
        return "";
    }

    private List<Item> initImages()
    {
        initImagesBean();
        return searchAndLoadImages();
    }

    private void initProfileAndStatement(List<Item> items)
    {
        if (items != null && items.size() > 0)
        {
            profile = ObjectLoader.loadProfile(items.get(0).getMetadataSet().getProfile(), session.getUser());
        }
        statement = getSelectedStatement();
    }

    private String initEditor(List<Item> items)
    {
        try
        {
            isProfileWithStatements = true;
            if (statement != null)
            {
                metadata = MetadataFactory.createMetadata(statement);
                editor = new MetadataMultipleEditor(items, profile, getSelectedStatement());
                lockImages(items);
                ((SuggestBean)BeanHelper.getSessionBean(SuggestBean.class)).init(profile);
            }
            else
            {
                logger.error("No statement found");
                isProfileWithStatements = false;
                BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("profile_empty"));
            }
            initModeMenu();
        }
        catch (Exception e)
        {
            BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error") + " " + e);
            logger.error("Error init Edit page", e);
        }
        return "";
    }

    private void initImagesBean()
    {
        editType = (String)((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest())
                .getParameter("type");
        if ("selected".equals(editType))
        {
            imagesBean = (SelectedBean)BeanHelper.getSessionBean(SelectedBean.class);
        }
        else if ("all".equals(editType))
        {
            imagesBean = (CollectionImagesBean)BeanHelper.getSessionBean(CollectionImagesBean.class);
        }
    }

    private void initModeMenu()
    {
        selectedMode = "basic";
        modeRadio = new ArrayList<SelectItem>();
        modeRadio.add(new SelectItem("basic", ((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                .getMessage("editor_basic")));
        if (this.statement.getMaxOccurs().equals("unbounded"))
        {
            modeRadio.add(new SelectItem("append", ((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                    .getMessage("editor_append")));
        }
        modeRadio.add(new SelectItem("overwrite", ((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                .getMessage("editor_overwrite")));
    }

    private void initStatementsMenu()
    {
        statementMenu = new ArrayList<SelectItem>();
        for (Statement s : profile.getStatements())
        {
            statementMenu.add(new SelectItem(s.getId().toString(), ((MetadataLabels)BeanHelper
                    .getSessionBean(MetadataLabels.class)).getInternationalizedLabels().get(s.getId())));
        }
    }

    public String changeStatement()
    {
        statement = getSelectedStatement();
        // Reload the images
        initEditor(initImages());
        return "";
    }

    public List<Item> searchAndLoadImages()
    {
        int elementsPerPage = imagesBean.getElementsPerPage();
        int currentPageNumber = imagesBean.getCurrentPageNumber();
        imagesBean.setElementsPerPage(10000);
        imagesBean.setCurrentPageNumber(1);
        SearchResult sr = imagesBean.search(imagesBean.getScList(), null);
        List<Item> items = (List<Item>)imagesBean.loadImages(sr);
        imagesBean.setElementsPerPage(elementsPerPage);
        imagesBean.setCurrentPageNumber(currentPageNumber);
        return items;
    }

    /**
     * For batch edit: Add the same values to all images and save.
     * 
     * @return
     * @throws IOException
     */
    public String addToAllSaveAndRedirect() throws IOException
    {
        addToAll();
        long before = System.currentTimeMillis();
        editor.save();
        long after = System.currentTimeMillis();
        redirectToView();
        System.out.println("saving = " + Long.valueOf(after - before));
        return "";
    }

    /**
     * For the Multiple Edit: Save the current values
     * 
     * @return
     * @throws IOException
     */
    public String saveAndRedirect() throws IOException
    {
        long before = System.currentTimeMillis();
        editor.save();
        long after = System.currentTimeMillis();
        redirectToView();
        System.out.println("saving = " + Long.valueOf(after - before));
        return "";
    }

    public String cancel() throws IOException
    {
        unlockImages();
        editor.getImages().clear();
        HistorySession hs = (HistorySession)BeanHelper.getSessionBean(HistorySession.class);
        FacesContext.getCurrentInstance().getExternalContext().redirect(hs.getPreviousPage().getUri().toString());
        return "";
    }

    private void lockImages(List<Item> items)
    {
        lockedImages = 0;
        for (int i = 0; i < items.size(); i++)
        {
            try
            {
                Locks.lock(new Lock(items.get(i).getId().toString(), session.getUser().getEmail()));
            }
            catch (Exception e)
            {
                editor.getImages().remove(i);
                lockedImages++;
                i--;
            }
        }
    }

    private void unlockImages()
    {
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        for (Item im : editor.getImages())
        {
            Locks.unLock(new Lock(im.getId().toString(), sb.getUser().getEmail()));
        }
    }

    public String addToAll()
    {
        for (Item im : editor.getImages())
        {
            if ("overwrite".equals(selectedMode))
            {
                im = removeAllMetadata(im);
                im.getMetadataSet().getMetadata().add(MetadataFactory.copyMetadata(metadata));
            }
            else if ("append".equals(selectedMode))
            {
                im.getMetadataSet().getMetadata().add(MetadataFactory.copyMetadata(metadata));
            }
            else if ("basic".equals(selectedMode))
            {
                addMetadataIfNotExists(im, MetadataFactory.copyMetadata(metadata));
            }
        }
        metadata = MetadataFactory.createMetadata(getSelectedStatement());
        return "";
    }

    public void redirectToView() throws IOException
    {
        unlockImages();
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        String path = ((CollectionImagesBean)BeanHelper.getSessionBean(CollectionImagesBean.class)).getCollection()
                .getId().getPath();
        FacesContext.getCurrentInstance().getExternalContext()
                .redirect(navigation.getApplicationUri() + "/images" + path);
    }

    public String clearAll()
    {
        metadata = MetadataFactory.createMetadata(statement);
        for (Item im : editor.getImages())
        {
            removeAllMetadata(im);
        }
        return "";
    }

    public String resetChanges()
    {
        getInit();
        return "";
    }

    private Item addMetadataIfNotExists(Item im, Metadata metadata)
    {
        boolean hasValue = false;
        int i = 0;
        for (Metadata md : im.getMetadataSet().getMetadata())
        {
            if (md.getStatement().toString().equals(metadata.getStatement().toString()))
            {
                if (MetadataHelper.isEmpty(md))
                {
                    ((List<Metadata>)im.getMetadataSet().getMetadata()).set(i, metadata);
                }
                hasValue = true;
            }
            i++;
        }
        if (!hasValue)
        {
            im.getMetadataSet().getMetadata().add(metadata);
        }
        return im;
    }

    /**
     * Remove all metadata values with the same type as the current selected metadata
     * 
     * @param im
     * @return
     */
    private Item removeAllMetadata(Item im)
    {
        for (int i = 0; i < im.getMetadataSet().getMetadata().size(); i++)
        {
            if (((List<Metadata>)im.getMetadataSet().getMetadata()).get(i).getStatement() == null
                    || ((List<Metadata>)im.getMetadataSet().getMetadata()).get(i).getStatement().toString()
                            .equals(metadata.getStatement().toString()))
            {
                ((List<Metadata>)im.getMetadataSet().getMetadata()).remove(i);
                i--;
            }
        }
        return im;
    }

    public Statement getSelectedStatement()
    {
        if (profile != null)
        {
            for (Statement s : profile.getStatements())
            {
                if (s.getId().toString().equals(selectedStatementName))
                {
                    return s;
                }
            }
        }
        return getDefaultStatement();
    }

    public Statement getDefaultStatement()
    {
        if (profile != null && profile.getStatements().iterator().hasNext())
        {
            return profile.getStatements().iterator().next();
        }
        return null;
    }

    public String addMetadata()
    {
        editor.addMetadata(getImagePosition(), getMdPosition());
        return "";
    }

    public String removeMetadata()
    {
        editor.removeMetadata(getImagePosition(), getMdPosition());
        return "";
    }

    public int getMdPosition()
    {
        return mdPosition;
    }

    public void setMdPosition(int mdPosition)
    {
        this.mdPosition = mdPosition;
    }

    public int getImagePosition()
    {
        return imagePosition;
    }

    public void setImagePosition(int imagePosition)
    {
        this.imagePosition = imagePosition;
    }

    public MetadataEditor getEditor()
    {
        return editor;
    }

    public void setEditor(MetadataEditor editor)
    {
        this.editor = editor;
    }

    public ImagesBean getImagesBean()
    {
        return imagesBean;
    }

    public void setImagesBean(ImagesBean imagesBean)
    {
        this.imagesBean = imagesBean;
    }

    public List<SelectItem> getStatementMenu()
    {
        return statementMenu;
    }

    public void setStatementMenu(List<SelectItem> statementMenu)
    {
        this.statementMenu = statementMenu;
    }

    public String getSelectedStatementName()
    {
        return selectedStatementName;
    }

    public void setSelectedStatementName(String selectedStatementName)
    {
        this.selectedStatementName = selectedStatementName;
    }

    public Metadata getMetadata()
    {
        return metadata;
    }

    public void setMetadata(Metadata metadata)
    {
        this.metadata = metadata;
    }

    public MetadataProfile getProfile()
    {
        return profile;
    }

    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }

    public List<SelectItem> getModeRadio()
    {
        return modeRadio;
    }

    public void setModeRadio(List<SelectItem> modeRadio)
    {
        this.modeRadio = modeRadio;
    }

    public String getSelectedMode()
    {
        return selectedMode;
    }

    public void setSelectedMode(String selectedMode)
    {
        this.selectedMode = selectedMode;
    }

    public String getEditType()
    {
        return editType;
    }

    public void setEditType(String editType)
    {
        this.editType = editType;
    }

    public Statement getStatement()
    {
        return statement;
    }

    public void setStatement(Statement statement)
    {
        this.statement = statement;
    }

    public boolean isProfileWithStatements()
    {
        return isProfileWithStatements;
    }

    public void setProfileWithStatements(boolean isProfileWithStatements)
    {
        this.isProfileWithStatements = isProfileWithStatements;
    }

    public int getLockedImages()
    {
        return lockedImages;
    }

    public void setLockedImages(int lockedImages)
    {
        this.lockedImages = lockedImages;
    }
}
