package de.mpg.imeji.presentation.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.GrantController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.SuperContainerBean;
import de.mpg.imeji.presentation.collection.CollectionListItem;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;

@ManagedBean(name = "PrivateBean")
@SessionScoped
public class PrivateBean extends SuperContainerBean<CollectionListItem> {
	private SessionBean sb;
	@ManagedProperty(value = "#{SessionBean.user}")
	private User user;
	private List<String> emails = new ArrayList<String>();
	private List<CollectionListItem> returnedCollections = new ArrayList<CollectionListItem>();
	private String email;
	private int totalNumberOfRecords;
	private static Logger logger = Logger.getLogger(PrivateBean.class);

	public PrivateBean() {
		super();
		this.sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		if (sb.getUser() == null) {
			try {
				ExternalContext ec = FacesContext.getCurrentInstance()
						.getExternalContext();
				ec.redirect(ec.getRequestContextPath() + "/");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Some IO Exception", e);
			}
		}
	}

	@Override
	public int getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	@Override
	public String getNavigationString() {
		return "pretty:privatePage";
	}

	@Override
	public List<CollectionListItem> retrieveList(int offset, int limit)
			throws Exception {
		CollectionController cc = new CollectionController();
		SearchQuery searchQuery = new SearchQuery();
		SearchPair sp = new SearchPair(
				SPARQLSearch.getIndex(SearchIndex.names.user),
				SearchOperators.EQUALS, user.getId().toString());
		searchQuery.addPair(sp);
		SortCriterion sortCriterion = new SortCriterion();
		sortCriterion.setIndex(SPARQLSearch.getIndex("user"));
		sortCriterion.setSortOrder(SortOrder.valueOf("DESCENDING"));
		SearchResult results = cc.search(searchQuery, sortCriterion, limit,
				offset, user);
		Collection<CollectionImeji> collections = new ArrayList<CollectionImeji>();
		collections = cc
				.retrieveLazy(results.getResults(), limit, offset, user);
		totalNumberOfRecords = results.getNumberOfRecords();
		this.returnedCollections = ImejiFactory.collectionListToListItem(
				collections, user);
		return returnedCollections;
	}

	public String save() {
		if (emailInputValid()) {
			return "";
		} else
			return "";
	}

	/**
	 * Method for Rest button. Reset all form value to empty value
	 */
	public void reset() {
		setEmail("");
		for (CollectionListItem col : returnedCollections) {
			col.setSelected(false);
			col.setSelectedGrant("");
		}
	}

	public boolean emailInputValid() {
		List<String> emails = Arrays.asList(getEmail().split("\\s*;\\s*"));
		for (String e : emails) {
			Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
			Matcher m = p.matcher(e);
			if (!m.matches()) {
				// TODO return validation error on the page
				return false;
			} else {
				if (e.equalsIgnoreCase(user.getEmail())) {
					// TODO can not set Grants to yourself
					return false;
				} else {
					UserController uc = new UserController(Imeji.adminUser);
					try {
						User u = uc.retrieve(e);
						for (CollectionListItem col : returnedCollections) {
							if (col.isSelected()) {
								if ("read".equalsIgnoreCase(col
										.getSelectedGrant())) {
									GrantController gc = new GrantController();
									gc.addGrants(u,
											AuthorizationPredefinedRoles.read(
													col.getId(), col
															.getProfileURI()
															.toString()), u);
								} else if ("upload".equalsIgnoreCase(col
										.getSelectedGrant())) {
									GrantController gc = new GrantController();
									gc.addGrants(u,
											AuthorizationPredefinedRoles
													.upload(col.getUri()
															.toString(), col
															.getProfileURI()
															.toString()), u);
								} else if ("write".equalsIgnoreCase(col
										.getSelectedGrant())) {
									GrantController gc = new GrantController();
									gc.addGrants(u,
											AuthorizationPredefinedRoles.edit(
													col.getUri().toString(),
													col.getProfileURI()
															.toString()), u);
								} else if ("delete".equalsIgnoreCase(col
										.getSelectedGrant())) {
									GrantController gc = new GrantController();
									gc.addGrants(u,
											AuthorizationPredefinedRoles
													.delete(col.getUri()
															.toString(), col
															.getProfileURI()
															.toString()), u);
								} else if ("admin".equalsIgnoreCase(col
										.getSelectedGrant())) {
									GrantController gc = new GrantController();
									gc.addGrants(u,
											AuthorizationPredefinedRoles.admin(
													col.getUri().toString(),
													col.getProfileURI()
															.toString()), u);
								}
							}
						}
					} catch (Exception e1) {
						logger.info("User does not exist, Some strange eMail Input exception ", e1);
						// TODO return user doesn't exits Error on the page
						return false;
					}
				}
			}
		}
		return true;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
