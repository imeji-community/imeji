package ingest;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.imeji.logic.vo.MetadataSet;

public class PropertyDescriptorTest
{
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @Test
    public void anyPropertyDescriptorTest()
    {
        // Item item = new Item();
        // item.setCollection(URI.create("collectionURI"));
        // item.setCreated(Calendar.getInstance());
        MetadataSet mds = new MetadataSet();
        try
        {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(mds.getClass())
                    .getPropertyDescriptors())
            {
                // propertyEditor.getReadMethod() exposes the getter
                // btw, this may be null if you have a write-only property
                if (propertyDescriptor.getWriteMethod() == null)
                    continue;
                // System.out.println(propertyDescriptor.getReadMethod().getName());
                // System.out.println(propertyDescriptor.getValue(propertyDescriptor.getReadMethod().getName()));
                // System.out.println(propertyDescriptor.getReadMethod().getName());
                // String value = (String)
                // item.getValueFromMethod(propertyDescriptor.getReadMethod().getName()).toString();
                //
                // System.out.println(value);
                // System.out.println(propertyDescriptor.getReadMethod());
                // System.out.println(propertyDescriptor.getWriteMethod());
                System.out.println(propertyDescriptor.getPropertyType());
            }
        }
        catch (IntrospectionException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
