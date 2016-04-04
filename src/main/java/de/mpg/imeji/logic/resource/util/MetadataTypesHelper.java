/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.resource.util;

import de.mpg.imeji.logic.resource.vo.Metadata;
import de.mpg.imeji.logic.resource.vo.Metadata.Types;

/**
 * Helper to work with {@link Metadata} {@link Types}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class MetadataTypesHelper {
  /**
   * Return the {@link Types} for one namespace
   * 
   * @param namespace
   * @return
   */
  public static Metadata.Types getTypesForNamespace(String namespace) {
    for (Types t : Types.values()) {
      if (t.getClazzNamespace().equals(namespace)) {
        return t;
      }
    }
    return null;
  }
}
