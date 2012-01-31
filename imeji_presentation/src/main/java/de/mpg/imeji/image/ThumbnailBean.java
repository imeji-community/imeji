/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.image;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.lang.MetadataLabels;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ObjectCachedLoader;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Properties.Status;
import de.mpg.jena.vo.Statement;

public class ThumbnailBean implements Serializable
{
	private String link = "";
	private String filename = "";
	private String caption = "";
	private URI id = null;
	private URI profile = null;

	private List<ImageMetadata> metadata = new ArrayList<ImageMetadata>();
	private List<Statement> statements = new ArrayList<Statement>();

	private boolean selected = false;
	private boolean isInActiveAlbum = false;

	//security
	private boolean editable = false;
	private boolean visible = false;
	private boolean deletable= false;

	private SessionBean sessionBean;

	private static Logger logger = Logger.getLogger(ThumbnailBean.class);

	public ThumbnailBean(Image image) 
	{
		sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		id = image.getId();
		link = image.getThumbnailImageUrl().toString();
		profile = image.getMetadataSet().getProfile();
		filename = image.getFilename();
		metadata = (List<ImageMetadata>) image.getMetadataSet().getMetadata();

		statements = loadStatements(image.getMetadataSet().getProfile());
		caption = findCaption();

		selected = sessionBean.getSelected().contains(id);

		if (sessionBean.getActiveAlbum() != null)
		{
			isInActiveAlbum =  sessionBean.getActiveAlbum().getAlbum().getImages().contains(image.getId());
		}

		initSecurity(image);
	}

	public String getInitPopup() throws Exception
	{
		List<Image> l = new ArrayList<Image>();
		Image im = new Image();
		im.getMetadataSet().setProfile(profile);
		l.add(im);
		((MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class)).init(l);
		return "";
	}

	private void initSecurity(Image image)
	{
		Security security = new Security();
		editable = security.check(OperationsType.UPDATE, sessionBean.getUser(), image) && image != null &&  !image.getProperties().getStatus().equals(Status.WITHDRAWN);
		visible = security.check(OperationsType.READ, sessionBean.getUser(), image);
		deletable =  security.check(OperationsType.DELETE, sessionBean.getUser(), image);
	}

	private List<Statement> loadStatements(URI uri)
	{
		try 
		{
			MetadataProfile profile = ObjectCachedLoader.loadProfile(uri);
			if (profile != null) 
			{
				return (List<Statement>) profile.getStatements();
			}
		} 
		catch (Exception e) 
		{
			BeanHelper.error(sessionBean.getMessage("error_profile_load") + " " + uri + "  " + sessionBean.getLabel("of") + " " + id);
			logger.error("Error load profile " + uri + " of image " + id, e);
		}
		return new ArrayList<Statement>();
	}

	private String findCaption()
	{
		for (Statement s : statements)
		{
			if (s.isDescription())
			{
				for (ImageMetadata md : metadata)
				{
					if (md.getNamespace().equals(s.getName()))
					{
						return md.getSearchValue();
					}
				}
			}
		}
		return filename;
	}

	public void selectedChanged(ValueChangeEvent event)
	{
		sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);

		if (event.getNewValue().toString().equals("true") && !sessionBean.getSelected().contains(id))
		{
			selected = true;
			select();
		}
		else if (event.getNewValue().toString().equals("false") && sessionBean.getSelected().contains(id))
		{
			selected = false;
			select();
		}
	}

	public String select()
	{
		if (!selected)
		{
			((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getSelected().remove(id);
		}
		else
		{
			((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getSelected().add(id);
		}
		return "";
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public URI getId() {
		return id;
	}

	public void setId(URI id) {
		this.id = id;
	}

	public List<ImageMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<ImageMetadata> metadata) {
		this.metadata = metadata;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		ThumbnailBean.logger = logger;
	}

	public boolean isInActiveAlbum() {
		return isInActiveAlbum;
	}

	public void setInActiveAlbum(boolean isInActiveAlbum) {
		this.isInActiveAlbum = isInActiveAlbum;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}


}
