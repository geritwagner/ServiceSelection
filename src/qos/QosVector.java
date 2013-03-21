package qos;

public class QosVector {

	private double price = 0.0;
	private double costs = 0.0;
	private double responseTime = 0.0;
	private double availability = 1.0;
	private double reliability = 1.0;
	
	
	// CONSTRUCTORS
	public QosVector() {
		
	}
	
	public QosVector(double price, double costs, double responseTime,
			double availability, double reliability) {
		this.price = price;
		this.costs = costs;
		this.responseTime = responseTime;
		this.availability = availability;
		this.reliability = reliability;
	}
	
	
	public void add(QosVector qosVectorToAdd) {
		addPrice(qosVectorToAdd.getPrice());
		addCosts(qosVectorToAdd.getCosts());
		addResponseTime(qosVectorToAdd.getResponseTime());
		addAvailability(qosVectorToAdd.getAvailability());
		addReliability(qosVectorToAdd.getReliability());
	}
	
	public void subtract(QosVector qosVectorToSubtract) {
		subtractPrice(qosVectorToSubtract.getPrice());
		subtractCosts(qosVectorToSubtract.getCosts());
		subtractResponseTime(qosVectorToSubtract.getResponseTime());
		subtractAvailability(qosVectorToSubtract.getAvailability());
		subtractReliability(qosVectorToSubtract.getReliability());
	}
	
	public void addPrice(double price) {
		this.price += price;
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
	public void addReliability(double reliability) {
		this.reliability *= reliability;
	}
	
	public void subtractPrice(double price) {
		this.price -= price;
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
	public void subtractReliability(double reliability) {
		this.reliability /= reliability;
	}
	
	
	// GETTERS AND SETTERS
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
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
	public double getReliability() {
		return reliability;
	}
	public void setReliability(double reliability) {
		this.reliability = reliability;
	}
	
	public String toString() {
		return "(" + price + ", " + costs + ", " + responseTime + ", " + 
				availability + ", " + reliability + ")";
	}

}
