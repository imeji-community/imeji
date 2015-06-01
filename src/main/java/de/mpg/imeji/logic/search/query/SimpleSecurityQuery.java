/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search.query;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiNamespaces;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.j2j.helper.J2JHelper;

import java.util.ArrayList;
import java.util.List;

import cern.colt.Arrays;

/**
 * Simple security query add to any imeji sparql query, a security filter
 * (according to user, searchtype, etc)
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SimpleSecurityQuery {
	/**
	 * Static factory for the security query. A {@link String} is returned which
	 * sould be added to the complete sparql query
	 * 
	 * @param user
	 * @param pair
	 * @param rdfType
	 * @param includeWithdrawn
	 * @return
	 */
	public static String queryFactory(User user, String rdfType, Status status,
			boolean isUserSearch) {
		String statusFilter = getStatusAsFilter(status);
		if (Status.RELEASED.equals(status) || Status.WITHDRAWN.equals(status)) {
			// If searching for released or withdrawn objects, no grant filter
			// is needed (released and withdrawn objects
			// are all public)
			
			return statusFilter + " .";
		} else if (user != null && user.getGrants().isEmpty()
				&& Status.PENDING.equals(status)) {
			// special case: a user without grants wants to see private objects:
			// not possible
			return "FILTER(false) .";
		}
		//
		else if ((user == null || user.getGrants().isEmpty())) {
			// if user is null or has no rights, then can only see the released
			// objects
			return "FILTER(?status=<" + Status.RELEASED.getUriString() + ">) .";
		}
		// else, check the grant and add the status filter...
		// return getUserGrantsAsFilter(user, rdfType) + statusFilter +
		// " . ?s a <" + rdfType + "> ";
		String userGrantsAsFilterSimple = getUserGrantsAsFilterSimple(user,
				rdfType, isUserSearch) +  statusFilter + " .";
 	    return userGrantsAsFilterSimple;
		// return getUserGrantsAsFilterSimple(user, rdfType, isUserSearch)
		// + statusFilter + " .";
	}

	/**
	 * Return a SPARQL Filter with the allowed status of an object
	 * 
	 * @param includeWithdrawn
	 * @return
	 */
	private static String getStatusAsFilter(Status status) {
		if (status == null) {
			return "FILTER (?status!=<" + Status.WITHDRAWN.getUriString()
					+ ">)";
		} else {
			return "FILTER (?status=<" + status.getUriString() + ">)";
		}
	}

	/**
	 * Simple Security query (is experimental, must be tested)
	 * 
	 * @param user
	 * @param rdfType
	 * @return
	 */
	private static String getUserGrantsAsFilterSimple(User user,
			String rdfType, boolean isUserSearch) {
		if (user.isAdmin())
			return "";
		
		String appendReleasedFilterStatus = " FILTER (?status=<"+Status.RELEASED.getUriString() + "> ) .";
		String containerFilter = getAllowedContainersFilter(user, rdfType);
		if (!isUserSearch)

			return  !containerFilter.equals("")? (  containerFilter +" . "):appendReleasedFilterStatus;
		else
			return containerFilter.equals("") ? appendReleasedFilterStatus : containerFilter ;

	}

	/**
	 * Return ths SPARQL filter with all container the {@link User} is allowed
	 * to view
	 * 
	 * @param user
	 * @param rdfType
	 * @return
	 */
	private static String getAllowedContainersFilter(User user, String rdfType) {
		List<String> uris = new ArrayList<>();
		if (J2JHelper.getResourceNamespace(new Album()).equals(rdfType)) {
			uris = AuthUtil.getListOfAllowedAlbums(user);
		} else {
			uris = AuthUtil.getListOfAllowedCollections(user);
		}
		
		String s = "";
		boolean addReleasedStatus = uris.size()>0;

		StringBuilder builder = new StringBuilder();
		String allowedContainerString = "";
		if (J2JHelper.getResourceNamespace(new Item()).equals(rdfType)) {
			int i = 0;
			for (String uri:uris){
				i++;
				builder.append((i==1?"{ ":" UNION {") + "?s "+getPredicateName(rdfType) +" <"+uri+"> }");
			}
			allowedContainerString = builder.toString();
			if (addReleasedStatus) {
				s= allowedContainerString + " UNION { ?s <"+ImejiNamespaces.STATUS+"> <"+ Status.RELEASED.getUriString()+"> }"; 
			}
		}
		else if ( (J2JHelper.getResourceNamespace(new CollectionImeji()).equals(
				rdfType)
				|| J2JHelper.getResourceNamespace(new Album()).equals(rdfType)))
		{
			int j = 0;
			for (String uri:uris){
				j++;
				builder.append(" <"+uri+"> " + (j==uris.size()?"":",") );
			}
			allowedContainerString = uris.size() > 0 ? "  FILTER (?s in ("+ builder.toString()+") ":"";
			if (addReleasedStatus && uris.size()>0) {
				s= allowedContainerString + " || ( ?status = <"+ Status.RELEASED.getUriString()+"> )";
				s+=") ";
			}
		}
		
		if (J2JHelper.getResourceNamespace(new Item()).equals(rdfType)) {
			// searching for items. Add to the Filter the item for which the
			// user has extra rights as well as the item which are public
			StringBuilder builderItems = new StringBuilder();
			int itNo = 0;
			List<String> allowedItems = AuthUtil.getListOfAllowedItem(user);
			String allowedItemsString = "";
			if (allowedItems.size()> 0) {
				for (String uri:allowedItems){
					itNo++;
					builderItems.append(" <"+uri+"> "+(itNo==allowedItems.size()?"":",") );
				}
				
				allowedItemsString = " UNION { ?s <"+ImejiNamespaces.STATUS+"> ?status. FILTER (?s in ("+ builderItems.toString()+")) }";
			}
			
			s += allowedItemsString;
		}
		
		if (J2JHelper.getResourceNamespace(new MetadataProfile()).equals(
				rdfType)) {
			// searching for profiles. Add to the Filter the profiles for which the
			// user has extra rights as well as the item which are public
			
			StringBuilder builderProfiles = new StringBuilder();
			int pNo = 0;
			List<String> allowedProfiles = AuthUtil.getListOfAllowedProfiles(user);
			String allowedProfilesString = "";
			if (allowedProfiles.size()> 0) {
				for (String uri:allowedProfiles){
					pNo++;
					builderProfiles.append(" <"+uri+"> "+(pNo==allowedProfiles.size()?"":",") );
				}
				
				allowedProfilesString = " { ?s <"+ImejiNamespaces.STATUS+"> ?status. FILTER (?s in ("+ builderProfiles.toString()+") || ( ?status = <"+ Status.RELEASED.getUriString()+"> )) }";
			}
			
			s += allowedProfilesString;

		}
		return s;
	}

	/**
	 * Return the variable name of for the object on with the security is
	 * checked
	 * 
	 * @param rdfType
	 * @return
	 */
	public static String getVariableName(String rdfType) {
		if (J2JHelper.getResourceNamespace(new CollectionImeji()).equals(
				rdfType)
				|| J2JHelper.getResourceNamespace(new Album()).equals(rdfType)) {
			return "s";
		} else {
			return "c";
		}
	}
	
	/**
	 * Return the predicate for the object on with the security is
	 * checked
	 * 
	 * @param rdfType
	 * @return
	 */
	public static String getPredicateName(String rdfType) {
		if (J2JHelper.getResourceNamespace(new CollectionImeji()).equals(
				rdfType)
				|| J2JHelper.getResourceNamespace(new Album()).equals(rdfType)) {
			return " a ";
		}
		else
		{
			return "<"+ImejiNamespaces.COLLECTION+">";
		}
	}
}
