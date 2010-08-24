package de.mpg.jena.vo.complextypes;

import java.text.SimpleDateFormat;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("license")
public class License extends ComplexType
{
    private SimpleDateFormat date;
    private String dateFormat = "dd/mm/yyyy";

    public License()
    {
        super(AllowedTypes.LICENCE);
    }

    public License(SimpleDateFormat date)
    {
        super(AllowedTypes.LICENCE);
        this.date = date;
        date.applyPattern(dateFormat);
    }

    public String getDateString()
    {
        return date.format(date);
    }
}
