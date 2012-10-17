/**
 * 
 */
package de.fub.imeji.ingest.core.helper.GenericTypes;

/**
 * @author hnguyen
 *
 */
public class GenericInputType {

	@SuppressWarnings("hiding")
	public static <GenericInputType> GenericInputType getInstance(Class<GenericInputType> _class)
    {
        try
        {
            return _class.newInstance();
        }
        catch (Exception _ex)
        {
            _ex.printStackTrace();
        }
        return null;
    }
}
