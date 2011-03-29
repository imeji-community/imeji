package de.mpg.jena.vo.complextypes;

import java.io.Serializable;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("date")
@Embedded
public class Date extends ComplexType implements Serializable
{
    private String date;
    private long dateTime;
    
    public Date()
    {
        super(ComplexTypes.DATE);
    }

	public String getDate() {
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}
    


    
    
}
