package de.mpg.imeji.vo.list;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.vo.list.util.ListParameters;
import de.mpg.jena.vo.CollectionImeji;


public class CollectionListVO extends ListVO
{

    protected List<CollectionImeji> list = new ArrayList<CollectionImeji>();
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
    public CollectionListVO(List<CollectionImeji> list, ListParameters filter, HandlerType type)
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

    public List<CollectionImeji> getList()
    {
        return list;
    }

    public void setList(List<CollectionImeji> list)
    {
        this.list = list;
    }
}
