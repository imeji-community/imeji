package test.bean;

import thewebsemantic.binding.RdfBeanId;

public class Molecule extends RdfBeanId<Molecule>{

	Molecule[] neighbors;
	String[] symbols;

	public Molecule() {
		super();
	}
	
	public Molecule(String id) {
		super(id);
	}

	public Molecule[] getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(Molecule[] neighbors) {
		this.neighbors = neighbors;
	}

	public String[] getSymbols() {
		return symbols;
	}

	public void setSymbols(String[] symbols) {
		this.symbols = symbols;
	}

}
