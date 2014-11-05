package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.net.URI;
import java.util.List;




public class MetadataTO implements  Serializable{

	private static final long serialVersionUID = 910643283709366997L;

	private List<Object> Labels;
	
	private Object value;
	
	private URI statementUri;
	
	private URI typeUri;

	public List<Object> getLabels() {
		return Labels;
	}

	public void setLabels(List<Object> labels) {
		Labels = labels;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public URI getStatementUri() {
		return statementUri;
	}

	public void setStatementUri(URI statementUri) {
		this.statementUri = statementUri;
	}

	public URI getTypeUri() {
		return typeUri;
	}

	public void setTypeUri(URI typeUri) {
		this.typeUri = typeUri;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	


	
	

}
