package qos;

import java.util.LinkedList;
import java.util.List;

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
