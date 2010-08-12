package de.mpg.escidoc.faces.metastore_test;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.solver.Explain.InfoLevel;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFTest;

import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.schemas.item.x09.ItemDocument.Item;
import de.mpg.escidoc.faces.metastore_test.util.NodeVisitor;
import de.mpg.escidoc.faces.metastore_test.vocabulary.ESCIDOC;
import de.mpg.escidoc.faces.metastore_test.vocabulary.FACES4;

/**
 * Class to access the MPDL Metastore.
 * @author frank
 *
 */
public class ResourceHandler implements URIS
{
    /**
     * Create an item resource in the base Metastore graph.
     * Optionally create a related metadata resource in the Faces metadata graph.
     * @param itemDoc {@link ItemDocument} the ItemDocument to derive the properties from.
     * @param withMetadata {@link Boolean} create a Faces metadata resource.
     * @param metadataMap {@link HashMap} properties and values of Faces metadata.
     */
    public void createFaceItem(ItemDocument itemDoc, boolean withMetadata, HashMap<Property, RDFNode> metadataMap)
    {
        Item item = itemDoc.getItem();
        String id = item.getObjid().substring(item.getObjid().indexOf(":") + 1);
        Model base = DataFactory.model(BASE_MODEL);
        Model face = DataFactory.model(FACE_MODEL);
        base.setNsPrefix("item", BASE_URI);
        base.setNsPrefix("srel", SREL_URI);
        base.setNsPrefix("prop", PROP_URI);
        face.setNsPrefix("face", FACE_MD_URI);
        face.setNsPrefix("terms", TERMS_URI);
        try
        {
            base.begin();
            Resource r = base.createResource(BASE_URI + id);
            if (withMetadata)
            {
                r.addProperty(base.createProperty(BASE_URI + "metadata"), base.createResource(FACE_MD_URI + id));
            }
            r.addProperty(ESCIDOC.Relation.contentmodel, item.getProperties().getContentModel().getObjid());
            r.addProperty(ESCIDOC.Relation.context, item.getProperties().getContext().getObjid());
            r.addProperty(ESCIDOC.Relation.createdby, item.getProperties().getCreatedBy().getObjid());
            r.addProperty(ESCIDOC.publicstatus, item.getProperties().getPublicStatus().toString());
            for (int c = 0; c < item.getComponents().sizeOfComponentArray(); c++)
            {
                r.addProperty(ESCIDOC.Relation.component, item.getComponents().getComponentArray(c).getObjid());
            }
            base.commit();
            if (withMetadata)
            {
                face.begin();
                Resource m = face.createResource(FACE_MD_URI + id);
                for (Entry<Property, RDFNode> e : metadataMap.entrySet())
                {
                    m.addProperty(e.getKey(), e.getValue());
                }
                face.commit();
            }
        }
        finally
        {
            base.close();
            face.close();
        }
    }

    /**
     * Create a Faces metadata resource in the Faces metadata graph.
     * @param itemId id of the item resource.
     * @param metadataMap properties and values of Faces metadata.
     */
    public void createFaceMetadata(String itemId, HashMap<Property, RDFNode> metadataMap)
    {
        Model base = DataFactory.model(BASE_MODEL);
        Model face = DataFactory.model(FACE_MODEL);
        base.setNsPrefix("item", BASE_URI);
        base.setNsPrefix("srel", SREL_URI);
        base.setNsPrefix("prop", PROP_URI);
        face.setNsPrefix("face", FACE_MD_URI);
        face.setNsPrefix("terms", TERMS_URI);
        try
        {
            boolean hasMeta = base.contains(ResourceFactory.createResource(BASE_URI + itemId), ResourceFactory
                    .createProperty(PROP_URI, "public-status"));
            if (hasMeta)
            {
                base.begin();
                Resource r = base.createResource(BASE_URI + itemId);
                r.addProperty(base.createProperty(BASE_URI + "metadata"), base.createResource(FACE_MD_URI + itemId));
                base.commit();
                face.begin();
                Resource m = face.createResource(FACE_MD_URI + itemId);
                for (Entry<Property, RDFNode> e : metadataMap.entrySet())
                {
                    m.addProperty(e.getKey(), e.getValue());
                }
                face.commit();
            }
            else
            {
                System.out.println("FACE item with id " + itemId + " does not exist!");
            }
        }
        finally
        {
            base.close();
            face.close();
        }
    }

