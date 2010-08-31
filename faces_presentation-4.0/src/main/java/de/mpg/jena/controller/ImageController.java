package de.mpg.jena.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDB;

import thewebsemantic.Bean2RDF;
import thewebsemantic.Sparql;

import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.Image.Visibility;

public class ImageController extends ImejiController{
	
	
	public ImageController(User user)
	{
		super(user);
	}
	
	public void create(Image img, URI coll)
	{
		writeCreateProperties(img.getProperties(), user);
		img.setCollection(coll);
		base.begin();
		CollectionImeji ic = rdf2Bean.load(CollectionImeji.class, coll);
		ic.getImages().add(img.getId());
		bean2RDF.saveDeep(img);
		bean2RDF.saveDeep(ic);
		base.commit();
	}
	
	public void create(Collection<Image> images, URI coll)
	{
	    //store.getLoader().startBulkUpdate();
		base.begin();
		CollectionImeji ic = rdf2Bean.load(CollectionImeji.class, coll);
		for(Image img : images)
		{
			writeCreateProperties(img.getProperties(), user);
			img.setCollection(coll);
			ic.getImages().add(img.getId());
			bean2RDF.saveDeep(img);
			//System.out.println("Img created!");
		}
		base.commit();
	}
	
	public void update(Image img)
	{
		writeUpdateProperties(img.getProperties(), user);
		base.begin();
		bean2RDF.saveDeep(img);
		base.commit();
	}
	

	public void update(Collection<Image> images)
	{
	    base.begin();
		for(Image img : images)
		{
			writeUpdateProperties(img.getProperties(), user);
			bean2RDF.saveDeep(img);
		}
		base.commit();
	}
	
	public Image retrieve(URI imgUri)
	{
		return rdf2Bean.load(Image.class, imgUri);
	}
	
	public Collection<Image> search(User user, List<SearchCriterion> scList, SortCriterion sortCri, int limit, int offset)
    {
        String query = createQuery(scList, sortCri, "http://imeji.mpdl.mpg.de/image", limit, offset);
        return Sparql.exec(getModel(), Image.class, query);
    }
	
	/*
	public ImejiImage retrieve(Collection<URI> imgUris, ImejiUser user)
	{
		return RDF2Bean.load(ImejiImage.class, imgUri);
	}
	*/
	
	public Collection<Image> retrieveAll()
	{
		return rdf2Bean.load(Image.class);
	}
	
	public void delete(Image img, User user)
	{
		
	}
	
	public void release()
	{
		
	}
	
	public void withdraw()
	{
		
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
	    /*
	    File f = new File("/home/haarlaender/basic_imeji_data.rdf");
	    f.createNewFile();
		FileOutputStream fos = new FileOutputStream(f);
		base.write(fos, "RDF/XML");
		fos.flush();
		fos.close();
		*/
	    base.write(System.out);
	    
	    SearchCriterion sc = new SearchCriterion(ImejiNamespaces.PROPERTIES_CREATED_BY_USER_GRANT_TYPE, "http://imeji.mpdl.mpg.de/grantType/CONTAINER_ADMIN");
	    SearchCriterion sc2 = new SearchCriterion(ImejiNamespaces.PROPERTIES_CREATED_BY_USER_GRANT_FOR, "http://test.de");
	    
	    List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
	    scList.add(sc);
	    scList.add(sc2);
	    
	    ImageController ic = new ImageController(null);
	    String qu = ic.createQuery(scList, null, "http://imeji.mpdl.mpg.de/collection", 10 ,0);
	    
	    Collection<CollectionImeji> result = Sparql.exec(base, CollectionImeji.class, qu);
	    System.out.println("Found: " +result.size() + "results ");
	}
	
