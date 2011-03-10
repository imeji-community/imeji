package de.mpg.jena.util;

import java.text.SimpleDateFormat;

import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.Organization;
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
	
	public static ImageMetadata newMetadataWithNonNullValues(Statement st)
	{
		ImageMetadata md = null;
		
		ComplexTypes type = ComplexTypeHelper.getComplexType(st.getType());
		
		switch (type) 
		{
			case DATE:
				Date d = new Date();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
				try {
					d.setDate(format.parse("2000-01-02"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				md = d;
				break;
			case GEOLOCATION:
				Geolocation g = new Geolocation();
				g.setLatitude(0);
				g.setLongitude(0);
				g.setName("");
				md = g;
				break;
			case LICENSE:
				License l = new License();
				l.setLicense("");
				md = l;
				break;
			case NUMBER:
				Number n = new Number();
				n.setNumber(0);
				md = n;
				break;
			case PERSON:
				ConePerson cp = new ConePerson();
				Person p = new Person();
				p.setFamilyName("");
				p.setGivenName("");
				Organization o = new Organization();
				o.setName("");
				p.getOrganizations().add(o);
				cp.setPerson(p);
				md = cp;
				break;
			case PUBLICATION:
				Publication pub = new Publication();
				pub.setUri(java.net.URI.create(""));
				md = pub;
			case URI:
				URI u= new URI();
				u.setUri(java.net.URI.create(""));
				md = u;
				break;
			default:
				Text t = new Text();
				t.setText("");
				md = t;
				break;
		}
		
		md.setType(type);
		md.setNamespace(st.getName());
		
		return md;
		
	}
}
