package de.mpg.escidoc.faces.metastore;

import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.solver.Explain.InfoLevel;

public class DataFactory implements URIS
{
    static Model rdf_model;
    static Dataset rdf_dataset;
    static DataSource rdf_datasource;
    
    public static Model model(String path2db)
    {
        TDB.setExecutionLogging(InfoLevel.INFO);
        TDB.getContext().set(TDB.symLogExec, true) ;
        rdf_model = TDBFactory.createModel(path2db);
        return rdf_model;
    }
    
    public static void removeData(String path2db)
    {
        TDB.setExecutionLogging(InfoLevel.INFO);
        TDB.getContext().set(TDB.symLogExec, true) ;
        rdf_model = TDBFactory.createModel(path2db);
        rdf_model.removeAll();
    }
    
    public static void removeResource(String path2db, String id)
    {
        TDB.setExecutionLogging(InfoLevel.INFO);
        TDB.getContext().set(TDB.symLogExec, true) ;
        rdf_model = TDBFactory.createModel(path2db);
        rdf_model.removeAll(rdf_model.createResource(BASE_URI + id), null, null);
    }
    
    public static Dataset dataset(String assemblerFile)
    {
        rdf_dataset = DatasetFactory.assemble(assemblerFile);
        return rdf_dataset;
    }
    
    public static Dataset dataset(Model model)
    {
        rdf_dataset = DatasetFactory.assemble(model);
        return rdf_dataset;
    }
    
    public static DataSource datasource(Model defaultModel, Map<String, Model> namedModels)
    {
        rdf_datasource = DatasetFactory.create();
        rdf_datasource.setDefaultModel(defaultModel);
        for (Entry<String, Model> e : namedModels.entrySet())
        {
            rdf_datasource.addNamedModel(e.getKey(), e.getValue());
        }
        return rdf_datasource;
    }
}
