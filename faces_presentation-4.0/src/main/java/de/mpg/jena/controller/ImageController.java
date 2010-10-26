package de.mpg.jena.controller;

import java.net.URI;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import thewebsemantic.Sparql;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.imeji.util.LoginHelper;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.exceptions.AuthenticationException;
import de.mpg.jena.controller.exceptions.AuthorizationException;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.Image.Visibility;
import de.mpg.jena.vo.Properties.Status;

public class ImageController extends ImejiController
{
    private String additionalQuery = "";
    private static Logger logger = null;

    public ImageController(User user)
    {
        super(user);
        logger = Logger.getLogger(ImageController.class);
    }

    public void create(Image img, URI coll) throws Exception
    {
        CollectionImeji ic = rdf2Bean.load(CollectionImeji.class, coll);
        writeCreateProperties(img.getProperties(), user);
        checkUserCredentials(img, ic);
        // img.getProperties().setStatus(Status.RELEASED);
        img.setVisibility(Visibility.PUBLIC);
        img.setCollection(coll);
        img.setId(ObjectHelper.getURI(Image.class, Integer.toString(getUniqueId())));
        base.begin();
        ic.getImages().add(img.getId());
        bean2RDF.saveDeep(img);
        bean2RDF.saveDeep(ic);
        cleanGraph();
        base.commit();
    }

    public void create(Collection<Image> images, URI coll) throws Exception
    {
        // store.getLoader().startBulkUpdate();
        base.begin();
        CollectionImeji ic = rdf2Bean.load(CollectionImeji.class, coll);
        for (Image img : images)
        {
            writeCreateProperties(img.getProperties(), user);
            checkUserCredentials(img, ic);
            // img.getProperties().setStatus(Status.RELEASED);
            img.setVisibility(Visibility.PUBLIC);
            img.setCollection(coll);
            ic.getImages().add(img.getId());
            bean2RDF.saveDeep(img);
            // System.out.println("Img created!");
        }
        // Workarround: activate lazylist
        bean2RDF.saveDeep(ic);
        cleanGraph();
        base.commit();
    }

    public void update(Image img) throws Exception
    {
        CollectionImeji ic = rdf2Bean.load(CollectionImeji.class, img.getCollection());
        checkUserCredentials(img, ic);
        writeUpdateProperties(img.getProperties(), user);
        base.begin();
        // Workarround: activate lazylist
        img.getMetadata().size();
        bean2RDF.saveDeep(img);
        cleanGraph();
        base.commit();
    }

    public void update(Collection<Image> images) throws Exception
    {
        base.begin();
        for (Image img : images)
        {
            CollectionImeji ic = rdf2Bean.load(CollectionImeji.class, img.getCollection());
            checkUserCredentials(img, ic);
            writeUpdateProperties(img.getProperties(), user);
            // Workarround: activate lazylist
            img.getMetadata().size();
            bean2RDF.saveDeep(img);
        }
        cleanGraph();
        base.commit();
    }

    public Image retrieve(URI imgUri)
    {
        return rdf2Bean.load(Image.class, imgUri);
    }

    public Image retrieve(String id)
    {
        return (Image)rdf2Bean.load(ObjectHelper.getURI(Image.class, id).toString());
    }

    public Collection<Image> search(List<SearchCriterion> scList, SortCriterion sortCri, int limit, int offset)
            throws Exception
    {
        additionalQuery = "";
        List<List<SearchCriterion>> list = new ArrayList<List<SearchCriterion>>();
        if (scList != null && scList.size() > 0)
            list.add(scList);
        String query = createQuery(list, sortCri, "http://imeji.mpdl.mpg.de/image", limit, offset);
        return Sparql.exec(getModel(), Image.class, query);
    }

    public Collection<Image> searchAdvanced(List<List<SearchCriterion>> scList, SortCriterion sortCri, int limit,
            int offset) throws Exception
    {
        additionalQuery = "";
        String query = createQuery(scList, sortCri, "http://imeji.mpdl.mpg.de/image", limit, offset);
        return Sparql.exec(getModel(), Image.class, query, Syntax.syntaxARQ);
    }

    public Collection<Image> searchAdvancedInContainer(URI containerUri, List<List<SearchCriterion>> scList,
            SortCriterion sortCri, int limit, int offset) throws Exception
    {
        additionalQuery = " . <" + containerUri.toString() + "> <http://imeji.mpdl.mpg.de/images> ?s";
        String query = createQuery(scList, sortCri, "http://imeji.mpdl.mpg.de/image", limit, offset);
        return Sparql.exec(getModel(), Image.class, query, Syntax.syntaxARQ);
    }

