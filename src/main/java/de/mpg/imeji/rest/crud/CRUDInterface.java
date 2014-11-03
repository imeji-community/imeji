package de.mpg.imeji.rest.crud;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;


public interface CRUDInterface<V> {
	 static Logger logger = Logger.getLogger(CRUDInterface.class);
	 
	public V create(V o, User u);
	 
	public V read(V o, User u);
	 
	public V update(V o, User u);
	 
	public boolean delete(V o, User u);

	

}
