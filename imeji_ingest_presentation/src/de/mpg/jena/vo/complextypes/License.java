/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo.complextypes;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("license")
@Embedded
public class License extends ComplexType implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7299049726766569899L;
	private SimpleDateFormat date;
    private String dateFormat = "dd/mm/yyyy";
    private String license = null;

    public License()
    {
        super(ComplexTypes.LICENSE);
    }

    public License(SimpleDateFormat date)
    {
        super(ComplexTypes.LICENSE);
        this.date = date;
        date.applyPattern(dateFormat);
    }

    public String getDateString()
    {
        return date.format(date);
    }
    
    public String getLicense()
    {
        return license;
    }

    public void setLicense(String str)
    {
        license = str;
    }
}
