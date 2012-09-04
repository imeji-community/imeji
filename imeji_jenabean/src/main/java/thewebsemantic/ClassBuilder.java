package thewebsemantic;

import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ClassBuilder {
	OntModel m;
	private static String indent = "   ";

	public void create(String uri, String namespace, String type) {
		StringBuilder buffer = new StringBuilder();
		OntModel m = ModelFactory.createOntologyModel();
		m.read(uri);
		//m.write(System.out);
		ExtendedIterator it = m.listOntProperties();
		
		List<OntProperty> props = it.toList();
		for (OntProperty ontProperty : props) { 
			if (ontProperty.getNameSpace().equals(namespace)) {
				if (ontProperty.isDatatypeProperty()) {
					buffer.append(literalSetter(namespace, type, ontProperty));
					buffer.append("\n");
					buffer.append(literalGetter(namespace, type, ontProperty));
					buffer.append("\n");
				} else if (ontProperty.isObjectProperty()) {
					buffer.append(resourceSetter(namespace, type, ontProperty));
					buffer.append("\n");
					buffer.append(resourceGetter(namespace, type, ontProperty));
					buffer.append("\n");
				} else if (RDFS.Literal.equals( ontProperty.getRange())) {
					buffer.append(literalSetter(namespace, type, ontProperty));
					buffer.append("\n");
					buffer.append(literalGetter(namespace, type, ontProperty));
					buffer.append("\n");					
				} else {
					buffer.append(resourceSetter(namespace, type, ontProperty));
					buffer.append("\n");
					buffer.append(resourceGetter(namespace, type, ontProperty));
					buffer.append("\n");
				} 
			}
		}
		System.out.println(buffer);
	}

	private Object resourceGetter(String namespace, String type,
			OntProperty ontProperty) {
		return indent + "public " + resourceCollection(ontProperty)
			+ ontProperty.getURI().substring(namespace.length()) + "();";
		
	}

	private Object resourceSetter(String namespace, String type,
			OntProperty ontProperty) {
		return functional(ontProperty) + indent + "public " + type + " "
			+ ontProperty.getURI().substring(namespace.length())
			+ "(Object t);";
	}

	private Object literalGetter(String namespace, String type,
			OntProperty ontProperty) {
		return indent + "public " + literalCollection(ontProperty)
				+ ontProperty.getURI().substring(namespace.length()) + "();";
	}

	private String literalCollection(OntProperty ontProperty) {
		return (ontProperty.isFunctionalProperty()) ? "String " : "Collection<String> ";
	}
	
	private String resourceCollection(OntProperty ontProperty) {
		return (ontProperty.isFunctionalProperty()) ? "Thing " : "Collection<Thing> ";
	}

	private String literalSetter(String namespace, String type,
			OntProperty ontProperty) {
		return functional(ontProperty) + indent + "public " + type + " "
				+ ontProperty.getURI().substring(namespace.length())
				+ "(Object o);";
	}

	private String functional(OntProperty ontProperty) {
		return (ontProperty.isFunctionalProperty()) ? indent + "@Functional\n" : "";
	}

	public static void main(String[] args) {
		new ClassBuilder().create("file:cim2003.owl",
				"http://iec.ch/TC57/2003/CIM-schema-cim10#", "CIM");
	}
}
