package de.mpg.imeji.logic.workflow;

import de.mpg.imeji.exceptions.WorkflowException;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.workflow.status.VersionReader;
import de.mpg.imeji.logic.workflow.status.impl.JenaVersionReader;
import de.mpg.imeji.presentation.beans.ConfigurationBean;

/**
 * Check update and delete Operation against the imeji Workflow. If not compliant, throw an error
 * 
 * @author bastiens
 *
 */
public class WorkflowManager {
  private final VersionReader versionReader = new JenaVersionReader();

  /**
   * Check update of Status for the object Properties
   * 
   * @param p
   * @throws WorkflowException
   */
  public void isValidUpdate(Properties p) throws WorkflowException {
    Status requestedStatus = p.getStatus();
    switch (requestedStatus) {
      case PENDING:
        isValidPending(p);
        return;
      case RELEASED:
        isValidRelease(p);
        return;
      case WITHDRAWN:
        isValidWithdrawn(p);
        return;
    }
    throw new WorkflowException(
        "Workflow operation not allowed: " + p.getId() + " can not have status " + p.getStatus());
  }


  /**
   * Check if an object can be deleted
   * 
   * @param p
   * @throws WorkflowException
   */
  public void isValidDelete(Properties p) throws WorkflowException {
    Status currentStatus = versionReader.getStatus(p);
    if (!isValidDelete(currentStatus)) {
      throw new WorkflowException("Workflow operation not allowed: " + p.getId()
          + " can not be deleted (current status: " + currentStatus + ")");
    }
  }

  /**
   * True if an object can be deleted according to its current status
   * 
   * @param currentStatus
   * @return
   */
  public boolean isValidDelete(Status currentStatus) {
    return currentStatus == Status.PENDING;
  }

  /**
   * Object can be updated with Status PENDING if:<br/>
   * * Object has status PENDING <br/>
   * * Object hast status RELEASED (To unrelease Objects)<br/>
   * <br/>
   * NOTE: Unrelease operation should be forbidden, as soon as Visibility is implemented
   * 
   * @param p
   * @return
   * @throws WorkflowException
   */
  public void isValidPending(Properties p) throws WorkflowException {
    Status currentStatus = versionReader.getStatus(p);
    if (currentStatus == Status.PENDING || currentStatus == Status.RELEASED) {
      return;
    }
    throw new WorkflowException("Workflow operation not allowed: " + p.getId()
        + " can not be updated with PENDING Status (current status: " + currentStatus + ")");
  }

  /**
   * Can be update with RELEASED Status if: <br/>
   * * imeji is not in private Modus <br/>
   * * Status is not Withdrawn <br/>
   * * Status is Pending and Version number has been incremented <br/>
   * * Status is Released and Version is same as before
   * 
   * @param p
   * @return
   * @throws WorkflowException
   */
  public void isValidRelease(Properties p) throws WorkflowException {
    if (ConfigurationBean.getPrivateModusStatic()) {
      throw new WorkflowException("Object publication is disabled!");
    }

    Status currentStatus = versionReader.getStatus(p);

    if (currentStatus == Status.WITHDRAWN) {
      throw new WorkflowException("Withdrawn Objects can not be Released");
    }

    int currentVersion = versionReader.getVersion(p);
    if (currentStatus == Status.PENDING && isVersionIncremented(p, currentVersion)) {
      return;
    }

    if (currentStatus == Status.RELEASED && !isVersionIncremented(p, currentVersion)) {
      return;
    }

    throw new WorkflowException("Workflow operation not allowed: " + p.getId()
        + " can not be updated with RELEASED Status (current status: " + currentStatus + ")");
  }

  /**
   * Object can be withdrawn if:<br/>
   * * Status is RELEASED
   * 
   * @param p
   * @throws WorkflowException
   */
  public void isValidWithdrawn(Properties p) throws WorkflowException {
    Status currentStatus = versionReader.getStatus(p);
    if (currentStatus == Status.RELEASED) {
      return;
    }
    throw new WorkflowException("Workflow operation not allowed: " + p.getId()
        + " can not be updated with WITHDRAWN Status (current status: " + currentStatus + ")");
  }

  /**
   * True if the Version of a passed object has been incremented
   * 
   * @param p
   * @return
   */
  private boolean isVersionIncremented(Properties p, int currentVersion) {
    return p.getVersion() == currentVersion + 1;
  }
}
