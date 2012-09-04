package thewebsemantic;

/**
 * An interface to make annotation processing within
 * Jenabean agnostic.  When used with JPA the internals
 * will need to support JPA Id and other misc annotations.
 * Classic dependency injection, we now depend on an interface
 * within the same package.
 */
public interface AnnotationHelper {

	boolean isGenerated(ValuesContext ctx);

	boolean isEmbedded(Object bean);

	boolean proxyRequired();

	<T> Class<? extends T> getProxy(Class<T> c) throws InstantiationException, IllegalAccessException;

}
