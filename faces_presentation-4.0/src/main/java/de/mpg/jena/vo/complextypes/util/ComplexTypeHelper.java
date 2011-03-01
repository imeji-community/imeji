package de.mpg.jena.vo.complextypes.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.metadata.MetadataBean.MdField;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.complextypes.ConePerson;
import de.mpg.jena.vo.complextypes.Date;
import de.mpg.jena.vo.complextypes.Geolocation;
import de.mpg.jena.vo.complextypes.License;
import de.mpg.jena.vo.complextypes.Number;
import de.mpg.jena.vo.complextypes.Publication;
import de.mpg.jena.vo.complextypes.Text;

public class ComplexTypeHelper
{
    public static ComplexTypes getComplexType(URI uri)
    {
        for (ComplexTypes type : ComplexTypes.values())
        {
            if (uri.equals(type.getURI()))
            {
                return type;
            }
        }
        return null;
    }

//    public static ComplexType newComplexType(URI uri)
//    {
//        switch (ComplexTypeHelper.getComplexType(uri))
//        {
//            case PERSON:
//                return new ConePerson();
//            case DATE:
//                return new Date();
//            case GEOLOCATION:
//                return new Geolocation();
//            case LICENSE:
//                return new License();
//            case NUMBER:
//                return new Number(0);
//            case URI:
//                return new de.mpg.jena.vo.complextypes.URI();
//            case PUBLICATION:
//                return new Publication();
//            default:
//            {
//                Text t = new Text();
//                t.setText("");
//                return t;
//            }
//        }
//    }
    
    
    public static String getSimpleValueAsString(ComplexType ct)
    {
    	if(ct instanceof ConePerson)
    	{
    		return ((ConePerson)ct).getPerson().getFamilyName() + ((ConePerson)ct).getPerson().getGivenName();
    	}
    	else if (ct instanceof Text)
    	{
    		return ((Text)ct).getText();
    	}
    	return "";
    }

    /**
     * Set a value of a field of a {@link ComplexType}
     * 
     * @param md
     * @param field
     * @param value
     * @return
     * @throws Exception
     */
    public static ComplexType setComplexTypeValue(ComplexType md, String field, Object value) throws Exception
    {
        return (ComplexType)setObjectValue(md, field, value);
    }

    /**
     * Set a value of a field of an object.
     * 
     * @param o
     * @param field
     * @param value
     * @return
     * @throws Exception
     */
    public static Object setObjectValue(Object o, String field, Object value) throws Exception
    {
        for (Field f : getComplexTypeFields(o.getClass(), true))
        {
            if (isField(f, o.getClass()) && f.getName().equals(field))
                f.set(o, value);
            else if (isField(f, o.getClass()) && hasFields(f))
            {
                if (f.get(o) == null)
                    f.set(o, f.getType().newInstance());
                setObjectValue(f.get(o), field, value);
            }
        }
        return o;
    }

    /**
     * Returns all fields of a {@link ComplexType}
     * 
     * @param c
     * @param includeComplexFields if true, include fields which are composed of other fields, i.e complex types
     * @return
     * @throws Exception
     */
    public static List<Field> getComplexTypeFields(Class<?> c, boolean includeComplexFields) throws Exception
    {
        List<Field> list = new ArrayList<Field>();
        for (Field f : c.getDeclaredFields())
        {
            f.setAccessible(true);
            if (isField(f, c) && (!hasFields(f) || includeComplexFields))
                list.add(f);
            else if (isField(f, c) && hasFields(f))
                list.addAll(getComplexTypeFields(f.getType(), includeComplexFields));
        }
        return list;
    }

    /**
     * Returns true if f has fields itself.
     * 
     * @param f
     * @return
     */
    public static boolean hasFields(Field f)
    {
        for (Field f1 : f.getType().getDeclaredFields())
        {
            if (isField(f1, f.getType()))
                return true;
        }
        return false;
    }

    /**
     * f is a field of c if it has getter and setter
     * 
     * @param f
     * @param c
     * @return
     */
    public static boolean isField(Field f, Class<?> c)
    {
        try
        {
            String getter = "get" + f.getName().toUpperCase().substring(0, 1) + f.getName().substring(1);
            String setter = "set" + f.getName().toUpperCase().substring(0, 1) + f.getName().substring(1);
            c.getMethod(getter, null);
            c.getMethod(setter, f.getType());
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
