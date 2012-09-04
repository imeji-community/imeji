package test.bean;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import thewebsemantic.Namespace;

@Namespace("http://test#")
public class TypeTesterBean {
	private String myString;
	private long myLong;
	private int myInt;
	private float myFloat;
	private double myDouble;
	private Date myDate;
	private char myChar;
	private boolean myBoolean;
	private Calendar calendar;
	private BigDecimal myBigDecimal;
	private short myShort;
	
	public BigDecimal getMyBigDecimal() {
		return myBigDecimal;
	}

	public void setMyBigDecimal(BigDecimal myBigDecimal) {
		this.myBigDecimal = myBigDecimal;
	}

	public char getMyChar() {
		return myChar;
	}

	public void setMyChar(char myChar) {
		this.myChar = myChar;
	}

	public String getMyString() {
		return myString;
	}

	public void setMyString(String myString) {
		this.myString = myString;
	}

	public long getMyLong() {
		return myLong;
	}

	public void setMyLong(long myLong) {
		this.myLong = myLong;
	}

	public int getMyInt() {
		return myInt;
	}

	public void setMyInt(int myInt) {
		this.myInt = myInt;
	}

	public float getMyFloat() {
		return myFloat;
	}

	public void setMyFloat(float myFloat) {
		this.myFloat = myFloat;
	}
	
	public double getMyDouble() {
		return myDouble;
	}
	
	public void setMyDouble(double myDouble) {
		this.myDouble = myDouble;
	}
	
	public Date getMyDate() {
		return myDate;
	}
	
	public void setMyDate(Date myDate) {
		this.myDate = myDate;
	}

	public boolean isMyBoolean() {
		return myBoolean;
	}

	public void setMyBoolean(boolean myBoolean) {
		this.myBoolean = myBoolean;
	}
	
	public void setMyCalendar(Calendar c) {
		this.calendar = c;
	}
	
	public Calendar getMyCalendar() {
		return calendar;
	}

	public short getMyShort() {
		return myShort;
	}

	public void setMyShort(short myShort) {
		this.myShort = myShort;
	}
	



}
