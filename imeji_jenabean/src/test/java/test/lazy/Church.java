package test.lazy;

import java.util.Collection;

import thewebsemantic.Id;

public class Church {

	Collection<Person> staff;
	Collection<Person> member;
	
	@Id
	int id;
	
}
