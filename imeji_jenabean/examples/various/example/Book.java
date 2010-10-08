package example;

import java.util.Date;

import static example.DublinCore.*;
import thewebsemantic.Id;
import thewebsemantic.RdfProperty;

/**
 * Example domain object for storing book info.
 * 
 * RdfProperty annotations are used to indicate the Dublin Core property to bean
 * property bindings. 
 * 
 * Note, all properties need not be bound via the RdfProperty annotation. Take
 * for example the isbn property of this bean, which is mapped as part of the
 * beans own namespace. The Id annotation is important, indicating which
 * property is the "natural key" for the bean. If this were missing Bean2RDF
 * would use the beans hashCode() method instead...which is not ideal since the
 * default hashCode() isn't guaranteed to return a unique integer. own.
 * 
 */
public class Book {
	private String contributor;
	private String coverage;
	private String creator;
	private Date date;
	private String description;
	private String format;
	private String identifier;
	private String language;
	private String publisher;
	private String relation;
	private String rights;
	private String source;
	private String subject;
	private String title;
	private String type;
	private String isbn;

	@Id
	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isdn) {
		this.isbn = isdn;
	}

	@RdfProperty(CONTRIBUTOR)
	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	@RdfProperty(COVERAGE)
	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	@RdfProperty(CREATOR)
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@RdfProperty(DATE)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@RdfProperty(DESCRIPTION)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@RdfProperty(FORMAT)
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@RdfProperty(IDENTIFIER)
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@RdfProperty(LANGUAGE)
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@RdfProperty(PUBLISHER)
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	@RdfProperty(RELATION)
	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	@RdfProperty(RIGHTS)
	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	@RdfProperty(SOURCE)
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@RdfProperty(SUBJECT)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@RdfProperty(TITLE)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@RdfProperty(TYPE)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
