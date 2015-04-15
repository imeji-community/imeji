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
package de.mpg.imeji.logic.auth;

import de.mpg.imeji.exceptions.NotAllowedError;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.presentation.album.AlbumBean;
import de.mpg.imeji.presentation.beans.PropertyBean;
import de.mpg.imeji.presentation.collection.CollectionListItem;

import java.net.URI;
import java.util.List;

/**
 * Authorization rules for imeji objects (defined by their uri) for one
 * {@link User}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Authorization {

	/**
	 * Return true if the {@link User} can create the object defined by the uri
	 * 
	 * @param user
	 * @param uri
	 * @return
	 * @throws NotAllowedError
	 */
	public boolean create(User user, String uri) {
		if (hasGrant(
				user,
				toGrant(uri,
						getGrantTypeAccordingToObjectType(uri, GrantType.CREATE))))
			return true;
		return false;
	}

	/**
	 * Return true if the {@link User} can read the object defined by the uri
	 * 
	 * @param user
	 * @param uri
	 * @return
	 * @throws NotAllowedError
	 */
	public boolean read(User user, String uri) {
		if (hasGrant(
				user,
				toGrant(uri,
						getGrantTypeAccordingToObjectType(uri, GrantType.READ))))
			return true;
		return false;
	}

	/**
	 * Return true if the {@link User} can update the object defined by the uri
	 * 
	 * @param user
	 * @param uri
	 * @return
	 * @throws NotAllowedError
	 */
	public boolean update(User user, String uri) {
		if (hasGrant(
				user,
				toGrant(uri,
						getGrantTypeAccordingToObjectType(uri, GrantType.UPDATE))))
			return true;
		return false;
	}

	/**
	 * Return true if the {@link User} can delete the object defined by the uri
	 * 
	 * @param user
	 * @param uri
	 * @return
	 * @throws NotAllowedError
	 */
	public boolean delete(User user, String uri) {
		if (hasGrant(
				user,
				toGrant(uri,
						getGrantTypeAccordingToObjectType(uri, GrantType.DELETE))))
			return true;
		return false;
	}

	/**
	 * Return true if the {@link User} can administrate the object defined by
	 * the uri
	 * 
	 * @param user
	 * @param uri
	 * @return
	 * @throws NotAllowedError
	 */
	public boolean administrate(User user, String uri) {
		if (hasGrant(
				user,
				toGrant(uri,
						getGrantTypeAccordingToObjectType(uri, GrantType.ADMIN))))
			return true;
		return false;
	}

	/**
	 * Return true if the user can update the content of the object defined by
	 * the uri
	 * 
	 * @param user
	 * @param uri
	 * @return
	 */
	public boolean updateContent(User user, String uri) {
		if (hasGrant(user, toGrant(uri, GrantType.UPDATE_CONTENT)))
			return true;
		return false;
	}

	/**
	 * Return true if the user can delete the content of the object defined by
	 * the uri
	 * 
	 * @param user
	 * @param uri
	 * @return
	 */
	public boolean deleteContent(User user, String uri) {
		if (hasGrant(user, toGrant(uri, GrantType.DELETE_CONTENT)))
			return true;
		return false;
	}

	/**
	 * Return true if the user can administrate the content of the object
	 * defined by the uri
	 * 
	 * @param user
	 * @param uri
	 * @return
	 */
	public boolean adminContent(User user, String uri) {
		if (hasGrant(user, toGrant(uri, GrantType.ADMIN_CONTENT)))
			return true;
		return false;
	}

	/**
	 * Return true if the {@link User} can create the object
	 * 
	 * @param user
	 * @param url
	 * @return
	 * @throws NotAllowedError
	 */
	public boolean create(User user, Object obj) {
		if (hasGrant(
				user,
				toGrant(getRelevantURIForSecurity(obj, false, true),
						GrantType.CREATE)))
			return true;
		return false;
	}

	/**
	 * Check if the object can be created when the object is not existing
	 * 
	 * @param user
	 * @param url
	 * @return
	 * @throws NotAllowedError
	 */
	public boolean createNew(User user, Object obj) {
		if (user != null && obj instanceof Album)
			return true;
		if (hasGrant(
				user,
				toGrant(getRelevantURIForSecurity(obj, true, true),
						GrantType.CREATE)))
			return true;
		return false;
	}

	/**
	 * Return true if the {@link User} can read the object
	 * 
	 * @param user
	 * @param url
	 * @return
	 * @throws NotAllowedError
	 */
	public boolean read(User user, Object obj) {
		if (isPublic(obj))
			return true;
		else if (hasGrant(
				user,
				toGrant(getRelevantURIForSecurity(obj, false, true),
						getGrantTypeAccordingToObjectType(obj, GrantType.READ))))
			return true;
		else if (hasGrant(
				user,
				toGrant(getRelevantURIForSecurity(obj, false, false),
						getGrantTypeAccordingToObjectType(obj, GrantType.READ))))
			return true;
		return false;
	}

	/**
	 * Return true if the {@link User} can update the object
	 * 
	 * @param user
	 * @param url
	 * @return
	 * @throws NotAllowedError
	 */
	public boolean update(User user, Object obj) {
		if (hasGrant(
				user,
				toGrant(getRelevantURIForSecurity(obj, false, true),
						getGrantTypeAccordingToObjectType(obj, GrantType.UPDATE))))
			return true;
		return false;
	}

	/**
	 * Return true if the {@link User} can delete the object
	 * 
	 * @param user
	 * @param url
	 * @return
	 * @throws NotAllowedError
	 */
	public boolean delete(User user, Object obj) {
		if (!isPublic(obj)
				&& hasGrant(
						user,
						toGrant(getRelevantURIForSecurity(obj, false, true),
								getGrantTypeAccordingToObjectType(obj,
										GrantType.DELETE))))
			return true;
		return AuthUtil.isSysAdmin(user);
	}

	/**
	 * Return true if the {@link User} can administrate the object
	 * 
	 * @param user
	 * @param url
	 * @return
	 * @throws NotAllowedError
	 */
	public boolean administrate(User user, Object obj) {
		if (hasGrant(
				user,
				toGrant(getRelevantURIForSecurity(obj, false, true),
						getGrantTypeAccordingToObjectType(obj, GrantType.ADMIN))))
			return true;
		return false;
	}

	/**
	 * Return true if the user can update the content of the object
	 * 
	 * @param user
	 * @param url
	 * @return
	 */
	public boolean updateContent(User user, Object obj) {
		if (hasGrant(
				user,
				toGrant(getRelevantURIForSecurity(obj, false, true),
						GrantType.UPDATE_CONTENT)))
			return true;
		return false;
	}

	/**
	 * Return true if the user can delete the content of the object
	 * 
	 * @param user
	 * @param url
	 * @return
	 */
	public boolean deleteContent(User user, Object obj) {
		if (!isPublic(obj)
				&& hasGrant(
						user,
						toGrant(getRelevantURIForSecurity(obj, false, true),
								GrantType.DELETE_CONTENT)))
			return true;
		return AuthUtil.isSysAdmin(user);
	}

	/**
	 * Return true if the user can administrate the content of the object
	 * 
	 * @param user
	 * @param url
	 * @return
	 */
	public boolean adminContent(User user, Object obj) {
		if (hasGrant(
				user,
				toGrant(getRelevantURIForSecurity(obj, false, true),
						GrantType.ADMIN_CONTENT)))
			return true;
		return false;
	}

	/**
	 * True if the {@link User} has the given {@link Grant} or if the
	 * {@link User} is system Administrator
	 * 
	 * @param user
	 * @param g
	 * @return
	 */
	private boolean hasGrant(User user, Grant g) {
		List<Grant> all = AuthUtil.getAllGrantsOfUser(user);
		if (all.contains(g))
			return true;
		if (all.contains(toGrant(PropertyBean.baseURI(), GrantType.ADMIN)))
			return true;
		return false;
	}

	/**
	 * Create a {@link Grant} out of the {@link GrantType} and the given uri
	 * 
	 * @param uri
	 * @param type
	 * @return
	 */
	private Grant toGrant(String uri, GrantType type) {
		if (uri == null)
			return null;
		return new Grant(type, URI.create(uri));
	}

	/**
	 * Return the uri which is relevant for the {@link Authorization}
	 * 
	 * @param obj
	 * @return
	 */
	private String getRelevantURIForSecurity(Object obj, boolean createNew,
			boolean getContainer) {
		if (obj instanceof Item)
			return getContainer ? ((Item) obj).getCollection().toString()
					: ((Item) obj).getId().toString();
		else if (obj instanceof Container)
			return createNew ? AuthorizationPredefinedRoles.IMEJI_GLOBAL_URI
					: ((Container) obj).getId().toString();
		else if (obj instanceof CollectionListItem)
			return ((CollectionListItem) obj).getUri().toString();
		else if (obj instanceof AlbumBean)
			return ((AlbumBean) obj).getAlbum().getId().toString();
		else if (obj instanceof MetadataProfile)
			return createNew ? AuthorizationPredefinedRoles.IMEJI_GLOBAL_URI
					: ((MetadataProfile) obj).getId().toString();
		else if (obj instanceof User)
			return ((User) obj).getId().toString();
		else if (obj instanceof URI)
			return obj.toString();
		return PropertyBean.baseURI();
	}

	/**
	 * If the Object is an {@link Item} then the {@link GrantType} must be
	 * changed to fit the authorization on container level
	 * 
	 * @param obj
	 * @param type
	 * @return
	 */
	private GrantType getGrantTypeAccordingToObjectType(Object obj,
			GrantType type) {
		if (obj == null)
			return type;
		if (obj instanceof Item || isItemUri(obj.toString())) {
			switch (type) {
			case UPDATE:
				return GrantType.UPDATE_CONTENT;
			case DELETE:
				return GrantType.DELETE_CONTENT;
			case ADMIN:
				return GrantType.ADMIN_CONTENT;
			default:
				return type;
			}
		}
		return type;
	}

	/**
	 * True if the uri is the uri of an {@link Item}
	 * 
	 * @param uri
	 * @return
	 */
	private boolean isItemUri(String uri) {
		return uri.contains("/item/");
	}

	/**
	 * True if the {@link Object} is public (i.e. has been released)
	 * 
	 * @param obj
	 * @return
	 */
	private boolean isPublic(Object obj) {
		if (obj instanceof Item)
			return isPublicStatus(((Item) obj).getStatus());
		else if (obj instanceof Container)
			return isPublicStatus(((Container) obj).getStatus());
		else if (obj instanceof Space)
			return isPublicStatus(((Space) obj).getStatus());
		else if (obj instanceof MetadataProfile)
			return isPublicStatus(((MetadataProfile) obj).getStatus());
		else if (obj instanceof Person)
			return true;
		else if (obj instanceof Organization)
			return true;
		return false;
	}

	/**
	 * True if the {@link Status} is a public status(i.e. not need to have
	 * special grants to read the object)
	 * 
	 * @param status
	 * @return
	 */
	private boolean isPublicStatus(Status status) {
		return status.equals(Status.RELEASED)
				|| status.equals(Status.WITHDRAWN);
	}
}
