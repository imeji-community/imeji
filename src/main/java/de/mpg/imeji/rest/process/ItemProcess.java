package de.mpg.imeji.rest.process;

import javax.servlet.http.HttpServletRequest;

import de.escidoc.core.client.Authentication;
import de.mpg.imeji.logic.auth.AuthenticationFactory;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.crud.ItemCRUD;
import de.mpg.j2j.exceptions.NotFoundException;

public class ItemProcess{
	
	public static Item readItem(HttpServletRequest req, String id){
		
		String login = req.getParameter("username");
		String pwd = req.getParameter("password");	
		      
		Authentication auth = null;
		User u = null;
		if(login != null && pwd != null)
		auth = (Authentication) AuthenticationFactory.factory(login, pwd);
		if(auth != null)
			u = ((de.mpg.imeji.logic.auth.Authentication) auth).doLogin(); 
		
		Item item = null;
		
		ItemCRUD icrud = new ItemCRUD();
		try {
			item = icrud.read(id, u);
		} catch (NotFoundException e) {
			
		}catch(NotAllowedError e){
			System.err.println(e.getMessage());
		}catch(Exception e){
			System.err.println("exception");
		}



		req = null;
		return item;
	}

	
	
	


}
