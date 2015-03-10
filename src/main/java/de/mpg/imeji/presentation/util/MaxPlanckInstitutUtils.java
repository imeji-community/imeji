package de.mpg.imeji.presentation.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility Class for specific method for the Max Planck Institute
 * 
 * @author saquet
 *
 */
public class MaxPlanckInstitutUtils {

	private static String MAX_PLANCK_INSTITUTES_IP_URL = "http://www2.mpdl.mpg.de/seco-irmapubl/expoipra_mpi?style=expo01";

	/**
	 * Return the name of the Max Planck Institute according to the IP. If not
	 * found, return null
	 * 
	 * @param ip
	 * @return
	 */
	public static String getInstituteForIP(String userIP) {
		Map<String, String> mpiMap = getMPIMap();
		for (String ipRange : mpiMap.keySet()) {
			if (IPUtils.isInRange(ipRange, userIP))
				return mpiMap.get(ipRange);
		}
		return null;
	}

	/**
	 * Get the file with the the mpis and their IPs, and return it as a
	 * {@link Map}
	 * 
	 * @return
	 */
	public static Map<String, String> getMPIMap() {
		try {
			URL mpiCSV = new URL(MAX_PLANCK_INSTITUTES_IP_URL);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					mpiCSV.openStream()));
			String line = "";
			String cvsSplitBy = ";";
			Map<String, String> maps = new HashMap<String, String>();
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] institute = line.split(cvsSplitBy);
				maps.put(institute[1], institute[4]);
			}
			return maps;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
