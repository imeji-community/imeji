/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.admin;

import com.hp.hpl.jena.sparql.sse.Item;
import com.hp.hpl.jena.tdb.solver.stats.StatsCollector;

public class AdminBean 
{
	public AdminBean() {
		// TODO Auto-generated constructor stub
	}
	
	public Item getJenaTDBStats()
	{
		StatsCollector sc = new StatsCollector();
        Item imageStats = null;// StatsCollector.gatherTDB( (GraphTDB) ImejiJena.imageModel.getGraph());
        return imageStats;
	}
	
	public void setJenaTDBStats(Item item)
	{
		//Do nothing;
	}
}
