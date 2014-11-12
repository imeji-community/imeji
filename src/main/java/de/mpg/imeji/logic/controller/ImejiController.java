/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.List;

import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.helper.DateHelper;
import de.mpg.j2j.helper.J2JHelper;

/**
 * Abstract class for the controller in imeji dealing with imeji VO:
 * {@link Item} {@link CollectionImeji} {@link Album} {@link User}
 * {@link MetadataProfile}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class ImejiController {
	/**
	 * Default constructor for {@link ImejiController}
	 */
	public ImejiController() {
	}

	/**
	 * Add the {@link Properties} to an imeji object when it is created
	 * 
	 * @param properties
	 * @param user
	 */
	protected void writeCreateProperties(Properties properties, User user) {
		J2JHelper.setId(properties,
				IdentifierUtil.newURI(properties.getClass()));
		Calendar now = DateHelper.getCurrentDate();
		properties
				.setCreatedBy(ObjectHelper.getURI(User.class, user.getEmail()));
		properties.setModifiedBy(ObjectHelper.getURI(User.class,
				user.getEmail()));
		properties.setCreated(now);
		properties.setModified(now);
		if (properties.getStatus() == null)
			properties.setStatus(Status.PENDING);
	}

	/**
	 * Add the {@link Properties} to an imeji object when it is updated
	 * 
	 * @param properties
	 * @param user
	 */
	protected void writeUpdateProperties(Properties properties, User user) {
		properties.setModifiedBy(ObjectHelper.getURI(User.class,
				user.getEmail()));
		properties.setModified(DateHelper.getCurrentDate());
	}

	/**
	 * Add the {@link Properties} to an imeji object when it is released
	 * 
	 * @param properties
	 * @param user
	 */
	protected void writeReleaseProperty(Properties properties, User user) {
		properties.setVersion(1);
		properties.setVersionDate(DateHelper.getCurrentDate());
		properties.setStatus(Status.RELEASED);
	}

	/**
	 * Add the {@link Properties} to an imeji object when it is withdrawn
	 * 
	 * @param properties
	 * @param comment
	 */
	protected void writeWithdrawProperties(Properties properties, String comment) {
		if (comment != null && !"".equals(comment)) {
			properties.setDiscardComment(comment);
		}
		if (properties.getDiscardComment() == null
				|| "".equals(properties.getDiscardComment())) {
			throw new RuntimeException(
					"Discard error: A Discard comment is needed");
		}
		properties.setStatus(Status.WITHDRAWN);
	}

	/**
	 * True if at least one {@link Item} is locked by another {@link User}
	 * 
	 * @param uris
	 * @param user
	 * @return
	 */
	public boolean hasImageLocked(List<String> uris, User user) {
		for (String uri : uris) {
			if (Locks.isLocked(uri.toString(), user.getEmail())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return a single object as a list of object
	 * 
	 * @param o
	 * @return
	 */
	public List<?> toList(Object o) {
		List<Object> list = new ArrayList<Object>();
		list.add(o);
		return list;
	}
}
