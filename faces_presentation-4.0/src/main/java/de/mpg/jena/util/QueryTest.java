/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.util;

import java.util.Iterator;
import java.util.LinkedList;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;

import de.mpg.jena.ImejiJena;
import de.mpg.jena.search.ImejiSPARQL;
import de.mpg.jena.vo.Image;

public class QueryTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ds();
	}

	public static void query()
	{
		String query = "SELECT * WHERE { ?s ?p ?o}";
		Class<Image> c = Image.class;
		System.out.println("model: " + ImejiJena.getModelName(c));
		LinkedList<Image> list = ImejiSPARQL.execAndLoad(query, c);
		Image i = list.getFirst();
		System.out.println(i.getEscidocId());
	}
	
	public static void ds()
	{
		Dataset set = TDBFactory.createDataset("/home/frank/data/imeji_tdb/imeji_data");
		// Model m = set.getNamedModel("image");
		Iterator<String> names = set.listNames();
		while (names.hasNext())
		{
			System.out.println(names.next());
		}
		String query = "SELECT DISTINCT ?s WHERE {?s a <http://imeji.mpdl.mpg.de/image> . ?s <http://imeji.mpdl.mpg.de/properties> ?props . ?props <http://imeji.mpdl.mpg.de/status> ?status . ?s <http://imeji.mpdl.mpg.de/collection> ?coll .FILTER(?status=<http://imeji.mpdl.mpg.de/status/RELEASED>) . ?props <http://imeji.mpdl.mpg.de/creationDate> ?sort0}  ORDER BY DESC(?sort0)  LIMIT 10";
		QueryExecution qexec = QueryExecutionFactory.create(query, set);
		qexec.getContext().set(TDB.symUnionDefaultGraph, true);
		ResultSet rs = qexec.execSelect();
		while (rs.hasNext())
		{
			QuerySolution qs = rs.next();
			System.out.println(qs.getResource("?s").getURI());
		}
	}
}
