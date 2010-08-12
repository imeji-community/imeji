package de.mpg.escidoc.faces.metastore.controller;

import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import de.mpg.escidoc.faces.metastore.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.escidoc.faces.metastore.vo.Properties;
import de.mpg.escidoc.faces.metastore.vo.User;
import de.mpg.escidoc.faces.metastore_test.DataFactory;

public class ImejiController {
	
	
	protected static Model base = DataFactory.model("R://imeji_tdb//imeji_data");
	
	/*
	protected static Model base = null;
	static{
		
		
		StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.PostgreSQL) ;
		JDBC.loadDriverPGSQL();
		String jdbcURL = "jdbc:postgresql://dev-coreservice.mpdl.mpg.de:5432/imeji"; 
		SDBConnection conn = new SDBConnection(jdbcURL, "postgres", "postgres") ;
		Store store = SDBFactory.connectStore(conn, storeDesc);
		base = SDBFactory.connectDefaultModel(store);
		

	}
	*/
	
	
	
	/*
	static
	{
		try {
			String path = PropertyReader.getProperty("imeji.tdb.path");
			System.out.println("TDB at: " + path);
			base = DataFactory.model(path);
		} catch (Exception e) {
			
		}
	}
	*/
	protected static Bean2RDF bean2RDF = new Bean2RDF(base);
	protected static RDF2Bean rdf2Bean = new RDF2Bean(base);

	protected static void writeCreateProperties(Properties properties, User user) {
		Date now = new Date();
		properties.setCreatedBy(user);
		properties.setModifiedBy(user);
		properties.setCreationDate(now);
		properties.setLastModificationDate(now);
	}
	
	protected static void writeUpdateProperties(Properties properties, User user) {
		Date now = new Date();
		properties.setModifiedBy(user);
		properties.setLastModificationDate(now);
	}
	
	protected String createQuery(List<SearchCriterion> scList, String type)
	{
		String query = "";

		int j = 0;
		for(SearchCriterion sc : scList)
		{
			ImejiNamespaces ns = sc.getNamespace();
			int i = 0;
			String variablename = "";
			while (ns != null) {
				variablename = "?v" +  String.valueOf(i+1) + String.valueOf(j);
				String lastVariablename = "?v" +  String.valueOf(i) + String.valueOf(j);
				query = ". " + variablename + " <" + ns.getNs() + "> " + lastVariablename + " "  + query;
				ns = ns.getParent();
				i++;
			}
			
			query = query.replaceAll(java.util.regex.Pattern.quote(variablename), "?s");
			j++;
		}
		
		String filter = " . FILTER(";
		j=0;
		for(SearchCriterion sc : scList)
		{
			if (j > 0)
			{
				if(sc.getOperator().equals(SearchCriterion.Operator.AND))
					filter += " && ";
				else if(sc.getOperator().equals(SearchCriterion.Operator.OR))
					filter += " || ";
			}
			
			filter += "regex(?v0" + j + ", '" + sc.getValue() + "')";
			
			j++;
			
		}
		filter+=")";
		
		
		String completeQuery = "SELECT ?s WHERE { ?s a <" + type + "> " + query + filter + " }";
			
		System.out.println("Created Query:\n"+completeQuery);
		return completeQuery;
	}
}
