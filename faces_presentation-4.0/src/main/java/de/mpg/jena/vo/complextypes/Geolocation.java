package de.mpg.jena.vo.complextypes;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("geolocation")
@Embedded
public class Geolocation extends ComplexType
{
    public Geolocation()
    {
        super(ComplexTypes.GEOLOCATION);
    }
    
    public Geolocation(String latitude, String longitude)
    {
        super(ComplexTypes.GEOLOCATION);
    }
}
