package thewebsemantic.vocabulary;

import java.util.Collection;

import thewebsemantic.As;
import thewebsemantic.Namespace;
import thewebsemantic.Thing;

@Namespace("http://www.w3.org/2002/12/cal#")
public interface Ical extends As {
	interface Vevent extends Ical {
	}

	Ical tzid(Object o);

	Collection<String> tzid();

	Ical class_(Object o);

	Collection<String> class_();

	Ical method(Object o);

	Collection<String> method();

	Ical transp(Object o);

	Collection<String> transp();

	Ical dtstamp(Object t);

	Collection<Thing> dtstamp();

	Ical dtend(Object t);

	Collection<Thing> dtend();

	Ical repeat(Object o);

	Collection<String> repeat();

	Ical version(Object o);

	Collection<String> version();

	Ical requestStatus(Object o);

	Collection<String> requestStatus();

	Ical categories(Object o);

	Collection<String> categories();

	Ical rrule(Object t);

	Collection<Thing> rrule();

	Ical trigger(Object t);

	Collection<Thing> trigger();

	Ical attendee(Object t);

	Collection<Thing> attendee();

	Ical uid(Object o);

	Collection<String> uid();

	Ical organizer(Object t);

	Collection<Thing> organizer();

	Ical duration(Object t);

	Collection<Thing> duration();

	Ical tzoffsetto(Object o);

	Collection<String> tzoffsetto();

	Ical created(Object t);

	Collection<Thing> created();

	Ical summary(Object o);

	Collection<String> summary();

	Ical description(Object o);

	Collection<String> description();

	Ical priority(Object o);

	Collection<String> priority();

	Ical lastModified(Object t);

	Collection<Thing> lastModified();

	Ical calscale(Object o);

	Collection<String> calscale();

	Ical dtstart(Object t);

	Collection<Thing> dtstart();

	Ical location(Object o);

	Collection<String> location();

	Ical percentComplete(Object o);

	Collection<String> percentComplete();

	Ical tzoffsetfrom(Object o);

	Collection<String> tzoffsetfrom();

	Ical geo(Object t);

	Collection<Thing> geo();

	Ical exrule(Object t);

	Collection<Thing> exrule();

	Ical status(Object o);

	Collection<String> status();

	Ical relatedTo(Object o);

	Collection<String> relatedTo();

	Ical prodid(Object o);

	Collection<String> prodid();

	Ical rdate(Object t);

	Collection<Thing> rdate();

	Ical freebusy(Object t);

	Collection<Thing> freebusy();

	Ical comment(Object o);

	Collection<String> comment();

	Ical action(Object o);

	Collection<String> action();

	Ical resources(Object o);

	Collection<String> resources();

	Ical recurrenceId(Object t);

	Collection<Thing> recurrenceId();

	Ical due(Object t);

	Collection<Thing> due();

	Ical contact(Object o);

	Collection<String> contact();

	Ical completed(Object t);

	Collection<Thing> completed();

	Ical tzname(Object o);

	Collection<String> tzname();

	// Ical X-(Object o);
	// Collection<String> X-();
	Ical exdate(Object t);

	Collection<Thing> exdate();

	Ical sequence(Object o);

	Collection<String> sequence();

	Ical attach(Object t);

	Collection<Thing> attach();

	Ical url(Object t);

	Collection<Thing> url();

	Ical tzurl(Object t);

	Collection<Thing> tzurl();
}
