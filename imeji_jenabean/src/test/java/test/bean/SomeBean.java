/**
 * 
 */
package test.bean;
import java.util.Collection;
import thewebsemantic.Id;
import thewebsemantic.Namespace;

/**
 * Test class to prove Jena saving & loading Jenabeans with List fields
 */
@Namespace("http://my-uri.com/ns")
public class SomeBean {
	private Collection<String> stringList;
	private String[] stringArray;
	private String id;

	public Collection<String> getStringList() {
		return stringList;
	}
	public void setStringList(Collection<String> stringList) {
		this.stringList = stringList;
	}
	@Id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String[] getStringArray() {
		return stringArray;
	}
	public void setStringArray(String[] stringArray) {
		this.stringArray = stringArray;
	}
	
	
}
