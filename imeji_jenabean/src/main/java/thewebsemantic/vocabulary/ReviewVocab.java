package thewebsemantic.vocabulary;

import java.util.Collection;

import thewebsemantic.As;
import thewebsemantic.Namespace;
import thewebsemantic.Thing;

@Namespace("http://www.purl.org/stuff/rev#")
public interface ReviewVocab extends As {
	   interface Review extends ReviewVocab {}
	   interface Comment extends ReviewVocab {}
	   interface Feedback extends ReviewVocab {}
	   ReviewVocab hasReview(Object t);
	   Collection<Thing> hasReview();
	   ReviewVocab commenter(Object t);
	   Collection<Thing> commenter();
	   ReviewVocab hasComment(Object t);
	   Collection<Thing> hasComment();
	   ReviewVocab hasFeedback(Object t);
	   Collection<Thing> hasFeedback();
	   ReviewVocab rating(Object o);
	   int rating();
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
