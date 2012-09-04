package test.fieldaccess;

import java.net.URI;
import java.util.Collection;

import thewebsemantic.Id;

public class Company {

	@Id
	URI identifier;
	String name;
	Industry industry;
	Collection<Product> products;
	transient String dontsaveme;

}
