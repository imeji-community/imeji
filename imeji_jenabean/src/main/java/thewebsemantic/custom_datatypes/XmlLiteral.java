package thewebsemantic.custom_datatypes;

import java.io.Serializable;

public class XmlLiteral implements Serializable {

	private String xml;
	
	public XmlLiteral(String xml)
	{
		this.xml = xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getXml() {
		return xml;
	}
	
	
}