    /*
     * public Collection<Image> searchAdvanced(List<List<SearchCriterion>> scList, SortCriterion sortCri, int limit, int
     * offset) throws Exception { additionalQuery = ""; String query = createQuery(scList, sortCri,
     * "http://imeji.mpdl.mpg.de/image", limit, offset); return Sparql.exec(getModel(), Image.class, query); }
     */
    public Collection<Image> searchImageInContainer(URI containerUri, List<SearchCriterion> scList,
            SortCriterion sortCri, int limit, int offset) throws Exception
    {
        additionalQuery = " . <" + containerUri.toString() + "> <http://imeji.mpdl.mpg.de/images> ?s";
        List<List<SearchCriterion>> list = new ArrayList<List<SearchCriterion>>();
        if (scList != null && scList.size() > 0)
            list.add(scList);
        String query = createQuery(list, sortCri, "http://imeji.mpdl.mpg.de/image", limit, offset);
        return Sparql.exec(getModel(), Image.class, query, Syntax.syntaxARQ);
    }

    /*
     * public ImejiImage retrieve(Collection<URI> imgUris, ImejiUser user) { return RDF2Bean.load(ImejiImage.class,
     * imgUri); }
     */
    /*
     * public Collection<Image> retrieveAll() { return rdf2Bean.load(Image.class); }
     */
    public void delete(Image img, User user)
    {
        String itemId = img.getEscidocId();
        try
        {
            ServiceLocator.getItemHandler(getEscidocUserHandle()).delete(itemId);
        }
        catch (Exception e)
        {
            logger.warn("Error deleting image in escidoc: ", e);
        }
        bean2RDF.delete(img);
    }

    public void release()
    {
    }

    public void withdraw()
    {
    }

    public String getEscidocUserHandle() throws Exception
    {
        String userName = PropertyReader.getProperty("imeji.escidoc.user");
        String password = PropertyReader.getProperty("imeji.escidoc.password");
        return LoginHelper.login(userName, password);
    }

    private void checkUserCredentials(Image i, CollectionImeji c) throws Exception
    {
        if (user == null)
        {
            throw new AuthenticationException("User is null!");
        }
        else if (!ObjectHelper.getURI(User.class, user.getEmail()).equals(c.getProperties().getCreatedBy()))
        {
            for (Grant g : user.getGrants())
            {
                if (g.getGrantFor().equals(c.getId())
                        && (g.getGrantType().equals(GrantType.CONTAINER_ADMIN)
                                || g.getGrantType().equals(GrantType.CONTAINER_EDITOR)
                                || g.getGrantType().equals(GrantType.IMAGE_EDITOR) || g.getGrantType().equals(
                                GrantType.IMAGE_UPLOADER)))
                {
                    return;
                }
            }
            throw new AuthorizationException("User not authorized to create/update image " + c.getId());
        }
    }

    public static User createUser() throws Exception
    {
        User user = new User();
        user.setEmail("imeji@mpdl.mpg.de");
        user.setName("Imeji Test User");
        user.setNick("itu");
        user.setEncryptedPassword(UserController.convertToMD5("test"));
        user.getGrants().add(new Grant(GrantType.CONTAINER_ADMIN, URI.create("http://test.de")));
        System.out.println(user.getEncryptedPassword());
        new UserController(null).create(user);
        return user;
    }

