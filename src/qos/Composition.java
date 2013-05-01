package qos;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Composition implements Comparator<Composition> {
	
	private List<ServiceCandidate> serviceCandidatesList;
	private QosVector qosVectorAggregated;
	private double utility;
	private double fitness;
	
	
	// CONSTRUCTORS
	public Composition() {
		this.serviceCandidatesList = new LinkedList<ServiceCandidate>();
		this.qosVectorAggregated = new QosVector();
		this.utility = 0.0;
		this.fitness = 0.0;
	}

	public Composition(List<ServiceCandidate> serviceCandidatesList, 
			QosVector qosVectorAggregated, double utility, double fitness) {
		this.serviceCandidatesList = serviceCandidatesList;
		this.qosVectorAggregated = qosVectorAggregated;
		this.utility = utility;
		this.fitness = fitness;
	}
	
	public void buildAggregatedQosVector() {
		double costs = 0.0;
		double responseTime = 0.0;
		double availability = 1.0;
		for (ServiceCandidate candidate : serviceCandidatesList) {
			costs += candidate.getQosVector().getCosts();
			responseTime += candidate.getQosVector().getResponseTime();
			availability *= candidate.getQosVector().getAvailability();
		}
		qosVectorAggregated = new QosVector(costs, responseTime, availability);
	}
	
	// Compute the composition's utility value as follows:
	// - Sum up all utility values of the composition's service candidates
	// - Devide this sum by the number of service candidates
	public void computeUtilityValue() {
		double utility = 0.0;
		for (ServiceCandidate serviceCandidate : serviceCandidatesList) {
			utility += serviceCandidate.getUtility();
		}
		this.utility = utility / serviceCandidatesList.size();
	}
	
	// Compute the fitness of a composition. The computation is based on the 
	// approach of Ai et al. (2008) but differs from it in some details.
	public void computeFitness(Map<String, Constraint> constraintsMap) {
		double fitness = utility;
		// If constraints are violated, use the second part of the formula.
		// Fitness = Utility * (1 - Distance)
		if (!isWithinConstraints(constraintsMap)) {
			fitness -= (computeDistanceToConstraints(
					constraintsMap) * fitness);	
		}
		// If no constraints are violated, use the first part of the formula.
		// Fitness = 1 + Utility
		else {
			fitness += 1.0;
		}
		this.fitness = fitness;
	}
	
	// Compute the distance of a composition's aggregated QoS attributes to 
	// the given constraints.
	private double computeDistanceToConstraints(
			Map<String, Constraint> constraintsMap) {
		double distance = 0.0;
		if (constraintsMap.get(Constraint.COSTS) != null &&  
				qosVectorAggregated.getCosts() > 
				constraintsMap.get(Constraint.COSTS).getValue()) {
			distance += constraintsMap.get(
					Constraint.COSTS).getWeight() / 100.0;
		}
		if (constraintsMap.get(Constraint.RESPONSE_TIME) != null &&  
				qosVectorAggregated.getResponseTime() > 
				constraintsMap.get(Constraint.RESPONSE_TIME).getValue()) {
			distance += constraintsMap.get(
					Constraint.RESPONSE_TIME).getWeight() / 100.0;
		}
		if (constraintsMap.get(Constraint.AVAILABILITY) != null &&  
				qosVectorAggregated.getAvailability() < 
				constraintsMap.get(Constraint.AVAILABILITY).getValue()) {
			distance += constraintsMap.get(
					Constraint.AVAILABILITY).getWeight() / 100.0;
		}
		return distance;
	}

	// Add a service candidate to the list. Automatically update the QoS 
	// vector and the utility value.
	public void addServiceCandidate(ServiceCandidate serviceCandidate) {
		serviceCandidatesList.add(serviceCandidate);
		qosVectorAggregated.add(serviceCandidate.getQosVector());
		utility = (utility * (serviceCandidatesList.size() - 1) + 
				serviceCandidate.getUtility()) / serviceCandidatesList.size();
	}
	
	// Remove the last service candidate from the list. Automatically update 
	// the QoS vector and the utility value.
	public void removeServiceCandidate() {
		ServiceCandidate serviceCandidate = 
				serviceCandidatesList.remove(serviceCandidatesList.size() - 1);
		qosVectorAggregated.subtract(
				serviceCandidate.getQosVector());
		utility = (utility * (serviceCandidatesList.size() + 1) - 
				serviceCandidate.getUtility()) / serviceCandidatesList.size();
	}
	
	public boolean isWithinConstraints(
			Map<String, Constraint> constraintsMap) {
		boolean isWithinConstraints = true;
		QosVector qosVector = this.getQosVectorAggregated();
		Constraint costs = constraintsMap.get(Constraint.COSTS);
		Constraint responseTime = constraintsMap.get(Constraint.RESPONSE_TIME);
		Constraint availability = constraintsMap.get(Constraint.AVAILABILITY);
		if (costs != null && qosVector.getCosts() > costs.getValue()) {
			isWithinConstraints = false;
		}
		if (responseTime != null && 
				qosVector.getResponseTime() > responseTime.getValue()) {
			isWithinConstraints = false;
		}
		if (availability != null && 
				qosVector.getAvailability() < availability.getValue()) {
			isWithinConstraints = false;
		}
		return isWithinConstraints;
	}
	
	// Returns the IDs of the compositions's service candidates.
	public String getServiceCandidatesAsString() {
		String serviceCandidates = "";
		for (ServiceCandidate serviceCandidate : serviceCandidatesList) {
			serviceCandidates = serviceCandidates + " " + 
					serviceCandidate.getServiceCandidateId();
		}
		return serviceCandidates;
	}
	
	// Compare two compositions by comparing the IDs of each of their 
	// service candidates.
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Composition other = (Composition) obj;
		for (int i = 0; i < getServiceCandidatesList().size(); i++) {
			if (getServiceCandidatesList().get(i).getServiceCandidateId() != 
					other.getServiceCandidatesList().get(i).
					getServiceCandidateId()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return serviceCandidatesList.hashCode();
	}
	
	@Override
	public int compare(Composition o1, Composition o2) {
		if (o1.getUtility() < o2.getUtility()) {
			// If o1.getUtility() is less than o2.getUtility(), the 
			// method should normally return -1. But as sort() sorts 
			// the list in ascending order, we need to do the 
			// opposite here.
			return 1;
		}
		else if (o1.getUtility() > o2.getUtility()) {
			return -1;
		}
		else {
			return 0;
		}
	}

	
	// GETTERS AND SETTERS
	public List<ServiceCandidate> getServiceCandidatesList() {
		return serviceCandidatesList;
	}
	public void setServiceCandidateList(
			List<ServiceCandidate> serviceCandidatesList) {
		this.serviceCandidatesList = serviceCandidatesList;
		// Automatically compute the new aggregated QoS vector and the new
		// utility value.
		buildAggregatedQosVector();
		computeUtilityValue();
	}
	public QosVector getQosVectorAggregated() {
		return qosVectorAggregated;
	}
	public void setQosVectorAggregated(QosVector qosVectorAggregated) {
		this.qosVectorAggregated = qosVectorAggregated;
	}
	public double getUtility() {
		return utility;
	}
	public void setUtility(double utility) {
		this.utility = utility;
	}
	public double getFitness() {
		return fitness;
	}
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

}
