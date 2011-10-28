package de.mpg.imeji.metadata.util;

import java.io.Serializable;

import org.apache.log4j.Logger;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.complextypes.ConePerson;
import de.mpg.jena.vo.complextypes.Date;
import de.mpg.jena.vo.complextypes.Geolocation;
import de.mpg.jena.vo.complextypes.License;
import de.mpg.jena.vo.complextypes.Number;
import de.mpg.jena.vo.complextypes.Publication;
import de.mpg.jena.vo.complextypes.Text;
import de.mpg.jena.vo.complextypes.URI;

public class MetadataHelper implements Serializable
{
	private static Logger logger = Logger.getLogger(MetadataHelper.class);
	
	public static boolean isEmpty(ImageMetadata md)
	{
		if (md instanceof Text)
		{
			if (((Text) md).getText() == null || "".equals(((Text) md).getText())) return true;
		}
		else if (md instanceof Date)
		{
			if (((Date) md).getDate() == null || "".equals(((Date) md).getDate()) || Double.isNaN(((Date) md).getDateTime())) return true;
		}
		else if (md instanceof Geolocation)
		{
			return Double.isNaN(((Geolocation) md).getLatitude()) ||  Double.isNaN(((Geolocation) md).getLongitude());
		}
		else if (md instanceof License)
		{
			if (((License) md).getLicense() == null || "".equals(((License) md).getLicense())) return true;
		}
		else if (md instanceof Publication)
		{
			if (((Publication) md).getUri() == null || "".equals(((Publication) md).getUri().toString())) return true;
		}
		else if (md instanceof Number)
		{
			return Double.isNaN(((Number) md).getNumber());
		}
		else if (md instanceof ConePerson)
		{
			if (((ConePerson) md).getPerson() == null || ((ConePerson) md).getPerson().getFamilyName() == null ||
					"".equals(((ConePerson) md).getPerson().getFamilyName())) 
				return true;
		}
		else if (md instanceof URI)
		{
			if (((URI) md).getUri() == null || "".equals(((URI) md).getUri().toString())) return true;
		}
		
		return false;
	}
	
	public static ImageMetadata setConeID(ImageMetadata md)
	{
		if (md.getType().equals(ComplexTypes.PERSON))
		{
			String id = ((ConePerson)md).getPerson().getIdentifier();
			
			try
			{
				if (id.contains("http"))
				{
					((ConePerson)md).setConeId(java.net.URI.create(id));
					return md;
				}
			}
			catch (Exception e) 
			{
				BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error") + " CONE ID");
			}
			((ConePerson)md).setConeId(null);
		}
		return md;
	}
}
