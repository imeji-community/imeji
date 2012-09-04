package test.id;

import java.util.Date;

public class DateId extends Date {
	
	public String toString() {
		return this.getTime() + "";
	}
}
