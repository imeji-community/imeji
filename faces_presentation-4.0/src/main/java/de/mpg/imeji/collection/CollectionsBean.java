package de.mpg.imeji.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.beans.SuperContainerBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.search.SearchResult;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Properties.Status;

public class CollectionsBean extends SuperContainerBean<CollectionListItem>
{
	private int totalNumberOfRecords;
	private SessionBean sb;
	private String query = "";

	public CollectionsBean()
	{
		super();
		this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
	}

	@Override
	public String getNavigationString()
	{
		return "pretty:collections";
	}

	@Override
	public int getTotalNumberOfRecords()
	{
		return totalNumberOfRecords;
	}

	@Override
	public List<CollectionListItem> retrieveList(int offset, int limit) throws Exception
	{
		UserController uc = new UserController(sb.getUser());
		//initMenus();
		if (sb.getUser() != null)
		{
			sb.setUser(uc.retrieve(sb.getUser().getEmail()));
		}

		CollectionController controller = new CollectionController(sb.getUser());
		Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();

		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();

		if (getFilter() != null)
		{
			scList.add(getFilter());
		}

		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("q"))
		{
			query = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("q");
		}

		if (!"".equals(query))
		{
			if (query.startsWith("\"") && query.endsWith("\""))
			{
				scList.add(new SearchCriterion(ImejiNamespaces.CONTAINER_METADATA_TITLE, query));
				scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_DESCRIPTION, query, Filtertype.REGEX));
				scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_PERSON_FAMILY_NAME, query, Filtertype.REGEX));
				scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_PERSON_GIVEN_NAME, query, Filtertype.REGEX));
				scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_PERSON_COMPLETE_NAME, query, Filtertype.REGEX));
				scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_PERSON_ORGANIZATION_NAME, query, Filtertype.REGEX));
				scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.COLLECTION_PROFILE, query, Filtertype.URI));
			}
			else
			{
				for (String s : query.split("\\s"))
				{
					scList.add(new SearchCriterion(ImejiNamespaces.CONTAINER_METADATA_TITLE, s));
					scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_DESCRIPTION, s, Filtertype.REGEX));
					scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_PERSON_FAMILY_NAME, s, Filtertype.REGEX));
					scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_PERSON_GIVEN_NAME, s, Filtertype.REGEX));
					scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.CONTAINER_METADATA_PERSON_ORGANIZATION_NAME, s, Filtertype.REGEX));
					scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.COLLECTION_PROFILE, s, Filtertype.URI));
				}
			}
		}

		SortCriterion sortCriterion = new SortCriterion();
		sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
		sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));

		SearchResult results = controller.search(scList, sortCriterion, limit, offset);
		collections = controller.load(results.getResults(), limit, offset);
		totalNumberOfRecords = results.getNumberOfRecords();

		return  ImejiFactory.collectionListToListItem(collections, sb.getUser());
	}

	public SessionBean getSb() 
	{
		return sb;
	}

	public void setSb(SessionBean sb) 
	{
		this.sb = sb;
	}

	public String selectAll() 
	{
		for(CollectionListItem bean: getCurrentPartList())
		{
			if(bean.getStatus() == Status.PENDING.toString())
			{
				bean.setSelected(true);
				if(!(sb.getSelectedCollections().contains(bean.getId())))
				{
					sb.getSelectedCollections().add(bean.getId());
				}
			}
		}
		return "";
	}

	public String selectNone()
	{
		sb.getSelectedCollections().clear();
		return "";
	}

	public String deleteAll() throws Exception
	{
		int count = 0;
		for(URI uri : sb.getSelectedCollections())
		{
			CollectionController collectionController = new CollectionController(sb.getUser());
			CollectionImeji collection = collectionController.retrieve(uri);
			collectionController.delete(collection, sb.getUser());
			count++;
		}
		sb.getSelectedCollections().clear();
		if (count == 0) BeanHelper.warn(sb.getMessage("error_delete_no_collection_selected"));
		else BeanHelper.info(count + " " + sb.getMessage("success_collections_delete"));
		return "pretty:collections";
	}

	public void setQuery(String query)
	{
		this.query  = query;
	}

	public String getQuery()
	{
		return query;
	}
}