    public static void main(String[] arg) throws Exception
    {
        User user = createUser();
        ImageController ic = new ImageController(user);
        base.write(System.out);
        List<SearchCriterion> scList2 = ImagesBean
                .transformQuery2("IMAGE_COLLECTION_PROFILE.URI=\"http://imeji.mpdl.mpg.de/mdprofile/0\" AND ( ( IMAGE_METADATA_NAME.EQUALS=\"author\" AND IMAGE_METADATA_COMPLEXTYPE_TEXT.REGEX=\"test\" ) OR ( IMAGE_METADATA_NAME.EQUALS=\"mdname2\" AND IMAGE_METADATA_COMPLEXTYPE_TEXT.REGEX=\"test2\" ) )");
        System.out.println("test");
        System.out.println(ic.createQuery2(scList2, null, "type", -1, 0));
        SearchCriterion sc0 = new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_TEXT,
                "white", Filtertype.REGEX);
        SearchCriterion sc1 = new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_NAME, "colour",
                Filtertype.EQUALS);
        List<SearchCriterion> scList0 = new ArrayList<SearchCriterion>();
        scList0.add(sc0);
        scList0.add(sc1);
        SearchCriterion sc2 = new SearchCriterion(Operator.AND,
                ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_FAMILY_NAME, "white", Filtertype.REGEX);
        SearchCriterion sc3 = new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_NAME, "author",
                Filtertype.EQUALS);
        List<SearchCriterion> scList1 = new ArrayList<SearchCriterion>();
        scList1.add(sc2);
        scList1.add(sc3);
        List<List<SearchCriterion>> scList = new ArrayList<List<SearchCriterion>>();
        scList.add(scList0);
        scList.add(scList1);
        // Collection<Image> result = ic.searchAdvanced(scList, null, -1, 0);
        /*
         * Resource r = base.getResource("http://imeji.mpdl.mpg.de/collection/1"); r.removeProperties();
         */
        Collection<CollectionImeji> result = rdf2Bean.load(CollectionImeji.class);
        // CollectionImeji res = rdf2Bean.load(CollectionImeji.class, "http://imeji.mpdl.mpg.de/collection/1");
        System.out.println("IMAGE_MD_NAME.REGEX=\"bla va\" ".matches("\\s*[^\\s]+=\".*\"\\s+"));
        // System.out.println("Found: " +result.size() + "results ");
        /*
         * for (Image img : result) { System.out.println(img.getId()); }
         */
        String q = "SELECT DISTINCT * WHERE { ?s a <http://imeji.mpdl.mpg.de/collection> }";
        // String q =
        // "SELECT DISTINCT * WHERE { ?s a <http://imeji.mpdl.mpg.de/image>  .  OPTIONAL { ?s <http://imeji.mpdl.mpg.de/image/metadata> ?v0 . ?v0 <http://www.w3.org/2000/01/rdf-schema#member> ?v1 . ?v1 <http://imeji.mpdl.mpg.de/image/metadata/name> ?v10 . ?v1 <http://purl.org/dc/terms/type> ?v2 . ?v2 <http://imeji.mpdl.mpg.de/metadata/enumType> ?v4 .  OPTIONAL { ?v2 <http://imeji.mpdl.mpg.de/metadata/date> ?v3 }}}";
        // Syntax.
        Query queryObject = QueryFactory.create(q, Syntax.syntaxARQ);
        QueryExecution qe = QueryExecutionFactory.create(queryObject, base);
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(System.out, results);
        /*
         * while(results.hasNext()) { QuerySolution qs = results.next(); Resource s = qs.getResource("?s");
         * s.removeProperties(); //base.remove(qs.getResource("?s"), qs.get("?p").as(Property.class), qs.get("?o")); }
         */
    }

    public static void main2(String[] arg) throws Exception
    {
        User user = createUser();
        CollectionController icc = new CollectionController(user);
        ImageController iic = new ImageController(user);
        ProfileController pc = new ProfileController(user);
        for (int j = 0; j < 2; j++)
        {
            CollectionImeji coll = new CollectionImeji();
            // coll.setId(new URI("http://imeji.mpdl.mpg.de/collection/" + UUID.randomUUID().toString()));
            coll.getMetadata().setTitle("TestCollection " + j);
            coll.getMetadata().setDescription("TestDesc " + j);
            Person person = new Person();
            person.setGivenName("Bastien");
            person.setFamilyName("Saquet");
            Organization org = new Organization();
            org.setName("Test Org Unit");
            coll.getMetadata().getPersons().add(person);
            person.getOrganizations().add(org);
            MetadataProfile mdp = new MetadataProfile();
            mdp.setDescription("blaaaa");
            pc.create(mdp);
            coll.setProfile(mdp);
            System.out.println("Create collection");
            icc.create(coll);
            System.out.println("End create coll");
            // base.write(System.out);
            List<Image> imgList = new LinkedList<Image>();
            for (int i = 0; i < 10; i++)
            {
                System.out.println("Add image: " + i);
                Image im = new Image();
                // im.getMetadata().add(new ImageMetadata("description", new Text("description for coll " + j +
                // " image " + i )));
                // im.getMetadata().add(new ImageMetadata("title" , new Text("title for coll " + j + " image " + i )));
                im.setId(new URI("http://dev-coreservice.mpdl.mpg.de/ir/item/escidoc:" + UUID.randomUUID()));
                im.setFullImageUrl(new URI("http://colab.mpdl.mpg.de/mediawiki/skins/monobook/mpdl-logo.png"));
                im.setThumbnailImageUrl(new URI("http://colab.mpdl.mpg.de/mediawiki/skins/monobook/mpdl-logo.png"));
                im.setWebImageUrl(new URI("http://colab.mpdl.mpg.de/mediawiki/skins/monobook/mpdl-logo.png"));
                im.setVisibility(Visibility.PUBLIC);
                im.setCollection(coll.getId());
                // im.getMetadata().add(new ImageMetadata("emotion", new Text("happy")));
                // XmlLiteral xmlString = new XmlLiteral("<faces-md>age</faces-md>");
                // im.setMetadata(xmlString);
                imgList.add(im);
            }
            long startCreateImg = System.currentTimeMillis();
            System.out.println("start creating " + imgList.size() + "images");
            iic.create(imgList, coll.getId());
            long stopCreatingImg = System.currentTimeMillis();
            System.out.println("stop creating image in" + String.valueOf(stopCreatingImg - startCreateImg));
        }
        // base.write(System.out, "RDF/XML");
        List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
        System.out.println("start retrieval");
        // scList.add(new SearchCriterion(ImejiNamespaces.CONTAINER_METADATA_PERSON_FAMILY_NAME, "Saquet"));
        scList.add(new SearchCriterion(ImejiNamespaces.CONTAINER_METADATA_TITLE, "New Title"));
        long startR = System.currentTimeMillis();
        System.out.println("start retrieving all collections");
        Collection<CollectionImeji> result = icc.search(null, null, 1000, 0);
        long stopR = System.currentTimeMillis();
        System.out.println("stop retriveing all collections in " + String.valueOf(stopR - startR));
        // Collection<ImejiCollection> result = new RDF2Bean(base).load(ImejiCollection.class);
        Collection<Image> images = new LinkedList<Image>();
        for (CollectionImeji resColl : result)
        {
            resColl.getProperties().setStatus(Status.RELEASED);
            System.out.println(resColl.getId() + " Collection " + resColl.getMetadata().getTitle() + " has "
                    + resColl.getImages().size() + " images");
            for (URI imgUri : resColl.getImages())
            {
                // long startRetrImg = System.currentTimeMillis();
                // System.out.println("start retrieving img");
                Image img = iic.retrieve(imgUri);
                // long stopRetrImg = System.currentTimeMillis();
                // System.out.println("stop retriveing img " + String.valueOf(stopRetrImg-startRetrImg));
                // img.getMetadata().add(new ImageMetadata("markus", new Text("mh")));
                images.add(img);
                /*
                 * for(ImejiImageMetadata imgMeta : img.getMetadata()) { System.out.print(imgMeta.getElementNamespace()
                 * + " -- "); System.out.print(imgMeta.getName() + " -- "); System.out.println(imgMeta.getValue() +
                 * " -- "); }
                 */
            }
            /*
             * long start = System.currentTimeMillis(); System.out.println("start updating"); icc.create(resColl, user);
             * long stop = System.currentTimeMillis(); System.out.println("end updating in " +
             * String.valueOf(stop-start));
             */
        }
        long start = System.currentTimeMillis();
        System.out.println("start updating " + images.size() + " images");
        iic.update(images);
        long stop = System.currentTimeMillis();
        System.out.println("end updating img in " + String.valueOf(stop - start));
        base.write(System.out);
        base.close();
        System.out.println(rdf2Bean.load(User.class, "haarlaender@mpdl.mpg.de").getName());
        /*
         * String q =
         * "SELECT ?v00 WHERE { ?s a <http://imeji.mpdl.mpg.de/collection> . ?s <http://imeji.mpdl.mpg.de/container/metadata> ?v10 . ?v10 <http://purl.org/dc/elements/1.1/title> ?v00 }"
         * ; Query queryObject = QueryFactory.create(q); QueryExecution qe = QueryExecutionFactory.create(queryObject,
         * base); ResultSet results = qe.execSelect(); ResultSetFormatter.out(System.out, results); qe.close();
         */
    }

    @Override
    protected String getSpecificFilter() throws Exception
    {
        // Add filters for user management
        String filter = "(";
        if (user == null)
        {
            filter += "?collStatus = <http://imeji.mpdl.mpg.de/status/RELEASED> && ?visibility = <http://imeji.mpdl.mpg.de/image/visibility/PUBLIC>";
        }
        else
        {
            String userUri = "http://xmlns.com/foaf/0.1/Person/" + URLEncoder.encode(user.getEmail(), "UTF-8");
            filter += "(?collStatus = <http://imeji.mpdl.mpg.de/status/RELEASED> && ?visibility = <http://imeji.mpdl.mpg.de/image/visibility/PUBLIC>)";
            filter += " || ?collCreatedBy=<" + userUri + ">";
            for (Grant grant : user.getGrants())
            {
                switch (grant.getGrantType())
                {
                    case CONTAINER_ADMIN: // Add specifics here
                    default:
                        filter += " || ?collection=<" + grant.getGrantFor().toString() + ">";
                }
            }
        }
        filter += ")";
        return filter;
    }

    @Override
    protected String getSpecificQuery() throws Exception
    {
        return additionalQuery
                + " . ?s <http://imeji.mpdl.mpg.de/collection> ?collection . ?s <http://imeji.mpdl.mpg.de/visibility> ?visibility . ?collection <http://imeji.mpdl.mpg.de/properties> ?collprops . ?collprops <http://imeji.mpdl.mpg.de/createdBy> ?collCreatedBy . ?collprops <http://imeji.mpdl.mpg.de/status> ?collStatus ";
    }
}
