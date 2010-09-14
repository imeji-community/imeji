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
    private Date date;
    
    public Date()
    {
        super(ComplexTypes.DATE);
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public Date getDate()
    {
        return date;
    }
    
    
}
