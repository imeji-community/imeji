package de.mpg.jena.vo.md;

public class Text extends ComplexType
{
    private String text;
    
    public Text()
    {
        super(AllowedTypes.TEXT);
    }
    
    public Text(String value)
    {
        super(AllowedTypes.TEXT);
        text = value;
    }
    
    public String getText()
    {
        return text;
    }
}
