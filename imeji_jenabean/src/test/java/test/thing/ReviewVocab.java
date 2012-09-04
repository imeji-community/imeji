package test.thing;

import java.util.Collection;

import com.hp.hpl.jena.rdf.model.Literal;

import thewebsemantic.As;
import thewebsemantic.Functional;
import thewebsemantic.Namespace;
import thewebsemantic.Thing;

@Namespace("http://www.purl.org/stuff/rev#")
public interface ReviewVocab extends As {
	   interface Review extends ReviewVocab {}
	   interface Comment extends ReviewVocab {}
	   interface Feedback extends ReviewVocab {}
	   ReviewVocab hasReview(Thing t);
	   Collection<Thing> hasReview();
	   ReviewVocab commenter(Thing t);
	   Collection<Thing> commenter();
	   ReviewVocab hasComment(Thing t);
	   Collection<Thing> hasComment();
	   ReviewVocab hasFeedback(Thing t);
	   Collection<Thing> hasFeedback();
	   
	   @Functional
	   ReviewVocab rating(Object i);
	   Literal rating();
	   ReviewVocab type(Object o);
	   Collection<String> type();
	   ReviewVocab reviewer(Object t);
	   Collection<Thing> reviewer();
	   ReviewVocab title(Object o);
	   Collection<String> title();
	   ReviewVocab positiveVotes(Object o);
	   Collection<String> positiveVotes();
	   ReviewVocab totalVotes(Object o);
	   Collection<String> totalVotes();
	   ReviewVocab text(Object o);
	   Collection<String> text();
}
