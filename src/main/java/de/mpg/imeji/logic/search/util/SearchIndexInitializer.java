package de.mpg.imeji.logic.search.util;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.vocabulary.RDF;

import de.mpg.imeji.logic.ImejiNamespaces;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.vo.Metadata;

/**
 * Initialize the imeji {@link SearchIndex}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchIndexInitializer {
	/**
	 * Initialize all {@link SearchIndex} in imeji
	 * 
	 * @return
	 */
	public static Map<String, SearchIndex> init() {
		Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
		indexes.putAll(initBasisIndexes());
		indexes.putAll(initMetadataIndexes());
		return indexes;
	}

	/**
	 * Initialize all {@link SearchIndex} which are not related to
	 * {@link Metadata}
	 * 
	 * @return
	 */
	private static Map<String, SearchIndex> initBasisIndexes() {
		Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
		indexes = put(indexes, new SearchIndex(SearchIndex.IndexNames.item.name(),
				"http://imeji.org/terms/item"));
		indexes = put(indexes, new SearchIndex(SearchIndex.IndexNames.user.name(),
				"http://imeji.org/terms/user"));
		/**
		 * Fulltext search index
		 */
		indexes = put(indexes, new SearchIndex(SearchIndex.IndexNames.all.name(),
				"http://imeji.org/terms/fulltext"));
		/**
		 * Properties indexes
		 */
		indexes = put(indexes, new SearchIndex(
				SearchIndex.IndexNames.creator.name(), ImejiNamespaces.CREATOR));
		indexes = put(indexes, new SearchIndex(SearchIndex.IndexNames.editor.name(),
				ImejiNamespaces.MODIFIED_BY));
		indexes = put(indexes, new SearchIndex(
				SearchIndex.IndexNames.created.name(), ImejiNamespaces.DATE_CREATED));
		indexes = put(indexes,
				new SearchIndex(SearchIndex.IndexNames.modified.name(),
						ImejiNamespaces.LAST_MODIFICATION_DATE));
		indexes = put(indexes, new SearchIndex(SearchIndex.IndexNames.status.name(),
				ImejiNamespaces.STATUS));
		indexes = put(indexes,
				new SearchIndex(SearchIndex.IndexNames.checksum.name(),
						"http://imeji.org/terms/checksum"));
		/**
		 * Grant indexes
		 */
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.grant.name(),
						"http://xmlns.com/foaf/0.1/grants", indexes
								.get(SearchIndex.IndexNames.creator.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.grant_type.name(),
						"http://imeji.org/terms/grantType", indexes
								.get(SearchIndex.IndexNames.grant.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.grant_for.name(),
						"http://imeji.org/terms/grantFor", indexes
								.get(SearchIndex.IndexNames.grant.name())));
		/**
		 * Item Indexes
		 */
		indexes = put(indexes,
				new SearchIndex(SearchIndex.IndexNames.filename.name(),
						"http://imeji.org/terms/filename"));
		indexes = put(indexes,
				new SearchIndex(SearchIndex.IndexNames.filetype.name(),
						"http://imeji.org/terms/filetype"));
		indexes = put(indexes,
				new SearchIndex(SearchIndex.IndexNames.visibility.name(),
						"http://imeji.org/terms/visibility"));
		indexes = put(indexes, new SearchIndex(SearchIndex.IndexNames.mds.name(),
				"http://imeji.org/terms/metadataSet"));
		/**
		 * Collection indexes
		 */
		indexes = put(indexes, new SearchIndex(SearchIndex.IndexNames.col.name(),
				"http://imeji.org/terms/collection"));
		/**
		 * Album indexes
		 */
		indexes = put(indexes, new SearchIndex(SearchIndex.IndexNames.alb.name(),
				"http://imeji.org/terms/album"));
		/**
		 * Container metadata indexes
		 */
		indexes = put(indexes, new SearchIndex(
				SearchIndex.IndexNames.cont_md.name(),
				"http://imeji.org/terms/container/metadata"));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.cont_title.name(),
						"http://purl.org/dc/elements/1.1/title", indexes
								.get(SearchIndex.IndexNames.cont_md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.cont_description.name(),
						"http://purl.org/dc/elements/1.1/description", indexes
								.get(SearchIndex.IndexNames.cont_md.name())));
		indexes = put(indexes,
				new SearchIndex(SearchIndex.IndexNames.cont_person.name(),
						"http://purl.org/escidoc/metadata/terms/0.1/creator",
						indexes.get(SearchIndex.IndexNames.cont_md.name())));
		indexes = put(indexes, new SearchIndex(
				SearchIndex.IndexNames.cont_person_family.name(),
				"http://purl.org/escidoc/metadata/terms/0.1/family-name",
				indexes.get(SearchIndex.IndexNames.cont_person.name())));
		indexes = put(indexes, new SearchIndex(
				SearchIndex.IndexNames.cont_person_given.name(),
				"http://purl.org/escidoc/metadata/terms/0.1/given-name",
				indexes.get(SearchIndex.IndexNames.cont_person.name())));
		indexes = put(indexes, new SearchIndex(
				SearchIndex.IndexNames.cont_person_name.name(),
				"http://purl.org/escidoc/metadata/terms/0.1/complete-name",
				indexes.get(SearchIndex.IndexNames.cont_person.name())));
		indexes = put(
				indexes,
				new SearchIndex(
						SearchIndex.IndexNames.cont_person_org.name(),
						"http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit",
						indexes.get(SearchIndex.IndexNames.cont_person.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.cont_person_org_name.name(),
						"http://purl.org/dc/elements/1.1/title", indexes
								.get(SearchIndex.IndexNames.cont_person_org.name())));
		/**
		 * Metadata profile indexes
		 */
		indexes = put(indexes, new SearchIndex(SearchIndex.IndexNames.prof.name(),
				"http://imeji.org/terms/mdprofile"));
		/**
		 * Image Metadata indexes
		 */
		indexes = put(indexes, new SearchIndex(SearchIndex.IndexNames.md.name(),
				ImejiNamespaces.METADATA));
		indexes = put(indexes,
				new SearchIndex(SearchIndex.IndexNames.statement.name(),
						"http://imeji.org/terms/statement"));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.type.name(), RDF.type
						.getNameSpace(), indexes.get(SearchIndex.IndexNames.md
						.name())));
		return indexes;
	}

	/**
	 * Initialized all {@link SearchIndex} related to {@link Metadata}
	 * 
	 * @return
	 */
	private static Map<String, SearchIndex> initMetadataIndexes() {
		Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.text.name(),
						"http://imeji.org/terms/text", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.number.name(),
						"http://imeji.org/terms/number", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.date.name(),
						"http://imeji.org/terms/date", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.time.name(),
						"http://imeji.org/terms/time", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.title.name(),
						"http://purl.org/dc/terms/title", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.longitude.name(),
						"http://imeji.org/terms/longitude", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.latitude.name(),
						"http://imeji.org/terms/latitude", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.license.name(),
						"http://imeji.org/terms/license", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.url.name(),
						"http://imeji.org/terms/uri", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.label.name(),
						"http://www.w3.org/2000/01/rdf-schema#label", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.citation.name(),
						"http://imeji.org/terms/citation", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.citation_style.name(),
						"http://imeji.org/terms/citationStyle", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.cone.name(),
						"http://imeji.org/terms/coneId", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.person.name(),
						"http://xmlns.com/foaf/0.1/person", indexes
								.get(SearchIndex.IndexNames.md.name())));
		indexes = put(
				indexes,
				new SearchIndex(
						SearchIndex.IndexNames.person_name.name(),
						"http://purl.org/escidoc/metadata/terms/0.1/complete-name",
						indexes.get(SearchIndex.IndexNames.person.name())));
		indexes = put(
				indexes,
				new SearchIndex(
						SearchIndex.IndexNames.person_family.name(),
						"http://purl.org/escidoc/metadata/terms/0.1/family-name",
						indexes.get(SearchIndex.IndexNames.person.name())));
		indexes = put(
				indexes,
				new SearchIndex(
						SearchIndex.IndexNames.person_given.name(),
						"http://purl.org/escidoc/metadata/terms/0.1/given-name",
						indexes.get(SearchIndex.IndexNames.person.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.person_id.name(),
						"http://purl.org/dc/elements/1.1/identifier", indexes
								.get(SearchIndex.IndexNames.person.name())));
		indexes = put(indexes,
				new SearchIndex(SearchIndex.IndexNames.person_role.name(),
						"http://purl.org/escidoc/metadata/terms/0.1/role",
						indexes.get(SearchIndex.IndexNames.person.name())));
		indexes = put(
				indexes,
				new SearchIndex(
						SearchIndex.IndexNames.person_org.name(),
						"http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit",
						indexes.get(SearchIndex.IndexNames.person.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.person_org_title.name(),
						"http://purl.org/dc/terms/title", indexes
								.get(SearchIndex.IndexNames.person_org.name())));
		indexes = put(
				indexes,
				new SearchIndex(SearchIndex.IndexNames.person_org_id.name(),
						"http://purl.org/dc/terms/identifier", indexes
								.get(SearchIndex.IndexNames.person_org.name())));
		indexes = put(
				indexes,
				new SearchIndex(
						SearchIndex.IndexNames.person_org_description.name(),
						"http://purl.org/dc/terms/description", indexes
								.get(SearchIndex.IndexNames.person_org.name())));
		indexes = put(indexes,
				new SearchIndex(SearchIndex.IndexNames.person_org_city.name(),
						"http://purl.org/escidoc/metadata/terms/0.1/city",
						indexes.get(SearchIndex.IndexNames.person_org.name())));
		indexes = put(indexes,
				new SearchIndex(SearchIndex.IndexNames.person_org_country.name(),
						"http://purl.org/escidoc/metadata/terms/0.1/country",
						indexes.get(SearchIndex.IndexNames.person_org.name())));
		return indexes;
	}

	private static Map<String, SearchIndex> put(Map<String, SearchIndex> map,
			SearchIndex index) {
		map.put(index.getName(), index);
		return map;
	}
}
