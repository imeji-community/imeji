package de.mpg.imeji.logic.search.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.vocabulary.RDF;

import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.j2j.helper.J2JHelper;

public class SearchIndexInitializer
{

    public static Map<String, SearchIndex> init()
    {
        System.out.println("Initializing indexes!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
        indexes.putAll(initBasisIndexes());
        indexes.putAll(initMetadataIndexes());
        for (Entry<String, SearchIndex> e : indexes.entrySet())
        {
             System.out.println(e.getKey() + " : " + e.getValue().getNamespace());
        }
        return indexes;
    }

    private static Map<String, SearchIndex> initBasisIndexes()
    {
        Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
        indexes = put(indexes, new SearchIndex(SearchIndex.names.ID_URI.name(), "http://imeji.org/terms/id"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.MY_IMAGES.name(), "http://imeji.org/terms/"));
        /**
         * Properties indexes
         */
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES.name(), "http://imeji.org/terms/properties"));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_CREATED_BY.name(), "http://imeji.org/terms/createdBy", indexes.get(SearchIndex.names.PROPERTIES.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_MODIFIED_BY.name(), "http://imeji.org/terms/modifiedBy", indexes.get(SearchIndex.names.PROPERTIES.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_CREATION_DATE.name(), "http://purl.org/dc/terms/created", indexes.get(SearchIndex.names.PROPERTIES.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_LAST_MODIFICATION_DATE.name(), "http://purl.org/dc/terms/modified", indexes.get(SearchIndex.names.PROPERTIES.name())));
        indexes = put(indexes, new SearchIndex(SearchIndex.names.PROPERTIES_STATUS.name(), "http://imeji.org/terms/status", indexes.get(SearchIndex.names.PROPERTIES.name())));
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

        for (Metadata.Types t : Metadata.Types.values())
        {
            indexes = put(indexes, new SearchIndex("METADATA_" + t.name(), t.getClazzNamespace()));
            for (Field f : J2JHelper.getAllObjectFields(t.getClazz()))
            {
                String namespace = J2JHelper.getNamespace(f);
                indexes = put(indexes,
                        new SearchIndex("METADATA_" + t.name() + "_" + f.getName().toUpperCase(), namespace, indexes.get(t.name())));
            }
        }
        return indexes;
    }

    private static Map<String, SearchIndex> put(Map<String, SearchIndex> map, SearchIndex index)
    {
        map.put(index.getName(), index);
        return map;
    }
}
