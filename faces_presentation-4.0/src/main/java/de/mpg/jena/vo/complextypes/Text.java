package de.mpg.jena.vo.complextypes;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("text")
public class Text extends ComplexType
{
    private String text;

    public Text()
    {
        super(ComplexTypes.TEXT);
    }

    public Text(String value)
    {
        super(ComplexTypes.TEXT);
        text = value;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String str)
    {
        text = str;
    }
}
