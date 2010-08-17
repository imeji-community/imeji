package de.mpg.imeji.vo.list;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.vo.CollectionVO;
import de.mpg.imeji.vo.list.util.ListParameters;


public class CollectionListVO extends ListVO
{

    protected List<CollectionVO> list = new ArrayList<CollectionVO>();
    /**
     * The value for drop down menu
     */
    private List<SelectItem> menu = new ArrayList<SelectItem>();
   
    /**
     * Constructor for customized new list.
     * @param list
     * @param filter
     * @param type
     */
    public CollectionListVO(List<CollectionVO> list, ListParameters filter, HandlerType type)
    {
        this.list = list;
        this.size = this.list.size();
        this.parameters = filter;
        handler = type;
        init();
    }
    
    private void init()
    {
        menu.add(new SelectItem("", "Select a Collection"));
        
        for (int i = 0; i < size; i++)
        {
            menu.add(new SelectItem(list.get(i).getId().getPath(), list.get(i).getMetadata().getTitle()));
        }
    }

    public List<CollectionVO> getList()
    {
        return list;
    }

    public void setList(List<CollectionVO> list)
    {
        this.list = list;
    }
}
