package de.mpg.imeji.logic.controller;

import java.net.URI;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.NotFoundException;

public class StatementController extends ImejiController{
	
	private static Logger logger = Logger.getLogger(StatementController.class);
	
	private static final ReaderFacade reader = new ReaderFacade(Imeji.statementModel);
	private static final ReaderFacade writer = new ReaderFacade(Imeji.statementModel);
	
	public StatementController(){
		super();
	}
	
	public Statement retrieve(URI uri, User user) throws NotFoundException
	{
		Statement s;
		try {
			s = ((Statement) reader.read(uri.toString(), user, new Statement()));
		} catch (Exception e) {
			throw new NotFoundException("Statement (URL: " + uri
					+ " ) not found.");
		}
		return s;
	}
	
	

}
