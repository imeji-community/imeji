package de.mpg.imeji.logic.workflow;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.WorkflowException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.vo.Properties;

/**
 * Utility Class to use the {@link Imeji} workflow implementation
 * 
 * @author bastiens
 *
 */
public class ImejiWorkflow {
  public final static WorkflowManager MANAGER = new WorkflowManager();
  private final static Logger LOGGER = Logger.getLogger(ImejiWorkflow.class);

  /**
   * True if the object ca be deleted
   * 
   * @param o
   * @return
   */
  public static boolean isValidDelete(Object o) {
    if (o instanceof Properties) {
      try {
        MANAGER.isValidDelete((Properties) o);
        return true;
      } catch (WorkflowException e) {
        LOGGER.error("Object can not be deleted", e);
      }
    }
    return false;
  }

  /**
   * True if the Object can be updated
   * 
   * @param o
   * @return
   */
  public static boolean isValidUpdate(Object o) {
    if (o instanceof Properties) {
      try {
        MANAGER.isValidUpdate((Properties) o);
        return true;
      } catch (WorkflowException e) {
        LOGGER.error("Object can not be updated", e);
      }
    }
    return false;
  }

}
