package example;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.hp.hpl.jena.ontology.OntModel;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

public class SessionListener implements HttpSessionListener {

	public void sessionCreated(HttpSessionEvent e) {
		ServletContext ctx = e.getSession().getServletContext();
		OntModel m = (OntModel)ctx.getAttribute("model");
		e.getSession().setAttribute(Constants.WRITER, new Bean2RDF(m));
		e.getSession().setAttribute(Constants.READER, new RDF2Bean(m));
	}

	public void sessionDestroyed(HttpSessionEvent e) {

	}

}
