/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo;

import java.lang.annotation.Annotation;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.complextypes.ConePerson;
import de.mpg.jena.vo.complextypes.Date;
import de.mpg.jena.vo.complextypes.Geolocation;
import de.mpg.jena.vo.complextypes.License;
import de.mpg.jena.vo.complextypes.Number;
import de.mpg.jena.vo.complextypes.Publication;
import de.mpg.jena.vo.complextypes.Text;
import de.mpg.jena.vo.complextypes.URI;

public abstract class ComplexType extends ImageMetadata
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 152783713726661003L;


	@Namespace("http://imeji.mpdl.mpg.de/")
	@RdfType("complexTypes")
	public enum ComplexTypes
	{
		PERSON(ConePerson.class), TEXT(Text.class), NUMBER(Number.class), DATE(Date.class), LICENSE(License.class), 
		GEOLOCATION(Geolocation.class), URI(URI.class), PUBLICATION(Publication.class);

		private Class<? extends ComplexType> type;

		public @interface searchable 
		{
			boolean value = true;
		}

		private ComplexTypes(Class<? extends ComplexType> type)
		{
			this.type = type;
		}

		public Class<? extends ComplexType> getClassType()
		{
			return type;
		}

		public java.net.URI getURI()
		{
			Annotation rdfTypeAnn = this.getClassType().getAnnotation(thewebsemantic.RdfType.class);
			java.net.URI uri = java.net.URI.create("http://imeji.mpdl.mpg.de/complexTypes/"
					+ rdfTypeAnn.toString().split("@thewebsemantic.RdfType\\(value=")[1].split("\\)")[0].toUpperCase());
			return uri;
		}
		
		public String getType()
		{
			Annotation rdfTypeAnn = this.getClassType().getAnnotation(thewebsemantic.RdfType.class);
			String uri = "http://imeji.mpdl.mpg.de/metadata/"+ rdfTypeAnn.toString().split("@thewebsemantic.RdfType\\(value=")[1].split("\\)")[0].toLowerCase();
			return uri;
		}
	}


	public ComplexType(ComplexTypes type)
	{
		this.setType(type);
	}

}
