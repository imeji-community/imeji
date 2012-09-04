package test.thing;

import java.net.URI;
import java.util.Collection;

import thewebsemantic.As;
import thewebsemantic.Functional;
import thewebsemantic.Namespace;
import thewebsemantic.Thing;

@Namespace("http://xmlns.com/foaf/0.1/")
public interface Foaf extends As {
	interface Person extends Foaf {
	}

	Foaf jabberID(String s);

	Collection<String> jabberID();

	Foaf nick(Object string);

	Collection<Thing> nick();

	Foaf dnaChecksum(String s);

	Collection<String> dnaChecksum();

	Foaf topic(Thing t);

	Collection<Thing> topic();

	Foaf theme(Thing t);

	Collection<Thing> theme();

	Foaf msnChatID(String s);

	Collection<String> msnChatID();

	Foaf familyName(String s);

	Collection<String> family_name();

	Foaf openid(Thing t);

	Collection<Thing> openid();

	Foaf schoolHomepage(Object uri);

	Collection<Thing> schoolHomepage();

	Foaf pastProject(Thing t);

	Collection<Thing> pastProject();

	Foaf plan(String s);

	Collection<String> plan();

	Foaf myersBriggs(String s);

	Collection<String> myersBriggs();

	Foaf mbox(Thing t);

	Collection<Thing> mbox();

	@Functional
	Foaf gender(String s);

	String gender();

	Foaf sha1(Thing t);

	Collection<Thing> sha1();

	Foaf publications(Thing t);

	Collection<Thing> publications();

	Foaf holdsAccount(Thing t);

	Collection<Thing> holdsAccount();

	Foaf currentProject(Thing t);

	Collection<Thing> currentProject();

	Foaf workInfoHomepage(Object t);

	Collection<Thing> workInfoHomepage();

	Foaf made(Thing t);

	Collection<Thing> made();

	Foaf aimChatID(String s);

	Collection<String> aimChatID();

	@Functional
	Foaf primaryTopic(Thing t);

	Thing primaryTopic();

	Foaf thumbnail(Thing t);

	Collection<Thing> thumbnail();

	Foaf based_near(Thing t);

	Collection<Thing> based_near();

	Foaf workplaceHomepage(Object t);

	Collection<Thing> workplaceHomepage();

	Foaf logo(Thing t);

	Collection<Thing> logo();

	Foaf weblog(Object t);

	Collection<Thing> weblog();

	Foaf title(Object t);

	Collection<Thing> title();

	Foaf fundedBy(Thing t);

	Collection<Thing> fundedBy();

	Foaf depiction(Thing t);

	Collection<Thing> depiction();

	Foaf accountServiceHomepage(Thing t);

	Collection<Thing> accountServiceHomepage();

	Foaf page(Thing t);

	Collection<Thing> page();

	Foaf isPrimaryTopicOf(Thing t);

	Collection<Thing> isPrimaryTopicOf();

	Foaf surname(String s);

	Collection<String> surname();

	Foaf firstName(String s);

	Collection<String> firstName();

	Foaf homepage(Object uri);

	Collection<Thing> homepage();

	Foaf knows(Thing t);

	Collection<Thing> knows();

	Foaf depicts(Thing t);

	Collection<Thing> depicts();

	Foaf interest(Thing t);

	Collection<Thing> interest();

	Foaf geekcode(String s);

	Collection<String> geekcode();

	Foaf mbox_sha1sum(String s);

	Collection<String> mbox_sha1sum();

	Foaf accountName(String s);

	Collection<String> accountName();

	Foaf membershipClass(Thing t);

	Collection<Thing> membershipClass();

	Foaf tipjar(Thing t);

	Collection<Thing> tipjar();

	Foaf maker(Thing t);

	Collection<Thing> maker();

	Foaf name(String s);

	Collection<String> name();

	Foaf img(Thing t);

	Collection<Thing> img();

	@Functional
	Foaf birthday(String s);

	String birthday();

	Foaf givenname(Thing t);

	Collection<Thing> givenname();

	Foaf member(Thing t);

	Collection<Thing> member();

	Foaf yahooChatID(String s);

	Collection<String> yahooChatID();

	Foaf icqChatID(String s);

	Collection<String> icqChatID();

	Foaf phone(Thing t);

	Collection<Thing> phone();

	Foaf topic_interest(Thing t);

	Collection<Thing> topic_interest();
}
