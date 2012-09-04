package test.resource;

import thewebsemantic.Resource;
import thewebsemantic.binding.RdfBeanId;

public class Bug extends RdfBeanId<Bug>{
	
	String name;
	int age;
	Resource similarTo;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Resource getSimilarTo() {
		return similarTo;
	}
	public void setSimilarTo(Resource similarTo) {
		this.similarTo = similarTo;
	}

}
