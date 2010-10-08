package action;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

import com.hp.hpl.jena.ontology.OntModel;

import example.StripesContext;

public class BaseAction implements ActionBean {
	protected StripesContext context;
	
	public OntModel m() {
		return (OntModel)context.getServletContext().getAttribute("model");
	}
	
	public StripesContext getContext() {
		return context;
	}

	public void setContext(ActionBeanContext c) {
		context = (StripesContext)c;
	}

	protected String hashPassword(String s) throws NoSuchAlgorithmException {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-1");
		md.update(s.getBytes());
		return Hex.toHex(md.digest());
	}
	

}
