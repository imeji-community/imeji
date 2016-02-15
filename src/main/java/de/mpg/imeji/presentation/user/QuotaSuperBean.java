package de.mpg.imeji.presentation.user;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import de.mpg.imeji.presentation.beans.ConfigurationBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Super class to implements quota methods, which can be reused by JSF Bean displaying quota menu
 * <br/>
 * NOTE: This is NOT a JSF Bean
 * 
 * @author bastiens
 *
 */
public class QuotaSuperBean {

  private String quota = ConfigurationBean.getDefaultQuotaStatic();
  private List<SelectItem> quotaMenu;
  private static final int BYTES_PER_GB = 1073741824;
  private static final Logger LOGGER = Logger.getLogger(QuotaSuperBean.class);

  /**
   * default Constructor
   */
  public QuotaSuperBean() {
    quotaMenu = new ArrayList<>();
    SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    for (String limit : ConfigurationBean.getQuotaLimitsStaticAsList()) {
      if (NumberUtils.isNumber(limit)) {
        quotaMenu.add(new SelectItem(limit));
      } else {
        quotaMenu.add(new SelectItem(limit, session.getLabel(limit)));
      }
    }
  }

  /**
   * Return the Quota define in GB in bytes
   * 
   * @param gigabyte
   * @return
   */
  public long getQuotaInBytes() {
    try {
      if (ConfigurationBean.QUOTA_UNLIMITED.equals(quota)) {
        return Long.MAX_VALUE;
      }
      return (long) ((Double.parseDouble(quota)) * BYTES_PER_GB);
    } catch (Exception e) {
      LOGGER.error("Error parsing quota: ", e);
      return 0;
    }

  }

  /**
   * 
   * @return the quota
   */
  public String getQuota() {
    return quota;
  }

  /**
   * @param quota the quota to set
   */
  public void setQuota(String quota) {
    this.quota = quota;
  }

  /**
   * @return the quotaMenu
   */
  public List<SelectItem> getQuotaMenu() {
    return quotaMenu;
  }

  /**
   * @param quotaMenu the quotaMenu to set
   */
  public void setQuotaMenu(List<SelectItem> quotaMenu) {
    this.quotaMenu = quotaMenu;
  }
}
