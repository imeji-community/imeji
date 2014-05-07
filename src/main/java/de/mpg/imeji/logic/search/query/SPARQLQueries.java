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

import java.net.URI;

import org.opensaml.ws.wssecurity.Username;

import com.hp.hpl.jena.sparql.pfunction.library.container;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.UserGroup;
import de.mpg.j2j.helper.J2JHelper;

/**
 * SPARQL queries for imeji
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SPARQLQueries
{
    /**
     * Select all {@link Metadata} which are restricted, according to their {@link Statement}
     * 
     * @return
     */
    public static String selectMetadataRestricted()
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s ?sort0 WHERE {  ?it a <http://imeji.org/terms/item>"
                + " . ?it <http://imeji.org/terms/collection> ?sort0"
                + ". ?it <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?s . ?s <http://imeji.org/terms/statement> ?st"
                + " . ?st <http://imeji.org/terms/restricted> ?r  .FILTER(?r='true'^^<http://www.w3.org/2001/XMLSchema#boolean>) }";
    }

    /**
     * REturn the namespace of a {@link Metadata} if defined in the {@link MetadataProfile}
     * 
     * @param uri
     * @return
     */
    public static String selectMetadataNamespace(String uri)
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE { <" + uri
                + "> <http://imeji.org/terms/statement> ?st . ?st <http://imeji.org/terms/namespace> ?s }";
    }

    /**
     * Select all {@link Username}
     * 
     * @return
     */
    public static String selectUserAll(String name)
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <http://imeji.org/terms/user> . ?s <http://xmlns.com/foaf/0.1/name> ?name . filter(regex(?name, '"
                + name + "','i'))}";
    }

    /**
     * Select {@link User} having a {@link Grant} for an object defined by its uri
     * 
     * @return
     */
    public static String selectUserWithGrantFor(String uri)
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {OPTIONAL{ ?s <http://imeji.org/terms/grant> ?g . ?g <http://imeji.org/terms/grantFor> <"
                + uri
                + ">} . filter(bound(?g)) . ?s a <http://imeji.org/terms/user> . ?s <http://xmlns.com/foaf/0.1/name> ?name } ORDER BY DESC(?name)";
    }

    /**
     * Select {@link User} having a {@link Grant} for an object defined by its uri
     * 
     * @return
     */
    public static String selectUserBy(String uri)
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {OPTIONAL{ ?s <http://imeji.org/terms/grant> ?g . ?g <http://imeji.org/terms/grantFor> <"
                + uri
                + ">} . filter(bound(?g)) . ?s a <http://imeji.org/terms/user> . ?s <http://xmlns.com/foaf/0.1/name> ?name } ORDER BY DESC(?name)";
    }

    public static String selectUserGroupWithGrantFor(String uri)
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {OPTIONAL{ ?s <http://imeji.org/terms/grant> ?g . ?g <http://imeji.org/terms/grantFor> <"
                + uri
                + ">} . filter(bound(?g)) . ?s a <http://imeji.org/terms/userGroup> . ?s <http://xmlns.com/foaf/0.1/name> ?name } ORDER BY DESC(?name)";
    }

    /**
     * Select all {@link UserGroup}
     * 
     * @return
     */
    public static String selectUserGroupAll(String name)
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <http://imeji.org/terms/userGroup> . ?s <http://xmlns.com/foaf/0.1/name> ?name . filter(regex(?name, '"
                + name + "','i'))}";
    }

    /**
     * Select all {@link UserGroup}
     * 
     * @return
     */
    public static String selectUserGroupOfUser(User user)
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <http://imeji.org/terms/userGroup> . ?s <http://xmlns.com/foaf/0.1/member> <"
                + user.getId().toString() + ">}";
    }

    /**
     * Select all admin users
     * 
     * @return
     */
    public static String selectAdminUser()
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s "
                + "WHERE { ?s <http://imeji.org/terms/grantType>ADMIN}";
    }

    /**
     * @param fileUrl
     * @return
     */
    public static String selectCollectionIdOfFile(String fileUrl)
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {" + "optional{"
                + "?it <http://imeji.org/terms/webImageUrl> <" + fileUrl
                + ">} . optional {?it <http://imeji.org/terms/thumbnailImageUrl> <" + fileUrl
                + ">} . optional{ ?it <http://imeji.org/terms/fullImageUrl> <" + fileUrl + ">}"
                + " . ?it <http://imeji.org/terms/collection> ?s . } LIMIT 1 ";
    }

    /**
     * @param fileUrl
     * @return
     */
    public static String selectItemIdOfFile(String fileUrl)
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {" + "optional{"
                + "?s <http://imeji.org/terms/webImageUrl> <" + fileUrl
                + ">} . optional {?s <http://imeji.org/terms/thumbnailImageUrl> <" + fileUrl
                + ">} . optional{ ?s <http://imeji.org/terms/fullImageUrl> <" + fileUrl + ">}} LIMIT 1 ";
    }

    /**
     * @param fileUrl
     * @return
     */
    public static String selectAlbumIdOfFile(String id)
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {"
                + " ?s a <http://imeji.org/terms/album> . ?s <http://imeji.org/terms/item> <" + id + "> } ";
    }

    /**
     * Select all {@link Metadata} which are not related to a statement. Happens when a {@link Statement} is removed
     * from a {@link MetadataProfile}
     * 
     * @return
     */
    public static String selectMetadataUnbounded()
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s ?sort0 WHERE {?mds <http://imeji.org/terms/metadata> ?s"
                + " . ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?sort0 . ?s <http://imeji.org/terms/statement> ?st"
                + " . not exists{?p a <http://imeji.org/terms/mdprofile> . ?p <http://imeji.org/terms/statement> ?st}}";
    }

    /**
     * Select all {@link Statement} which are not bounded to a {@link MetadataProfile}. Should not happen...
     * 
     * @return
     */
    public static String selectStatementUnbounded()
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s <http://purl.org/dc/terms/type> ?type"
                + " . not exists{ ?p a <http://imeji.org/terms/mdprofile> . ?p <http://imeji.org/terms/statement> ?s}}";
    }

    /**
     * Select all {@link Grant} which are not valid anymore. For instance, when a {@link User}, or an object (
     * {@link CollectionImeji}, {@link Album}) is deleted, some related {@link Grant} might stay in the database, even
     * if they are not needed anymore.
     * 
     * @return
     */
    public static String selectGrantUnbounded()
    {
        return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE { ?s <http://imeji.org/terms/grantType> ?type"
                + " . not exists{ ?user <http://imeji.org/terms/grant> ?s}}";
    }

    /**
     * Delete all grants granting for the given uri
     * 
     * @param uri
     * @return
     */
    public static String updateRemoveGrantsFor(String uri)
    {
        return "WITH <http://imeji.org/user> DELETE {?user <http://imeji.org/terms/grant> ?s . ?s ?p ?o } "
                + "USING <http://imeji.org/user>  WHERE {?user <http://imeji.org/terms/grant> ?s . ?s <http://imeji.org/terms/grantFor> <"
                + uri + ">}";
    }

    /**
     * Select all {@link CollectionImeji} available imeji
     * 
     * @return
     */
    public static String selectCollectionAll()
    {
        return "SELECT ?s WHERE { ?s a <http://imeji.org/terms/collection>}";
    }

    /**
     * Select all {@link Album} available imeji
     * 
     * @return
     */
    public static String selectAlbumAll()
    {
        return "SELECT ?s WHERE { ?s a <http://imeji.org/terms/album>}";
    }

    /**
     * Select all {@link Item} available imeji
     * 
     * @return
     */
    public static String selectItemAll()
    {
        return "SELECT ?s WHERE { ?s a <http://imeji.org/terms/item>}";
    }

    /**
     * Clean the statement: <br/>
     * - Remove Statement which parent doesn't exist
     * 
     * @return
     */
    public static String cleanStatement()
    {
        return "WITH <" + Imeji.profileModel + "> " + "DELETE {?s ?p ?o} " + "USING <" + Imeji.profileModel + "> "
                + "WHERE { ?s <http://imeji.org/terms/parent> ?parent  . not exists{?parent ?pr ?ob} . ?s ?p ?o }";
    }

    /**
     * Update all {@link Item}. Remove the {@link Metadata} which have a non existing {@link Statement}
     * 
     * @return
     */
    public static String updateRemoveAllMetadataWithoutStatement(String profileURI)
    {
        return "WITH <http://imeji.org/item> " + "DELETE {?mds <http://imeji.org/terms/metadata> ?s . ?s ?p ?o } "
                + "USING <http://imeji.org/item> " + "USING <http://imeji.org/metadataProfile> "
                + "WHERE {?mds <http://imeji.org/terms/mdprofile> <" + profileURI
                + "> . ?mds <http://imeji.org/terms/metadata> ?s . ?s <http://imeji.org/terms/statement> ?st"
                + " . NOT EXISTS{<" + profileURI + "> <http://imeji.org/terms/statement> ?st}" + " . ?s ?p ?o }";
    }

    /**
     * Update all {@link Item}. Remove all {@link Metadata} which doesn't have any content (no triples as subject)
     * 
     * @return
     */
    public static String updateEmptyMetadata()
    {
        return "WITH <http://imeji.org/item> " + "DELETE {?mds <http://imeji.org/terms/metadata> ?s} "
                + "USING <http://imeji.org/item> "
                + "WHERE {?mds <http://imeji.org/terms/metadata> ?s . NOT EXISTS{?s ?p ?o}}";
    }

    /**
     * Count all {@link Item} of a {@link container}
     * 
     * @param uri
     * @return
     */
    public static String countCollectionSize(URI uri)
    {
        return "SELECT count(DISTINCT ?s) WHERE {?s <http://imeji.org/terms/collection> <"
                + uri.toString()
                + "> . ?s <http://imeji.org/terms/status> ?status . FILTER (?status!=<http://imeji.org/terms/status#WITHDRAWN>)}";
    }

    /**
     * Count all {@link Item} of a {@link Album}
     * 
     * @param uri
     * @return
     */
    public static String countAlbumSize(URI uri)
    {
        return "SELECT count(DISTINCT ?s) WHERE {<"
                + uri.toString()
                + "> <http://imeji.org/terms/item> ?s . ?s <http://imeji.org/terms/status> ?status . FILTER (?status!=<http://imeji.org/terms/status#WITHDRAWN>)}";
    }

    /**
     * Return all the {@link Item} of a {@link container}
     * 
     * @param uri
     * @param limit
     * @return
     */
    public static String selectCollectionItems(URI uri, User user, int limit)
    {
        if (user == null)
            return "SELECT DISTINCT ?s WHERE {?s <http://imeji.org/terms/collection> <"
                    + uri.toString()
                    + "> . ?s <http://imeji.org/terms/status> ?status .  filter(?status=<http://imeji.org/terms/status#RELEASED>)} LIMIT "
                    + limit;
        return "SELECT DISTINCT ?s WHERE {?s <http://imeji.org/terms/collection> <"
                + uri.toString()
                + "> . ?s <http://imeji.org/terms/status> ?status . OPTIONAL{<"
                + user.getId().toString()
                + "> <http://imeji.org/terms/grant> ?g . ?g <http://imeji.org/terms/grantFor> ?c} . filter(bound(?g) || ?status=<http://imeji.org/terms/status#RELEASED>) . FILTER (?status!=<http://imeji.org/terms/status#WITHDRAWN>)} LIMIT "
                + limit;
    }

    /**
     * Return all the {@link Item} of a {@link Album}
     * 
     * @param uri
     * @param user
     * @param limit
     * @return
     */
    public static String selectAlbumItems(URI uri, User user, int limit)
    {
        if (user == null)
            return "SELECT DISTINCT ?s WHERE {<"
                    + uri.toString()
                    + "> <http://imeji.org/terms/item> ?s . ?s <http://imeji.org/terms/status> ?status .  filter(?status=<http://imeji.org/terms/status#RELEASED>)} LIMIT "
                    + limit;
        return "SELECT DISTINCT ?s WHERE {<" + uri.toString() + "> <http://imeji.org/terms/item> ?s . "
                + SimpleSecurityQuery.queryFactory(user, J2JHelper.getResourceNamespace(new Item()), null, false)
                + " ?s <http://imeji.org/terms/collection> ?c . ?c <http://imeji.org/terms/status> ?status}LIMIT "
                + limit;
    }

    public static String selectContainerItemByFilename(URI containerURI, String filename)
    {
        return "SELECT DISTINCT ?s WHERE {?s <http://imeji.org/terms/filename> ?el . FILTER(regex(?el, '^"
                + filename
                + "\\\\..+', 'i')) .?s <http://imeji.org/terms/collection> <"
                + containerURI.toString()
                + "> . ?s <http://imeji.org/terms/status> ?status . FILTER (?status!=<http://imeji.org/terms/status#WITHDRAWN>)} LIMIT 2";
    }
}
