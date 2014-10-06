package de.mpg.imeji.presentation.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpResponseException;
import org.apache.log4j.Logger;

/**
 * SErvlet to call Data viewer service
 * 
 * @author saquet
 *
 */
public class DataViewerServlet extends HttpServlet {

	private static final long serialVersionUID = -4602021617386831403L;
	private static Logger logger = Logger.getLogger(FileServlet.class);

	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Data Viewer Servlet initialized");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String id = req.getParameter("id");
			resp.getWriter().append("id" + id);
		} catch (HttpResponseException he) {
			resp.sendError(he.getStatusCode(), he.getMessage());
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

}
