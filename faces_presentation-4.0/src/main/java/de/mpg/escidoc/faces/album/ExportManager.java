package de.mpg.escidoc.faces.album;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletResponse;

import de.escidoc.schemas.item.x09.ItemDocument.Item;
import de.escidoc.schemas.itemlist.x09.ItemListDocument;
import de.escidoc.schemas.itemlist.x09.ItemListDocument.ItemList;
import de.mpg.escidoc.faces.album.ExportParameters.ExportType;
import de.mpg.escidoc.faces.statistics.StatisticsBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.ItemRefFilter;
import de.mpg.escidoc.services.exportmanager.Export;
import de.mpg.escidoc.services.exportmanager.ExportHandler;
import de.mpg.escidoc.services.exportmanager.ExportManagerException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class ExportManager 
{
	/**
	 * The parameters to export
	 */
	private ExportParameters parameters = new ExportParameters();
	private XmlTransforming xmlTransforming = null;
	private String userHandle = null;
	/**
	 * Useful to write statistics
	 */
	private String userId = null;
	
	/**
	 * Default constructor
	 */
	public ExportManager() 
	{
		parameters = new ExportParameters();
	}
	
	/**
	 * Constructor
	 * @param userHandle
	 * @param userId
	 */
	public ExportManager(String userHandle, String userId)
	{
		this();
		this.userHandle = userHandle;
		this.userId = userId;
	}
	
	/**
	 * Export an Album
	 * @param album
	 * @throws Exception
	 */
	public void doExport(AlbumVO album) throws Exception
	{
		InitialContext context = new InitialContext();
		xmlTransforming = (XmlTransforming)context.lookup(XmlTransforming.SERVICE_NAME);

		FilterTaskParamVO param = new FilterTaskParamVO();
      	ItemRefFilter itemRefFilter = param.new ItemRefFilter();
      	// Create the query to view the album
      	// Case of the album has at least one picture within
      	if (album.getMembers().size() > 0)
	      {
	          for (int i = 0; i < album.getMembers().size(); i++)
	          {
	              ItemRO item = new ItemRO();
	              item.setObjectId(album.getMembers().get(i).getObjectId());
	              itemRefFilter.getIdList().add(item);
	          }
	      }
      	// add the filter to the list
      	param.getFilterList().add(itemRefFilter);
	      
          // NEW
//	          List<de.mpg.escidoc.services.common.valueobjects.ItemVO> itemToExportList= 
//	              new ArrayList<de.mpg.escidoc.services.common.valueobjects.ItemVO>();  
//	          
//	          String itemToExportListXML = "";
       
          //List<Item> itemList = new ArrayList<Item>();
      	ItemList itemList = ItemList.Factory.newInstance();
          
      	for (int i = 0; i * 100 < itemRefFilter.getIdList().size(); i++)
      	{
      		FilterTaskParamVO paramTemp = new FilterTaskParamVO();
      		ItemRefFilter itemRefFilterTemp = param.new ItemRefFilter();
      		// Initialize the limit of the index
      		int limit = (i + 1) * 100;
      		// Check if there is more than 100 items still
      		if (limit > itemRefFilter.getIdList().size())
      		{
      			limit = itemRefFilter.getIdList().size();
      		}
              
      		// Create the param
      		itemRefFilterTemp.getIdList().addAll(itemRefFilter.getIdList().subList(i * 100 , limit));
      		paramTemp.getFilterList().add(0, itemRefFilterTemp);
      		String paramXml = xmlTransforming.transformToFilterTaskParam(paramTemp);
              
      		// Retrieve the XML of the item we want to export
      		String itemListXml = ServiceLocator.getItemHandler(userHandle).retrieveItems(paramXml);
              
      		ItemListDocument itemListDocument = ItemListDocument.Factory.parse(itemListXml);
                           
      		List<Item> list = new ArrayList<Item>(Arrays.asList(itemList.getItemArray()));
      		list.addAll(i*100,  Arrays.asList(itemListDocument.getItemList().getItemArray()));
              
      		Item[] array = new Item[list.size()]; 
              
      		for (int j = 0; j < list.size(); j++)
      		{
      			array[j] = list.get(j);
      		}
           
      		itemList.setItemArray(array);
      		itemListDocument = null;
              //itemToExportListTemp = null;
          }
          // END NEW
          
          // OLD
//	          //  transform to paramXml
//	          String paramXml = xmlTransforming.transformToFilterTaskParam(param);
//	          // Get the XML of the item we want to export
//	          String itemToExportListXML = ServiceLocator.getItemHandler(sessionBean.getUserHandle()).retrieveItems(paramXml);
//	          // Transform XML to List
//	          List<? extends ItemVO> itemToExportList = xmlTransforming.transformToItemList(itemToExportListXML);
          // END OLD
          
          
          // START: Generic items
  
          String itemListOriginalXml = itemList.xmlText();
          itemList = selectComponentResolution(itemList);
          String itemListFiteredXml = itemList.xmlText();
          
          File fileToExport = callExportService(parameters.getExportFormat(), itemListFiteredXml, itemListOriginalXml);
          itemListFiteredXml = null;
          itemListOriginalXml = null;
          // END : GenericItems
          
          // Create the xml
//	          itemToExportListXML = xmlTransforming.transformToItemList(itemToExportList);
//	          // remove the files we don't want.
//	          itemToExportList = setItemToExportWithResolution(itemToExportList);
//	          // Transform List filtered to XML
//	          String itemToExportListXMLFiltered = xmlTransforming.transformToItemList(itemToExportList);
          // Call export service
          //File fileToExport = callExportService(exportFormat, itemToExportListXMLFiltered, itemToExportListXML);
          // download the file
          download(fileToExport, parameters.getExportFormat());
          
          // create statistics Data
          createExportStatisticData();
          
          for (int i = 0; i < itemList.sizeOfItemArray(); i++)
          {
              createExportItemStatisticData(itemList.getItemArray(i).getProperties().getLatestRelease().getObjid());
          }
//	          
          // delete the file
          fileToExport.delete();
    }
	
	/**
	 * Call Export Handler Service according to export format
	 * @param exportFormat
	 * @param itemToExportListXMLFiltered
	 * @param itemToExportListXML
	 * @return
	 * @throws IOException
	 * @throws ExportManagerException
	 */
	 private File callExportService(ExportType exportFormat, String itemToExportListXMLFiltered, String itemToExportListXML) throws  IOException, ExportManagerException
	 {
		 ExportHandler exportHandler = new Export();
		 File resultFile = new File("result");
		 FileWriter fstream = new FileWriter(resultFile);
		 BufferedWriter out = new BufferedWriter(fstream);
		 byte[] exportResult = null;
		 
		 switch (exportFormat) 
		 {
			case CSV:
				exportResult = exportHandler.getOutput("CSV", null, null, itemToExportListXML);
				for (int i = 0; i < exportResult.length; i++)
				 {
					 out.write(exportResult[i]);
				 }
				break;
			case XML:
				out.write(itemToExportListXML);
				break;
			case CSV_AND_PICTURES:
				exportResult = exportHandler.getOutput("CSV", null, null, itemToExportListXMLFiltered);
				resultFile = exportHandler.generateArchiveFile("CSV", "zip", exportResult, itemToExportListXMLFiltered);
				break;
			case XML_AND_PICTURES:
				resultFile =  exportHandler.generateArchiveFile("XML", "zip", itemToExportListXML.getBytes(),
		                   										itemToExportListXMLFiltered);
				break;
			case PICTURES:
				resultFile =  exportHandler
                				.generateArchiveFile(null, "zip", "".getBytes(), itemToExportListXMLFiltered);
				break;
			default:
				break;
		 }
       
		 out.flush();
		 out.close();
      
		 return resultFile;
	 }
	 
	 /**
	  * Make download of file.
	  * @param fileResult
	  * @param exportFormat
	  * @throws IOException
	  */
	 private void download(File fileResult, ExportType exportFormat) throws IOException
	 {
		 FacesContext ctx = FacesContext.getCurrentInstance();
		 String fileName;
		 String contentType;
       
		 switch (exportFormat) 
		 {
		 	case CSV:
				fileName = "FacesExport.csv";
				contentType = "text/csv";
				break;
		 	case XML:
		 		 fileName = "FacesExport.xml";
				 contentType = "text/xml";
		 		break;
			default:
				fileName = "FacesExport.zip";
				contentType = "application/zip";
				break;
		 }
           
		 HttpServletResponse response = (HttpServletResponse)ctx.getExternalContext().getResponse();
		 response.setContentType(contentType);
		 response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
       
		 OutputStream out = response.getOutputStream();
		 InputStream in = new FileInputStream(fileResult);
       
		 byte[] bytes = new byte[1024];
		 int bytesRead;
       
		 while ((bytesRead = in.read(bytes)) != -1) 
		 {
			 out.write(bytes, 0, bytesRead);
		 }
       
		 in.close();
		 out.flush();
		 out.close();
		 fileResult.delete();
	 }
	 
	 /**
	  * Remove the component resolution that are not to be exported
	  * @param itemList
	  * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	  */
	 private ItemList selectComponentResolution(ItemList itemList) throws IOException, URISyntaxException
	 {
	      for (int i = 0; i < itemList.sizeOfItemArray(); i++)
	      {
	          for (int j = itemList.getItemArray(i).getComponents().sizeOfComponentArray() - 1; j >= 0; j--)
	          {
	        	  if (j < itemList.getItemArray(i).getComponents().sizeOfComponentArray() && !parameters.getWeb()
	                      && itemList.getItemArray(i).getComponents().getComponentArray(j).getProperties().getContentCategory().equals(PropertyReader.getProperty("xsd.metadata.content-category.web-resolution")))
	              {
	                  itemList.getItemArray(i).getComponents().removeComponent(j);
	              }
	              if (j < itemList.getItemArray(i).getComponents().sizeOfComponentArray() && !parameters.getOrignal()
	                      && itemList.getItemArray(i).getComponents().getComponentArray(j).getProperties().getContentCategory().equals(PropertyReader.getProperty("xsd.metadata.content-category.original-resolution")))
	              {
	                  itemList.getItemArray(i).getComponents().removeComponent(j);
	              }
	              if (j < itemList.getItemArray(i).getComponents().sizeOfComponentArray() && !parameters.getThumbnails()
	                      && itemList.getItemArray(i).getComponents().getComponentArray(j).getProperties().getContentCategory().equals(PropertyReader.getProperty("xsd.metadata.content-category.thumbnail")))
	              {
	                  itemList.getItemArray(i).getComponents().removeComponent(j);
	              }
	          }
	      }
	      
	      return itemList;
	  }
  
	  /**
	   * Remove the files in the component of each item when they are not to be exported
	   * @param itemToExportList - the list of the item to be exported with all files
	   * @return - the list of the item to be exported with only the requested file for export
	 * @throws URISyntaxException 
	 * @throws IOException 
	   */
	  private List< de.mpg.escidoc.services.common.valueobjects.ItemVO> setItemToExportWithResolution(List< de.mpg.escidoc.services.common.valueobjects.ItemVO> itemToExportList) throws IOException, URISyntaxException
	  {
	      for (int i = 0; i < itemToExportList.size(); i++)
	      {
	          for (int j = itemToExportList.get(i).getFiles().size() - 1; j >= 0; j--)
	          {
	              if (j < itemToExportList.get(i).getFiles().size() && !parameters.getWeb()
	                      && itemToExportList.get(i).getFiles().get(j).getDescription().equals(PropertyReader.getProperty("xsd.metadata.content-category.web-resolution")))
	              {
	                  itemToExportList.get(i).getFiles().remove(j);
	              }
	              if (j < itemToExportList.get(i).getFiles().size() && !parameters.getOrignal()
	                      && itemToExportList.get(i).getFiles().get(j).getDescription().equals(PropertyReader.getProperty("xsd.metadata.content-category.original-resolution")))
	              {
	                  itemToExportList.get(i).getFiles().remove(j);
	              }
	              if (j < itemToExportList.get(i).getFiles().size() && !parameters.getThumbnails()
	                      && itemToExportList.get(i).getFiles().get(j).getDescription().equals(PropertyReader.getProperty("xsd.metadata.content-category.thumbnail")))
	              {
	                  itemToExportList.get(i).getFiles().remove(j);
	              }
	          }
	      }
      
	      return itemToExportList;
	  }
	  
    private void createExportItemStatisticData(String itemId) throws Exception
    {
        String statisticDataXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
        "<statistic-record><scope objid=\"1\"/>" +
        "<parameter name=\"handler\"><stringvalue>"+
        StatisticsBean.INSTANCE_ID +
        "</stringvalue></parameter>" +
        "<parameter name=\"request\"><stringvalue>Faces export item</stringvalue></parameter>" +
        "<parameter name=\"interface\"><stringvalue>SOAP</stringvalue></parameter>" +
        "<parameter name=\"object_id\"><stringvalue>" + itemId + "</stringvalue></parameter>" +
        "<parameter name=\"user_id\"><stringvalue>" + userId + "</stringvalue></parameter>" +
        "<parameter name=\"successful\"><stringvalue>1</stringvalue></parameter>" +
        "<parameter name=\"internal\"><stringvalue>0</stringvalue></parameter>" +
        "</statistic-record>";
//	        
//	        StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getRequestBean(StatisticsBean.class);
//	        ServiceLocator.getStatisticDataHandler(statisticsBean.getStatisitcsEditorHandle()).create(statisticDataXml);
        StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getApplicationBean(StatisticsBean.class);
        
        ServiceLocator.getStatisticDataHandler(statisticsBean.getAdminUserHandle()).create(statisticDataXml);
    }
    
    private void createExportStatisticData() throws Exception
    {
        String statisticDataXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
        "<statistic-record><scope objid=\"1\"/>" +
        "<parameter name=\"handler\"><stringvalue>"+
        StatisticsBean.INSTANCE_ID +
        "</stringvalue></parameter>" +
        "<parameter name=\"request\"><stringvalue>Faces export</stringvalue></parameter>" +
        "<parameter name=\"interface\"><stringvalue>SOAP</stringvalue></parameter>" +
        "<parameter name=\"successful\"><stringvalue>1</stringvalue></parameter>" +
        "<parameter name=\"internal\"><stringvalue>0</stringvalue></parameter>" +
        "<parameter name=\"user_id\"><stringvalue>" + userId + "</stringvalue></parameter>" +
        "</statistic-record>";
//	        
//	        StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getRequestBean(StatisticsBean.class);
//	        ServiceLocator.getStatisticDataHandler(statisticsBean.getStatisitcsEditorHandle()).create(statisticDataXml);
        StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getApplicationBean(StatisticsBean.class);
        
        ServiceLocator.getStatisticDataHandler(statisticsBean.getAdminUserHandle()).create(statisticDataXml);
    }

	public ExportParameters getParameters() 
	{
		return parameters;
	}

	public void setParameters(ExportParameters parameters) 
	{
		this.parameters = parameters;
	}

	public String getUserHandle() {
		return userHandle;
	}

	public void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
