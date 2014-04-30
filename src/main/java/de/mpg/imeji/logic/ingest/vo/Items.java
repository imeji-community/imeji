/**
 * 
 */
package de.mpg.imeji.logic.ingest.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.mpg.imeji.logic.vo.Item;

/**
 * @author hnguyen
 */
@XmlRootElement(name = "items", namespace = "http://imeji.org/terms")
public class Items
{
    private List<Item> item;

    public Items()
    {
    }

    public Items(Collection<Item> items)
    {
        this.setItem(new ArrayList<Item>(items));
    }

    /**
     * @return the items
     */
    @XmlElement(name = "item", namespace = "http://imeji.org/terms")
    public List<Item> getItem()
    {
        return item;
    }

    /**
     * @param items the items to set
     */
    public void setItem(List<Item> item)
    {
        this.item = item;
    }
}
