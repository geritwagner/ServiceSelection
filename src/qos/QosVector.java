package qos;

public class QosVector {

	private double costs = 0.0;
	private double responseTime = 0.0;
	private double availability = 1.0;
	
	
	// CONSTRUCTORS
	public QosVector() {
		
	}
	
	public QosVector(double costs, double responseTime, double availability) {
		this.costs = costs;
		this.responseTime = responseTime;
		this.availability = availability;
	}
	
	
	public void add(QosVector qosVectorToAdd) {
		addCosts(qosVectorToAdd.getCosts());
		addResponseTime(qosVectorToAdd.getResponseTime());
		addAvailability(qosVectorToAdd.getAvailability());
	}
	
	public void subtract(QosVector qosVectorToSubtract) {
		subtractCosts(qosVectorToSubtract.getCosts());
		subtractResponseTime(qosVectorToSubtract.getResponseTime());
		subtractAvailability(qosVectorToSubtract.getAvailability());
	}
	
	public void addCosts(double costs) {
		this.costs += costs;
	}
	public void addResponseTime(double responseTime) {
		this.responseTime += responseTime;
	}
	public void addAvailability(double availability) {
		this.availability *= availability;
	}
	
	public void subtractCosts(double costs) {
		this.costs -= costs;
	}
	public void subtractResponseTime(double responseTime) {
		this.responseTime -= responseTime;
	}
	public void subtractAvailability(double availability) {
		this.availability /= availability;
	}
	
	
	// GETTERS AND SETTERS
	public double getCosts() {
		return costs;
	}
	public void setCosts(double costs) {
		this.costs = costs;
	}
	public double getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(double responseTime) {
		this.responseTime = responseTime;
	}
	public double getAvailability() {
		return availability;
	}
	public void setAvailability(double availability) {
		this.availability = availability;
	}
	
	public String toString() {
		return "(" + costs + ", " + responseTime + ", " + availability + ")";
	}

}
