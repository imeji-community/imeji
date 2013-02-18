package de.mpg.imeji.presentation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Servlet implementation class autocompleter
 */
@WebServlet(description = "act as bridge for front javascript query since javascript cannot query cross domain, e.g., from imeji to google", urlPatterns = { "/autocompleter" })
public class autocompleter extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String juqeryAutocompleteTestSource = "http://jqueryui.com/resources/demos/autocomplete/search.php?term=";
	String nameUri = "http://pubman.mpdl.mpg.de/cone/persons/query?format=json&n=10&m=full&q=";
	String leoUri = "https://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public autocompleter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String suggest = request.getParameter("term");
		String datasource = request.getParameter("datasource");
		String responseString = "";
		// test data
		JSONObject obj1 = new JSONObject();
		obj1.put("label", "yeye");
		obj1.put("value", new Integer(100));
		obj1.put("anytype", new Integer(100));
		JSONObject obj2 = new JSONObject();
		obj2.put("label", "foo33");
		obj2.put("value", new Integer(110));
		obj2.put("anytype", new Integer(100));
		JSONArray list = new JSONArray();
		list.add(obj1);
		list.add(obj2);
		StringWriter out1 = new StringWriter();
		list.writeJSONString(out1);
		String jsonText = out1.toString();

		if (suggest.toString().isEmpty()) {
			suggest = "a";
		} else if (!suggest.toString().isEmpty()) {
			try {
				HttpClient client = new HttpClient();
				GetMethod getMethod = new GetMethod(datasource
						+ URLEncoder.encode(suggest.toString(), "UTF-8"));
				client.executeMethod(getMethod);
				responseString = getMethod.getResponseBodyAsString().trim();
				responseString = passResult(responseString, datasource);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(formatResultString(responseString));
		// out.print(jsonText);
		out.flush();
	}

	private String formatResultString(String s) {
		if (s.contains("\"formatted_address\" :")) {
			// is https://developers.google.com/maps/documentation/geocoding
			try {
				s = parseMapsGoogleApiGeo(s);
			} catch (Exception e) {
				throw new RuntimeException(
						"Error parsing google geo api results", e);
			}
		}
		if (s.contains("\"status\" : \"ZERO_RESULTS\"")) {
			// google zero result
			s = "";
		}
		if (!s.startsWith("[")) {
			s = "[ " + s;
		}
		if (!s.endsWith("]")) {
			s = s + "]";
		}
		return s;
	}

	private String parseMapsGoogleApiGeo(String str) throws Exception {
		String response = "";
		String address = "";
		String latitude = "";
		String longitude = "";
		StringReader reader = new StringReader(str);
		int c = 0;
		boolean readingAddress = false;
		boolean readingLongitude = false;
		boolean readingLatitude = false;
		boolean readingLocation = false;
		String buffer = "";
		while ((c = reader.read()) != -1) {
			buffer += (char) c;
			if (buffer.contains("\"formatted_address\" : \"")) {
				// start reading address
				readingAddress = true;
				buffer = "";
			} else if (readingAddress && c == '"') {
				// stop reading address
				readingAddress = false;
				buffer = "";
			} else if (buffer.contains("\"location\" : ")) {
				// Enter in Location group
				readingLocation = true;
				buffer = "";
			} else if (readingLocation && c == '}') {
				// Sort out Location group
				readingLocation = false;
				buffer = "";
			} else if (readingLocation && buffer.contains("\"lat\" : ")) {
				// start reading latitude
				readingLatitude = true;
				buffer = "";
			} else if (readingLatitude && c == ',') {
				// stop reading latitude
				readingLatitude = false;
				buffer = "";
			} else if (readingLocation && buffer.contains("\"lng\" : ")) {
				// start reading longitude
				readingLongitude = true;
				buffer = "";
			} else if (readingLongitude && c == '\n') {
				// stop reading longitude
				readingLongitude = false;
				buffer = "";
			} else if (readingAddress) {
				address += (char) c;
			} else if (readingLongitude) {
				longitude += (char) c;
			} else if (readingLatitude) {
				latitude += (char) c;
			} else if (address != "" && latitude != "" && longitude != ""
					&& !readingAddress && !readingLocation) {
				// write results
				response += "{\"label\" : \"" + address + "\" , \"value\" : \""
						+ longitude + "\" , \"latitude\" : \"" + latitude
						+ "\"}, ";
				address = "";
				latitude = "";
				longitude = "";
			}
		}
		return "[" + response + "]";
	}

	private String passResult(String s, String source) throws IOException {
		System.out.println(source);
		if (source.equals(nameUri)) {
			Object obj = JSONValue.parse(s);
			JSONArray array = (JSONArray) obj;
			JSONArray result = new JSONArray();
			for (int i = 0; i < array.size(); ++i) {
				JSONObject object = (JSONObject) array.get(i);
				// JQuery UI auto-complete required format: array or label,
				// value pair
				object.put("label",
						object.get("http_xmlns_com_foaf_0_1_family_name"));
				object.put("value",
						object.get("http_xmlns_com_foaf_0_1_family_name"));
				result.add(object);
			}
			StringWriter out = new StringWriter();
			result.writeJSONString(out);
			String jsonText = out.toString();
			return jsonText;

		} else if (source.equals(leoUri)) {
			JSONObject obj = (JSONObject) JSONValue.parse(s);
			JSONArray array = (JSONArray) obj.get("results");
			JSONArray result = new JSONArray();
			for (int i = 0; i < array.size(); ++i) {
				JSONObject object = (JSONObject) array.get(i);
				// JQuery UI auto-complete required format: array or label,
				// value pair
				object.put("label", object.get("formatted_address"));
				object.put("value", object.get("formatted_address"));
				result.add(object);
			}
			StringWriter out = new StringWriter();
			result.writeJSONString(out);
			String jsonText = out.toString();
			return jsonText;

		}
		return s;

	}
}
