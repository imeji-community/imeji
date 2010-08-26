package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
/**
 * DataModel and List Manager for data input managed in form of data tables. The
 * data object input user interface is powered using a simple DataModel combined
 * by some addObject and removeObject methods. Internally the objects are stored
 * using a List<T>, which Type T has to be given by the implementing class. For
 * your viewing pleasure there is method called getObjectDataList(), delivering
 * all objects stored in the suitable vo accessed by getDataSetFromVO().
 * 
 * @author Mario Wagner
 * @param <T> the object type you want to manage in an data table
 */
public abstract class DataModelManager<T> {
	protected List<T> objectList = null;
	protected DataModel objectDM = null;

	// //////////////////////////////////////////////////////////////////////////
	//
	// Abstract enforcement section
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * I do not really know how to create and initialize a new object of type T,
	 * but you should be able to do so.
	 */
	public abstract T createNewObject();
	
	
	/**
	 * Tell me where in your value object or class there is a list of data to be
	 * managed and give it to me.
	 * 
	 * @return Set containing data object of type T
	 */
	//public abstract List<T> getDataListFromVO();
	
	// //////////////////////////////////////////////////////////////////////////
	//
	// Class implementation section
	//
	// //////////////////////////////////////////////////////////////////////////

	public List<T> getObjectList()
	{
		return objectList;
	}

	/**
	 * 
	 * @param objectList new List<T>
	 */
	public void setObjectList(List<T> objectList)
	{
		this.objectList = objectList;
		if (objectDM == null)
		{
			objectDM = new ListDataModel();
		}
		objectDM.setWrappedData(objectList);
	}
	
	/**
	 * The DataModel is always created by wrapping the data objects contained
	 * in the objectList. If this objectList is empty, there will be no rows to 
	 * be displayed.
	 * 
	 * @return DataModel
	 */
	public DataModel getObjectDM()
	{
		if (objectList == null)
		{
			objectList = new ArrayList<T>();
		}
		if (objectDM == null)
		{
			objectDM = new ListDataModel();
			objectDM.setWrappedData(objectList);
		}
		return objectDM;
	}

	/**
	 * Simple setter, not really used yet
	 * 
	 * @param objectDM new DataModel
	 */
	public void setObjectDM(DataModel objectDM)
	{
		this.objectDM = objectDM;
	}

	/**
	 * Adds a object of type T to the list (and therefore to the UI model)
	 */
	public String addObject()
	{
		T elem = createNewObject();
		int i = objectDM.getRowIndex();
		if(elem != null)
		{
			objectList.add(i + 1, elem);
		}
		return "";
	}

	/**
	 * Removes object of type T from the list (and therefore from the UI model)
	 */
	public String removeObject()
	{
		int i = objectDM.getRowIndex();
		removeObjectAtIndex(i);
		return "";
	}

	protected void removeObjectAtIndex(int i)
	{
		objectList.remove(i);
	}	

}
