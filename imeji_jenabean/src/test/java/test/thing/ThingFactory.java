package test.thing;

import thewebsemantic.Thing;

import com.hp.hpl.jena.rdf.model.Model;

public class ThingFactory {

	Model m;
	public ThingFactory(Model m) {
		this.m = m;
	}

	public Thing _(String string) {
		return new Thing(string, m);
	}

}
