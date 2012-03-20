package de.mpg.imeji.ingest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import java.util.Hashtable;

import java.util.List;
import java.util.NoSuchElementException;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import thewebsemantic.LocalizedString;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionBean;
import de.mpg.imeji.collection.CollectionSessionBean;

import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;

import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.search.SearchResult;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.MetadataSet;
import de.mpg.jena.vo.Properties;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.complextypes.Text;


public class IngestBean 
{
	private static Logger logger = Logger.getLogger(CollectionBean.class);
     
    
	private SessionBean sessionBean;
	private CollectionSessionBean collectionSessionBean;
	private ArrayList<MetadataProfile> mdProfiles;
	
	private IngestHelper ingestHelper;

	private ProfileController profileController;
	private ImageController imageController;
	private CollectionImeji colIme;
	
	private ArrayList<String> mdsFiles;
	private ArrayList<String> mdfFiles;
	
	private List<SelectItem> profilesMenu;
	
	private MetadataProfile currentMDProfile;
	
	public IngestBean() {

		this.sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		this.collectionSessionBean = (CollectionSessionBean) BeanHelper.getSessionBean(CollectionSessionBean.class);
		this.profileController = new ProfileController(sessionBean.getUser());
		this.imageController = new ImageController(this.sessionBean.getUser());
		this.ingestHelper = new IngestHelper();
		this.mdfFiles = new ArrayList<String>();
		this.mdsFiles = new ArrayList<String>();
		this.colIme = this.collectionSessionBean.getActive();
		this.profilesMenu = new ArrayList<SelectItem>();
		this.profilesMenu.add(new SelectItem(null,"Select Template"));

	}
	
	public IngestBean(CollectionImeji collection, User user) {
		this.sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		this.collectionSessionBean = (CollectionSessionBean) BeanHelper.getSessionBean(CollectionSessionBean.class);		
		this.profileController = new ProfileController(user);
		this.imageController = new ImageController(user);
		this.ingestHelper = new IngestHelper();
		this.mdfFiles = new ArrayList<String>();
		this.mdsFiles = new ArrayList<String>();
		this.colIme = collection;
		this.profilesMenu = new ArrayList<SelectItem>();
		this.profilesMenu.add(new SelectItem(null,"Select Template"));
	}

	/**
	 * @return the profilesMenu
	 */
	public List<SelectItem> getProfilesMenu() {
		return profilesMenu;
	}

	/**
	 * @return all profile names
	 */
	public ArrayList<String> getProfileNames() {
		ArrayList<String> profileNames = new ArrayList<String>(); 
		for (MetadataProfile mdp : this.mdProfiles) {
			profileNames.add(mdp.getTitle());
		}
		return profileNames;
	}
	
	/**
	 * @return all profile names which are created successfully
	 */
	public ArrayList<String> getProfileNamesSuccUP() {
		return this.mdsFiles;
	}
	
	/**
	 * @return all profile names which are created failed
	 */
	public ArrayList<String> getProfileNamesFailUP() {
		return this.mdfFiles;
	}
	
	
	
//	public String getInit()
//	{
//		UrlHelper.getParameterValue("col");
//		return "";
//	}
	
	/**
	 * @return the currentMDProfile
	 */
	public MetadataProfile getCurrentMDProfile() {
		return currentMDProfile;
	}

	/**
	 * @param currentMDProfile the currentMDProfile to set
	 */
	public void setCurrentMDProfile(MetadataProfile currentMDProfile) {
		this.currentMDProfile = currentMDProfile;
	}

	/**
	 * Uploads the ingest file
	 */
	public void save()
	{
		System.out.println("saved");
	}
	
