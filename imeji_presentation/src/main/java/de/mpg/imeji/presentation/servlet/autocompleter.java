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
	String nameUri = "http://pubman.mpdl.mpg.de/cone/persons/query?format=json&n=10&m=full&q=";
	String googleAPIUri = "https://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=";

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
		String suggest = request.getParameter("searchkeyword");
		String datasource = request.getParameter("datasource");
		String responseString = "";
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
		out.print(responseString);
		out.flush();
	}
	/*
	 * parse JSON string returned from remote source by JSON-simple
	 * add properties [ { label: "Choice1", value: "value1" }, ... ]
	 *  to fit JQuery UI auto-complete pop format
	 */
	private String passResult(String s, String source) throws IOException {
		if (source.equals(nameUri)) {
			Object obj = JSONValue.parse(s);
			JSONArray array = (JSONArray) obj;
			JSONArray result = new JSONArray();
			for (int i = 0; i < array.size(); ++i) {
				JSONObject object = (JSONObject) array.get(i);
				// JQuery UI auto-complete required format:
				// An array of objects with label and value properties: [ { label: "Choice1", value: "value1" }, ... ]
				object.put("label",
						object.get("http_purl_org_dc_elements_1_1_title"));
				object.put("value",
						object.get("http_xmlns_com_foaf_0_1_family_name"));
				result.add(object);
			}
			StringWriter out = new StringWriter();
			result.writeJSONString(out);
			String jsonText = out.toString();
			return jsonText;

		} else if (source.equals(googleAPIUri)) {
			JSONObject obj = (JSONObject) JSONValue.parse(s);
			JSONArray array = (JSONArray) obj.get("results");
			JSONArray result = new JSONArray();
			for (int i = 0; i < array.size(); ++i) {
				JSONObject object = (JSONObject) array.get(i);
				// JQuery UI auto-complete required format:
				// An array of objects with label and value properties: [ { label: "Choice1", value: "value1" }, ... ]
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
