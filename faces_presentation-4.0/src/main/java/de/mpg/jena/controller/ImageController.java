package de.mpg.jena.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.imeji.util.LoginHelper;
import de.mpg.jena.ImejiBean2RDF;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.ImejiRDF2Bean;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.sparql.ImejiSPARQL;
import de.mpg.jena.sparql.QuerySPARQL;
import de.mpg.jena.sparql.query.QuerySPARQLImpl;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.Image.Visibility;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.Properties.Status;
import de.mpg.jena.vo.User;

public class ImageController extends ImejiController
{
    private String additionalQuery = "";
    private static Logger logger = null;

    private static ImejiRDF2Bean imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.imageModel);
	private static ImejiBean2RDF imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);
    
    public ImageController(User user)
    {
        super(user);
        logger = Logger.getLogger(ImageController.class);
    }
    
    private void init()
    {
    	imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.imageModel);
    	imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);
    }

    public void create(Image img, URI coll) throws Exception
    {
    	imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);
        CollectionController cc = new CollectionController(user);
        CollectionImeji ic = cc.retrieve(coll);
        
    	writeCreateProperties(img.getProperties(), user);
        img.setVisibility(Visibility.PUBLIC);
        img.setCollection(coll);
        img.setId(ObjectHelper.getURI(Image.class, Integer.toString(getUniqueId())));
        img.getMetadataSet().setProfile(ic.getProfile());
        
        imejiBean2RDF.create(img, user);
      
        ic.getImages().add(img.getId());
        cc.update(ic);
        cleanGraph();
    }

    public void create(Collection<Image> images, URI coll) throws Exception
    {
        
    	CollectionController cc = new CollectionController(user);
    	CollectionImeji ic = cc.retrieve(coll);
        imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);
        
        for (Image img : images)
        {
        	 writeCreateProperties(img.getProperties(), user);
             img.setVisibility(Visibility.PUBLIC);
             img.setCollection(coll);
             img.setId(ObjectHelper.getURI(Image.class, Integer.toString(getUniqueId())));
             img.getMetadataSet().setProfile(ic.getProfile());
             imejiBean2RDF.create(img, user);
             ic.getImages().add(img.getId());
        }
        cc.update(ic);
        cleanGraph();
    }
    

    public void update(Image img) throws Exception
    {
    	imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel); 
    	imejiBean2RDF.saveDeep(img, user);
    	cleanGraph(ImejiJena.imageModel);
    }

    public void update(Collection<Image> images) throws Exception
    {
    	imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel); 
    	for (Image img : images)
        {
    		imejiBean2RDF.saveDeep(img, user);
        }
    	cleanGraph(ImejiJena.imageModel);
    }
    
    public Image retrieve(URI imgUri)
    {
    	imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.imageModel); 
    	return (Image)imejiRDF2Bean.load(imgUri.toString(), user);
    }

    public Image retrieve(String id)
    {
    	imejiRDF2Bean = new ImejiRDF2Bean(ImejiJena.imageModel);
    	return (Image)imejiRDF2Bean.load(ObjectHelper.getURI(Image.class, id).toString(), user);
    }
    
    /**
     * NOT WORKING
     * @param uri
     */
    public void getGraph(URI uri)
    {
    	additionalQuery = " . <" + uri.toString() + "> <http://imeji.mpdl.mpg.de/image/metadata> ?md . ?md <http://www.w3.org/2000/01/rdf-schema#member> ?list . ?list <http://purl.org/dc/terms/type> ?type";
    	QuerySPARQL querySPARQL = new QuerySPARQLImpl();
        String query = querySPARQL.createConstructQuery(new ArrayList<SearchCriterion>(), null,	"http://imeji.mpdl.mpg.de/image", additionalQuery , "?s=<http://imeji.mpdl.mpg.de/image/111>", 1, 0, user);
    	ImejiSPARQL.execConstruct(query).write(System.out, "RDF/XML-ABBREV");
    }
    
    /**
     * Get the number of all images
     * @return
     */
    public int allImagesSize()
    {
    	return ImejiSPARQL.execCount("SELECT ?s count(DISTINCT ?s) WHERE { ?s a <http://imeji.mpdl.mpg.de/image>}");
    }
    
    public int getNumberOfResults(List<SearchCriterion> scList) throws Exception
    {
    	additionalQuery = "";
        QuerySPARQL querySPARQL = new QuerySPARQLImpl();
        String query = querySPARQL.createCountQuery(scList, null, "http://imeji.mpdl.mpg.de/image", "", "", -1, 0, user);
    	return ImejiSPARQL.execCount(query);
    }

    public Collection<Image> search(List<SearchCriterion> scList, SortCriterion sortCri, int limit, int offset)
            throws Exception
    {
        additionalQuery = "";
        QuerySPARQL querySPARQL = new QuerySPARQLImpl();
        String query = querySPARQL.createQuery(scList, sortCri,	"http://imeji.mpdl.mpg.de/image", additionalQuery, "", limit, offset, user);
        return  ImejiSPARQL.execAndLoad(query, Image.class);
    }
    public Collection<Image> search(List<SearchCriterion> scList, SortCriterion sortCri, int limit, int offset, List<String> uris) throws Exception
	{
		additionalQuery = "";
		String additionalFilter ="";
		for (String uri : uris)
		{
			additionalFilter += " || ?s=<" + uri + ">";
		}
		QuerySPARQL querySPARQL = new QuerySPARQLImpl();
		String query = querySPARQL.createQuery(scList, sortCri,	"http://imeji.mpdl.mpg.de/image", additionalQuery, additionalFilter, limit, offset, user);
		return  ImejiSPARQL.execAndLoad(query, Image.class);
	}
    
    /**
     * @param scList
     * @param sortCri
     * @param limit
     * @param offset
     * @return
     * @throws Exception
     */
