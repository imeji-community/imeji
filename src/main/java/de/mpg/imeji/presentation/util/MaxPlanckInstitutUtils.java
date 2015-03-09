package de.mpg.imeji.presentation.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
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
			try {
				if (isInRange(ipRange, userIP))
					return mpiMap.get(ipRange);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
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

	/**
	 * True if the ip is include into the IP Range. IP range can be: <br/>
	 * 130.183.100-129.* <br/>
	 * 130.183.248.12-13 <br/>
	 * 85.183.114.142 <br/>
	 * 141.61.*.* <br/>
	 * 
	 * @param ipRange
	 * @param ip
	 * @return
	 * @throws UnknownHostException
	 */
	private static boolean isInRange(String ipRange, String ip)
			throws UnknownHostException {

		return ipToLong(InetAddress.getByName(ip)) > ipToLong(getMinIP(ipRange))
				&& ipToLong(InetAddress.getByName(ip)) < ipToLong(getMaxIP(ipRange));
	}

	/**
	 * Return the minimal IP of an IP range
	 * 
	 * @param ipRangeString
	 * @return
	 * @throws UnknownHostException
	 */
	private static InetAddress getMinIP(String ipRangeString)
			throws UnknownHostException {
		String ip = "";
		for (String s : ipRangeString.split("\\.")) {
			if (!"".equals(ip))
				ip += ".";
			if ("*".equals(s))
				ip += "0";
			else if (s.contains("-"))
				ip += s.split("-")[0];
			else
				ip += s;
		}
		try {
			return InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			// if some error return the locahost IP
			return InetAddress.getLocalHost();
		}
	}

	/**
	 * Return the maximal IP of an IP range
	 * 
	 * @param ipRangeString
	 * @return
	 * @throws UnknownHostException
	 */
	private static InetAddress getMaxIP(String ipRangeString)
			throws UnknownHostException {
		String ip = "";
		for (String s : ipRangeString.split("\\.")) {
			if (!"".equals(ip))
				ip += ".";
			if ("*".equals(s))
				ip += "255";
			else if (s.contains("-"))
				ip += s.split("-")[1];
			else
				ip += s;
		}
		return InetAddress.getByName(ip);
	}

	/**
	 * Convert an ip to a long with can be then compared to another
	 * 
	 * @param ip
	 * @return
	 */
	private static long ipToLong(InetAddress ip) {
		byte[] octets = ip.getAddress();
		long result = 0;
		for (byte octet : octets) {
			result <<= 8;
			result |= octet & 0xff;
		}
		return result;
	}

}
