package thewebsemantic.vocabulary;

import java.util.Collection;

import thewebsemantic.As;
import thewebsemantic.Namespace;
import thewebsemantic.Thing;

@Namespace(DCTerms.NS)
public interface DCTerms extends As{

	public static final String NS = "http://purl.org/dc/terms/";

	DCTerms hasFormat(Object t);

	Collection<Thing> hasFormat();

	DCTerms source(Object t);

	Collection<Thing> source();

	DCTerms isVersionOf(Object t);

	Collection<Thing> isVersionOf();

	DCTerms isReplacedBy(Object t);

	Collection<Thing> isReplacedBy();

	DCTerms created(Object o);

	Collection<String> created();

	DCTerms relation(Object t);

	Collection<Thing> relation();

	DCTerms conformsTo(Object t);

	Collection<Thing> conformsTo();

	DCTerms isReferencedBy(Object t);

	Collection<Thing> isReferencedBy();

	DCTerms rights(Object t);

	Collection<Thing> rights();

	DCTerms provenance(Object t);

	Collection<Thing> provenance();

	DCTerms dateAccepted(Object o);

	Collection<String> dateAccepted();

	DCTerms title(Object t);

	Collection<Thing> title();

	DCTerms accessRights(Object t);

	Collection<Thing> accessRights();

	DCTerms description(Object t);

	Collection<Thing> description();

	DCTerms abstract_(Object t);

	Collection<Thing> abstract_();

	DCTerms accrualPolicy(Object t);

	Collection<Thing> accrualPolicy();

	DCTerms medium(Object t);

	Collection<Thing> medium();

	DCTerms replaces(Object t);

	Collection<Thing> replaces();

	DCTerms creator(Object t);

	Collection<Thing> creator();

	DCTerms temporal(Object t);

	Collection<Thing> temporal();

	DCTerms bibliographicCitation(Object o);

	Collection<String> bibliographicCitation();

	DCTerms rightsHolder(Object t);

	Collection<Thing> rightsHolder();

	DCTerms issued(Object o);

	Collection<String> issued();

	DCTerms alternative(Object t);

	Collection<Thing> alternative();

	DCTerms requires(Object t);

	Collection<Thing> requires();

	DCTerms extent(Object t);

	Collection<Thing> extent();

	DCTerms accrualMethod(Object t);

	Collection<Thing> accrualMethod();

	DCTerms dateSubmitted(Object o);

	Collection<String> dateSubmitted();

	DCTerms instructionalMethod(Object t);

	Collection<Thing> instructionalMethod();

	DCTerms type(Object t);

	Collection<Thing> type();

	DCTerms date(Object o);

	Collection<String> date();

	DCTerms contributor(Object t);

	Collection<Thing> contributor();

	DCTerms hasPart(Object t);

	Collection<Thing> hasPart();

	DCTerms language(Object t);

	Collection<Thing> language();

	DCTerms coverage(Object t);

	Collection<Thing> coverage();

	DCTerms identifier(Object o);

	Collection<String> identifier();

	DCTerms tableOfContents(Object t);

	Collection<Thing> tableOfContents();

	DCTerms valid(Object o);

	Collection<String> valid();

	DCTerms educationLevel(Object t);

	Collection<Thing> educationLevel();

	DCTerms references(Object t);

	Collection<Thing> references();

	DCTerms isFormatOf(Object t);

	Collection<Thing> isFormatOf();

	DCTerms subject(Object t);

	Collection<Thing> subject();

	DCTerms dateCopyrighted(Object o);

	Collection<String> dateCopyrighted();

	DCTerms mediator(Object t);

	Collection<Thing> mediator();

	DCTerms hasVersion(Object t);

	Collection<Thing> hasVersion();

	DCTerms format(Object t);

	Collection<Thing> format();

	DCTerms isPartOf(Object t);

	Collection<Thing> isPartOf();

	DCTerms accrualPeriodicity(Object t);

	Collection<Thing> accrualPeriodicity();

	DCTerms publisher(Object t);

	Collection<Thing> publisher();

	DCTerms isRequiredBy(Object t);

	Collection<Thing> isRequiredBy();

	DCTerms license(Object t);

	Collection<Thing> license();

	DCTerms available(Object o);

	Collection<String> available();

	DCTerms spatial(Object t);

	Collection<Thing> spatial();

	DCTerms audience(Object t);

	Collection<Thing> audience();

	DCTerms modified(Object o);

	Collection<String> modified();
}
