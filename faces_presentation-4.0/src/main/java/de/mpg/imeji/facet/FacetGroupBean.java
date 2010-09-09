package de.mpg.imeji.facet;

import java.util.List;

public class FacetGroupBean
{
    private List<FacetBean> list;
    private String label;

    public FacetGroupBean(List<FacetBean> list, String label)
    {
        this.label = label;
        this.list = list;
    }

    public List<FacetBean> getList()
    {
        return list;
    }

    public void setList(List<FacetBean> list)
    {
        this.list = list;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }
}
