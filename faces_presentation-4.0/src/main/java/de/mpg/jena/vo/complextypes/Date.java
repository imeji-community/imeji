package de.mpg.jena.vo.complextypes;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("date")
public class Date extends ComplexType
{
    public Date()
    {
        super(AllowedTypes.DATE);
    }
}
