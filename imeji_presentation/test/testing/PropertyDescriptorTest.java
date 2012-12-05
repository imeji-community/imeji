package testing;

import static org.junit.Assert.*;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.imeji.logic.vo.Item;

public class PropertyDescriptorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void anyPropertyDescriptorTest() {
		try {
			for(PropertyDescriptor propertyDescriptor : 
			    Introspector.getBeanInfo(Item.class).getPropertyDescriptors()){

			    // propertyEditor.getReadMethod() exposes the getter
			    // btw, this may be null if you have a write-only property
			    System.out.println(propertyDescriptor.getReadMethod());
			    System.out.println(propertyDescriptor.getWriteMethod());
			}
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
