package de.mpg.escidoc.faces.metastore.controller;

import java.net.URI;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.mpg.escidoc.faces.metastore.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.escidoc.faces.metastore.vo.CollectionImeji;
import de.mpg.escidoc.faces.metastore.vo.Image;
import de.mpg.escidoc.faces.metastore.vo.ImageMetadata;
import de.mpg.escidoc.faces.metastore.vo.Organization;
import de.mpg.escidoc.faces.metastore.vo.Person;
import de.mpg.escidoc.faces.metastore.vo.User;
import de.mpg.escidoc.faces.metastore.vo.Image.Visibility;

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
		base.begin();
		CollectionImeji ic = rdf2Bean.load(CollectionImeji.class, coll);
		for(Image img : images)
		{
			writeCreateProperties(img.getProperties(), user);
			img.setCollection(coll);
			ic.getImages().add(img.getId());
			bean2RDF.saveDeep(img);
		}
		bean2RDF.saveDeep(ic);
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
	
	public void search(User user, List<SearchCriterion> scList)
	{
		
		
		
	}
	
	
	
	public static User createUser() throws Exception
	{
		User user = new User();
		user.setEmail("imeji@mpdl.mpg.de");
		user.setName("Imeji Test User");
		user.setNick("itu");
		user.setEncryptedPassword(UserController.convertToMD5("test"));
		System.out.println(user.getEncryptedPassword());
		new UserController(null).create(user);
		return user;
		
		
	}
	
	/*
	public static void main2(String[] arg) throws Exception
	{
		createUser();
	}
	*/
	
	public static void main(String[] arg) throws Exception
	{
		
		User user = createUser();
		
		CollectionController icc = new CollectionController(user);
		ImageController iic = new ImageController(user);

		

	
		
		for(int j=5; j<1;j++)
		{
			CollectionImeji coll = new CollectionImeji();

			
			//coll.setId(new URI("http://imeji.mpdl.mpg.de/collection/" + UUID.randomUUID().toString()));
			coll.getMetadata().setTitle("TestCollection " +j);
			coll.getMetadata().setDescription("TestDesc " +j);
			
			
			Person person = new Person();
			person.setGivenName("Bastien");
			person.setFamilyName("Saquet");
			Organization org = new Organization();
			org.setName("Test Org Unit");
			coll.getMetadata().getPerson().add(person);
			person.setOrganization(org);
			
			Thread.sleep(1000);
			
			System.out.println("Create collection");
			icc.create(coll);
			System.out.println("End create coll");
			
			List<Image> imgList = new LinkedList<Image>();
			for(int i=0; i<10;i++)
			{
				System.out.println("Add image: " +i );
				Image im = new Image();
				im.getMetadata().add(new ImageMetadata("http://purl.org/dc/elements/1.1/","description", "Test description for image in collection " + j + ", image "  + i));
				im.getMetadata().add(new ImageMetadata("http://purl.org/dc/elements/1.1/","title", "Test title " + i));
				
				im.setId(new URI("http://dev-coreservice.mpdl.mpg.de/ir/item/escidoc:" + UUID.randomUUID()));
				im.setFullImageUrl(new URI("http://dev-coreservice.mpdl.mpg.de/ir/item/escidoc:12345/component/blaaa/content"));
				
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
			
			Thread.sleep(1000);
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
				System.out.println("start updating " + images.size() + " images");
				iic.update(images);
				long stop = System.currentTimeMillis();
				System.out.println("end updating img in " + String.valueOf(stop-start));
		
		
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
