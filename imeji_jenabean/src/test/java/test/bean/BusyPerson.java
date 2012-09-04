package test.bean;

import java.util.List;

import thewebsemantic.Id;

public class BusyPerson {

	List<String> todoList;
	String ssn;
	
	public BusyPerson() {
		
	}
	
	public List<String> getTodoList() {
		return todoList;
	}

	public void setTodoList(List<String> todoList) {
		this.todoList = todoList;
	}

	@Id
	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

}
