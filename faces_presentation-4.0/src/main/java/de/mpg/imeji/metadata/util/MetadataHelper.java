package de.mpg.imeji.metadata.util;

import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.complextypes.ConePerson;
import de.mpg.jena.vo.complextypes.Date;
import de.mpg.jena.vo.complextypes.Geolocation;
import de.mpg.jena.vo.complextypes.License;
import de.mpg.jena.vo.complextypes.Number;
import de.mpg.jena.vo.complextypes.Publication;
import de.mpg.jena.vo.complextypes.Text;
import de.mpg.jena.vo.complextypes.URI;

public class MetadataHelper 
{
	public static boolean isEmpty(ImageMetadata md)
	{
		if (md instanceof Text)
		{
			if ("".equals(((Text) md).getText())) return true;
		}
		else if (md instanceof Date)
		{
			if (((Date) md).getDate() == null) return true;
		}
		else if (md instanceof Geolocation)
		{
			if (((Geolocation) md).getLatitude() == 0 &&
					((Geolocation) md).getLongitude() == 0)
				return true;
		}
		else if (md instanceof License)
		{
			if ("".equals(((License) md).getLicense())) return true;
		}
		else if (md instanceof Publication)
		{
			if ("".equals(((Publication) md).getUri().toString())) return true;
		}
		else if (md instanceof Number)
		{
			if (((Number) md).getNumber() == 0) return true;
		}
		else if (md instanceof ConePerson)
		{
			if ("".equals(((ConePerson) md).getPerson().getFamilyName())) return true;
		}
		else if (md instanceof URI)
		{
			if ("".equals(((URI) md).getUri().toString())) return true;
		}	
		return false;
	}
}
