package thewebsemantic.vocabulary;

import java.util.Collection;
import java.util.Date;

import com.hp.hpl.jena.rdf.model.Literal;

import thewebsemantic.As;
import thewebsemantic.Functional;
import thewebsemantic.Namespace;
import thewebsemantic.Thing;

@Namespace("http://purl.org/dc/elements/1.1/")
public interface DublinCore extends As {

	public DublinCore subject(Object s);
	public DublinCore contributor(String s);
	public DublinCore coverage(String s);
	public DublinCore creator(String s);

	@Functional
	public DublinCore date(Date d);
	public DublinCore description(String s);
	public DublinCore format(String s);
	public DublinCore identifier(String s);
	public DublinCore language(String s);
	public DublinCore publisher(String s);
	public DublinCore relation(String s);
	public DublinCore rights(String s);
	public DublinCore source(String s);

	@Functional
	public DublinCore title(String s);
	@Functional
	public DublinCore title(String s, String lang);
	public Collection<Literal> contributor();
	public String coverage();
	public Collection<Literal> creator();
	public Date date();
	public String description();
	public String format();
	public String identifier();
	public String language();
	public String publisher();
	public String relation();
	public String rights();
	public String source();
	public Collection<Literal> subject();
	public Collection<Thing> subject_();
	public String title();
	public String type();
}
