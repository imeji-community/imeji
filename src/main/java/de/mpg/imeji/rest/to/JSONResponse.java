package de.mpg.imeji.rest.to;

import javax.ws.rs.core.Response.Status;

public class JSONResponse {
	public Object object;
	public Status status;
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	
	
	

}
