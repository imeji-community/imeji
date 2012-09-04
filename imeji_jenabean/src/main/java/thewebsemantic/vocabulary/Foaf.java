package thewebsemantic.vocabulary;

import java.util.Collection;

import thewebsemantic.As;
import thewebsemantic.Functional;
import thewebsemantic.Namespace;
import thewebsemantic.Thing;

@Namespace("http://xmlns.com/foaf/0.1/")
public interface Foaf extends As {
	interface Person extends Foaf {}
	interface Image extends Foaf{}

	Foaf jabberID(String s);

	Collection<String> jabberID();

	Foaf nick(Object string);

	Collection<Thing> nick();

	Foaf dnaChecksum(String s);

	Collection<String> dnaChecksum();

	Foaf topic(Object t);

	Collection<Thing> topic();

	Foaf theme(Object t);

	Collection<Thing> theme();

	Foaf msnChatID(String s);

	Collection<String> msnChatID();

	Foaf familyName(String s);

	Collection<String> familyName();

	Foaf openid(Object t);

	Collection<Thing> openid();

	Foaf schoolHomepage(Object uri);

	Collection<Thing> schoolHomepage();

	Foaf pastProject(Object t);

	Collection<Thing> pastProject();

	Foaf plan(String s);

	Collection<String> plan();

	Foaf myersBriggs(String s);

	Collection<String> myersBriggs();

	Foaf mbox(Object t);

	Collection<Thing> mbox();

	@Functional
	Foaf gender(String s);

	String gender();

	@Functional
	Foaf sha1(Object o);
	String sha1();

	Foaf publications(Object t);

	Collection<Thing> publications();

	Foaf holdsAccount(Object t);

	Collection<Thing> holdsAccount();

	Foaf currentProject(Object t);

	Collection<Thing> currentProject();

	Foaf workInfoHomepage(Object t);

	Collection<Thing> workInfoHomepage();

	Foaf made(Object t);

	Collection<Thing> made();

	Foaf aimChatID(String s);

	Collection<String> aimChatID();

	@Functional
	Foaf primaryTopic(Object t);

	Thing primaryTopic();

	Foaf thumbnail(Object t);

	Collection<Thing> thumbnail();

	Foaf based_near(Object t);

	Collection<Thing> based_near();

	Foaf workplaceHomepage(Object t);

	Collection<Thing> workplaceHomepage();

	Foaf logo(Object t);

	Collection<Thing> logo();

	Foaf weblog(Object t);

	Collection<Thing> weblog();

	Foaf title(Object t);

	Collection<Thing> title();

	Foaf fundedBy(Object t);

	Collection<Thing> fundedBy();

	Foaf depiction(Object t);

	Collection<Thing> depiction();

	Foaf accountServiceHomepage(Object t);

	Collection<Thing> accountServiceHomepage();

	Foaf page(Object t);

	Collection<Thing> page();

	Foaf isPrimaryTopicOf(Object t);

	Collection<Thing> isPrimaryTopicOf();

	Foaf surname(String s);
	Foaf surname_(Object s);
	Collection<String> surname();

	Foaf firstName(String s);
	Foaf firstName_(Object s);
	Collection<String> firstName();

	Foaf homepage(Object uri);

	Collection<Thing> homepage();

	Foaf knows(Object t);

	Collection<Thing> knows();

	Foaf depicts(Object t);

	Collection<Thing> depicts();

	Foaf interest(Object t);

	Collection<Thing> interest();

	Foaf geekcode(String s);

	Collection<String> geekcode();

	Foaf mbox_sha1sum(String s);

	Collection<String> mbox_sha1sum();

	Foaf accountName(String s);

	Collection<String> accountName();

	Foaf membershipClass(Object t);

	Collection<Thing> membershipClass();

	Foaf tipjar(Object t);

	Collection<Thing> tipjar();

	Foaf maker(Object t);

	Collection<Thing> maker();

	Foaf name(String s);
	Foaf name_(Object s);
	Collection<String> name();

	Foaf img(Object t);

	Collection<Thing> img();

	@Functional
	Foaf birthday(String s);

	String birthday();

	Foaf givenname(Object t);

	Collection<Thing> givenname();

	Foaf member(Object t);

	Collection<Thing> member();

	Foaf yahooChatID(String s);

	Collection<String> yahooChatID();

	Foaf icqChatID(String s);

	Collection<String> icqChatID();

	Foaf phone(Object t);

	Collection<Thing> phone();

	Foaf topic_interest(Object t);

	Collection<Thing> topic_interest();
}
