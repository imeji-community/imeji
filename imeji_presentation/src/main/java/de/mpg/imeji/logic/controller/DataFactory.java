///**
// * License: src/main/resources/license/escidoc.license
// */
//
//package de.mpg.imeji.logic.controller;
//
//import java.util.Map;
//import java.util.Map.Entry;
//
//import com.hp.hpl.jena.query.DataSource;
//import com.hp.hpl.jena.query.Dataset;
//import com.hp.hpl.jena.query.DatasetFactory;
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.Property;
//import com.hp.hpl.jena.tdb.TDB;
//import com.hp.hpl.jena.tdb.TDBFactory;
//
///**
// * Helper class to interact with the persistent TDB store.
// * @author frank
// *
// */
//public class DataFactory
//{
//    static Model rdf_model;
//    static Dataset rdf_dataset;
//    static DataSource rdf_datasource;
//    
//    /**
//     * Obtain a Model from the persistent TDB store.
//     * @param path2db (location of the TDB store).
//     * @return {@link Model}
//     */
//    public static Model model(String path2db)
//    {
//        //TDB.setExecutionLoggin(InfoLevel.INFO);
//        TDB.getContext().set(TDB.symLogExec, false) ;
//        TDB.getContext().set(TDB.symUnionDefaultGraph, true) ;
//        rdf_model = TDBFactory.createModel(path2db);
//        return rdf_model;
//    }
//    
//    /**
//     * Remove all data (triples) from the specified TDB store.
//     * @param path2db (location of the TDB store).
//     */
//    public static void removeData(String path2db)
//    {
//       // TDB.setExecutionLogging(InfoLevel.INFO);
//        TDB.getContext().set(TDB.symLogExec, true) ;
//        rdf_model = TDBFactory.createModel(path2db);
//        rdf_model.removeAll();
//    }
//    
//    /**
//     * Remove a Resource from the specified model.
//     * If property is null, the entire resource will be removed.
//     * Otherwise only the specified property will be removed from the resource.
//     * @param path2db (location of the TDB store).
//     * @param uri (base URI of the resource):
//     * @param id (resource id).
//     * @param property (property of the resource). can be null.
//     */
//    public static void removeResource(String path2db, String uri, String id, Property property)
//    {
//        //TDB.setExecutionLogging(InfoLevel.INFO);
//        TDB.getContext().set(TDB.symLogExec, true) ;
//        rdf_model = TDBFactory.createModel(path2db);
//        if (property == null)
//        {
//            rdf_model.removeAll(rdf_model.createResource(uri + id), null, null);
//        }
//        else
//        {
//            rdf_model.removeAll(rdf_model.createResource(uri + id), property, null);
//        }
//    }
//    
//    /**
//     * Obtain a Dataset from the persistent TDB store.
//     * @param assemblerFile (dataset description file).
//     * @return {@link Dataset}
//     */
//    public static Dataset dataset(String assemblerFile)
//    {
//        rdf_dataset = DatasetFactory.assemble(assemblerFile);
//        return rdf_dataset;
//    }
//    
//    /**
//     * Obtain a Dataset from the persistent TDB store.
//     * @param model
//     * @return {@link Dataset}
//     */
//    public static Dataset dataset(Model model)
//    {
//        rdf_dataset = DatasetFactory.assemble(model);
//        return rdf_dataset;
//    }
//    
//    /**
//     * Obtain a Dataset from the persistent TDB store.
//     * @param defaultModel
//     * @param namedModels
//     * @return {@link DataSource}
//     */
//    public static DataSource datasource(Model defaultModel, Map<String, Model> namedModels)
//    {
//        rdf_datasource = DatasetFactory.create();
//        rdf_datasource.setDefaultModel(defaultModel);
//        for (Entry<String, Model> e : namedModels.entrySet())
//        {
//            rdf_datasource.addNamedModel(e.getKey(), e.getValue());
//        }
//        return rdf_datasource;
//    }
//}
