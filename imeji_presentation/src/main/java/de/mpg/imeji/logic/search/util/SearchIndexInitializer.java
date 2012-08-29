package de.mpg.imeji.logic.search.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.vocabulary.RDF;

import de.mpg.imeji.logic.search.vo.SearchIndex;

public class SearchIndexInitializer
{
    private static Logger logger = Logger.getLogger(SearchIndexInitializer.class);

    public static Map<String, SearchIndex> init()
    {
        Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
        indexes.putAll(initBasisIndexes());
        indexes.putAll(initMetadataIndexes());
        return indexes;
    }

    private static Map<String, SearchIndex> initBasisIndexes()
    {
        Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
        indexes = put(indexes, new SearchIndex(SearchIndex.names.ID_URI.name(), "http://imeji.org/terms/id"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.MY_IMAGES.name(), "http://imeji.org/terms/"));
        /**
         *  Fulltext search index
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.FULLTEXT.name(), "http://imeji.org/terms/fulltext"));
        /**
         * Properties indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_CREATED_BY.name(), "http://imeji.org/terms/createdBy"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_MODIFIED_BY.name(), "http://imeji.org/terms/modifiedBy"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_CREATION_DATE.name(), "http://purl.org/dc/terms/created"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_LAST_MODIFICATION_DATE.name(), "http://purl.org/dc/terms/modified"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_STATUS.name(), "http://imeji.org/terms/status"));
        
        /**
         * Grant indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_CREATED_BY_USER_GRANT.name(), "http://xmlns.com/foaf/0.1/grants", indexes.get(SearchIndex.names.PROPERTIES_CREATED_BY.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_CREATED_BY_USER_GRANT_TYPE.name(), "http://imeji.org/terms/grantType", indexes.get(SearchIndex.names.PROPERTIES_CREATED_BY_USER_GRANT.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_CREATED_BY_USER_GRANT_FOR.name(), "http://imeji.org/terms/grantFor", indexes.get(SearchIndex.names.PROPERTIES_CREATED_BY_USER_GRANT.name())));
        
        /**
         * Item Indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_FILENAME.name(), "http://imeji.org/terms/filename"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_VISIBILITY.name(), "http://imeji.org/terms/visibility"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_SET.name(), "http://imeji.org/terms/metadataSet"));
        
        /**
         * Collection indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_COLLECTION.name(), "http://imeji.org/terms/collection"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_COLLECTION_PROFILE.name(), "http://imeji.org/terms/mdprofile",indexes.get(SearchIndex.names.IMAGE_COLLECTION.name())));

        /**
         * Container metadata indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.CONTAINER_METADATA.name(), "http://imeji.org/terms/container/metadata"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.CONTAINER_METADATA_TITLE.name(), "http://purl.org/dc/elements/1.1/title", indexes.get(SearchIndex.names.CONTAINER_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.CONTAINER_METADATA_DESCRIPTION.name(), "http://purl.org/dc/elements/1.1/description", indexes.get(SearchIndex.names.CONTAINER_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON.name(), "http://purl.org/escidoc/metadata/terms/0.1/creator", indexes.get(SearchIndex.names.CONTAINER_METADATA.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON_FAMILY_NAME.name(), "http://purl.org/escidoc/metadata/terms/0.1/family-name", indexes.get(SearchIndex.names.CONTAINER_METADATA_PERSON.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON_GIVEN_NAME.name(), "http://purl.org/escidoc/metadata/terms/0.1/given-name", indexes.get(SearchIndex.names.CONTAINER_METADATA_PERSON.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON_COMPLETE_NAME.name(), "http://purl.org/escidoc/metadata/terms/0.1/complete-name", indexes.get(SearchIndex.names.CONTAINER_METADATA_PERSON.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON_ORGANIZATION.name(), "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", indexes.get(SearchIndex.names.CONTAINER_METADATA_PERSON.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.CONTAINER_METADATA_PERSON_ORGANIZATION_NAME.name(), "http://purl.org/dc/elements/1.1/title", indexes.get(SearchIndex.names.CONTAINER_METADATA_PERSON_ORGANIZATION.name())));

        /**
         * Metadata profile indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.COLLECTION_PROFILE.name(), "http://imeji.org/terms/mdprofile"));

        /**
         * Image Metadata indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA.name(), "http://imeji.org/terms/metadata"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_STATEMENT.name(), "http://imeji.org/terms/statement"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.IMAGE_METADATA_TYPE_RDF.name(), RDF.type.getNameSpace(), indexes.get(SearchIndex.names.IMAGE_METADATA.name()) ));

        return indexes;
    }

    private static Map<String, SearchIndex> initMetadataIndexes()
    {
        Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
        
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_TEXT", "http://imeji.org/terms/text", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_NUMBER", "http://imeji.org/terms/number", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_DATE", "http://imeji.org/terms/date", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_TIME", "http://imeji.org/terms/time", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_TITLE", "http://imeji.org/terms/title", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_LONGITUDE", "http://imeji.org/terms/longitude", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_LATITUTE", "http://imeji.org/terms/latitude", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_LICENSE", "http://imeji.org/terms/license", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_URI", "http://imeji.org/terms/uri", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_LABEL", "http://www.w3.org/2000/01/rdf-schema#label", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_CITATION", "http://imeji.org/terms/citation", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_CITATIONSTYLE", "http://imeji.org/terms/citationStyle", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_CONEID", "http://imeji.org/terms/coneId", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_PERSON", "http://xmlns.com/foaf/0.1/person", indexes.get(SearchIndex.names.IMAGE_METADATA.name())));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_PERSON_FAMLILYNAME", "http://purl.org/escidoc/metadata/terms/0.1/family-name", indexes.get("IMAGE_METADATA_PERSON")));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_PERSON_GIVENNAME", "http://purl.org/escidoc/metadata/terms/0.1/given-name", indexes.get("IMAGE_METADATA_PERSON")));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_PERSON_IDENTIFIER", "http://purl.org/dc/elements/1.1/identifier", indexes.get("IMAGE_METADATA_PERSON")));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_PERSON_ROLE", "http://purl.org/escidoc/metadata/terms/0.1/role", indexes.get("IMAGE_METADATA_PERSON")));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_PERSON_ORGANIZATION", "http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", indexes.get("IMAGE_METADATA_PERSON")));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_PERSON_ORGANIZATION_TITLE", "http://purl.org/dc/terms/title", indexes.get("IMAGE_METADATA_PERSON_ORGANIZATION")));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_PERSON_ORGANIZATION_IDENTIFIER", "http://purl.org/dc/terms/identifier", indexes.get("IMAGE_METADATA_PERSON_ORGANIZATION")));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_PERSON_ORGANIZATION_DESCRIPTION", "http://purl.org/dc/terms/description", indexes.get("IMAGE_METADATA_PERSON_ORGANIZATION")));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_PERSON_ORGANIZATION_CITY", "http://purl.org/escidoc/metadata/terms/0.1/city", indexes.get("IMAGE_METADATA_PERSON_ORGANIZATION")));
        indexes = put(indexes, new SearchIndex("IMAGE_METADATA_PERSON_ORGANIZATION_COUNTRY", "http://purl.org/escidoc/metadata/terms/0.1/country", indexes.get("IMAGE_METADATA_PERSON_ORGANIZATION")));

        return indexes;
    }

    private static Map<String, SearchIndex> put(Map<String, SearchIndex> map, SearchIndex index)
    {
        map.put(index.getName(), index);
        return map;
    }
}
