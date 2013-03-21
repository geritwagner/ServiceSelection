package qos;

public class Constraint {

	private String constraintTitle;
	private double constraintValue;
	private double constraintWeight;
	
	public static final String PRICE = "Max. Price";
	public static final String COSTS = "Max. Costs";
	public static final String RESPONSE_TIME = "Max. Response Time";
	public static final String AVAILABILITY = "Min. Availability";
	public static final String RELIABILITY = "Min. Reliability";
	public static final String PENALTY_FACTOR = "Penalty Factor";
	
	
	public Constraint(String title, double value, double weight) {
		this.constraintTitle = title;
		this.constraintValue = value;
		this.constraintWeight = weight;
	}
	
	
	public String getTitle() {
		return constraintTitle;
	}
	public void setTitle(String title) {
		this.constraintTitle = title;
	}
	public double getValue() {
		return constraintValue;
	}
	public void setValue(double value) {
		this.constraintValue = value;
	}
	public double getWeight() {
		return constraintWeight;
	}
	public void setWeight(double weight) {
		this.constraintWeight = weight;
	}
	
	
	public String toString() {
		return "(" + constraintTitle + ": " + constraintValue + 
				"; " + constraintWeight + ")";
	}
	
}
