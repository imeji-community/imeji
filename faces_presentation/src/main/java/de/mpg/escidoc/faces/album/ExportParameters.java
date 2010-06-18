package de.mpg.escidoc.faces.album;

public class ExportParameters 
{
	 // Export resolution
    private Boolean thumbnails = false;
    private Boolean web = true;
    private Boolean orignal = false;
    /**
     * Export format
     */
    private ExportType exportFormat = ExportType.CSV;
    
    public enum ExportType 
    {
    	CSV, XML, CSV_AND_PICTURES, XML_AND_PICTURES, PICTURES;
    }

	public ExportParameters() 
	{
		// Do nothing
	}

	
	public Boolean getThumbnails() 
	{
		return thumbnails;
	}

	public void setThumbnails(Boolean thumbnails) 
	{
		this.thumbnails = thumbnails;
	}

	public Boolean getWeb() 
	{
		return web;
	}

	public void setWeb(Boolean web) 
	{
		this.web = web;
	}

	public Boolean getOrignal() 
	{
		return orignal;
	}

	public void setOrignal(Boolean orignal) 
	{
		this.orignal = orignal;
	}
	
	public String getExportFormatAsString()
	{
		return exportFormat.toString();
	}

	public ExportType getExportFormat() {
		
		return exportFormat;
	}

	public void setExportFormat(ExportType exportFormat) 
	{
		this.exportFormat = exportFormat;
	}
}
