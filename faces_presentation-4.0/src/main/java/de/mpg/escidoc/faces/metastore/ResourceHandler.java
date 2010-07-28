package de.mpg.escidoc.faces.metastore;

import java.net.URL;
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
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.solver.Explain.InfoLevel;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFTest;

import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.schemas.item.x09.ItemDocument.Item;
import de.mpg.escidoc.faces.metastore.vocabulary.ESCIDOC;
import de.mpg.escidoc.faces.metastore.vocabulary.FACES4;

public class ResourceHandler implements URIS
{
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
            boolean hasMeta = base.contains(ResourceFactory.createResource(BASE_URI + itemId), ResourceFactory.createProperty(PROP_URI, "public-status"));
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
            System.out.println(QueryFor.faceItemRDFProperties(ids));
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
    
    public HashMap<String, String> retrieveFaceItemValues(String id)
    {
        Query query = null;
        String assemblerFile = FACES_DATASET_ASSEMBLER_FILE;
        Dataset dataset = DataFactory.dataset(assemblerFile);
        HashMap<String, String> valueMap = new HashMap<String, String>();
        query = QueryFactory.create(QueryFor.faceItemValuesAll(id));
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
        ResultSet result = qexec.execSelect();
        for (; result.hasNext(); )
        {
            QuerySolution solution = result.nextSolution();
            RDFNode predicate = solution.get("p");
            RDFNode object = solution.get("o");
            valueMap.put(predicate.toString(), object.toString());
        }
        return valueMap;
    }

    public HashMap<Property, RDFNode> facesMetadataMap(Resource emotion, Resource pic_group, Literal identifier,
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
