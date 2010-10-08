package thewebsemantic.vocabulary;

import java.util.Collection;

import thewebsemantic.Namespace;
import thewebsemantic.Thing;

@Namespace(Sioc.NS)
public interface Sioc extends Rdfs {
	
	public static final String NS = "http://rdfs.org/sioc/ns#";
	public interface Community extends Sioc {};
	public interface Container extends Sioc {};
	public interface Forum extends Sioc {};
	public interface Item extends Sioc {};
	public interface Post extends Sioc {};
	public interface Role extends Sioc {};
	public interface Site extends Sioc {};
	public interface Space extends Sioc {};
	public interface Thread extends Sioc {};
	public interface User extends Sioc {};
	public interface UserGroup extends Sioc {};

	Collection<Thing> previous_version();
	Sioc previous_version(Object t);
	Collection<Thing> next_version();
	Sioc next_version(Object t);
	Collection<Thing> topic();
	Sioc topic(Object t);
	Collection<Thing> avatar();
	Sioc avatar(Object t);
	Collection<Thing> links_to();
	Sioc links_to(Object t);
	Collection<Thing> has_subscriber();
	Sioc has_subscriber(Object t);
	Collection<Thing> moderator_of();
	Sioc moderator_of(Object t);
	Collection<Thing> has_creator();
	Sioc has_creator(Object t);
	Collection<Thing> member_of();
	Sioc member_of(Object t);
	Collection<Thing> has_function();
	Sioc has_function(Object t);
	Collection<Thing> subscriber_of();
	Sioc subscriber_of(Object t);
	Collection<Thing> usergroup_of();
	Sioc usergroup_of(Object t);
	Collection<Thing> has_owner();
	Sioc has_owner(Object t);
	Collection<Thing> parent_of();
	Sioc parent_of(Object t);
	Collection<Thing> has_reply();
	Sioc has_reply(Object t);
	Collection<Thing> has_member();
	Sioc has_member(Object t);
	Collection<Thing> has_container();
	Sioc has_container(Object t);
	Collection<Thing> previous_by_date();
	Sioc previous_by_date(Object t);
	Collection<Thing> has_space();
	Sioc has_space(Object t);
	Collection<Thing> has_parent();
	Sioc has_parent(Object t);
	Collection<Thing> next_by_date();
	Sioc next_by_date(Object t);
	Collection<Thing> has_administrator();
	Sioc has_administrator(Object t);
	Collection<Thing> has_usergroup();
	Sioc has_usergroup(Object t);
	Collection<Thing> administrator_of();
	Sioc administrator_of(Object t);
	Collection<Thing> modifier_of();
	Sioc modifier_of(Object t);
	Collection<Thing> scope_of();
	Sioc scope_of(Object t);
	Collection<Thing> has_moderator();
	Sioc has_moderator(Object t);
	Collection<Thing> account_of();
	Sioc account_of(Object t);
	Collection<Thing> latest_version();
	Sioc latest_version(Object t);
	Collection<Thing> container_of();
	Sioc container_of(Object t);
	Collection<Thing> host_of();
	Sioc host_of(Object t);
	Collection<Thing> has_host();
	Sioc has_host(Object t);
	Collection<Thing> has_modifier();
	Sioc has_modifier(Object t);
	Collection<Thing> reply_of();
	Sioc reply_of(Object t);
	Collection<Thing> has_discussion();
	Sioc has_discussion(Object t);
	Collection<Thing> owner_of();
	Sioc owner_of(Object t);
	Collection<Thing> has_scope();
	Sioc has_scope(Object t);
	Collection<Thing> attachment();
	Sioc attachment(Object t);
	Collection<Thing> creator_of();
	Sioc creator_of(Object t);
	Collection<Thing> email();
	Sioc email(Object t);
	Collection<Thing> reference();
	Sioc reference(Object t);
	Collection<Thing> function_of();
	Sioc function_of(Object t);
	Collection<Thing> space_of();
	Sioc space_of(Object t);
	Sioc about(Object resource);

	Collection<Thing> about();

	Sioc content(Object o);

	Object content();

	Sioc num_views(int i);

	int num_views();

	Sioc id(String s);

	String id();

	Sioc ip_address(String s);

	String ip_address();

	Sioc num_replies(int i);

	int num_replies();

	Sioc note(String s);

	String note();

	Sioc email_sha1(String s);

	String email_sha1();

	Sioc name(String s);

	String name();
	
	Sioc description(String s);
	String description();

}
