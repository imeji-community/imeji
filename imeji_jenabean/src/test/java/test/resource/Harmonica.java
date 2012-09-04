package test.resource;

import java.util.Collection;
import java.util.List;

import thewebsemantic.Resource;
import thewebsemantic.binding.RdfBeanId;

public class Harmonica extends RdfBeanId<Harmonica>{
	
	String name;
	int age;
	Collection<Resource> similarTo;
	List<Resource> differentFrom;

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
	public Collection<Resource> getSimilarTo() {
		return similarTo;
	}
	public void setSimilarTo(Collection<Resource> similarTo) {
		this.similarTo = similarTo;
	}
	public List<Resource> getDifferentFrom() {
		return differentFrom;
	}
	public void setDifferentFrom(List<Resource> differentFrom) {
		this.differentFrom = differentFrom;
	}
}
