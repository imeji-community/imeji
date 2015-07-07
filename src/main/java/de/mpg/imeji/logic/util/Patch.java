package de.mpg.imeji.logic.util;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Utility Class to work easily with {@link HttpPatch}
 * 
 * @author saquet
 *
 */
public class Patch {

  private CloseableHttpClient httpclient;
  private HttpHost target;
  private URI uri;
  private CloseableHttpResponse response;
  private HttpClientContext localContext;

  /**
   * Constructor for an {@link URI} with (Optional) authentication
   * 
   * @param uri
   * @param username
   * @param password
   */
  public Patch(URI uri, String username, String password) {
    this.uri = uri;
    target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
    CredentialsProvider credsProvider = new BasicCredentialsProvider();
    if (username != null && password != null) {
      credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()),
          new UsernamePasswordCredentials(username, password));
    }
    httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    AuthCache authCache = new BasicAuthCache();
    BasicScheme basicAuth = new BasicScheme();
    authCache.put(target, basicAuth);
    localContext = HttpClientContext.create();
    localContext.setAuthCache(authCache);
  }

  /**
   * Execute a Patch Method with a json as input
   * 
   * @param json
   * @return
   * @throws ClientProtocolException
   * @throws IOException
   */
  public void executeJSON(String json) throws ClientProtocolException, IOException {
    HttpPatch patch = new HttpPatch(uri);
    HttpEntity entity =
        EntityBuilder.create().setText(json).setContentType(ContentType.APPLICATION_JSON).build();
    patch.setEntity(entity);
    response = httpclient.execute(target, patch, localContext);
  }

  public CloseableHttpResponse getResponse() {
    return response;
  }

  public void close() throws IOException {
    response.close();
    httpclient.close();
  }

}
