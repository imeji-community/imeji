package de.fub.imeji.ingest.core.beans.metadata.terms;

import org.jdom2.Namespace;

public class RDFTerms {
	
	public static Namespace RDF_NAMESPACE = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	public static Namespace RDFS_NAMESPACE = Namespace.getNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	
	public static Namespace RDF_BOOLEAN_NAMESPACE = Namespace.getNamespace("rdf", "http://www.w3.org/2001/XMLSchema#boolean");
	public static Namespace RDF_STRING_NAMESPACE = Namespace.getNamespace("rdf", "http://www.w3.org/2001/XMLSchema#string");	
	
}
