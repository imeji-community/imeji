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
        indexes = put(indexes, new SearchIndex(SearchIndex.names.ID_URI.name(), "http://imeji.org/terms/id"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.MY_IMAGES.name(), "http://imeji.org/terms/"));
        /**
         * Fulltext search index
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.FULLTEXT.name(), "http://imeji.org/terms/fulltext"));
        /**
         * Properties indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_CREATED_BY.name(),
                "http://imeji.org/terms/createdBy"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_MODIFIED_BY.name(),
                "http://imeji.org/terms/modifiedBy"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_CREATION_DATE.name(),
                "http://purl.org/dc/terms/created"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_LAST_MODIFICATION_DATE.name(),
                "http://purl.org/dc/terms/modified"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_STATUS.name(),
                "http://imeji.org/terms/status"));
        /**
         * Grant indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_CREATED_BY_USER_GRANT.name(),
                "http://xmlns.com/foaf/0.1/grants", indexes.get(SearchIndex.names.PROPERTIES_CREATED_BY.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.PROPERTIES_CREATED_BY_USER_GRANT_TYPE.name(),
                        "http://imeji.org/terms/grantType", indexes
                                .get(SearchIndex.names.PROPERTIES_CREATED_BY_USER_GRANT.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.PROPERTIES_CREATED_BY_USER_GRANT_FOR.name(),
                        "http://imeji.org/terms/grantFor", indexes
                                .get(SearchIndex.names.PROPERTIES_CREATED_BY_USER_GRANT.name())));
        /**
         * Item Indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_FILENAME.name(),
                "http://imeji.org/terms/filename"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_VISIBILITY.name(),
                "http://imeji.org/terms/visibility"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_SET.name(),
                "http://imeji.org/terms/metadataSet"));
        /**
         * Collection indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_COLLECTION.name(),
                "http://imeji.org/terms/collection"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_COLLECTION_PROFILE.name(),
                "http://imeji.org/terms/mdprofile", indexes.get(SearchIndex.names.IMAGE_COLLECTION.name())));
        /**
         * Container metadata indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.CONTAINER_METADATA.name(),
                "http://imeji.org/terms/container/metadata"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.CONTAINER_METADATA_TITLE.name(),
                "http://purl.org/dc/elements/1.1/title", indexes.get(SearchIndex.names.CONTAINER_METADATA.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.CONTAINER_METADATA_DESCRIPTION.name(),
                        "http://purl.org/dc/elements/1.1/description", indexes.get(SearchIndex.names.CONTAINER_METADATA
                                .name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/creator", indexes
                                .get(SearchIndex.names.CONTAINER_METADATA.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON_FAMILY_NAME.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/family-name", indexes
                                .get(SearchIndex.names.CONTAINER_METADATA_PERSON.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON_GIVEN_NAME.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/given-name", indexes
                                .get(SearchIndex.names.CONTAINER_METADATA_PERSON.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON_COMPLETE_NAME.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/complete-name", indexes
                                .get(SearchIndex.names.CONTAINER_METADATA_PERSON.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON_ORGANIZATION.name(),
                        "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", indexes
                                .get(SearchIndex.names.CONTAINER_METADATA_PERSON.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON_ORGANIZATION_NAME.name(),
                        "http://purl.org/dc/elements/1.1/title", indexes
                                .get(SearchIndex.names.CONTAINER_METADATA_PERSON_ORGANIZATION.name())));
        /**
         * Metadata profile indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.COLLECTION_PROFILE.name(),
                "http://imeji.org/terms/mdprofile"));
        /**
         * Image Metadata indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA.name(),
                "http://imeji.org/terms/metadata"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_STATEMENT.name(),
                "http://imeji.org/terms/statement"));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.IMAGE_METADATA_TYPE_RDF.name(), RDF.type.getNameSpace(), indexes
                        .get(SearchIndex.names.IMAGE_METADATA.name())));
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
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_TEXT.name(),
                "http://imeji.org/terms/text", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_NUMBER.name(),
                "http://imeji.org/terms/number", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_DATE.name(),
                "http://imeji.org/terms/date", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_TIME.name(),
                "http://imeji.org/terms/time", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_TITLE.name(),
                "http://purl.org/dc/terms/title", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_LONGITUDE.name(),
                "http://imeji.org/terms/longitude", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_LATITUTE.name(),
                "http://imeji.org/terms/latitude", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_LICENSE.name(),
                "http://imeji.org/terms/license", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_URI.name(),
                "http://imeji.org/terms/uri", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_LABEL.name(),
                "http://www.w3.org/2000/01/rdf-schema#label", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_CITATION.name(),
                "http://imeji.org/terms/citation", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_CITATIONSTYLE.name(),
                "http://imeji.org/terms/citationStyle", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_CONEID.name(),
                "http://imeji.org/terms/coneId", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_PERSON.name(),
                "http://xmlns.com/foaf/0.1/person", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.IMAGE_METADATA_PERSON_FAMLILYNAME.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/family-name", indexes
                                .get(SearchIndex.names.IMAGE_METADATA_PERSON.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.IMAGE_METADATA_PERSON_GIVENNAME.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/given-name", indexes
                                .get(SearchIndex.names.IMAGE_METADATA_PERSON.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.IMAGE_METADATA_PERSON_IDENTIFIER.name(),
                        "http://purl.org/dc/elements/1.1/identifier", indexes
                                .get(SearchIndex.names.IMAGE_METADATA_PERSON.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.IMAGE_METADATA_PERSON_ROLE.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/role", indexes
                                .get(SearchIndex.names.IMAGE_METADATA_PERSON.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION.name(),
                        "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", indexes
                                .get(SearchIndex.names.IMAGE_METADATA_PERSON.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION_TITLE.name(),
                        "http://purl.org/dc/terms/title", indexes
                                .get(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION_IDENTIFIER.name(),
                        "http://purl.org/dc/terms/identifier", indexes
                                .get(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION_DESCRIPTION.name(),
                        "http://purl.org/dc/terms/description", indexes
                                .get(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION_CITY.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/city", indexes
                                .get(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION.name())));
        indexes = put(
                indexes,
                new SearchIndex(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION_COUNTRY.name(),
                        "http://purl.org/escidoc/metadata/terms/0.1/country", indexes
                                .get(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION.name())));
        return indexes;
    }

    private static Map<String, SearchIndex> put(Map<String, SearchIndex> map, SearchIndex index)
    {
        map.put(index.getName(), index);
        return map;
    }
}
