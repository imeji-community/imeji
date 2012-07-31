package de.mpg.imeji.logic.search.vo;

public class ComparableSearchResult implements Comparable<ComparableSearchResult>
{
    private String value = null;
    private String sortValue = "";

    public ComparableSearchResult(String s)
    {
        String[] t = s.split("\\?sortValue=");
        this.value = t[0];
        if (t.length > 1)
        {
            this.sortValue = t[1];
        }
    }

    public ComparableSearchResult(String value, String sortValue)
    {
        this.sortValue = sortValue;
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getSortValue()
    {
        return sortValue;
    }

    public void setSortValue(String sortValue)
    {
        this.sortValue = sortValue;
    }

    public int compareTo(ComparableSearchResult o)
    {
        return o.getSortValue().compareToIgnoreCase(sortValue);
    }
}
