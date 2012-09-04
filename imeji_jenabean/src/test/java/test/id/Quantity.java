package test.id;

import thewebsemantic.Id;

public class Quantity {
	
	@Id private double amount;
	private String units;
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}

}
