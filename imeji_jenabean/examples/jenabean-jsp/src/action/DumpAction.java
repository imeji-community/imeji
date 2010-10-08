package action;

import java.io.IOException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/blog/dump")
public class DumpAction extends BaseAction {
	
	@DefaultHandler
	public Resolution dump() throws IOException {
		context.getResponse().setContentType("text/xml");
		m().write(context.getResponse().getWriter());
		return null;
	}
}
