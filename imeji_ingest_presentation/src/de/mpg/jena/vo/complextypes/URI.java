/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo.complextypes;

import java.io.Serializable;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("uri")
@Embedded
public class URI extends ComplexType implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7932758873634502188L;
	private java.net.URI uri;

    public URI()
    {
        super(ComplexTypes.URI);
    }

    public java.net.URI getUri()
    {
        return uri;
    }

    public void setUri(java.net.URI uri)
    {
        this.uri = uri;
    }
}
