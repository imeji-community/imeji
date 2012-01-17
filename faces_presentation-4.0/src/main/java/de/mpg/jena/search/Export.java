/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.search;

import com.hp.hpl.jena.rdf.arp.ARP;
import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.ModelExtract;
import com.hp.hpl.jena.shared.JenaException;

import de.mpg.jena.ImejiJena;


public class Export 
{	
	public String export(SearchResult results)
	{
		String q = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s ? WHERE { ?s a <http://imeji.mpdl.mpg.de/image> ";
		
//		for(String uri : results.getResults())
//		{
//			q += " ?s <http://imeji.mpdl.mpg.de/image/id> <" + uri + "> .";
//		}
//		
		
		
		q += " } LIMIT 2";
		
		
		
//		JenaReader r = new JenaReader();
//		ARP parser = new ARP();
//		parser.
//		r.read(m, in, xmlBase)(ImejiJena.imageModel, results.getResults().get(0));
		
		return ImejiSPARQL.export(q);
	}
}
