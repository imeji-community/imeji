/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.imeji.logic.search.query;

import com.hp.hpl.jena.sparql.pfunction.library.container;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiNamespaces;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.presentation.beans.PropertyBean;
import de.mpg.j2j.helper.J2JHelper;

import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.opensaml.ws.wssecurity.Username;

import java.net.URI;

/**
 * SPARQL queries for imeji
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SPARQLQueries {
	/**
	 * Select all {@link Metadata} which are restricted, according to their
	 * {@link Statement}
	 * 
	 * @return
	 */
	public static String selectMetadataRestricted() {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s ?sort0 WHERE {  ?it a <http://imeji.org/terms/item>"
				+ " . ?it <http://imeji.org/terms/collection> ?sort0"
				+ ". ?it <http://imeji.org/terms/metadataSet> ?mds . ?mds <"
				+ ImejiNamespaces.METADATA
				+ "> ?s . ?s <http://imeji.org/terms/statement> ?st"
				+ " . ?st <http://imeji.org/terms/restricted> ?r  .FILTER(?r='true'^^<http://www.w3.org/2001/XMLSchema#boolean>) }";
	}

	/**
	 * REturn the namespace of a {@link Metadata} if defined in the
	 * {@link MetadataProfile}
	 * 
	 * @param uri
	 * @return
	 */
	public static String selectMetadataNamespace(String uri) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE { <"
				+ uri
				+ "> <http://imeji.org/terms/statement> ?st . ?st <http://imeji.org/terms/namespace> ?s }";
	}

	/**
	 * Select default {@link MetadataProfile}
	 *
	 * @return
	 */
	public static String selectDefaultMetadataProfile() {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE { "
				+ "?s a <http://imeji.org/terms/mdprofile> . ?s <http://imeji.org/terms/default> true"
				+ "}";
	}

	/**
	 * Select {@link MetadataProfile} which are not used by any
	 * {@link CollectionImeji}
	 * 
	 * @return
	 */
	public static String selectUnusedMetadataProfiles() {
		return " SELECT DISTINCT ?s WHERE { "
				+ "?s a <http://imeji.org/terms/mdprofile> . not exists{?c <http://imeji.org/terms/mdprofile> ?s}"
				+ "}";
	}

	/**
	 * Checks if provided {@link MetadataProfile} uri has other references than
	 * the reference in the provided resource {@link CollectionImeji}
	 * 
	 * @return
	 */
	public static String hasOtherMetadataProfileReferences(String profileUri,
			String resourceUri) {
		return " SELECT ?s WHERE { "
				+ "?s <http://imeji.org/terms/mdprofile> <" + profileUri + ">."
				+ " FILTER (?s != <" + resourceUri + ">  && ?s != <"
				+ profileUri + "> )} LIMIT 1";

	}

	/**
	 * Checks if provided {@link MetadataProfile} uri has any collection
	 * references {@link CollectionImeji}
	 * 
	 * @return
	 */
	public static String hasMetadataProfileReferences(String profileUri) {
		return " SELECT ?s WHERE { "
				+ "?s <http://imeji.org/terms/mdprofile> <" + profileUri + ">."
				+ " FILTER (?s != <" + profileUri + "> )} LIMIT 1";

	}

	/**
	 * Select all {@link Username}
	 * 
	 * @return
	 */
	public static String selectUserAll(String name) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <http://imeji.org/terms/user> . ?s <http://xmlns.com/foaf/0.1/name> ?name . ?s <http://xmlns.com/foaf/0.1/email> ?email. filter(regex(?name, '"
				+ name + "','i') || regex(?email, '" + name + "','i'))}";
	}

	/**
	 * Select all {@link Username}
	 * 
	 * @return
	 */
	public static String selectOrganizationByName(String name) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?person <http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit> ?s . ?s <http://purl.org/dc/terms/title> ?name . filter(regex(?name, '"
				+ name + "','i'))}";
	}

	/**
	 * Select all {@link Username}
	 * 
	 * @return
	 */
	public static String selectPersonByName(String name) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s <http://purl.org/escidoc/metadata/terms/0.1/complete-name>  ?name . filter(regex(?name, '"
				+ name + "','i'))}";
	}

	/**
	 * Select a User by its Email
	 * 
	 * @param email
	 * @return
	 */
	public static String selectUserByEmail(String email) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE { ?s <http://xmlns.com/foaf/0.1/email> ?email . filter(?email='"
				+ email + "')}";
	}

	/**
	 * Find all the user which have SysAdmin rights for imeji
	 * 
	 * @return
	 */
	public static String selectUserSysAdmin() {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {OPTIONAL{ ?s <http://imeji.org/terms/grant> ?g . ?g <http://imeji.org/terms/grantType> <"
				+ AuthUtil.toGrantTypeURI(GrantType.ADMIN).toString()
				+ ">. ?g <http://imeji.org/terms/grantFor> <"
				+ PropertyBean.baseURI()
				+ ">} . filter(bound(?g)) . ?s a <http://imeji.org/terms/user>}";
	}

	/**
	 * Select {@link User} having a {@link Grant} for an object defined by its
	 * uri
	 * 
	 * @return
	 */
	public static String selectUserWithGrantFor(String uri) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {OPTIONAL{ ?s <http://imeji.org/terms/grant> ?g . ?g <http://imeji.org/terms/grantFor> <"
				+ uri
				+ ">} . filter(bound(?g)) . ?s a <http://imeji.org/terms/user> . ?s <http://xmlns.com/foaf/0.1/name> ?name } ORDER BY DESC(?name)";
	}

	/**
	 * Return th {@link UserGroup} which have {@link Grant} for the object
	 * defined by the passed uri
	 * 
	 * @param uri
	 * @return
	 */
	public static String selectUserGroupWithGrantFor(String uri) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {OPTIONAL{ ?s <http://imeji.org/terms/grant> ?g . ?g <http://imeji.org/terms/grantFor> <"
				+ uri
				+ ">} . filter(bound(?g)) . ?s a <http://imeji.org/terms/userGroup> . ?s <http://xmlns.com/foaf/0.1/name> ?name } ORDER BY DESC(?name)";
	}

	/**
	 * Select all {@link UserGroup}
	 * 
	 * @return
	 */
	public static String selectUserGroupAll(String name) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <http://imeji.org/terms/userGroup> . ?s <http://xmlns.com/foaf/0.1/name> ?name . filter(regex(?name, '"
				+ name + "','i'))}";
	}

	/**
	 * Select all {@link UserGroup}
	 * 
	 * @return
	 */
	public static String selectUserGroupOfUser(User user) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <http://imeji.org/terms/userGroup> . ?s <http://xmlns.com/foaf/0.1/member> <"
				+ user.getId().toString() + ">}";
	}

	/**
	 * Select all admin users
	 * 
	 * @return
	 */
	public static String selectAdminUser() {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s "
				+ "WHERE { ?s <http://imeji.org/terms/grantType>ADMIN}";
	}

	/**
	 * Select Users to be notified by file download Note: Current
	 * <code>user</code> is excluded from the result set
	 *
	 * @return
	 * @param user
	 * @param c
	 */
	public static String selectUsersToBeNotifiedByFileDownload(User user,
			CollectionImeji c) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> "
				+ "SELECT DISTINCT ?s WHERE {"
				+ "filter(?c='"
				+ ObjectHelper.getId(c.getId())
				+ "'"
				+ (user != null ? " && ?s!=<" + user.getId().toString() + "> "
						: "")
				+ ") . ?s <http://imeji.org/terms/observedCollections> ?c }";
	}

	/**
	 * @param fileUrl
	 * @return
	 */
	public static String selectCollectionIdOfFile(String fileUrl) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {"
				+ "optional{"
				+ "?it <http://imeji.org/terms/webImageUrl> <"
				+ fileUrl
				+ ">} . optional {?it <http://imeji.org/terms/thumbnailImageUrl> <"
				+ fileUrl
				+ ">} . optional{ ?it <http://imeji.org/terms/fullImageUrl> <"
				+ fileUrl
				+ ">}"
				+ " . ?it <http://imeji.org/terms/collection> ?s . } LIMIT 1 ";
	}

	/**
	 * @param fileUrl
	 * @return
	 */
	public static String selectItemIdOfFile(String fileUrl) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {"
				+ "optional{"
				+ "?s <http://imeji.org/terms/webImageUrl> <"
				+ fileUrl
				+ ">} . optional {?s <http://imeji.org/terms/thumbnailImageUrl> <"
				+ fileUrl
				+ ">} . optional{ ?s <http://imeji.org/terms/fullImageUrl> <"
				+ fileUrl + ">}} LIMIT 1 ";
	}

	/**
	 * @param id
	 * @return
	 */
	public static String selectAlbumIdOfFile(String id) {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {"
				+ " ?s a <http://imeji.org/terms/album> . ?s <http://imeji.org/terms/item> <"
				+ id + "> } ";
	}

	/**
	 * Select all {@link Metadata} which are not related to a statement. Happens
	 * when a {@link Statement} is removed from a {@link MetadataProfile}
	 * 
	 * @return
	 */
	public static String selectMetadataUnbounded() {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s ?sort0 WHERE {?mds <"
				+ ImejiNamespaces.METADATA
				+ "> ?s"
				+ " . ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?sort0 . ?s <http://imeji.org/terms/statement> ?st"
				+ " . not exists{?p a <http://imeji.org/terms/mdprofile> . ?p <http://imeji.org/terms/statement> ?st}}";
	}

	/**
	 * Select all {@link Statement} which are not bounded to a
	 * {@link MetadataProfile}. Should not happen...
	 * 
	 * @return
	 */
	public static String selectStatementUnbounded() {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s <http://purl.org/dc/terms/type> ?type"
				+ " . not exists{ ?p a <http://imeji.org/terms/mdprofile> . ?p <http://imeji.org/terms/statement> ?s}}";
	}

	/**
	 * Select all {@link Grant} which are not valid anymore. For instance, when
	 * a {@link User}, or an object ( {@link CollectionImeji}, {@link Album}) is
	 * deleted, some related {@link Grant} might stay in the database, even if
	 * they are not needed anymore.
	 * 
	 * @return
	 */
	public static String selectGrantWithoutUser() {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE { ?s <http://imeji.org/terms/grantType> ?type"
				+ " . not exists{ ?user <http://imeji.org/terms/grant> ?s}}";
	}

	/**
	 * Select {@link Grant} which don't have any triple
	 * 
	 * @return
	 */
	public static String selectGrantEmtpy() {
		return "SELECT DISTINCT ?s WHERE {?user <http://imeji.org/terms/grant> ?s . not exists{?s ?p ?o}}";
	}

	/**
	 * Remove the emtpy Grants
	 * 
	 * @return
	 */
	public static String removeGrantEmtpy() {
		return "WITH <http://imeji.org/user> DELETE {?user <http://imeji.org/terms/grant> ?s} USING <http://imeji.org/user> WHERE {?user <http://imeji.org/terms/grant> ?s . not exists{?s ?p ?o}}";
	}

	/**
	 * Select Grant which don't have a grantfor
	 * 
	 * @return
	 */
	public static String selectGrantBroken() {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s <http://imeji.org/terms/grantFor> ?for"
				+ " . not exists{?for ?p ?o} .filter (?for!= <http://imeji.org/> &&  ?for != <"
				+ PropertyBean.baseURI() + ">)}";
	}

	/**
	 * Delete all grants granting for the given uri
	 * 
	 * @param uri
	 * @return
	 */
	public static String updateRemoveGrantsFor(String uri) {
		return "WITH <http://imeji.org/user> DELETE {?user <http://imeji.org/terms/grant> ?s . ?s ?p ?o } "
				+ "USING <http://imeji.org/user>  WHERE {?user <http://imeji.org/terms/grant> ?s . ?s <http://imeji.org/terms/grantFor> <"
				+ uri + "> . ?s ?p ?o}";
	}

	/**
	 * Select all {@link CollectionImeji} available imeji
	 * 
	 * @return
	 */
	public static String selectCollectionAll() {
		return "SELECT ?s WHERE { ?s a <http://imeji.org/terms/collection>}";
	}

	/**
	 * Select all {@link Album} available imeji
	 * 
	 * @return
	 */
	public static String selectAlbumAll() {
		return "SELECT ?s WHERE { ?s a <http://imeji.org/terms/album>}";
	}

	/**
	 * Select all {@link Item} available imeji
	 * 
	 * @return
	 */
	public static String selectItemAll() {
		return "SELECT ?s WHERE { ?s a <http://imeji.org/terms/item>}";
	}

	/**
	 * Clean the statement: <br/>
	 * - Remove Statement which parent doesn't exist
	 * 
	 * @return
	 */
	public static String cleanStatement() {
		return "WITH <"
				+ Imeji.profileModel
				+ "> "
				+ "DELETE {?s ?p ?o} "
				+ "USING <"
				+ Imeji.profileModel
				+ "> "
				+ "WHERE { ?s <http://imeji.org/terms/parent> ?parent  . not exists{?parent ?pr ?ob} . ?s ?p ?o }";
	}

	/**
	 * Update all {@link Item}. Remove the {@link Metadata} which have a non
	 * existing {@link Statement}
	 * 
	 * @return
	 */
	public static String updateRemoveAllMetadataWithoutStatement(
			String profileURI) {
		String profileQuery = profileURI != null ? "<" + profileURI + ">"
				: "?profile";
		return "WITH <http://imeji.org/item> " + "DELETE {?mds <"
				+ ImejiNamespaces.METADATA + "> ?s . ?s ?p ?o } "
				+ "USING <http://imeji.org/item> "
				+ "USING <http://imeji.org/metadataProfile> "
				+ "WHERE {?mds <http://imeji.org/terms/mdprofile> "
				+ profileQuery + " . ?mds <" + ImejiNamespaces.METADATA
				+ "> ?s . ?s <http://imeji.org/terms/statement> ?st"
				+ " . NOT EXISTS{" + profileQuery
				+ " <http://imeji.org/terms/statement> ?st}" + " . ?s ?p ?o }";
	}

	/**
	 * Update the filesize of a {@link Item}
	 * 
	 * @param itemId
	 * @param fileSize
	 * @return
	 */
	public static String insertFileSize(String itemId, String fileSize) {
		return "WITH <http://imeji.org/item> " + "INSERT {<" + itemId
				+ "> <http://imeji.org/terms/fileSize> " + fileSize + "}"
				+ "USING <http://imeji.org/item> " + "WHERE{<" + itemId
				+ "> ?p ?o}";
	}

	/**
	 * Remove all Filesize of all {@link Item}
	 * 
	 * @return
	 */
	public static String deleteAllFileSize() {
		return "WITH <http://imeji.org/item> DELETE {?s <http://imeji.org/terms/fileSize> ?size} where{?s <http://imeji.org/terms/fileSize> ?size}";
	}

	/**
	 * Update all {@link Item}. Remove all {@link Metadata} which doesn't have
	 * any content (no triples as subject)
	 * 
	 * @return
	 */
	public static String updateEmptyMetadata() {
		return "WITH <http://imeji.org/item> " + "DELETE {?mds <"
				+ ImejiNamespaces.METADATA + "> ?s} "
				+ "USING <http://imeji.org/item> " + "WHERE {?mds <"
				+ ImejiNamespaces.METADATA + "> ?s . NOT EXISTS{?s ?p ?o}}";
	}

	/**
	 * Count all {@link Item} of a {@link container}
	 * 
	 * @param uri
	 * @return
	 */
	public static String countCollectionSize(URI uri) {
		return "SELECT count(DISTINCT ?s) WHERE {?s <http://imeji.org/terms/collection> <"
				+ uri.toString()
				+ "> . ?s <"
				+ ImejiNamespaces.STATUS
				+ "> ?status . FILTER (?status!=<" + Status.WITHDRAWN.getUriString() + ">)}";
	}

	/**
	 * Count all {@link Item} of a {@link Album}
	 * 
	 * @param uri
	 * @return
	 */
	public static String countAlbumSize(URI uri) {
		return "SELECT count(DISTINCT ?s) WHERE {<" + uri.toString()
				+ "> <http://imeji.org/terms/item> ?s . ?s <"
				+ ImejiNamespaces.STATUS + "> ?status . FILTER (?status!=<"
				+ Status.WITHDRAWN.getUriString() + ">)}";
	}

	/**
	 * Return all the {@link Item} of a {@link container}
	 * 
	 * @param uri
	 * @param limit
	 * @return
	 */
	public static String selectCollectionItems(URI uri, User user, int limit) {
		if (user == null)
			return "SELECT DISTINCT ?s WHERE {?s <http://imeji.org/terms/collection> <"
					+ uri.toString()
					+ "> . ?s <"
					+ ImejiNamespaces.STATUS
					+ "> ?status .  filter(?status=<"
					+ Status.RELEASED.getUriString()
					+ ">)} LIMIT " + limit;
		return "SELECT DISTINCT ?s WHERE {?s <http://imeji.org/terms/collection> <"
				+ uri.toString()
				+ "> . ?s <"
				+ ImejiNamespaces.STATUS
				+ "> ?status . OPTIONAL{<"
				+ user.getId().toString()
				+ "> <http://imeji.org/terms/grant> ?g . ?g <http://imeji.org/terms/grantFor> ?c} . filter(bound(?g) || ?status=<"
				+ Status.RELEASED.getUriString()
				+ ">) . FILTER (?status!=<"
				+ Status.WITHDRAWN.getUriString() + ">)} LIMIT " + limit;
	}

	/**
	 * Return all the {@link Item} of a {@link Album}
	 * 
	 * @param uri
	 * @param user
	 * @param limit
	 * @return
	 */
	public static String selectAlbumItems(URI uri, User user, int limit) {
		if (user == null)
			return "SELECT DISTINCT ?s WHERE {<"
					+ uri.toString()
					+ "> <http://imeji.org/terms/item> ?s . ?s <"
					+ ImejiNamespaces.STATUS
					+ "> ?status .  filter(?status=<" + Status.RELEASED.getUriString() + ">)} LIMIT "
					+ limit;
		return "SELECT DISTINCT ?s WHERE {<"
				+ uri.toString()
				+ "> <http://imeji.org/terms/item> ?s . "
				+ SimpleSecurityQuery
						.queryFactory(user,
								J2JHelper.getResourceNamespace(new Item()),
								null, false)
				+ " ?s <"
				+ ImejiNamespaces.STATUS
				+ "> ?status . ?s <http://imeji.org/terms/collection> ?c} LIMIT "
				+ limit;
	}

	public static String selectContainerItemByFilename(URI containerURI,
			String filename) {
		filename = removeforbiddenCharacters(filename);
		return "SELECT DISTINCT ?s WHERE {?s <http://imeji.org/terms/filename> ?el . FILTER(regex(?el, '^"
				+ filename
				+ "\\\\..+', 'i')) .?s <http://imeji.org/terms/collection> <"
				+ containerURI.toString()
				+ "> . ?s <"
				+ ImejiNamespaces.STATUS
				+ "> ?status . FILTER (?status!=<" + Status.WITHDRAWN.getUriString() + ">)} LIMIT 2";

	}

	/**
	 * Search for all Institute of all {@link User} . An institute is defined by
	 * the emai of the {@link User}, for instance user@mpdl.mpg.de has institute
	 * mpdl.mpg.de
	 * 
	 * @return
	 */
	public static String selectAllInstitutes() {
		return "SELECT DISTINCT ?s WHERE {?user <http://xmlns.com/foaf/0.1/email> ?email . let(?s := str(replace(?email, '(.)+@', '', 'i')))}";
	}

	/**
	 * Search for all {@link Item} within a {@link CollectionImeji} belonging
	 * the the institute, and sum all fileSize
	 * 
	 * @param instituteName
	 * @return
	 */
	public static String selectInstituteFileSize(String instituteName) {
		return "SELECT (SUM(?size) AS ?s) WHERE {?c <http://purl.org/dc/terms/creator> ?user . ?user <http://xmlns.com/foaf/0.1/email> ?email .filter(regex(?email, '"
				+ instituteName
				+ "', 'i')) . ?c a <http://imeji.org/terms/collection> . ?item <http://imeji.org/terms/collection> ?c . ?item <http://imeji.org/terms/fileSize> ?size}";
	}

	public static String escapeWithUnicode(String s) {
		String[] escapedCharacters = { "(", ")" };
		for (int i = 0; i < escapedCharacters.length; i++) {
			s = s.replace(escapedCharacters[i],
					escapeCharacterWithUnicode(escapedCharacters[i]));
		}
		return s;
	}

	private static String escapeCharacterWithUnicode(String c) {
		return "\\u00" + UnicodeEscaper.hex(c.codePointAt(0));
	}

	/**
	 * Chararters ( and ) can not be accepted in the sparql query and must
	 * therefore removed
	 * 
	 * @param s
	 * @return
	 */
	public static String removeforbiddenCharacters(String s) {
		String[] forbidden = { "(", ")" };
		for (int i = 0; i < forbidden.length; i++) {
			s = s.replace(forbidden[i], ".");
		}
		return s;
	}

}