    /**
     * Retrieve an RDF/XML representation of Faces item(s).
     * @param ids item id(s).
     * @param properties 
     * @param metadata
     */
    public void retrieveFaceItemRDF(String[] ids, boolean properties, boolean metadata)
    {
        Query query = null;
        String assemblerFile = FACES_DATASET_ASSEMBLER_FILE;
        Dataset dataset = DataFactory.dataset(assemblerFile);
        if (properties && metadata)
        {
            query = QueryFactory.create(QueryFor.faceItemRDFAll(ids));
        }
        else if (properties && !metadata)
        {
            query = QueryFactory.create(QueryFor.faceItemRDFProperties(ids));
        }
        else if (!properties && metadata)
        {
            query = QueryFactory.create(QueryFor.faceItemRDFMetadata(ids));
        }
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        Model result = qexec.execConstruct();
        result.write(System.out, "RDF/XML-ABBREV");
        qexec.close();
    }

    /**
     * Retrieve the values (=objects) of a Faces resource.
     * @param id
     * @param properties
     * @param metadata
     * @return {@link ArrayList}
     */
    public ArrayList<String[]> retrieveFaceItemValues(String id, boolean properties, boolean metadata)
    {
        Query query = null;
        String assemblerFile = FACES_DATASET_ASSEMBLER_FILE;
        Dataset dataset = DataFactory.dataset(assemblerFile);
        ArrayList<String[]> valueMap = new ArrayList<String[]>();
        if (properties && metadata)
        {
            query = QueryFactory.create(QueryFor.faceItemValuesAll(id));
        }
        else if (properties && !metadata)
        {
            query = QueryFactory.create(QueryFor.faceItemValuesProperties(id));
        }
        else if (!properties && metadata)
        {
            query = QueryFactory.create(QueryFor.faceItemValuesMetadata(id));
        }
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        // testing remote access to SPARQL endpoint:
        // QueryExecution qexec =
        // QueryExecutionFactory.sparqlService("http://dev-faces.mpdl.mpg.de:8080/metastore/sparql", query);
        ResultSet result = qexec.execSelect();
        for (; result.hasNext();)
        {
            QuerySolution solution = result.nextSolution();
            RDFNode predicate = solution.get("p");
            RDFNode object = solution.get("o");
            RDFVisitor visitor = new NodeVisitor();
            String key = (String)predicate.visitWith(visitor);
            String value = (String)object.visitWith(visitor);
            String[] entry = new String[] { key, value };
            valueMap.add(entry);
        }
        qexec.close();
        return valueMap;
    }

    /**
     * Update a Faces metadata resource.
     * If the specified property does not exist, it will be added to the resource.
     * Otherwise the existing value will be replaced with newValue.
     * @param id resource id.
     * @param property property to change.
     * @param newValue new value of the specified property.
     */
    public void updateFacesMetadataValue(String id, Property property, RDFNode newValue)
    {
        Model face = DataFactory.model(FACE_MODEL);
        Resource resource = face.getResource(FACE_MD_URI + id);
        if (resource.hasProperty(property))
        {
            Statement stmt = resource.getProperty(property);
            stmt.changeObject(newValue);
        }
        else
        {
            resource.addProperty(property, newValue);
        }
        face.write(System.out, "RDF/XML-ABBREV");
        face.close();
    }

    /**
     * Create a Map with properties and values for Faces metadata resources.
     * @param emotion
     * @param pic_group
     * @param identifier
     * @param age
     * @param age_group
     * @param gender
     * @return {@link HashMap}
     */
    public HashMap<Property, RDFNode> facesMetadataMap(Resource emotion, Resource pic_group, Resource identifier,
            Literal age, Resource age_group, Resource gender)
    {
        HashMap<Property, RDFNode> propMap = new HashMap<Property, RDFNode>();
        propMap.put(FACES4.emotion, emotion);
        propMap.put(FACES4.picture_group, pic_group);
        propMap.put(DC.identifier, identifier);
        propMap.put(FACES4.age, age);
        propMap.put(FACES4.age_group, age_group);
        propMap.put(FACES4.gender, gender);
        return propMap;
    }
}
