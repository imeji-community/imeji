/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.controller;

import static com.google.common.io.Files.copy;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.ImejiNamespaces;
import de.mpg.imeji.logic.ImejiTriple;
import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.PropertyReader;
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

	
	public static final String LOGO_STORAGE_SUBDIRECTORY = "/thumbnail";

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
		properties.setCreatedBy(user.getId());
		properties.setModifiedBy(user.getId());
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
		properties.setModifiedBy(user.getId());
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
	 * Get all the triples which need to be updated by a release
	 * 
	 * @param uri
	 * @param securityUri
	 * @return
	 */
	protected List<ImejiTriple> getUpdateTriples(String uri, User user, Object o) {
		List<ImejiTriple> triples = new ArrayList<ImejiTriple>();
		triples.add(new ImejiTriple(uri, ImejiNamespaces.MODIFIED_BY, user
				.getId(), o));
		triples.add(new ImejiTriple(uri,
				ImejiNamespaces.LAST_MODIFICATION_DATE, DateHelper
						.getCurrentDate(), o));
		return triples;
	}

	/**
	 * Get all the triples which need to be updated by an update
	 * 
	 * @param uri
	 * @param securityUri
	 * @return
	 */
	protected List<ImejiTriple> getReleaseTriples(String uri, Object o) {
		List<ImejiTriple> triples = new ArrayList<ImejiTriple>();
		triples.add(new ImejiTriple(uri, ImejiNamespaces.VERSION, 1, o));
		triples.add(new ImejiTriple(uri, ImejiNamespaces.VERSION_DATE,
				DateHelper.getCurrentDate(), o));
		triples.add(new ImejiTriple(uri, ImejiNamespaces.STATUS,
				Status.RELEASED.getURI(), o));
		return triples;
	}
	
	/**
	 * Get all the triples which need to be updated by an update
	 * 
	 * @param uri
	 * @param securityUri
	 * @return
	 */
	protected List<ImejiTriple> getContainerLogoTriples(String uri, Object o, String logoUrl) {
		List<ImejiTriple> triples = new ArrayList<ImejiTriple>();
		triples.add(new ImejiTriple(uri, ImejiNamespaces.CONTAINER_LOGO,
				URI.create(logoUrl), o));
		return triples;
	}
	

	/**
	 * Get all the triples which need to be updated by an update
	 * 
	 * @param uri
	 * @param securityUri
	 * @return
	 * @throws UnprocessableError
	 */
	protected List<ImejiTriple> getWithdrawTriples(String uri, Object o,
			String comment) throws UnprocessableError {
		List<ImejiTriple> triples = new ArrayList<ImejiTriple>();
		if (comment != null && !"".equals(comment))
			triples.add(new ImejiTriple(uri,
					ImejiNamespaces.DISCARD_COMMENT, comment, o));
		else
			throw new UnprocessableError(
					"Discard error: A Discard comment is needed");
		triples.add(new ImejiTriple(uri, ImejiNamespaces.STATUS,
				Status.WITHDRAWN.getURI(), o));
		return triples;
	}

	/**
	 * Add the {@link Properties} to an imeji object when it is withdrawn
	 * 
	 * @param properties
	 * @param comment
	 * @throws UnprocessableError
	 */
	protected void writeWithdrawProperties(Properties properties, String comment)
			throws ImejiException {
		if (comment != null && !"".equals(comment)) {
			properties.setDiscardComment(comment);
		}
		if (properties.getDiscardComment() == null
				|| "".equals(properties.getDiscardComment())) {
			throw new UnprocessableError(
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
	
	
	  /**
     * Update logo of {@link Container}
     *
     * @param Container
     * @param f
     * @param user
     * @return
     * @throws ImejiException
	 * @throws URISyntaxException 
     */
    public Container updateFile(Container container, File f, User user) throws ImejiException, IOException, URISyntaxException {
    	String storageUrl = StringHelper.normalizeURI(PropertyReader
                .getProperty("imeji.instance.url"))
                + "file/"
                + container.getIdString() 
                + LOGO_STORAGE_SUBDIRECTORY
                + StringHelper.urlSeparator;
    	
    	File storageDir = new File(
                PropertyReader.getProperty("imeji.storage.path") + container.getIdString()+LOGO_STORAGE_SUBDIRECTORY);
        
    	String storagePath = StringHelper.normalizePath(storageDir.getAbsolutePath());
    	if (f != null) {
	    	container.setLogoUrl(URI.create(generateUrl(ObjectHelper.getId(container.getId()), f.getName(), storageUrl)));
	        update(f, transformUrlToPath(container.getLogoUrl().toURL().toString(), storageUrl, storagePath));
    	}
    	else
    	{
    		deleteFile(transformUrlToPath(container.getLogoUrl().toURL().toString(), storageUrl, storagePath));
	    	container.setLogoUrl(null);
    	}
        
        return container;
    }
    
    /**
     * Delete logo of {@link Container}
     *
     * @param Container
     * @param f
     * @param user
     * @return
     * @throws ImejiException
	 * @throws URISyntaxException 
     */
    public void deleteFile(String fileUrl) throws ImejiException, IOException, URISyntaxException {
    	File f = new File(fileUrl);
    	FileUtils.deleteQuietly(f.getParentFile());
    }
    
    /**
     * Create the URL of the file from its filename, its id, and its resolution.
     * Important: the filename is decoded, to avoid problems by reading this url
     *
     * @param id
     * @param filename
     * @return
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws UnsupportedEncodingException
     */
    public String generateUrl(String id, String filename, String storageUrl) throws IOException, URISyntaxException {
        filename = StringHelper.normalizeFilename(filename);
        return storageUrl + filename;
    }
    
    /**
     * Copy the file in the file system
     *
     * @param toCopy
     * @param path
     * @return
     * @throws IOException
     */
    private String update(File toCopy, String path) throws IOException {
        File f = new File(path);
        if (f.getParentFile().exists()) {
            //clean space dir
            FileUtils.cleanDirectory(f.getParentFile());
        } else {
            //create space dir
            f.getParentFile().mkdirs();
        }
        copy(toCopy, f);
        return f.getAbsolutePath();
    }
    

    /**
     * Transform an url to a file system path
     *
     * @param url
     * @return
     */
    public String transformUrlToPath(String url, String storageUrl, String storagePath) {
        return URI.create(url).getPath().replace(URI.create(storageUrl).getPath(), storagePath).replace(
                StringHelper.urlSeparator, StringHelper.fileSeparator);
    }
}
