package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MetadataSetTO implements Serializable{

	private static final long serialVersionUID = 5826924314949469841L;

	private int position;
	
	private List<LabelTO> Labels;
	
	private MetadataTO value;
	
	private URI statementUri;
	
	private URI typeUri;
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public List<LabelTO> getLabels() {
		return Labels;
	}

	public void setLabels(List<LabelTO> labels) {
		Labels = labels;
	}

	public MetadataTO getValue() {
		return value;
	}

	public void setValue(MetadataTO value) {
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


}
