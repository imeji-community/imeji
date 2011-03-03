package de.mpg.jena.util;

import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.complextypes.ConePerson;
import de.mpg.jena.vo.complextypes.Date;
import de.mpg.jena.vo.complextypes.Geolocation;
import de.mpg.jena.vo.complextypes.License;
import de.mpg.jena.vo.complextypes.Number;
import de.mpg.jena.vo.complextypes.Publication;
import de.mpg.jena.vo.complextypes.Text;
import de.mpg.jena.vo.complextypes.URI;
import de.mpg.jena.vo.complextypes.util.ComplexTypeHelper;

public class MetadataFactory 
{
	public static ImageMetadata newMetadata(Statement st)
	{
		ImageMetadata md = null;
		
		ComplexTypes type = ComplexTypeHelper.getComplexType(st.getType());
		
		switch (type) 
		{
			case DATE:
				md = new Date();
				break;
			case GEOLOCATION:
				md = new Geolocation();
				break;
			case LICENSE:
				md = new License();
				break;
			case NUMBER:
				md = new Number();
				break;
			case PERSON:
				md = new ConePerson();
				break;
			case PUBLICATION:
				md = new Publication();
			case URI:
				md= new URI();
				break;
			default:
				md = new Text();
				break;
		}
		
		md.setType(type);
		md.setNamespace(st.getName());
		
		return md;
		
	}
}
