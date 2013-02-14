package de.mpg.imeji.presentation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

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
		String responseString = "";
		if (suggest.toString().isEmpty()) {
			suggest = "a";
		} else if (!suggest.toString().isEmpty()) {
			try {
				HttpClient client = new HttpClient();
				GetMethod getMethod = new GetMethod(juqeryAutocompleteTestSource
								+ URLEncoder.encode(suggest.toString(), "UTF-8"));
				client.executeMethod(getMethod);
				responseString = getMethod.getResponseBodyAsString().trim();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(responseString);
		out.flush();
	}

}
