/**
 * 
 */
package de.mpg.escidoc.faces.metastore;

/**
 * @author frank
 *
 */
public interface URIS
{
    static final String BASE_MODEL = "/home/frank/TDB/items";
    static final String BASE_URI = "http://dev-coreservice.mpdl.mpg.de/metastore/items/";
    static final String MD_URI = "http://dev-coreservice.mpdl.mpg.de/metastore/metadata/";
    static final String PROP_URI = "http://escidoc.de/core/01/properties/";
    static final String SREL_URI = "http://escidoc.de/core/01/structural-relations/";
    static final String FACE_MODEL = "/home/frank/TDB/md_faces";
    static final String FACE_MD_URI = MD_URI + "faces/";
    static final String TERMS_URI = "http://purl.org/escidoc/metadata/terms/0.1/";
    static final String FACES_DATASET_ASSEMBLER_FILE = "src/main/resources/assembler/namedgraphs4faces.ttl";
}
