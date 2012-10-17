package de.fub.imeji.ingest.core.zuse.metadata.terms;


import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Namespace;

import de.fub.imeji.ingest.core.beans.metadata.terms.DCTerms;
import de.fub.imeji.ingest.core.beans.metadata.terms.Default;
import de.fub.imeji.ingest.core.beans.metadata.terms.Terms;
import de.fub.imeji.ingest.core.helper.validator.LocaleValidator;


public class ZuseDCTerms extends DCTerms {
	
	public static Namespace DCTERMS_ZUSE = Namespace.getNamespace("iz","http://zuse.zib.de/imeji/terms#" );
	
	public static Namespace DCTERMS_CREATED_NAMESPACE = Namespace.getNamespace("created","http://purl.org/dc/terms/created" );
	public static Namespace DCTERMS_CREATOR_NAMESPACE = Namespace.getNamespace("creator", "http://purl.org/dc/terms/creator");
	public static Namespace DCTERMS_TYPE_NAMESPACE = Namespace.getNamespace("type", "http://purl.org/dc/terms/type");
	public static Namespace DCTERMS_IDENTIFIER_NAMESPACE = Namespace.getNamespace("identifier", "http://purl.org/dc/terms/identifier");
	public static Namespace DCTERMS_ALTERNATIVE_NAMESPACE = Namespace.getNamespace("alternative", "http://purl.org/dc/terms/alternative");
	public static Namespace DCTERMS_TITLE_NAMESPACE = Namespace.getNamespace("title", "http://purl.org/dc/terms/title");
	public static Namespace DCTERMS_SUBJECT_NAMESPACE = Namespace.getNamespace("subject", "http://purl.org/dc/terms/subject");
	public static Namespace DCTERMS_FORMAT_NAMESPACE = Namespace.getNamespace("format", "http://purl.org/dc/terms/format");
	public static Namespace DCTERMS_MEDIUM_NAMESPACE = Namespace.getNamespace("medium", "http://purl.org/dc/terms/medium");
	public static Namespace DCTERMS_DESCRIPTION_NAMESPACE = Namespace.getNamespace("description", "http://purl.org/dc/terms/description");
	public static Namespace DCTERMS_EXTENT_NAMESPACE = Namespace.getNamespace("extent", "http://purl.org/dc/terms/extent");
	public static Namespace DCTERMS_HASVERSION_NAMESPACE = Namespace.getNamespace("hasVersion", "http://purl.org/dc/terms/hasVersion");
	public static Namespace DCTERMS_HASFORMAT_NAMESPACE = Namespace.getNamespace("hasFormat", "http://purl.org/dc/terms/hasFormat");
	public static Namespace DCTERMS_CONTRIBUTOR_NAMESPACE = Namespace.getNamespace("contributor", "http://purl.org/dc/terms/contributor");
	public static Namespace DCTERMS_SPATIAL_NAMESPACE = Namespace.getNamespace("spatial", "http://purl.org/dc/terms/spatial");
	public static Namespace DCTERMS_SOURCE_NAMESPACE = Namespace.getNamespace("source", "http://purl.org/dc/terms/source");
	
	/**
	 * 
	 * @param termLabel
	 * @param termURI
	 * @param attributes
	 */
	public ZuseDCTerms(String termLabel, String termURI, List<Attribute> attributes) {
		super(termLabel, termURI, attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms TITLE(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_TITLE_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms DESCRIPTION(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();

		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCMITYPE+":Dataset",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}

		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_DESCRIPTION_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms FORMAT(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCTERMS+":PhysicalMedium",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_FORMAT_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms IDENTIFIER(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_IDENTIFIER_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms IDENTIFIERDATASET(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCMITYPE+":Dataset",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_IDENTIFIER_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms IDENTIFIERCOLLECTION(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCMITYPE+":Collection",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_IDENTIFIER_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms ALTERNATIVE(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCMITYPE+":Dataset",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_ALTERNATIVE_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms SUBJECT(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}

		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_SUBJECT_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms CREATOR(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();

		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCTERMS+":Agent",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_CREATOR_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms EXTENT(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_EXTENT_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms EXTENTPHYSICALMEDIUM(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();

		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCTERMS+":PhysicalMedium",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}

		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_EXTENT_NAMESPACE.getPrefix(),ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms EXTENTSIZEORDURATION(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();

		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCTERMS+":SizeOrDuration",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}

		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}

		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_EXTENT_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI() ,attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms MEDIUM(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();

		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCTERMS+":PhysicalMedium",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}

		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_MEDIUM_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms TYPE(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();

		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCMITYPE+":Collection",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}

		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_TYPE_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms CREATED(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCTERMS+":W3CDTF",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_CREATED_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms HASVERSION(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();

		if(type) {
			Attribute attribute = new Attribute("type", ZuseDCTerms.DCMITYPE+":Text",Terms.XSI_NAMESPACE);
			attributes.add(attribute);
		}

		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}

		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_HASVERSION_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms CONTRIBUTOR(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();

		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCTERMS+":Agent",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_CONTRIBUTOR_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms SPATIAL(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCTERMS+":Location",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_SPATIAL_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms SOURCE(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCMITYPE+":Dataset",Terms.XSI_NAMESPACE);
			attributes.add(attributeType);
		}
		
		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);
		
		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_SOURCE_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
	
	/**
	 * 
	 * @param type
	 * @param lang
	 * @return
	 */
	public static ZuseDCTerms HASFORMAT(long id, boolean type, @Default("en") String lang) {
		List<Attribute> attributes = new ArrayList<Attribute>();

		if(type) {
			Attribute attributeType = new Attribute("type", ZuseDCTerms.DCMITYPE+":Image",Terms.XSI_NAMESPACE);		
			attributes.add(attributeType);
		}

		if(lang != null && !lang.isEmpty() && LocaleValidator.checkValidLanguage(lang)) {
			Attribute attributeLang = new Attribute("lang", lang, Namespace.XML_NAMESPACE);
			attributes.add(attributeLang);
		}
		
		Attribute attributeID = new Attribute("id", String.valueOf(id), Namespace.NO_NAMESPACE);
		attributes.add(attributeID);

		return new ZuseDCTerms(ZuseDCTerms.DCTERMS_HASFORMAT_NAMESPACE.getPrefix(), ZuseDCTerms.DCTERMS_NAMESPACE.getURI(), attributes);
	}
}
