package de.mpg.jena.vo.md;

import java.text.SimpleDateFormat;

public class License extends ComplexType
{
    private SimpleDateFormat date;
    private String dateFormat = "dd/mm/yyyy";

    public License()
    {
        super(AllowedTypes.DATE);
    }

    public License(SimpleDateFormat date)
    {
        super(AllowedTypes.DATE);
        this.date = date;
        date.applyPattern(dateFormat);
    }

    public String getDateString()
    {
        return date.format(date);
    }
}
