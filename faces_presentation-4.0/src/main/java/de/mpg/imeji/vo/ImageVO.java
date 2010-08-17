package de.mpg.imeji.vo;

import de.mpg.jena.vo.Image;

/**
 * Presentation VO for Image
 * @author saquet
 *
 */
public class ImageVO extends Image
{
    private boolean selected = false;
    
    public ImageVO()
    {
	// TODO Auto-generated constructor stub
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
    
    
}
