package de.mpg.imeji.logic.auth.exception;

/**
 * 
 * @author: yao
 * @className: UnprocessableError
 * @classDescription: Error by unable to process the data
 * @createTime: 22.12.201413:58:25
 * @modifyTime: 22.12.201413:58:25

 */
public class UnprocessableError extends Exception {
	 /**
	 * 
	 */
	private static final long serialVersionUID = -2949658202758865427L;

	public UnprocessableError(String message)
	    {
	        super(message);
	    }
}
