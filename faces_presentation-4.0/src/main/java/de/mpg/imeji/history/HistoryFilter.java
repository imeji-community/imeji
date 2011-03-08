package de.mpg.imeji.history;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class HistoryFilter  implements Filter{
	
	private FilterConfig filterConfig = null;


	public void destroy() 
	{
		filterConfig = null;
	}

	public void doFilter(ServletRequest serv, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException 
	{
		HttpServletRequest request = (HttpServletRequest) serv;
		
		System.out.println(request.getPathInfo());

		chain.doFilter(serv, resp);
 
	}

	public void init(FilterConfig arg0) throws ServletException 
	{
		this.filterConfig = arg0;
		
	}

}
