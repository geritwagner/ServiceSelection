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
	
	// TODO: Funktion ist analog schon in der Klasse QosVector vorhanden. 
	//		 Fraglich, was besser ist. Hier wird der Vektor immer wieder neu 
	//		 berechnet, was eigtl nicht schlecht ist. In der anderen Variante 
	//		 wird immer nur hinzugefügt bzw. weggenommen.
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

	
	public void addServiceCandidate(ServiceCandidate serviceCandidate) {
		serviceCandidatesList.add(serviceCandidate);
		qosVectorAggregated.add(serviceCandidate.getQosVector());
	}
	
	// Remove the last service candidate from the list.
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
	
	// Compare two compositions by comparing the IDs of each of their 	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Composition other = (Composition) obj;
		for (int count = 0; 
				count < getServiceCandidatesList().size(); count++) {
				if (getServiceCandidatesList().get(count).
						getServiceCandidateId() != other.
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
