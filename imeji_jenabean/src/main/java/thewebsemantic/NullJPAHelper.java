package thewebsemantic;

import thewebsemantic.Generated;

public class NullJPAHelper implements AnnotationHelper {

	public boolean isGenerated(ValuesContext ctx) {
		return ctx.getAccessibleObject().isAnnotationPresent(Generated.class);
	}

	public boolean isEmbedded(Object bean) {
		return bean.getClass().getAnnotation(Embedded.class) != null;
	}

	public boolean proxyRequired() {
		return false;
	}

	public <T> Class<? extends T> getProxy(Class<T> c) throws InstantiationException,
			IllegalAccessException {
		throw new UnsupportedOperationException();
	}

}
