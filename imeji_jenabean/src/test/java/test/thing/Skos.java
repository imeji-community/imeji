package test.thing;

import java.util.Collection;

import thewebsemantic.As;
import thewebsemantic.Namespace;
import thewebsemantic.Thing;

@Namespace("http://www.w3.org/2008/05/skos#")
public interface Skos extends As {
	interface ConceptScheme extends Skos{}
	interface Concept extends Skos{}
	Skos narrowerTransitive(Thing t);
	Collection<Thing> narrowerTransitive();
	Skos broaderTransitive(Thing t);
	Collection<Thing> broaderTransitive();
	Skos note(Thing t);
	Collection<Thing> note();
	Skos related(Thing t);
	Collection<Thing> related();
	Skos closeMatch(Thing t);
	Collection<Thing> closeMatch();
	Skos semanticRelation(Thing t);
	Collection<Thing> semanticRelation();
	Skos inScheme(Thing t);
	Collection<Thing> inScheme();
	Skos broader(Thing t);
	Collection<Thing> broader();
	Skos narrower(Thing t);
	Collection<Thing> narrower();
	Skos broadMatch(Thing t);
	Collection<Thing> broadMatch();
	Skos scopeNote(Thing t);
	Collection<Thing> scopeNote();
	Skos exactMatch(Thing t);
	Collection<Thing> exactMatch();
	Skos prefLabel(String a, String lang);
	Collection<String> prefLabel();
	Skos hiddenLabel(Object o);
	Collection<String> hiddenLabel();
	Skos historyNote(Thing t);
	Collection<Thing> historyNote();
	Skos editorialNote(Thing t);
	Collection<Thing> editorialNote();
	Skos definition(Thing t);
	Skos definition(String def, String lang);
	Collection<Thing> definition();
	Skos changeNote(Thing t);
	Collection<Thing> changeNote();
	Skos narrowMatch(Thing t);
	Collection<Thing> narrowMatch();
	Skos relatedMatch(Thing t);
	Collection<Thing> relatedMatch();
	Skos altLabel(Object o, String lang);
	Collection<String> altLabel();
	Skos topConceptOf(Thing t);
	Collection<Thing> topConceptOf();
	Skos example(Thing t);
	Collection<Thing> example();
	Skos hasTopConcept(Thing t);
	Collection<Thing> hasTopConcept();
	Skos member(Thing t);
	Collection<Thing> member();
}
