package de.mpg.imeji.facet;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class ImagesCollectionFacets 
{
	private List<Facet> facets = new ArrayList<Facet>();
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	
	public ImagesCollectionFacets(CollectionImeji col, List<SearchCriterion> scList) throws Exception 
	{
		ProfileController pc = new ProfileController(sb.getUser());
		MetadataProfile profile = pc.retrieve(col.getProfile());
		
		for (Statement st : profile.getStatements()) 
		{
			// DO FACETS
		}
	}
}
