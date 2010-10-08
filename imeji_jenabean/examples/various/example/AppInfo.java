package example;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://hello#")
public class AppInfo {
    
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String APPINFO_INSTANCE = "AppInfoInstance";
    private String serverBaseUri;
    private String serverBaseUrl;
    private NamedModel repositoryNamedModel;
    
    @Id
    public String getId() {
        return null;
    }
    
    //@RdfProperty("serverBaseUri") 
    public String getServerBaseUri() {
        return this.serverBaseUri;
    }
    
    public void setServerBaseUri(String baseUriServer) {
        this.serverBaseUri = serverBaseUri;
    }
    
    //@RdfProperty("serverBaseUrl")
    public String getServerBaseUrl() {
        return this.serverBaseUrl;
    }
    
    public void setServerBaseUrl(String serverUrl) {
        this.serverBaseUrl = serverUrl;
    }

    //@RdfProperty("repositoryNamedModel")
    public NamedModel getRepositoryNamedModel() {
        return this.repositoryNamedModel;
    }
    
    public void setRepositoryNamedModel(NamedModel repositoryNamedModel) { 
        this.repositoryNamedModel = repositoryNamedModel;
    }
}

