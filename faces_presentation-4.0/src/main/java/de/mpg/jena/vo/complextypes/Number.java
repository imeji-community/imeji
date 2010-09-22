package de.mpg.jena.vo.complextypes;

import java.io.Serializable;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("number")
@Embedded
public class Number extends ComplexType implements Serializable
{
    private double number;

    public Number()
    {
        super(ComplexTypes.NUMBER);
    }
    
    public Number(double value)
    {
        super(ComplexTypes.NUMBER);
        setNumber(value);
    }

   

    public void setNumber(double number)
    {
        this.number = number;
    }

    public double getNumber()
    {
        return number;
    }
}
