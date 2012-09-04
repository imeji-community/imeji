package test.bean;

import java.util.Collection;

import thewebsemantic.binding.RdfBeanId;

public class FatBean extends RdfBeanId<FatBean> {
	private Collection<String> hamburgers;
	private Collection<String> shakes;
	private Collection<String> steaks;
	private Collection<String> fries;
	private Collection<String> beers;
	
	public FatBean() {super();}
	
	public FatBean(String s) {
		super(s);
	}
	public Collection<String> getHamburgers() {
		return hamburgers;
	}
	public void setHamburgers(Collection<String> hamburgers) {
		this.hamburgers = hamburgers;
	}
	public Collection<String> getShakes() {
		return shakes;
	}
	public void setShakes(Collection<String> shakes) {
		this.shakes = shakes;
	}
	public Collection<String> getSteaks() {
		return steaks;
	}
	public void setSteaks(Collection<String> steaks) {
		this.steaks = steaks;
	}
	public Collection<String> getFries() {
		return fries;
	}
	public void setFries(Collection<String> fries) {
		this.fries = fries;
	}
	public Collection<String> getBeers() {
		return beers;
	}
	public void setBeers(Collection<String> beers) {
		this.beers = beers;
	}
	
	
}
