package de.mpg.imeji.logic.search.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.j2j.helper.J2JHelper;

public class SearchIndexInitializer
{
    public static Map<String, SearchIndex> init()
    {
        Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
        indexes.putAll(initBasisIndexes());
        indexes.putAll(initMetadataIndexes());
        for (Entry<String, SearchIndex> e :indexes.entrySet())
        {
           // System.out.println(e.getKey() + " : " + e.getValue().getNamespace());
        }
        return indexes;
    }

    private static Map<String, SearchIndex> initBasisIndexes()
    {
        Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
        indexes.put("ID_URI", new SearchIndex("http://imeji.org/terms/id"));
        indexes.put("MY_IMAGES", new SearchIndex("http://imeji.org/terms/"));
        /**
         * Properties indexes
         */
        indexes.put("PROPERTIES", new SearchIndex("http://imeji.org/terms/properties"));
        indexes.put("PROPERTIES_CREATED_BY", new SearchIndex("http://imeji.org/terms/createdBy", indexes.get("PROPERTIES")));
        indexes.put("PROPERTIES_MODIFIED_BY", new SearchIndex("http://imeji.org/terms/modifiedBy", indexes.get("PROPERTIES")));
        indexes.put("PROPERTIES_CREATION_DATE", new SearchIndex("http://purl.org/dc/terms/created", indexes.get("PROPERTIES")));
        indexes.put("PROPERTIES_LAST_MODIFICATION_DATE", new SearchIndex("http://purl.org/dc/terms/modified", indexes.get("PROPERTIES")));
        indexes.put("PROPERTIES_STATUS", new SearchIndex("http://imeji.org/terms/status", indexes.get("PROPERTIES")));
        /**
         * Grant indexes
         */
        indexes.put("PROPERTIES_CREATED_BY_USER_GRANT", new SearchIndex("http://xmlns.com/foaf/0.1/grants", indexes.get("PROPERTIES_CREATED_BY")));
        indexes.put("PROPERTIES_CREATED_BY_USER_GRANT_TYPE", new SearchIndex("http://imeji.org/terms/grantType", indexes.get("PROPERTIES_CREATED_BY_USER_GRANT")));
        indexes.put("PROPERTIES_CREATED_BY_USER_GRANT_FOR", new SearchIndex("http://imeji.org/terms/grantFor", indexes.get("PROPERTIES_CREATED_BY_USER_GRANT")));
        /**
         * Item Indexes
         */
        indexes.put("IMAGE_FILENAME", new SearchIndex("http://imeji.org/terms/filename"));
        indexes.put("IMAGE_VISIBILITY", new SearchIndex("http://imeji.org/terms/visibility"));
        indexes.put("IMAGE_METADATA_SET", new SearchIndex("http://imeji.org/terms/metadataSet"));
        indexes.put("IMAGE_METADATA", new SearchIndex("http://imeji.org/terms/metadata"));
        /**
         * Collection indexes
         */
        indexes.put("IMAGE_COLLECTION", new SearchIndex("http://imeji.org/terms/collection"));
       
        indexes.put("IMAGE_COLLECTION_PROFILE", new SearchIndex("http://imeji.org/terms/mdprofile", indexes.get("IMAGE_COLLECTION")));
        /**
         * Container metadata indexes
         */
        indexes.put("CONTAINER_METADATA", new SearchIndex("http://imeji.org/terms/container/metadata"));
        indexes.put("CONTAINER_METADATA_TITLE", new SearchIndex("http://purl.org/dc/elements/1.1/title", indexes.get("CONTAINER_METADATA")));
        indexes.put("CONTAINER_METADATA_DESCRIPTION", new SearchIndex("http://purl.org/dc/elements/1.1/description", indexes.get("CONTAINER_METADATA")));
        indexes.put("CONTAINER_METADATA_PERSON", new SearchIndex("http://purl.org/escidoc/metadata/terms/0.1/creator", indexes.get("CONTAINER_METADATA"), true));
        indexes.put("CONTAINER_METADATA_PERSON_FAMILY_NAME", new SearchIndex("http://purl.org/escidoc/metadata/terms/0.1/family-name", indexes.get("CONTAINER_METADATA_PERSON")));
        indexes.put("CONTAINER_METADATA_PERSON_GIVEN_NAME", new SearchIndex("http://purl.org/escidoc/metadata/terms/0.1/given-name", indexes.get("CONTAINER_METADATA_PERSON")));
        indexes.put("CONTAINER_METADATA_PERSON_COMPLETE_NAME", new SearchIndex("http://purl.org/escidoc/metadata/terms/0.1/complete-name", indexes.get("CONTAINER_METADATA_PERSON")));
        indexes.put("CONTAINER_METADATA_PERSON_ORGANIZATION", new SearchIndex("http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit", indexes.get("CONTAINER_METADATA_PERSON"), true));
        indexes.put("CONTAINER_METADATA_PERSON_ORGANIZATION_NAME", new SearchIndex("http://purl.org/dc/elements/1.1/title", indexes.get("CONTAINER_METADATA_PERSON_ORGANIZATION")));
        /**
        * Metadata profile indexes
        */
        indexes.put("COLLECTION_PROFILE", new SearchIndex("http://imeji.org/terms/mdprofile"));
        /**
         * Image Metadata indexes
         */
        indexes.put("IMAGE_METADATA", new SearchIndex("http://imeji.org/terms/metadata"));
        indexes.put("IMAGE_METADATA_STATEMENT", new SearchIndex("http://imeji.org/terms/statement"));
        indexes.put("IMAGE_METADATA_SEARCH", new SearchIndex("http://imeji.org/terms/searchValue"));

        return indexes;
    }

    private static Map<String, SearchIndex> initMetadataIndexes()
    {
        Map<String, SearchIndex> indexes = new HashMap<String, SearchIndex>();
        for (Metadata.Types t : Metadata.Types.values())
        {
            indexes.put(t.name(), new SearchIndex(t.getClazzNamespace()));
            for (Field f : J2JHelper.getAllObjectFields(t.getClazz()))
            {
                String namespace = J2JHelper.getNamespace(f);
                indexes.put(t.name() + "_" + f.getName(),new SearchIndex(namespace, indexes.get(t.name())));
            }
        }
        return indexes;
    }
}
