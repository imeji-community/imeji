package test.annon;

import java.util.ArrayList;
import java.util.Collection;

import thewebsemantic.Namespace;
import thewebsemantic.Uri;

@Namespace("http://thewebsemantic.com/")
public class Thing {
	String name;
	Collection<Thing> knows = new ArrayList<Thing>();
	String uri;
	
	public Thing(String uri) {		
		this.uri=uri;
	}
	
	@Uri
	public String uri() {return uri;}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Collection<Thing> getKnows() {
		return knows;
	}

	public void setKnows(Collection<Thing> knows) {
		this.knows = knows;
	}


}
