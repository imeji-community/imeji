package test.error;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Bad {
	
	Set<String> things;
	HashMap<String, String > other;
	
	public HashMap<String, String> getOther() {
		return other;
	}

	public void setOther(HashMap<String, String> other) {
		this.other = other;
	}

	public Bad() {
		things = new HashSet<String>();
		other = new HashMap<String, String>();
	}
	
	public Set<String> getThings() {
		return things;
	}

	public void setThings(Set<String> things) {
		this.things = things;
	}

}
