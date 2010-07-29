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
    // location of the TDB files for the default model
    static final String BASE_MODEL = "/home/frank/TDB/items";
    // URI for the default model (graph)
    static final String BASE_URI = "http://dev-coreservice.mpdl.mpg.de/metastore/items/";
    // base URI for all metadata models (graphs)
    static final String MD_URI = "http://dev-coreservice.mpdl.mpg.de/metastore/metadata/";
    // URI for eSciDoc properties
    static final String PROP_URI = "http://escidoc.de/core/01/properties/";
    // URI for eSciDoc structuralrelations
    static final String SREL_URI = "http://escidoc.de/core/01/structural-relations/";
    // location of the TDB files for the faces metadata model
    static final String FACE_MODEL = "/home/frank/TDB/md_faces";
    // URI for Faces metadata model (graph)
    static final String FACE_MD_URI = MD_URI + "faces/";
    // URI for eSciDoc metadata terms
    static final String TERMS_URI = "http://purl.org/escidoc/metadata/terms/0.1/";
    // location of the assembler file for the Faces dataset
    static final String FACES_DATASET_ASSEMBLER_FILE = "src/main/resources/assembler/namedgraphs4faces.ttl";
}
