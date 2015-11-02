package de.mpg.imeji.logic.jobs;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.vo.MetadataProfile;

/**
 * Clean empty {@link MetadataProfile}, which are not referenced by any collection
 * 
 * @author saquet
 *
 */
public class CleanGrantsJob implements Callable<Integer> {
  private static final Logger logger = Logger.getLogger(CleanGrantsJob.class);

  @Override
  public Integer call() throws Exception {
    logger.info("Cleaning grants...");
    ImejiSPARQL.execUpdate(SPARQLQueries.removeGrantWithoutObject());
    ImejiSPARQL.execUpdate(SPARQLQueries.removeGrantWithoutUser());
    ImejiSPARQL.execUpdate(SPARQLQueries.removeGrantEmtpy());
    logger.info("...done!");
    return 1;
  }
}
