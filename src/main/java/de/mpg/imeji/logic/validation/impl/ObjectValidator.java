/**
 * 
 */
package de.mpg.imeji.logic.validation.impl;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.validation.Validator.Method;
import de.mpg.imeji.logic.vo.User;

/**
 * @author natasab
 *
 */
public abstract class ObjectValidator {
	
	protected Validator.Method validateForMethod;

	/**
	 * @param validateForMethod the validateForMethod to set
	 */
	public void setValidateForMethod(Validator.Method validateForMethod) {
		this.validateForMethod = validateForMethod;
	}
	
	public Validator.Method getValidateForMethod() {
		return validateForMethod;
	}

	
	public ObjectValidator(Method method){
		setValidateForMethod(method);
	}
	
	public boolean isDelete()
	{
		return this.validateForMethod.equals(Validator.Method.DELETE);
	}
	
}
