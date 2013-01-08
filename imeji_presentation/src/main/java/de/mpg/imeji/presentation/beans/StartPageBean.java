package de.mpg.imeji.presentation.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

public class StartPageBean
{
    private List<Item> carousselImages;
    private SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    private final static int CAROUSSEL_SIZE = 6;

    public StartPageBean()
    {
        loadCarrousselImages();
    }

    private void loadCarrousselImages()
    {
        carousselImages = new ArrayList<Item>();
        ItemController ic = new ItemController(session.getUser());
        List<Item> items = (List<Item>)ic.loadItems(getRandomResults(ic.searchImages(null, null)), -1, 0);
        for (Item item : items)
        {
            carousselImages.add(item);
        }
    }

    private List<String> getRandomResults(SearchResult sr)
    {
        List<String> l = new ArrayList<String>();
        Random r = new Random();
        for (int i = 0; i < CAROUSSEL_SIZE; i++)
        {
            if (sr.getNumberOfRecords() > 1)
            {
                l.add(sr.getResults().get(r.nextInt(sr.getNumberOfRecords() - 1)));
            }
        }
        return l;
    }

    public void setCarousselImages(List<Item> carousselImages)
    {
        this.carousselImages = carousselImages;
    }

    public List<Item> getCarousselImages()
    {
        return carousselImages;
    }
}
