package de.mpg.jena.vo.md;

public class Number extends ComplexType
{
    private Integer number;

    public Number()
    {
        super(AllowedTypes.NUMBER);
    }
    
    public Number(Integer value)
    {
        super(AllowedTypes.NUMBER);
        number = value;
    }

    public int getInteger()
    {
        return number;
    }
}
