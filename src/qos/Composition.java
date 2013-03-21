package qos;

import java.util.LinkedList;
import java.util.List;

public class Composition {
	
	private int id;
	private String name;
	private QosVector qosVectorAggregated;
	private List<ServiceCandidate> serviceCandidatesList = 
			new LinkedList<ServiceCandidate>();
	
	
	// CONSTRUCTORS
	public Composition() {
		
	}

	public Composition(int id, String name,
			List<ServiceCandidate> serviceCandidatesList) {
		this.id = id;
		this.name = name;
		this.serviceCandidatesList = serviceCandidatesList;
		qosVectorAggregated = new QosVector();
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

	
	// GETTERS AND SETTERS
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public QosVector getQosVectorAggregated() {
		return qosVectorAggregated;
	}
	public void setQosVectorAggregated(QosVector qosVectorAggregated) {
		this.qosVectorAggregated = qosVectorAggregated;
	}
	public List<ServiceCandidate> getServiceCandidatesList() {
		return serviceCandidatesList;
	}
	public void setServiceCandidateList(List<ServiceCandidate> serviceCandidatesList) {
		this.serviceCandidatesList = serviceCandidatesList;
	}
	
}
