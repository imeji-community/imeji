package thewebsemantic;

import java.beans.BeanInfo;
import java.beans.MethodDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class TypeWrapperFactory {
	
	public static Logger logger = Logger.getLogger("com.thewebsemantic");
	 
	public static TypeWrapper newwrapper(Class<?> c) {
		BeanInfo info = TypeWrapper.beanInfo(c);
		if (c.isEnum())
			return new EnumTypeWrapper(c);
		for (MethodDescriptor md : info.getMethodDescriptors())
			if (isId(md))
				return new IdMethodTypeWrapper(c, md.getMethod());
			else if (isUri(md))
				return new UriMethodTypeWrapper(c, md.getMethod());

		// now try field annotations
		Field[] fields = Util.getDeclaredFields(c);
		for (Field f : fields)
			if (isId(f))
				return new IdFieldTypeWrapper(c, f, fields);
		
		return new DefaultTypeWrapper(c);
	}
	

	private static boolean isId(AccessibleObject m) {
		for (Annotation a : m.getAnnotations()) {
			if ("Id".equals(a.annotationType().getSimpleName()))
				return true;
		}
		return false;
	}

	private static boolean isId(MethodDescriptor md) {
		return isId(md.getMethod());
	}

	private static boolean isUri(Method m) {
		return m.isAnnotationPresent(Uri.class);
	}

	private static boolean isUri(MethodDescriptor md) {
		return isUri(md.getMethod());
	}
}