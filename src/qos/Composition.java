package qos;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Composition {
	
	private List<ServiceCandidate> serviceCandidatesList = 
			new LinkedList<ServiceCandidate>();
	private QosVector qosVectorAggregated;
	private double utility;
	
	
	// CONSTRUCTORS
	public Composition() {
		
	}

	public Composition(List<ServiceCandidate> serviceCandidatesList, 
			QosVector qosVectorAggregated, double utility) {
		this.serviceCandidatesList = serviceCandidatesList;
		this.qosVectorAggregated = qosVectorAggregated;
		this.utility = utility;
	}
	
	public void buildAggregatedQosVector() {
		double costs = 0.0;
		double responseTime = 0.0;
		double availability = 0.0;
		for (ServiceCandidate candidate : serviceCandidatesList) {
			costs += candidate.getQosVector().getCosts();
			responseTime += candidate.getQosVector().getResponseTime();
			availability *= candidate.getQosVector().getAvailability();
		}
		qosVectorAggregated = new QosVector(costs, responseTime, availability);
	}
	
	public void computeUtilityValue(Map<String, Constraint> constraintsMap, 
			QosVector max, QosVector min) {
		double utility = 0.0;
		for (ServiceCandidate candidate : serviceCandidatesList) {
			utility += candidate.computeUtilityValue(constraintsMap, max, min);
		}
		// (Q_Max - Q_i) / (Q_max - Q_min) * W		negative criteria
		// (Q_i - Q_min) / (Q_max - Q_min) * W		positive criteria
//		double utility = 0.0;
//		if (constraintsMap.get(Constraint.COSTS) != null) {
//			utility += ((max.getCosts() - qosVectorAggregated.getCosts()) / 
//					(max.getCosts() - min.getCosts())) * constraintsMap.get(
//							Constraint.COSTS).getWeight() / 100;
//		}
//		if (constraintsMap.get(Constraint.RESPONSE_TIME) != null) { 
//			utility += ((max.getResponseTime() - 
//					qosVectorAggregated.getResponseTime()) / 
//					(max.getResponseTime() - min.getResponseTime())) * 
//					constraintsMap.get(
//							Constraint.RESPONSE_TIME).getWeight() / 100;
//		}
//		if (constraintsMap.get(Constraint.AVAILABILITY) != null)
//			utility += ((qosVectorAggregated.getAvailability() - 
//					min.getAvailability()) / 
//					(max.getAvailability() - min.getAvailability(
//					))) * constraintsMap.get(
//							Constraint.AVAILABILITY).getWeight() / 100;
		this.utility = utility / serviceCandidatesList.size();
	}

	
	public void addServiceCandidate(ServiceCandidate serviceCandidate) {
		serviceCandidatesList.add(serviceCandidate);
		qosVectorAggregated.add(serviceCandidate.getQosVector());
	}
	
	// REMOVES THE LAST SERVICE CANDIDATE OF THE LIST.
	public void removeServiceCandidate() {
		ServiceCandidate serviceCandidateRemoved = 
				serviceCandidatesList.remove(serviceCandidatesList.size() - 1);
		qosVectorAggregated.subtract(
				serviceCandidateRemoved.getQosVector());
	}
	
	
	public String getServiceCandidatesAsString() {
		String serviceCandidates = "";
		for (ServiceCandidate serviceCandidate : serviceCandidatesList) {
			serviceCandidates = serviceCandidates + " " + 
					serviceCandidate.getServiceCandidateId();
		}
		return serviceCandidates;
	}
	
	public boolean equals(Composition composition) {
		for (int count = 0; 
			count < getServiceCandidatesList().size(); count++) {
			if (getServiceCandidatesList().get(count).
					getServiceCandidateId() != composition.
					getServiceCandidatesList().get(count).
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
	
}
