package de.mpg.imeji.image;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import de.mpg.jena.vo.Image;


public class SingleImageBrowse 
{
	private ImagesBean imagesBean = null;
	private Image currentImage = null;
	
	private String next = null;
	private String previous = null;
		
	public SingleImageBrowse(ImagesBean imagesBean, Image image) 
	{
		this.imagesBean = imagesBean;
		currentImage = image;
		init();
	}
	
	public void init()
	{
		String baseUrl = imagesBean.getImageBaseUrl();
		
		Image nextImage = getNextImageFromList();
		Image prevImage = getPreviousImageFromList();
		
		String direction = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("nav");

		if (nextImage == null && prevImage == null)
		{
			if ("prev".equals(direction))
			{
				loadPreviousPage();
			}
			else if ("next".equals(direction))
			{
				loadNextPage();
			}
			nextImage = getNextImageFromList();
			prevImage = getPreviousImageFromList();
		}
		
		if (nextImage == null && loadNextPage())
		{
			nextImage = getFirstImageOfPage();
			loadPreviousPage();
		}
		
		if (prevImage == null && loadPreviousPage()) 
		{
			prevImage = getLastImageOfPage();
			loadNextPage();
		}
		
		if (nextImage != null)
		{
			next = baseUrl + nextImage.getId().getPath() + "/view?nav=next";
		}
		
		if (prevImage != null)
		{
			previous = baseUrl + prevImage.getId().getPath() + "/view?nav=prev";
		}
	}
	
	public Image getNextImageFromList()
	{
		for(int i=0; i < imagesBean.getImages().size() - 1 ; i++)
		{
			if (((List<Image>)imagesBean.getImages()).get(i).getId().equals(currentImage.getId()))
			{
				return ((List<Image>)imagesBean.getImages()).get(i + 1);
			}
		}
		return null;
	}
	
	public Image getPreviousImageFromList()
	{
		for(int i= 1; i < imagesBean.getImages().size() ; i++)
		{
			if (((List<Image>)imagesBean.getImages()).get(i).getId().equals(currentImage.getId()))
			{
				return ((List<Image>)imagesBean.getImages()).get(i - 1);
			}
		}
		return null;
	}
	
	public Image getFirstImageOfPage()
	{
		if (!imagesBean.getImages().isEmpty())
		{
			return ((List<Image>)imagesBean.getImages()).get(0);
		}
		return null;
	}
	
	public Image getLastImageOfPage()
	{
		if (!imagesBean.getImages().isEmpty())
		{
			return ((List<Image>)imagesBean.getImages()).get(imagesBean.getImages().size() - 1);
		}
		return null;
	}
	
	public boolean loadNextPage()
	{
		if (imagesBean.getCurrentPageNumber() < imagesBean.getPaginatorPageSize())
		{
			imagesBean.goToNextPage();
			imagesBean.update();
			return true;
		}
		return false;
	}
	
	public boolean loadPreviousPage()
	{
		if(imagesBean.getCurrentPageNumber() > 1)
		{
			imagesBean.goToPreviousPage();
			imagesBean.update();
			return true;
		}
		return false;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}
	

}
