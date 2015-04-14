package de.mpg.imeji.logic.controller;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.query.URLQueryTransformer;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Space;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.imeji.presentation.util.PropertyReader;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
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


    private static final ReaderFacade reader = new ReaderFacade(
            Imeji.spaceModel);
    private static final WriterFacade writer = new WriterFacade(
            Imeji.spaceModel);

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
                    + "file"
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
        return createAskValidate(space, user, true);
    }
    public URI createNoValidate(Space space, User user)
            throws ImejiException {
        return createAskValidate(space, user, false);
    }

    public URI createAskValidate(Space space, User user, boolean validate) throws ImejiException {
        if(validate){
            validate(space, user);
        }
        writeCreateProperties(space, user);
        GrantController gc = new GrantController();
        gc.addGrants(user, AuthorizationPredefinedRoles.admin(space.getId()
                .toString(), null), user);
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
        if (f.exists()) {
            removeFile(f);
        } else {
            f.getParentFile().mkdirs();
            f.createNewFile();
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
        return storageUrl + id + StringHelper.urlSeparator
                + StringHelper.urlSeparator
                + filename;
    }

    /**
     * Transform and url to a file system path
     *
     * @param url
     * @return
     */
    public String transformUrlToPath(String url) {
        return URI.create(url).getPath().replace(URI.create(storageUrl).getPath(), storagePath).replace(
                StringHelper.urlSeparator, StringHelper.fileSeparator);
        // .replace(filename, StringHelper.normalizeFilename(filename));
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
     * Remove a single {@link File}
     *
     * @param f
     */
    public void removeFile(File f) {
        if (f.exists()) {
            boolean deleted = FileUtils.deleteQuietly(f);
            if (!deleted) {
                throw new RuntimeException(
                        "Impossible to delete the existing file.");
            }
        }
    }

    /**
     * Remove a single {@link File}
     *
     * @param uri
     * @throws IOException
     */
    public void removeDirectory(URI uri) throws IOException {
        File f = new File(transformUrlToPath(uri.toURL().toString())).getParentFile();
        if (f.exists())
            FileUtils.deleteDirectory(f);
    }

    public void validate (Space space, User u) throws ImejiException {
        if ( isNullOrEmptyTrim(space.getTitle())) {
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
     * Retrieve spaces filtered by query
     *
     * @param user
     * @param q
     * @return
     * @throws ImejiException
     */
    public List<Space> retrieve(User user, String q) throws ImejiException {
        List<Space> aList = new ArrayList<>();
        try {
            for (String spaceId: search(!isNullOrEmptyTrim(q) ? URLQueryTransformer.parseStringQuery(q) : null, user, null, 0, 0).getResults()) {
                aList.add(retrieve(URI.create(spaceId), user));
            }
        } catch (Exception e) {
            throw new UnprocessableError("Cannot retrieve spaces:", e);

        }
        return aList;
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
        removeDirectory(space.getLogoUrl());
        writer.delete(WriterFacade.toList(space), user);
    }

    /**
     * Search for space
     *
     * @param searchQuery
     * @param user
     * @param sortCri
     * @param limit
     * @param offset
     * @return
     */
    public SearchResult search(SearchQuery searchQuery, User user,
                               SortCriterion sortCri, int limit, int offset) {
        Search search = SearchFactory.create(Search.SearchType.SPACE);
        return search.search(searchQuery, sortCri, user);
    }
}
