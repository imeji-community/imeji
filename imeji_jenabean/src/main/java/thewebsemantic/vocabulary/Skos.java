package thewebsemantic.vocabulary;

import java.util.Collection;

import thewebsemantic.As;
import thewebsemantic.Namespace;
import thewebsemantic.Thing;

@Namespace("http://www.w3.org/2008/05/skos#")
public interface Skos extends As {
	interface ConceptScheme extends Skos{}
	interface Concept extends Skos{}
	Skos narrowerTransitive(Object t);
	Collection<Thing> narrowerTransitive();
	Skos broaderTransitive(Object t);
	Collection<Thing> broaderTransitive();
	Skos note(Object t);
	Collection<Thing> note();
	Skos related(Object t);
	Collection<Thing> related();
	Skos closeMatch(Object t);
	Collection<Thing> closeMatch();
	Skos semanticRelation(Object t);
	Collection<Thing> semanticRelation();
	Skos inScheme(Object t);
	Collection<Thing> inScheme();
	Skos broader(Object t);
	Collection<Thing> broader();
	Skos narrower(Object t);
	Collection<Thing> narrower();
	Skos broadMatch(Object t);
	Collection<Thing> broadMatch();
	Skos scopeNote(Object t);
	Collection<Thing> scopeNote();
	Skos exactMatch(Object t);
	Collection<Thing> exactMatch();
	Skos prefLabel(String a, String lang);
	Collection<String> prefLabel();
	Skos hiddenLabel(Object o);
	Collection<String> hiddenLabel();
	Skos historyNote(Object t);
	Collection<Thing> historyNote();
	Skos editorialNote(Object t);
	Collection<Thing> editorialNote();
	Skos definition(Object t);
	Skos definition(String def, String lang);
	Collection<Thing> definition();
	Skos changeNote(Object t);
	Collection<Thing> changeNote();
	Skos narrowMatch(Object t);
	Collection<Thing> narrowMatch();
	Skos relatedMatch(Object t);
	Collection<Thing> relatedMatch();
	Skos altLabel(Object o, String lang);
	Collection<String> altLabel();
	Skos topConceptOf(Object t);
	Collection<Thing> topConceptOf();
	Skos example(Object t);
	Collection<Thing> example();
	Skos hasTopConcept(Object t);
	Collection<Thing> hasTopConcept();
	Skos member(Object t);
	Collection<Thing> member();
}
