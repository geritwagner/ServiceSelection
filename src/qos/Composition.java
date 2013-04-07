package qos;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Composition implements Comparator<Composition> {
	
	private List<ServiceCandidate> serviceCandidatesList;
	private QosVector qosVectorAggregated;
	private double utility;
	
	
	// CONSTRUCTORS
	public Composition() {
		this.serviceCandidatesList = new LinkedList<ServiceCandidate>();
		this.qosVectorAggregated = new QosVector();
		this.utility = 0.0;
	}

	public Composition(List<ServiceCandidate> serviceCandidatesList, 
			QosVector qosVectorAggregated, double utility) {
		this.serviceCandidatesList = serviceCandidatesList;
		this.qosVectorAggregated = qosVectorAggregated;
		this.utility = utility;
	}
	
	// TODO: Funktion ist analog schon in der Klasse QosVector vorhanden. 
	//		 Umstellung auf diese Variante vornehmen?
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

	// 
	public void addServiceCandidate(ServiceCandidate serviceCandidate) {
		serviceCandidatesList.add(serviceCandidate);
		// TODO: Umstellung auf buildAggregatedQosVector() vornehmen?
		qosVectorAggregated.add(serviceCandidate.getQosVector());
	}
	
	// Remove the last service candidate from the list.
	public void removeServiceCandidate() {
		ServiceCandidate serviceCandidateRemoved = 
				serviceCandidatesList.remove(serviceCandidatesList.size() - 1);
		// TODO: Umstellung auf buildAggregatedQosVector() vornehmen?
		qosVectorAggregated.subtract(
				serviceCandidateRemoved.getQosVector());
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
	
}