	/**
	 * Uploads the ingest file
	 */
	public void cancel()
	{
		System.out.println("cancel");
	}
	
	
	/**
	 * Creating meta data profile from xml file
	 * @deprecated
	 */
	public ArrayList<MetadataProfile> createMetadataProfiles(File xmlFile)
	{
		URI uri = null;

		this.ingestHelper.extractXmlMDObjects(xmlFile);
		this.mdProfiles = this.getMetadataProfiles(this.ingestHelper.getProfileNames(),this.ingestHelper.getMdObjs());
		
		try {
			for (MetadataProfile mdprofile : this.mdProfiles) {
				uri = this.profileController.create(mdprofile);
				logger.info("Meta data profile: "+uri.toString()+" created!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.mdProfiles;
	}
	
	/**
	 * Creating meta data profile from xml file
	 */
	public ArrayList<MetadataProfile> loadMetadataProfiles(InputStream xmlFileStream)
	{
 
		this.ingestHelper.extractXmlMDObjects4Zuse(xmlFileStream);
		this.mdProfiles = this.getMetadataProfiles(this.ingestHelper.getProfileNames(),this.ingestHelper.getMdObjs());
		
		this.mdsFiles.clear();
		this.mdfFiles.clear();


		
		try {
			for (MetadataProfile mdprofile : this.mdProfiles) {
//				this.profileController.delete(mdprofile,this.sessionBean.getUser());
				try {
//					uri = this.profileController.create(mdprofile);		// TODO, this function shall load the profiles and not creating it yet.
//					uri = mdprofile.getId();					
//					logger.info("Meta data profile: "+uri.toString()+" loaded!");
					
					logger.info("Meta data profile: "+mdprofile.getTitle()+" loaded!");
					this.mdsFiles.add(mdprofile.getTitle());
					this.profilesMenu.add(new SelectItem(mdprofile.getId().toString(),mdprofile.getTitle()));
					
				} catch (java.lang.RuntimeException re) {
					logger.info(re.toString());
					this.mdfFiles.add(mdprofile.getTitle());
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.mdProfiles;
	}
	
	/**
	 * Uploads the ingest file
	 * @deprecated
	 */
	public void uploadIngestFile()
	{
		
		StringBuffer curDir = new StringBuffer(System.getProperty("user.dir")+"\\attachments\\");
		StringBuffer fileName = new StringBuffer("ZusePlan10Samples.xml");
		
//		// get active collection in fetch meta data profile
//		this.collectionSession = (CollectionSessionBean) BeanHelper.getSessionBean(CollectionSessionBean.class);
//		CollectionImeji colim = this.collectionSession.getActive();
//		if(colim.getProfile() != null)
//			System.out.println("Meta data profile id: "+colim.getProfile().getPath()+ "---"+curDir);
//		else {
//			System.out.println("Meta data profile id..." + curDir);
//		}

		File xmlFilename = new File(curDir.append(fileName).toString());
		URI uri = null;

		this.ingestHelper.extractXmlMDObjects(xmlFilename);
		this.mdProfiles = this.getMetadataProfiles(this.ingestHelper.getProfileNames(),this.ingestHelper.getMdObjs());
		this.colIme = this.collectionSessionBean.getActive();
		
		try {
			for (MetadataProfile mdprofile : this.mdProfiles) {
				uri = this.profileController.create(mdprofile);
				this.profileController.update(mdprofile);
				logger.info("Meta data profile: "+uri.toString()+" created!");
			}						
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		this.colIme.setProfile(uri);
//		this.ingest();
	}
	
	/**
	 * Gets the meta data profiles
	 * @param profiles
	 * @param mdObjs
	 * @return
	 */
	public ArrayList<MetadataProfile> getMetadataProfiles(Hashtable<String, Integer> profiles, ArrayList<XmlMDBean> mdObjs) {
		
		ArrayList<MetadataProfile> mdps = new ArrayList<MetadataProfile>(); 
		
		Enumeration<String> e = profiles.keys();
		String key;
		do {
			try {
				key = new String(e.nextElement());
			} catch (NoSuchElementException nsee) {
				// nomore meta data found
				break;
			}
			
			// gets the object for parsing meta data profile
			XmlMDBean mdo = mdObjs.get(profiles.get(key));
			
			mdps.add(this.getMetadataProfile(mdo));
			
		} while(!key.isEmpty());
		
		return mdps;
	}
	
	/**
	 * Gets the meta data profiles
	 * @param profiles
	 * @param mdObjs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public MetadataProfile getMetadataProfile(XmlMDBean mdObj) {
		MetadataProfile mdp = new MetadataProfile();
		
		// get active collection in fetch meta data profile
//		this.collectionSessionBean = (CollectionSessionBean) BeanHelper.getSessionBean(CollectionSessionBean.class);
		
//		this.colIme = this.collectionSessionBean.getActive();
		
		String posfix;
		if(this.colIme.getProfile() != null)
			//get the id of meta data profile
			posfix = this.colIme.getProfile().getPath();
		else {
			throw new NoSuchElementException("Meta data profile path not available!");
		}

		// maps the generic meta data (as String) to imeji meta data profile
		List<Statement> stList = new ArrayList<Statement>();
		Statement st = new Statement();
		Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);  	

		String localMachine = navigation.getApplicationUri();
		
		String ppname = mdObj.getProjectName()+"_"+mdObj.getProfileName()+"("+this.colIme.getId()+")";
		
		String url = IngestHelper.replaceSpace(localMachine + posfix + "/" + ppname); 
		
		mdp.setId(URI.create(url));
		mdp.setDescription(ppname);
		mdp.setProperties(new Properties());
		mdp.setTitle(ppname);
		
		Enumeration<String> e = mdObj.getMetadatas().keys();
		String tag;
		int pos = 0;
		do {
			try {
				tag = new String(e.nextElement());
			} catch (NoSuchElementException nsee) {
				// no more meta data found
				break;
			}

			ArrayList<LocalizedString> labels = new ArrayList<LocalizedString>();
			labels.add(new LocalizedString(tag,"de"));
//			labels.add(new LocalizedString(tag,"en"));
			
			ArrayList<LocalizedString> literalConstraints = new ArrayList<LocalizedString>();
//			literalConstraints.add(new LocalizedString(tag,"de"));
//			literalConstraints.add(new LocalizedString(tag,"en"));
			
			st = new Statement();
			
			//TODO: need to implement multiple label handling like for internationalization
			//TODO: multiple occurrences of one label
			
//			url = localMachine.getHostName() + posfix + "/" + tags;
			url = IngestHelper.replaceSpace(localMachine + posfix + "/" + tag);
			
			
			st.setName(URI.create(url));
			st.setPos(pos++);
			st.setType(new Text().getType().getURI());
			st.setLabels(labels);
			st.setLiteralConstraints(literalConstraints);
			
//			for (LocalizedString label : labels) {				
//				st.getLabels().add(label);
//			}
//			
//			for (LocalizedString literalConstraint : literalConstraints) {
//				st.getLiteralConstraints().add(literalConstraint);
//			}
			
			stList.add(st);
			
		} while(!tag.isEmpty());
		
		mdp.setStatements(stList);
		
		return mdp;
		
	}
	
    public String load() throws IOException
    {
//    	http://localhost:8080/faces/edit/mdprofile/12?ingest=1
//    	http://localhost:8080/faces/import/mdprofiles/collection/13?totalNum=1&done=1
    	
    	Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);  	
    	FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getApplicationUri() + "/edit" + this.colIme.getProfile().getPath() + "?ingest=1");    	
        return "";
    }
	
//	public void ingest(ArrayList<XmlMDBean> mdObjs, CollectionImeji colIme) {
	public ArrayList<String> ingest() {
		ArrayList<String> ingestedEntries = new ArrayList<String>();		
		MetadataProfile mdp;
		try {
			mdp = this.profileController.retrieve(this.colIme.getProfile());
		} catch (Exception e1) {
			logger.info("Ingest: cannot retrieve metadata profile, because no profile available!!");
			return ingestedEntries;
		}

		Collection<Statement> sts = mdp.getStatements();
		
		// Load all images in a specific collection
		SearchResult result = this.imageController.searchImagesInContainer(colIme.getId(), new ArrayList<SearchCriterion>(), null, -1, 0);		
		Collection<Image> imgs = imageController.loadImages(result.getResults(),-1, 0);
		
		if(imgs.isEmpty()) {
			logger.info("Cannot ingest, because no image/s available!!");
			return ingestedEntries;
		}
		
		int counter = 0;
		for (Iterator<Image> iterator = imgs.iterator(); iterator.hasNext();) {
			
			Image image = (Image) iterator.next();
			
			//TODO: mapping of image file, but need to make this generic.
			
			XmlMDBean mdo = this.ingestHelper.getMDBeanObject(this.ingestHelper.getMdObjs(), image.getFilename());
			if(mdo == null) {
//				logger.info("No Meta data profile available created!");		
				continue;
			} else {
				ingestedEntries.add("Counter_"+(++counter)+"_"+mdo.getProjectName()+"_"+mdo.getProfileName()+"_"+image.getFilename());
				logger.info("Could ingest meta data for image file: "+image.getFilename());
			}
			MetadataSet mds = image.getMetadataSet();
			List<ImageMetadata> imd = (List<ImageMetadata>) this.ingestHelper.mappingFromXmlMDObjectToImageMD(mdo,(ArrayList<Statement>) sts);			
			mds.setMetadata(imd);
			image.getMetadataSet().setMetadata(imd);
		}
		
//		logger.info("Files ingested!");
		
		try {
			this.imageController.update(imgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ingestedEntries;
	}
	
	/**
	 * Creates a meta data profile from an ingest file.
	 * @param ingestFileName, name of the ingest file
	 * @return the meta data profile
	 * @deprecated
	 */
	public MetadataProfile ingestProfile(String ingestFileName)
	{
		MetadataProfile mdp = new MetadataProfile();
		
		IngestHelper ih = new IngestHelper();
		
		File xmlFilename = new File(ingestFileName);
				
		ArrayList<XmlMDBean> mdObjs = ih.extractXmlMDObjects(xmlFilename);
		
		
		String posfix = "";
		
		// get active collection in fetch meta data profile
//		this.collectionSessionBean = (CollectionSessionBean) BeanHelper.getSessionBean(CollectionSessionBean.class);
		
//		this.colIme = this.collectionSessionBean.getActive();
		
		if(this.colIme.getProfile() != null)
			//get the id of meta data profile
			posfix = this.colIme.getProfile().getPath();
		else {
			return null;
		}
		
		// maps the generic meta data (as String) to imeji meta data profile
		List<Statement> stList = new ArrayList<Statement>();
		Statement st = new Statement();
		
    	Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);  
		String localMachine = navigation.getApplicationUrl();
		
//		InetAddress localMachine = null;	
//		try {
//			localMachine = InetAddress.getLocalHost();
//			
//		} catch (UnknownHostException uhe) {
//			// handle exception
//			return null;
//		}
		
		for (XmlMDBean xmlMDOject : mdObjs) {
			// clean the statement list at the beginning
			stList.clear();
			
			//Metadata Label : http://imeji.mpdl.mpg.de/mdprofile/866/Testing_2
//			String url = localMachine.getHostName() + posfix + "/" + xmlMDOject.getProjectName();
			String url = localMachine + posfix + "/" + xmlMDOject.getProjectName();
			
			st.setName(URI.create(url));
			stList.add(st);
			
			XmlMDBean element = (XmlMDBean)xmlMDOject;
			Enumeration<String> e = element.getMetadatas().keys();
			String tags;
			do {
				try {
					tags = new String(e.nextElement());
				} catch (NoSuchElementException nsee) {
					// nomore meta data found
					break;
				}
				
//				System.out.println(tags);				
				
				//TODO: need to implement multiple label handling like for internationalization
				//TODO: multiple occurrences of one label
//				Collection<LocalizedString> labels;
				
//				url = localMachine.getHostName() + posfix + tags;
				url = localMachine + posfix + "/" + tags;
				st.setName(URI.create(url));
				stList.add(st);
				
			} while(!tags.isEmpty());
			mdp.setStatements(stList);
		}
		
		return mdp;
	}

	public void setProfilesMenu(ArrayList<MetadataProfile> mdProfiles) {
		try {
			for (MetadataProfile mdprofile : mdProfiles) {					
				this.profilesMenu.add(new SelectItem(mdprofile.getId(), mdprofile.getTitle()));
			}									
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
