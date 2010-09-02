package de.mpg.imeji.image;

import javax.faces.event.ActionEvent;

import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.vo.Image;

public class ImageBean
{
    private Image image;
    private boolean selected;

    public ImageBean(Image img)
    {
        this.image = img;
    }

    public void setImage(Image image)
    {
        this.image = image;
    }

    public Image getImage()
    {
        return image;
    }

    public void select(ActionEvent event)
    {
        if (!selected)
            selected = true;
        else
            selected = false;
        
        ImagesBean ib = (ImagesBean)BeanHelper.getSessionBean(ImagesBean.class);
        ib.getSelected().add(this.image);
    }

    /**
     * @return the selected
     */
    public boolean isSelected()
    {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public String getThumbnailImageUrlAsString()
    {
        return "";
        //return image.getThumbnailImageUrl().toString();
    }
    
    public String getId()
    {
        return image.getId().toString();
    }
}
