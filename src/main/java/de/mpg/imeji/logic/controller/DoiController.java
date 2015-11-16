package de.mpg.imeji.logic.controller;

import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import util.JenaUtil;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.presentation.beans.ConfigurationBean;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.imeji.rest.doi.CollectionDO;
import de.mpg.imeji.rest.doi.CreatorsDO;
import de.mpg.imeji.rest.doi.IdentifierDO;
import de.mpg.imeji.rest.doi.TitleDO;
import de.mpg.imeji.rest.resources.test.integration.item.ItemTestBase;

import javax.ws.rs.client.WebTarget;


public class DoiController {
  
  private Client client = ClientBuilder.newClient();
  private String doiUser; ;
  private String doiPassword;
  
  public static CollectionDO transformToDO(CollectionImeji col){
    CollectionDO dcol = new CollectionDO();
    TitleDO title = new TitleDO(col.getMetadata().getTitle());
    List<CreatorsDO> creators = new ArrayList<CreatorsDO>();
    for(Person author : col.getMetadata().getPersons()){
      creators.add(new CreatorsDO(author.getCompleteName()));
    }
   
    dcol.setIdentifier(new IdentifierDO());
    dcol.getTitles().add(title);
    dcol.setPublisher("MPG");
    dcol.setCreators(creators);
    dcol.setPublicationYear(String.valueOf(col.getCreated().get(Calendar.YEAR)));
    
    return dcol;
  }
  
  /**
   * Get a DOI for a {@link CollectionImeji} 
   * 
   * @param col
   * @throws Exception
   */
  public String getNewDoi(CollectionImeji col, String doiUser, String doiPassword) throws ImejiException{
    

        
    CollectionDO dcol = transformToDO(col);
    
    String xml;
    String doi;
    
    //convert to xml
    StringWriter sw = new StringWriter();

    try {
      JAXBContext context = JAXBContext.newInstance(CollectionDO.class );
      Marshaller m;
      m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
      m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://datacite.org/schema/kernel-3 http://schema.datacite.org/meta/kernel-3/metadata.xsd");
      m.marshal(dcol, sw );
    } catch (JAXBException e) {
        throw new ImejiException(
            "Error occured, when contacting DOxI.");    
    }

    xml = sw.toString().trim();
    
    // REST request to the DOI service for creating a new DOI
    Response response = client.target("https://test.doi.mpdl.mpg.de").path("/doxi/rest/doi")
      .queryParam("url", "https://dev-faces.mpdl.mpg.de/collection/8Sk5SYmjw2CxtNn")
//      .queryParam("url", col.getId())
      .register(HttpAuthenticationFeature.basic(doiUser, doiPassword))
      .register(MultiPartFeature.class)
      .register(JacksonFeature.class)
      .request(MediaType.TEXT_PLAIN)
      .put(Entity.entity(xml, "text/xml"));
    
    
    int statusCode = response.getStatus();

    // throw Exception if the DOI service request fails
    if (statusCode != 201) {
        String responseBody = response.readEntity(String.class);
        throw new ImejiException(
                "Error occured, when contacting DOxI. StatusCode="
                        + statusCode + "\nServer responded with: "
                        + responseBody);
    }   
    
    doi = response.readEntity(String.class);
    return doi;

  }

}
