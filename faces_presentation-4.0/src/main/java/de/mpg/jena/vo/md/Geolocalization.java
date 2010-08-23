package de.mpg.jena.vo.md;


public class Geolocalization extends ComplexType
{
    public Geolocalization()
    {
        super(AllowedTypes.GEOLOCALIZATION);
    }
    
    public Geolocalization(String latitude, String longitude)
    {
        super(AllowedTypes.GEOLOCALIZATION);
    }
}
