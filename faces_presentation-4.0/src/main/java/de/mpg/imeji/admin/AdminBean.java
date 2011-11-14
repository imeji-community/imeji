package de.mpg.imeji.admin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.hp.hpl.jena.sparql.sse.Item;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.solver.stats.StatsCollector;
import com.hp.hpl.jena.tdb.store.GraphTDB;

import de.mpg.jena.ImejiJena;

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
