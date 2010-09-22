package de.mpg.jena.vo.complextypes;

import java.io.Serializable;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("geolocation")
@Embedded
public class Geolocation extends ComplexType implements Serializable
{
    private double longitude;
    private double latitude;
    
    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public Geolocation()
    {
        super(ComplexTypes.GEOLOCATION);
    }
    
    public Geolocation(String latitude, String longitude)
    {
        super(ComplexTypes.GEOLOCATION);
    }
}
