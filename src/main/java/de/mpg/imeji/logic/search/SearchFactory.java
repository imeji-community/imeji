/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.imeji.logic.search;


import de.mpg.imeji.logic.search.Search.SearchObjectTypes;
import de.mpg.imeji.logic.search.elasticsearch.ElasticSearch;
import de.mpg.imeji.logic.search.jenasearch.JenaSearch;

/**
 * Factory for {@link Search}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchFactory {

  private static SEARCH_IMPLEMENTATIONS defaultSearch = SEARCH_IMPLEMENTATIONS.JENA;

  public enum SEARCH_IMPLEMENTATIONS {
    JENA, ELASTIC;
  }

  /**
   * Create a new {@link Search}
   * 
   * @return
   */
  public static Search create() {
    return create(defaultSearch);
  }

  /**
   * Create A new {@link Search}
   * 
   * @param impl
   * @return
   */
  public static Search create(SEARCH_IMPLEMENTATIONS impl) {
    return create(SearchObjectTypes.ALL, impl);
  }

  /**
   * Create a new {@link Search}
   * 
   * @param type
   * @param impl TODO
   * @return
   */
  public static Search create(SearchObjectTypes type, SEARCH_IMPLEMENTATIONS impl) {
    switch (impl) {
      case JENA:
        return new JenaSearch(type, null);
      case ELASTIC:
        return new ElasticSearch(type);
    }
    return null;
  }

  /**
   * Create a new {@link Search} !!! Only for JENA Search !!!
   * 
   * @param type
   * @param containerUri
   * @return
   */
  public static Search create(SearchObjectTypes type, String containerUri) {
    return new JenaSearch(type, containerUri);
  }
}
