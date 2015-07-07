package de.mpg.imeji.presentation.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Utility Class for specific method for the Max Planck Institute
 * 
 * @author saquet
 *
 */
public class MaxPlanckInstitutUtils {
  private static final Logger LOGGER = Logger.getLogger(MaxPlanckInstitutUtils.class);

  /**
   * URL of the file with IP range information for all MPIs
   */
  private static String MAX_PLANCK_INSTITUTES_IP_URL =
      "http://www2.mpdl.mpg.de/seco-irmapubl/expoipra_mpi?style=expo01";
  /**
   * Map of the institute names with their IP range
   */
  private static Map<String, String> MPINameMap;
  /**
   * Map the institute Id with their ID
   */
  private static Map<String, String> IdMap;

  /**
   * Return the name of the Max Planck Institute according to the IP. If not found, return null
   * 
   * @param ip
   * @return
   */
  public static String getInstituteNameForIP(String userIP) {
    Map<String, String> mpiMap = getMPINameMap();
    if (mpiMap != null) {
      for (String ipRange : mpiMap.keySet()) {
        if (IPUtils.isInRange(ipRange, userIP))
          return mpiMap.get(ipRange);
      }
    }
    return null;
  }

  /**
   * Return the Id of the
   * 
   * @param userIP
   * @return
   */
  public static String getInstituteIdForIP(String userIP) {
    Map<String, String> mpiMap = getIdMap();
    if (mpiMap != null) {
      for (String ipRange : mpiMap.keySet()) {
        if (IPUtils.isInRange(ipRange, userIP))
          return mpiMap.get(ipRange);
      }
    }
    return null;
  }

  /**
   * Get the file with the the mpis and their IPs, and return it as a {@link Map}
   * 
   * @return
   */
  public static Map<String, String> getMPINameMap() {
    if (MPINameMap == null)
      try {
        MPINameMap = getMPIMap(1, 4);
      } catch (Exception e) {
        LOGGER.error("There was a problem with finding the MPINameMap: " + e.getLocalizedMessage());
        return null;
      }
    return MPINameMap;
  }

  /**
   * Get the file with the the mpis and their IPs, and return it as a {@link Map}
   * 
   * @return
   */
  public static Map<String, String> getIdMap() {
    if (IdMap == null)
      try {
        IdMap = getMPIMap(1, 2);
      } catch (Exception e) {
        LOGGER.error("There was a problem with finding the IdMap: " + e.getLocalizedMessage());
        return null;
      }
    return IdMap;
  }

  /**
   * Return a map from the file MAX_PLANCK_INSTITUTES_IP_URL with the chosen key/value. <br/>
   * The format of the file is following: <br/>
   * line;ip_range;institute_id;institute_city;institute_name
   * 
   * @return
   */
  private static Map<String, String> getMPIMap(int keyPosition, int valuePosition) {
    try {
      URL mpiCSV = new URL(MAX_PLANCK_INSTITUTES_IP_URL);

      BufferedReader br = new BufferedReader(new InputStreamReader(mpiCSV.openStream()));
      String line = "";
      String cvsSplitBy = ";";
      Map<String, String> maps = new HashMap<String, String>();
      while ((line = br.readLine()) != null) {
        // use comma as separator
        String[] institute = line.split(cvsSplitBy);
        maps.put(institute[keyPosition], institute[valuePosition]);
      }
      return maps;

    } catch (Exception e) {
      LOGGER.error("There was a problem by getting the MPI Map: (" + e.getLocalizedMessage()
          + ")! Check your internet connection. ");
      return null;
    }
  }

}
