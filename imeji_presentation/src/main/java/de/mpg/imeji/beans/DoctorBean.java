/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.beans;

import java.util.List;

import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.Scripts;
import de.mpg.jena.util.DataDoctor;

public class DoctorBean 
{
	private SessionBean sb;
	private List<String> report;
	private DataDoctor doc;
	
	public DoctorBean() 
	{
		sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		doc = new DataDoctor(sb.getUser());
		try 
		{
			report = doc.runDoctor(false);
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
	}

	public String cleanJenaData() throws Exception 
	{
		DataDoctor doc = new DataDoctor(sb.getUser());
		doc.runDoctor(true);
		return "";
	}
	
	public String runScript() throws Exception
	{
		Scripts s = new Scripts();
		//s.setCompleteNamesForContainers(sb.getUser());
		return "";
	}

	public List<String> getReport() 
	{
		return report;
	}

	public void setReport(List<String> report) 
	{
		this.report = report;
	}
	
	
}
