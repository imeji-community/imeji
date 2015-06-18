package de.mpg.imeji.rest.to;

public class HTTPError {
	
	public String code;
	public String title;
	public String message;
	public String exceptionReport;
	
	
	public String getExceptionReport() {
		return exceptionReport;
	}
	public void setExceptionReport(String exceptionReport) {
		this.exceptionReport = exceptionReport;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	

}
