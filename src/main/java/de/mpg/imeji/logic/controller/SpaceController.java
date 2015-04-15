package de.mpg.imeji.logic.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.imeji.presentation.util.PropertyReader;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import static com.google.common.io.Files.copy;
import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;

/**
 * CRUD methods for {@link Space}
 *
 * @author vmakarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * */
public class SpaceController extends ImejiController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SpaceController.class);

    private static final ReaderFacade reader = new ReaderFacade(Imeji.spaceModel);
    private static final WriterFacade writer = new WriterFacade(Imeji.spaceModel);

    /**
     * The directory path where files are stored
     */
    private final String storagePath;
    /**
     * The URL used to access the storage (this is a dummy url, used by the
     * internal storage to parse file location)
     */
    private String storageUrl = null;


    public SpaceController() {
        try {
            File storageDir = new File(
                    PropertyReader.getProperty("imeji.spaces.storage.path"));
            storagePath = StringHelper.normalizePath(storageDir
                    .getAbsolutePath());
            storageUrl = StringHelper.normalizeURI(PropertyReader
                    .getProperty("imeji.instance.url"))
                    + "spaces/file"
                    + StringHelper.urlSeparator;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Internal spaces storage couldn't be initialized!!!!!", e);
        }
    }

    /**
     * Creates a new space. - Add a unique id - Write user properties
     *
     * @param space
     * @param user
     */
    public URI create(Space space, User user)
            throws ImejiException {
        return create(space, user, true);
    }
    public URI createNoValidate(Space space, User user)
            throws ImejiException {
        return create(space, user, false);
    }

    public URI create(Space space, User user, boolean validate) throws ImejiException {
        if(validate){
            validate(space, user);
        }
        space.setStatus(Properties.Status.RELEASED);
        writeCreateProperties(space, user);
        //TODO: here is future grants definitions
        /*GrantController gc = new GrantController();
        gc.addGrants(user, AuthorizationPredefinedRoles.admin(space.getId()
                .toString(), null), user);*/
        writer.create(WriterFacade.toList(space), user);
        return space.getId();
    }

    /**
     * Updates a space -Logged in users: --User is space owner --OR
     * user is space editor
     *
     * @param space
     * @param user
     * @throws ImejiException
     */
    public Space update(Space space, User user) throws ImejiException {
        writeUpdateProperties(space, user);
        writer.update(WriterFacade.toList(space), user);
        return retrieve(space.getId(), user);
    }


    /**
     * Update logo of {@link Space}
     *
     * @param space
     * @param f
     * @param user
     * @return
     * @throws ImejiException
     */
    public Space updateFile(Space space, File f, User user) throws ImejiException, IOException {

        space.setLogoUrl(URI.create(generateUrl(ObjectHelper.getId(space.getId()), f.getName())));
        update(f, transformUrlToPath(space.getLogoUrl().toURL().toString()));

        return update(space, user);
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
     * Create the URL of the file from its filename, its id, and its resolution.
     * Important: the filename is decoded, to avoid problems by reading this url
     *
     * @param id
     * @param filename
     * @return
     * @throws UnsupportedEncodingException
     */
    public String generateUrl(String id, String filename) {
        filename = StringHelper.normalizeFilename(filename);
        return storageUrl + id
                + StringHelper.urlSeparator
                + filename;
    }

    /**
     * Transform an url to a file system path
     *
     * @param url
     * @return
     */
    public String transformUrlToPath(String url) {
        return URI.create(url).getPath().replace(URI.create(storageUrl).getPath(), storagePath).replace(
                StringHelper.urlSeparator, StringHelper.fileSeparator);
    }

    /**
     * Transform the path of the item into a path
     *
     * @param path
     * @return
     */
    public String transformPathToUrl(String path) {
        return path.replace(storagePath, storageUrl).replace(
                StringHelper.fileSeparator, StringHelper.urlSeparator);
    }



    /**
     * Remove space file storage
     *
     * @param space
     * @throws IOException
     */
    public void removeFile(Space space) throws IOException {
        if (space == null || space.getLogoUrl() == null)
            return;
        String url = space.getLogoUrl().toURL().toString();
        if (isNullOrEmptyTrim(url))
            return;
        File f = new File(transformUrlToPath(url)).getParentFile();
        if (f.exists())
            FileUtils.deleteDirectory(f);
    }

    public void validate (Space space, User u) throws ImejiException {
        if (isNullOrEmptyTrim(space.getTitle())) {
            throw new BadRequestException("error_space_need_title");
        }
    }

    /**
     * Updates an space -Logged in users: --User is space owner --OR
     * user is space editor
     *
     * @param space
     * @param user
     * @throws ImejiException
     */
    public void updateLazy(Space space, User user) throws ImejiException {
        writeUpdateProperties(space, user);
        writer.updateLazy(WriterFacade.toList(space), user);
    }

    /**
     * Retrieve {@link Space}
     *
     * @param spaceId
     * @param user
     * @return
     * @throws ImejiException
     */
    public Space retrieve(URI spaceId, User user) throws ImejiException {
        return (Space) reader.read(spaceId.toString(), user,
                new Space());
    }


    /**
     * Retrieve all imeji {@link Album}
     *
     * @return
     * @throws ImejiException
     */
    public List<Space> retrieveAll() throws ImejiException {
        return Lists.transform(ImejiSPARQL.exec(SPARQLQueries.selectSpaceAll(),
                        Imeji.spaceModel),
                new Function<String, Space>() {
                    @Override
                    public Space apply(String id) {
                        try {
                            return retrieve(URI.create(id), Imeji.adminUser);
                        } catch (ImejiException e) {
                            LOGGER.info("Cannot retrieve space: " + id );
                        }
                        return null;
                    }
                });
    }

    /**
     * Retrieve an {@link Space} without its {@link Item}
     *
     * @param uri
     * @param user
     * @return
     * @throws ImejiException
     */
    public Space retrieveLazy(URI uri, User user) throws ImejiException {
        return (Space) reader.readLazy(uri.toString(), user, new Space());
    }

    /**
     * Delete the {@link Space}
     *
     * @param space
     * @param user
     * @throws ImejiException
     */
    public void delete(Space space, User user) throws ImejiException, IOException {
        removeFile(space);
        writer.delete(WriterFacade.toList(space), user);
    }

}
