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
	/**
	 * Create new Metadata
	 * @param type
	 * @return
	 */
	public static ImageMetadata newMetadata(ComplexTypes type)
	{
		try 
		{
			ImageMetadata md =  type.getClassType().newInstance();
			md.setType(type);
			return md;
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error creating new instance of Complextype: " + e);
		}
	}
	
	/**
	 * Create a new Metadata
	 * @param st
	 * @return
	 */
	public static ImageMetadata newMetadata(Statement st)
	{
		ImageMetadata md = newMetadata(ComplexTypeHelper.getComplexType(st.getType()));
		md.setNamespace(st.getName());
		return md;
	}
	
	/**
	 * Copy metadata to another, and perform some transformation (add search values, format dates, etc.)
	 * @param metadata
	 * @return
	 */
	public static ImageMetadata copyMetadata(ImageMetadata metadata)
	{
		ImageMetadata md = newMetadata(metadata.getType());
		
		String searchValue="";
				
		switch (metadata.getType()) 
		{
			case DATE:
				md = new Date();
				((Date)md).setDate(((Date)metadata).getDate());
			 	if (((Date)md).getDate() != null)
				{
			 		long time = DateFormatter.getTime(((Date)md).getDate());
			 		((Date)md).setDateTime(time);
				}
			 	searchValue += " " +((Date)metadata).getDate();
				break;
			case GEOLOCATION:
				md = new Geolocation();
				((Geolocation)md).setLatitude(((Geolocation)metadata).getLatitude());
				((Geolocation)md).setLongitude(((Geolocation)metadata).getLongitude());
				((Geolocation)md).setName(((Geolocation)metadata).getName());
				searchValue += " " + ((Geolocation)metadata).getLatitude() + " " 
				+ ((Geolocation)metadata).getLongitude() + " " + ((Geolocation)metadata).getName();
				break;
			case LICENSE:
				md = new License();
				((License)md).setLicense(((License)metadata).getLicense());
				searchValue += " " + ((License)metadata).getLicense();
				break;
			case NUMBER:
				md = new Number();
				((Number)md).setNumber(((Number)metadata).getNumber());
				searchValue += " " + ((Number)metadata).getNumber();
				break;
			case PERSON:
				md = new ConePerson();
				((ConePerson)md).setPerson(((ConePerson)metadata).getPerson());
				((ConePerson)md).setConeId(((ConePerson)metadata).getConeId());
				searchValue += " " + ((ConePerson)metadata).getConeId() + " " + ((ConePerson)metadata).getPerson().getFamilyName()
							+ " " +  ((ConePerson)metadata).getPerson().getGivenName() + " " + ((ConePerson)metadata).getPerson().getAlternativeName()
							+ " " +  ((ConePerson)metadata).getPerson().getIdentifier();
				for (Organization o :  ((ConePerson)metadata).getPerson().getOrganizations())
				{
					searchValue += " " + o.getCountry() + " " + o.getDescription() + " " + o.getIdentifier() + " " + o.getName() + " " + o.getCity();
				}
				break;
			case PUBLICATION:
				md = new Publication();
				((Publication)md).setCitation(((Publication)metadata).getCitation());
				((Publication)md).setExportFormat(((Publication)metadata).getExportFormat());
				((Publication)md).setUri(((Publication)metadata).getUri());
				searchValue += " " + ((Publication)md).getCitation() + " " +((Publication)md).getUri();
				break;
			case URI:
				md= new URI();
				((URI)md).setUri(((URI)metadata).getUri());
				searchValue += " " +((URI)md).getUri();
				break;
			case TEXT:
				md = new Text();
				((Text)md).setText(((Text)metadata).getText());
				searchValue += ((Text)md).getText();
				break;
			default:
				throw new RuntimeException("Unknown metadata type " + metadata.getType());
		}
		
		md.setSearchValue(searchValue.replaceAll("null", "").trim());
		md.setType(metadata.getType());
		md.setNamespace(metadata.getNamespace());
		md.setPos(metadata.getPos());
		
		return md;
		
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
					d.setDate("2000-01-02");
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
				pub.setExportFormat("");
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
