package de.mpg.imeji.rest.resources;


import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Response;


public interface ImejiResource {
	

    public Response readAll(HttpServletRequest req);
    
    public Response read(HttpServletRequest req, String id);
    
    public Response create(HttpServletRequest req);
    
    public Response delete(HttpServletRequest req, String id);

}
