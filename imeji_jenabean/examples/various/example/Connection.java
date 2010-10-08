package example;

import static example.AssemblerVocabulary.NS;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace(NS)
public class Connection {
	
	private String dbClass;	
	private String dbPassword;
	private String dbType;
	private String dbURL;	
	private String dbUser;	
	
	@RdfProperty(NS + "dbClass")
	public String getDbClass() {
		return dbClass;
	}
	public void setDbClass(String dbClass) {
		this.dbClass = dbClass;
	}
	

	
	@RdfProperty(NS + "dbPassword")
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	

	
	@RdfProperty(NS + "dbType")
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	

	
	@RdfProperty(NS + "dbURL")
	public String getDbURL() {
		return dbURL;
	}
	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}
	

	@RdfProperty(NS + "dbUser")
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	
}
