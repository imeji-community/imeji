package de.mpg.imeji.rest.to;

import java.util.ArrayList;
import java.util.List;

public class MetadataProfileTO extends PropertiesTO{

	private static final long serialVersionUID = 1450623577903800529L;

    private String title;
    
    private String description;
    
    private List<StatementTO> statements = new ArrayList<StatementTO>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<StatementTO> getStatements() {
		return statements;
	}

	public void setStatements(List<StatementTO> statements) {
		this.statements = statements;
	}
    
}
