package de.mpg.imeji.logic.controller;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.doi.models.DOICollection;
import de.mpg.imeji.logic.doi.util.DOIUtil;
import de.mpg.imeji.logic.vo.CollectionImeji;


public class DoiController {
    
  /**
   * Get a DOI for a {@link CollectionImeji} 
   * 
   * @param col
   * @throws Exception
   */
  public String getNewDoi(CollectionImeji col, String doiServiceUrl, String doiUser, String doiPassword) throws ImejiException{
            
    DOICollection dcol = DOIUtil.transformToDO(col);
        
    String xml = DOIUtil.convertToXML(dcol);

    String doi = DOIUtil.makeDOIRequest(doiServiceUrl, doiUser, doiPassword, xml);
    
    return doi;

  }

}
