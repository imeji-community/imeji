package de.mpg.jena.vo.complextypes;

import java.io.Serializable;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("text")
@Embedded
public class Text extends ComplexType implements Serializable
{
    private String text;

    public Text()
    {
        super(ComplexTypes.TEXT);
    }

    /*
     * Do not use constructors with one string parameter, Jenabean has problems with it.
    public Text(String value)
    {
        super(ComplexTypes.TEXT);
        text = value;
    }
*/
    public String getText()
    {
        return text;
    }

    public void setText(String str)
    {
        text = str;
    }
}
