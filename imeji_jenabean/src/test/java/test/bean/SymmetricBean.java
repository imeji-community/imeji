package test.bean;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://jenabean.com/")
public class SymmetricBean {

	@RdfProperty(value = "http://jenabean.com/isNextTo", symmetric = true)
	private Collection<SymmetricBean> adjacent;
	private String id;
	
	public SymmetricBean(String id) {
		this.id = id;
		adjacent = new LinkedList<SymmetricBean>();
	}
	
	@RdfProperty(value = "http://jenabean.com/isNextTo", symmetric = true)
	public Collection<SymmetricBean> getAdjacent() {
		return adjacent;
	}

	public void setAdjacent(Collection<SymmetricBean> adjacent) {
		this.adjacent = adjacent;
	}
	
	public void addAdjacent(SymmetricBean bean) {
		adjacent.add(bean);
	}
	
	@Id
	public String getId() {return id;}
}