//    public Collection<Image> searchAdvanced(List<List<SearchCriterion>> scList, SortCriterion sortCri, int limit, int offset) throws Exception
//    {
//        additionalQuery = "";
//        List<SearchCriterion> l = new ArrayList<SearchCriterion>();
//        for (List<SearchCriterion> sl : scList)
//        {
//        	l.addAll(sl);
//        }
//        String query = 	QuerySPARQLImpl.createQueryOld("SELECT", scList, sortCri, "http://imeji.mpdl.mpg.de/image", "", "", limit, offset);
//        return ImejiSPARQL.execAndLoad(query, Image.class);
//    }
//    
//    public Collection<Image> searchAdvanced(Model model, List<List<SearchCriterion>> scList, SortCriterion sortCri, int limit,
//            int offset) throws Exception
//    {
//        additionalQuery = "";
//        String query = createQuery("SELECT", scList, sortCri, "http://imeji.mpdl.mpg.de/image", limit, offset);
//        return  ImejiSPARQL.execAndLoad(query, Image.class);
//    }

//    public Collection<Image> searchAdvancedInContainer(URI containerUri, List<List<SearchCriterion>> scList,
//            SortCriterion sortCri, int limit, int offset) throws Exception
//    {
//        additionalQuery = " . <" + containerUri.toString() + "> <http://imeji.mpdl.mpg.de/images> ?s";
//        String query = createQuery("SELECT", scList, sortCri, "http://imeji.mpdl.mpg.de/image", limit, offset);
//        return ImejiSPARQL.execAndLoad(query, Image.class);
//    }
//    
//    public Collection<Image> searchAdvancedInContainer(Model model,URI containerUri, List<List<SearchCriterion>> scList,
//            SortCriterion sortCri, int limit, int offset) throws Exception
//    {
//        additionalQuery = " . <" + containerUri.toString() + "> <http://imeji.mpdl.mpg.de/images> ?s";
//        String query = createQuery("SELECT", scList, sortCri, "http://imeji.mpdl.mpg.de/image", limit, offset);
//        return ImejiSPARQL.execAndLoad(model, query, Image.class);
//    }
    
    public int getNumberOfResultsInContainer(URI containerUri, List<SearchCriterion> scList) throws Exception
    {
    	additionalQuery = " . <" + containerUri.toString() + "> <http://imeji.mpdl.mpg.de/images> ?s";
    	QuerySPARQL querySPARQL = new QuerySPARQLImpl();
        String query = querySPARQL.createCountQuery(scList, null,	"http://imeji.mpdl.mpg.de/image", additionalQuery, "", -1, 0, user);
    	return ImejiSPARQL.execCount(query);
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
        QuerySPARQL querySPARQL = new QuerySPARQLImpl();
        String query = querySPARQL.createQuery(scList, sortCri,	"http://imeji.mpdl.mpg.de/image", additionalQuery, "", limit, offset, user);
        return ImejiSPARQL.execAndLoad(query, Image.class);
    }
    
//    public Collection<Image> searchImageInContainer(Model model, URI containerUri, List<SearchCriterion> scList,
//            SortCriterion sortCri, int limit, int offset) throws Exception
//    {
//        additionalQuery = " . <" + containerUri.toString() + "> <http://imeji.mpdl.mpg.de/images> ?s";
//        List<List<SearchCriterion>> list = new ArrayList<List<SearchCriterion>>();
//        if (scList != null && scList.size() > 0)
//            list.add(scList);
//        String query = createQuery("SELECT",list, sortCri, "http://imeji.mpdl.mpg.de/image", limit, offset);
//        System.out.println("eeee");
//        return  ImejiSPARQL.execAndLoad(model, query, Image.class);
//    }

    /*
     * public ImejiImage retrieve(Collection<URI> imgUris, ImejiUser user) { return RDF2Bean.load(ImejiImage.class,
     * imgUri); }
     */
    /*
     * public Collection<Image> retrieveAll() { return rdf2Bean.load(Image.class); }
     */
    public void delete(Image img, User user) throws Exception
    {
    	String itemId = img.getEscidocId();
    	imejiBean2RDF = new ImejiBean2RDF(ImejiJena.imageModel);
    	imejiBean2RDF.delete(img, user);
    	 try
         {
             ServiceLocator.getItemHandler(getEscidocUserHandle()).delete(itemId);
         }
         catch (Exception e)
         {
             logger.warn("Error deleting image in escidoc: ", e);
         }
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

    /**
     * @deprecated
     * @return
     * @throws Exception
     */
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
           // mdp = pc.create(mdp);
            coll.setProfile(mdp.getId());
            System.out.println("Create collection");
            //icc.create(coll);
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
                    	if (grant.getGrantFor() != null) filter += " || ?collection=<" + grant.getGrantFor().toString() + ">";
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
