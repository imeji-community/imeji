package test.transi_ent;

import java.net.URI;
import java.util.Collection;
import thewebsemantic.Id;
import thewebsemantic.Transient;

@SuppressWarnings("unused")
public class Company {

	URI identifier;
	String name;
	String dontsaveme;
	int size;
	
	@Transient
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Id
	public URI getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(URI identifier) {
		this.identifier = identifier;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Transient
	public String getDontsaveme() {
		return dontsaveme;
	}
	public void setDontsaveme(String dontsaveme) {
		this.dontsaveme = dontsaveme;
	}

}

