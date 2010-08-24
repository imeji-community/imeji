package de.mpg.jena.vo.complextypes;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("geolocation")
public class Geolocation extends ComplexType
{
    public Geolocation()
    {
        super(AllowedTypes.GEOLOCATION);
    }
    
    public Geolocation(String latitude, String longitude)
    {
        super(AllowedTypes.GEOLOCATION);
    }
}
