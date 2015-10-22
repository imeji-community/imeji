package de.mpg.imeji.logic.jobs;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.jobs.executors.NightlyExecutor;

/**
 * This job calls all Jobs which should be run every night, and is called by the
 * {@link NightlyExecutor} every night
 * 
 * @author bastiens
 * 
 */
public class NightlyJob implements Runnable {
  private static final Logger logger = Logger.getLogger(NightlyJob.class);

  @Override
  public void run() {
    logger.info("Running Nightly Jobs");
    Imeji.executor.submit(new CleanTempFilesJob());
    try {
      Imeji.executor.submit(new StorageUsageAnalyseJob());
    } catch (Exception e) {
      logger.error("Error: " + e.getMessage());
    }
    Imeji.executor.submit(new CleanInactiveUsersJob());
    Imeji.executor.submit(new ReadMaxPlanckIPMappingJob());
    Imeji.executor.submit(new CleanEmptyMetadataProfileJob());
    Imeji.executor.submit(new CleanMetadataJob(null));
    Imeji.executor.submit(new CleanGrantsJob());
    Imeji.executor.submit(new CleanStatementsJob());

  }

}
