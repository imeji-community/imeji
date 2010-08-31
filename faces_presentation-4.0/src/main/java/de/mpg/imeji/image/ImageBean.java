package de.mpg.imeji.image;

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
        return image.getThumbnailImageUrl().toString();
    }
}


