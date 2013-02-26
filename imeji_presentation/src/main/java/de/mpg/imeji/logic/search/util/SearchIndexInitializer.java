package de.mpg.imeji.logic.search.util;

import java.util.HashMap;
import java.util.Map;
import com.hp.hpl.jena.vocabulary.RDF;

import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.vo.Metadata;

/**
 * Initialize the imeji {@link SearchIndex}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchIndexInitializer
{
    /**
     * Initialize all {@link SearchIndex} in imeji
     * 
     * @return
     */
    public static Map<String, SearchIndex> init()
    {
        Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
        indexes.putAll(initBasisIndexes());
        indexes.putAll(initMetadataIndexes());
        return indexes;
    }

    /**
     * Initialize all {@link SearchIndex} which are not related to {@link Metadata}
     * 
     * @return
     */
    private static Map<String, SearchIndex> initBasisIndexes()
    {
        Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
        indexes = put(indexes, new SearchIndex(SearchIndex.names.item.name(), "http://imeji.org/terms/id"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.user.name(), "http://imeji.org/terms/"));
        /**
         * Fulltext search index
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.all.name(), "http://imeji.org/terms/fulltext"));
        /**
         * Properties indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.creator.name(), "http://imeji.org/terms/createdBy"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.editor.name(), "http://imeji.org/terms/modifiedBy"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.created.name(), "http://purl.org/dc/terms/created"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.modified.name(), "http://purl.org/dc/terms/modified"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.status.name(), "http://imeji.org/terms/status"));
        /**
         * Grant indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.grant.name(), "http://xmlns.com/foaf/0.1/grants",
                indexes.get(SearchIndex.names.creator.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.grant_type.name(), "http://imeji.org/terms/grantType",
                indexes.get(SearchIndex.names.grant.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.grant_for.name(), "http://imeji.org/terms/grantFor",
                indexes.get(SearchIndex.names.grant.name())));
        /**
         * Item Indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.filename.name(), "http://imeji.org/terms/filename"));
        indexes = put(indexes,
                new SearchIndex(SearchIndex.names.visibility.name(), "http://imeji.org/terms/visibility"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.mds.name(), "http://imeji.org/terms/metadataSet"));
        /**
         * Collection indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.col.name(), "http://imeji.org/terms/collection"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.prof.name(), "http://imeji.org/terms/mdprofile",
                indexes.get(SearchIndex.names.col.name())));
        /**
         * Container metadata indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.cont_md.name(),
                "http://imeji.org/terms/container/metadata"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.cont_title.name(),
                "http://purl.org/dc/elements/1.1/title", indexes.get(SearchIndex.names.cont_md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.cont_description.name(),
                "http://purl.org/dc/elements/1.1/description", indexes.get(SearchIndex.names.cont_md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.cont_person.name(),
                "http://purl.org/escidoc/metadata/terms/0.1/creator", indexes.get(SearchIndex.names.cont_md.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.cont_person_family.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/family-name", indexes
                                .get(SearchIndex.names.cont_person.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.cont_person_given.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/given-name", indexes
                                .get(SearchIndex.names.cont_person.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.cont_person_name.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/complete-name", indexes
                                .get(SearchIndex.names.cont_person.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.cont_person_org.name(),
                        "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", indexes
                                .get(SearchIndex.names.cont_person.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.cont_person_org_name.name(),
                "http://purl.org/dc/elements/1.1/title", indexes.get(SearchIndex.names.cont_person_org.name())));
        /**
         * Metadata profile indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.profile.name(), "http://imeji.org/terms/mdprofile"));
        /**
         * Image Metadata indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.md.name(), "http://imeji.org/terms/metadata"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.statement.name(), "http://imeji.org/terms/statement"));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.type.name(), RDF.type.getNameSpace(), indexes
                        .get(SearchIndex.names.md.name())));
        return indexes;
    }

    /**
     * Initialized all {@link SearchIndex} related to {@link Metadata}
     * 
     * @return
     */
    private static Map<String, SearchIndex> initMetadataIndexes()
    {
        Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.text.name(), "http://imeji.org/terms/text", indexes
                        .get(SearchIndex.names.md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.number.name(), "http://imeji.org/terms/number",
                indexes.get(SearchIndex.names.md.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.date.name(), "http://imeji.org/terms/date", indexes
                        .get(SearchIndex.names.md.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.time.name(), "http://imeji.org/terms/time", indexes
                        .get(SearchIndex.names.md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.title.name(), "http://purl.org/dc/terms/title",
                indexes.get(SearchIndex.names.md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.longitude.name(), "http://imeji.org/terms/longitude",
                indexes.get(SearchIndex.names.md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.latitude.name(), "http://imeji.org/terms/latitude",
                indexes.get(SearchIndex.names.md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.license.name(), "http://imeji.org/terms/license",
                indexes.get(SearchIndex.names.md.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.link.name(), "http://imeji.org/terms/uri", indexes
                        .get(SearchIndex.names.md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.label.name(),
                "http://www.w3.org/2000/01/rdf-schema#label", indexes.get(SearchIndex.names.md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.citation.name(), "http://imeji.org/terms/citation",
                indexes.get(SearchIndex.names.md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.citation_style.name(),
                "http://imeji.org/terms/citationStyle", indexes.get(SearchIndex.names.md.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.cone.name(), "http://imeji.org/terms/coneId", indexes
                        .get(SearchIndex.names.md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.person.name(), "http://xmlns.com/foaf/0.1/person",
                indexes.get(SearchIndex.names.md.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.person_family.name(),
                "http://purl.org/escidoc/metadata/terms/0.1/family-name", indexes.get(SearchIndex.names.person.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.person_given.name(),
                "http://purl.org/escidoc/metadata/terms/0.1/given-name", indexes.get(SearchIndex.names.person.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.person_id.name(),
                "http://purl.org/dc/elements/1.1/identifier", indexes.get(SearchIndex.names.person.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.person_role.name(),
                "http://purl.org/escidoc/metadata/terms/0.1/role", indexes.get(SearchIndex.names.person.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.person_org.name(),
                        "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", indexes
                                .get(SearchIndex.names.person.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.person_org_title.name(),
                "http://purl.org/dc/terms/title", indexes.get(SearchIndex.names.person_org.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.person_org_id.name(),
                "http://purl.org/dc/terms/identifier", indexes.get(SearchIndex.names.person_org.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.person_org_description.name(),
                "http://purl.org/dc/terms/description", indexes.get(SearchIndex.names.person_org.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.person_org_city.name(),
                "http://purl.org/escidoc/metadata/terms/0.1/city", indexes.get(SearchIndex.names.person_org.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.person_org_country.name(),
                "http://purl.org/escidoc/metadata/terms/0.1/country", indexes.get(SearchIndex.names.person_org.name())));
        return indexes;
    }

    private static Map<String, SearchIndex> put(Map<String, SearchIndex> map, SearchIndex index)
    {
        map.put(index.getName(), index);
        return map;
    }
}
