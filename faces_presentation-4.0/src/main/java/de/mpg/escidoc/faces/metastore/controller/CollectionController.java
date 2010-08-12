package de.mpg.escidoc.faces.metastore.controller;

import java.util.Collection;
import java.util.List;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.Sparql;

import de.mpg.escidoc.faces.metastore.vo.CollectionImeji;
import de.mpg.escidoc.faces.metastore.vo.User;

public class CollectionController extends ImejiController{

	
	public void create(CollectionImeji ic, User user)
	{
		writeCreateProperties(ic.getProperties(), user);
		base.begin();
		Bean2RDF writer = new Bean2RDF(base);
		writer.saveDeep(ic);
		base.commit();
	}
	
	public Collection<CollectionImeji> retrieveAll(User user)
	{
		RDF2Bean reader = new RDF2Bean(base);
		return reader.load(CollectionImeji.class);
	}
	
	
	
	public Collection<CollectionImeji> search(User user, List<SearchCriterion> scList)
	{
		String query = createQuery(scList, "http://imeji.mpdl.mpg.de/collection");
		return Sparql.exec(base, CollectionImeji.class, query);
	}
	
}
