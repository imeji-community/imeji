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
@RdfType("number")
@Embedded
public class Number extends ComplexType implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3880261725840444890L;
	private double number = Double.NaN;

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
