package de.mpg.imeji.rest.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class TestItem {
  public static void main(String[] args) {
    ClientConfig config = new ClientConfig();

    Client client = ClientBuilder.newClient(config);

    WebTarget target = client.target(getBaseURI());

    System.out.println(target.path("items").request("yxmK_oGNdRHmL3YR")
        .accept(MediaType.APPLICATION_JSON).get(Response.class).toString());
    System.out.println(target.path("items").request("yxmK_oGNdRHmL3YR")
        .accept(MediaType.APPLICATION_JSON).get(String.class));


    Response response =
        target.path("collections/J__Ib4WvHys0X_qK").request(MediaType.APPLICATION_JSON).get();
    System.out.println(response.getStatus());
    System.out.println(response.readEntity(String.class));

  }

  private static URI getBaseURI() {

    return UriBuilder.fromUri("http://localhost:8080/imeji/rest/").build();

  }

}
