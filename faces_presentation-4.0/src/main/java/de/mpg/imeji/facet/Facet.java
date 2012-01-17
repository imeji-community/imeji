/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.facet;

import java.io.Serializable;
import java.net.URI;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.lang.MetadataLabels;
import de.mpg.imeji.util.BeanHelper;

public class Facet implements Serializable
{
	private URI uri;
	private String label;
	private int count;
	private FacetType type;
	private  URI metadataURI;

	public enum FacetType
	{
		TECHNICAL, COLLECTION, SEARCH;
	}

	public Facet(URI uri, String label, int count, FacetType type, URI metadataURI)
	{
		this.count = count;
		this.label = label;
		this.uri = uri;
		this.type = type;
		this.metadataURI = metadataURI;
	}

	public URI getUri()
	{
		return uri;
	}

	public void setUri(URI uri)
	{
		this.uri = uri;
	}

	public String getinternationalizedLabel()
	{
		String s = label;

		if (FacetType.TECHNICAL.name().equals(type.name()))
		{
			s = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("facet_" + label.toLowerCase());
		}
		else if (FacetType.COLLECTION.name().equals(type.name()))
		{
			s = ((MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class)).getInternationalizedLabels().get(metadataURI);
			if (isNotDefine())
			{
				s = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("facet_not_define");// + " " + s;
			}
		}
		else if (FacetType.SEARCH.name().equals(type.name()))
		{
			s = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("search");
		}

		if (s == null || (label != null && s.equals("facet_" + label.toLowerCase())))
		{
			return label;
		}

		return s;
	}

	public String getNotDefineType()
	{
		return ((MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class)).getInternationalizedLabels().get(metadataURI);
	}

	public boolean isNotDefine()
	{
		if (label == null) return false;
		return (label.toLowerCase().startsWith("no "));
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public FacetType getType() {
		return type;
	}

	public void setType(FacetType type) {
		this.type = type;
	}

	public URI getMetadataURI() {
		return metadataURI;
	}

	public void setMetadataURI(URI metadataURI) {
		this.metadataURI = metadataURI;
	}


}
