package qos;

import java.util.LinkedList;
import java.util.List;

public class ServiceClass {
	
	private int serviceClassId;
	private String name;
	private QosVector qosVectorMin = new QosVector();
	private QosVector qosVectorMax = new QosVector();
	private List<ServiceCandidate> serviceCandidateList = 
			new LinkedList<ServiceCandidate>();
	
	
	// CONSTRUCTORS
	public ServiceClass() {
		
	}
	
	public ServiceClass(int serviceClassId, String name,
			List<ServiceCandidate> serviceCandidateList) {
		this.serviceClassId = serviceClassId;
		this.name = name;
		this.serviceCandidateList = serviceCandidateList;
	}
	
	
	// GETTERS AND SETTERS
	public int getServiceClassId() {
		return serviceClassId;
	}
	public void setServiceClassId(int serviceClassId) {
		this.serviceClassId = serviceClassId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public QosVector getQosVectorMax() {
		return qosVectorMax;
	}
	public QosVector getQosVectorMin() {
		return qosVectorMin;
	}
	public void setQosVectorMin(QosVector qosVectorMin) {
		this.qosVectorMin = qosVectorMin;
	}
	public void setQosVectorMax(QosVector qosVectorMax) {
		this.qosVectorMax = qosVectorMax;
	}
	public List<ServiceCandidate> getServiceCandidateList() {
		return serviceCandidateList;
	}
	public void setServiceCandidateList(List<ServiceCandidate> serviceCandidateList) {
		this.serviceCandidateList = serviceCandidateList;
	}
	public int getSize() {
		return serviceCandidateList.size();
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + serviceClassId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceClass other = (ServiceClass) obj;
		if (serviceClassId != other.serviceClassId)
			return false;
		return true;
	}
	
}
