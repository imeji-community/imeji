package de.mpg.jena.vo.complextypes;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("date")
@Embedded
public class Date extends ComplexType
{
    private java.util.Date date;
    
    public Date()
    {
        super(ComplexTypes.DATE);
    }

    public void setDate(java.util.Date date)
    {
        this.date = date;
    }

    public java.util.Date getDate()
    {
        return date;
    }
    
    
}
