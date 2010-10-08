package test.bean;

import java.util.ArrayList;
import java.util.Collection;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;


@Namespace("http://test#")
public class DeepBean {
	private String someStringData;
	private Collection<DeepBean> children;
	
	@RdfProperty("http://test#hasManyFlees")
	public String getSomeStringData() {
		return someStringData;
	}

	public void setSomeStringData(String someStringData) {
		this.someStringData = someStringData;
	}

	public DeepBean() {	
		children = new ArrayList<DeepBean>();		
	}
	
	@Id
	public String id() {
		return hashCode() + "";
	}

	@RdfProperty("http://test#hasFriend")
	public Collection<DeepBean> getChildren() {
		return children;
	}

	public void setChildren(Collection<DeepBean> children) {
		this.children = children;
	}

	public void addChildren(DeepBean b) {
		children.add(b);
	}
}
