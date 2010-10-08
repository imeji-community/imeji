package thewebsemantic;

/**
 * written so that any class "c" will always return false for
 * c.isAssignableFrom(NoBinding.class).  
 * 
 * Instead of returning null, this class is returned to prevent the
 * null check...a null object pattern.
 */
final class NoBinding {

}