	public static void main2(String[] arg) throws Exception
	{
		
		User user = createUser();
		
		CollectionController icc = new CollectionController(user);
		ImageController iic = new ImageController(user);
		

	
		
		for(int j=0; j<2;j++)
		{
			CollectionImeji coll = new CollectionImeji();

			
			//coll.setId(new URI("http://imeji.mpdl.mpg.de/collection/" + UUID.randomUUID().toString()));
			coll.getMetadata().setTitle("TestCollection " +j);
			coll.getMetadata().setDescription("TestDesc " +j);
			coll.setProfile(null);
			
			
			Person person = new Person();
			person.setGivenName("Bastien");
			person.setFamilyName("Saquet");
			Organization org = new Organization();
			org.setName("Test Org Unit");
			coll.getMetadata().getPersons().add(person);
			person.getOrganizations().add(org);
			
			System.out.println("Create collection");
			icc.create(coll);
			System.out.println("End create coll");
			//base.write(System.out);
			
			List<Image> imgList = new LinkedList<Image>();
			for(int i=0; i<10;i++)
			{
				System.out.println("Add image: " +i );
				Image im = new Image();
				im.getMetadata().add(new ImageMetadata("http://purl.org/dc/elements/1.1/","description", "Test description for image in collection " + j + ", image "  + i));
				im.getMetadata().add(new ImageMetadata("http://purl.org/dc/elements/1.1/","title", "Test title " + i));
				
				im.setId(new URI("http://dev-coreservice.mpdl.mpg.de/ir/item/escidoc:"+UUID.randomUUID()));
				im.setFullImageUrl(new URI("http://colab.mpdl.mpg.de/mediawiki/skins/monobook/mpdl-logo.png"));
				im.setThumbnailImageUrl(new URI("http://colab.mpdl.mpg.de/mediawiki/skins/monobook/mpdl-logo.png"));
				im.setWebImageUrl(new URI("http://colab.mpdl.mpg.de/mediawiki/skins/monobook/mpdl-logo.png"));
				
				im.setVisibility(Visibility.PUBLIC);
				im.setCollection(coll.getId());
				ImageMetadata imm = new ImageMetadata();
				imm.setElementNamespace("http://example"+i);
				imm.setName("gender");
				imm.setValue("emotion");
				im.getMetadata().add(imm);
				//XmlLiteral xmlString = new XmlLiteral("<faces-md>age</faces-md>");
				//im.setMetadata(xmlString);
				
				imgList.add(im);

				
			}
			
			long startCreateImg = System.currentTimeMillis();
			System.out.println("start creating "+ imgList.size() +"images");
			iic.create(imgList, coll.getId());
			long stopCreatingImg = System.currentTimeMillis();
			System.out.println("stop creating image in" + String.valueOf(stopCreatingImg-startCreateImg));
			
			
			
			
			
		
		}
		
		
		
		
		//base.write(System.out, "RDF/XML");
		List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
		System.out.println("start retrieval");
		
		//scList.add(new SearchCriterion(ImejiNamespaces.CONTAINER_METADATA_PERSON_FAMILY_NAME, "Saquet"));
		scList.add(new SearchCriterion(ImejiNamespaces.CONTAINER_METADATA_TITLE, "New Title"));
		
		long startR = System.currentTimeMillis();
		System.out.println("start retrieving all collections");
		Collection<CollectionImeji> result = icc.retrieveAll();
		long stopR = System.currentTimeMillis();
		System.out.println("stop retriveing all collections in " + String.valueOf(stopR-startR));
		//Collection<ImejiCollection> result = new RDF2Bean(base).load(ImejiCollection.class);
		
		
		Collection<Image> images = new LinkedList<Image>();
		for(CollectionImeji resColl : result)
		{
			if (resColl.getImages().size()>1)
			{
				System.out.println(resColl.getId() + " Collection " +resColl.getMetadata().getTitle() + " has " + resColl.getImages().size() + " images");
				
				
				
				for(URI imgUri : resColl.getImages())
				{
					
					
					//long startRetrImg = System.currentTimeMillis();
					//System.out.println("start retrieving img");
					Image img = iic.retrieve(imgUri);
					//long stopRetrImg = System.currentTimeMillis();
					//System.out.println("stop retriveing img " + String.valueOf(stopRetrImg-startRetrImg));
					
					
					
					img.getMetadata().add(new ImageMetadata("markus","mh", "test"));
					

					images.add(img);
					
					
					
					
					/*
					for(ImejiImageMetadata imgMeta : img.getMetadata())
					{
					System.out.print(imgMeta.getElementNamespace() + " -- ");
					System.out.print(imgMeta.getName() + " -- ");
					System.out.println(imgMeta.getValue() + " -- ");
					}
					
					*/
					
				}
				
				/*
				long start = System.currentTimeMillis();
				System.out.println("start updating");
				icc.create(resColl, user);
				long stop = System.currentTimeMillis();
				System.out.println("end updating in " + String.valueOf(stop-start));
				*/
			
			}
		}
		
				
				long start = System.currentTimeMillis();
				System.out.println("start updating "+images.size() + " images");
				iic.update(images);
				long stop = System.currentTimeMillis();
				System.out.println("end updating img in " + String.valueOf(stop-start));
		
				 base.write(System.out);
				base.close();
               
				System.out.println(rdf2Bean.load(User.class, "haarlaender@mpdl.mpg.de").getName());	
				
				
		/*
		String q = "SELECT ?v00 WHERE { ?s a <http://imeji.mpdl.mpg.de/collection> . ?s <http://imeji.mpdl.mpg.de/container/metadata> ?v10 . ?v10 <http://purl.org/dc/elements/1.1/title> ?v00 }";
		
		Query queryObject = QueryFactory.create(q);
		QueryExecution qe = QueryExecutionFactory.create(queryObject, base);
		ResultSet results = qe.execSelect();
		ResultSetFormatter.out(System.out, results);
		qe.close();
		*/
	}
	
}
