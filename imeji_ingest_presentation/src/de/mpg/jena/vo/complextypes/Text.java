/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo.complextypes;

import java.io.Serializable;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.ComplexType.ComplexTypes.searchable;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("text")
@Embedded
public class Text extends ComplexType implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5892571071559297072L;
	private String text;

    public Text()
    {
        super(ComplexTypes.TEXT);
    }
    
    @searchable
    public String getText()
    {
        return text;
    }

    public void setText(String str)
    {
        text = str;
    }
}
