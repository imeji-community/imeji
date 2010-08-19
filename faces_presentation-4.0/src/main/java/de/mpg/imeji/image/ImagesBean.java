package de.mpg.imeji.image;

import java.util.Collection;
import java.util.List;

import de.mpg.imeji.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.vo.ImageVO;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.vo.Image;

public class ImagesBean extends BasePaginatorListSessionBean<ImageVO>
{
    private ImageController controller; 
    private int totalNumberOfRecords;
    
    public ImagesBean()
    {
        super();
        controller = new ImageController(null);
        
    }
    
    @Override
    public String getNavigationString()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTotalNumberOfRecords()
    {
       return totalNumberOfRecords;
    }

    @Override
    public List<ImageVO> retrieveList(int offset, int limit)
    {
        //totalNumberOfRecords = controller.search(null, null, null, 0, 0).size();

        Collection<Image> images = controller.search(null, null, null, limit, offset);
        
        return ImejiFactory.newImagesList(images);
    }
}
