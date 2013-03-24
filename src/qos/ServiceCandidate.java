package qos;

public class ServiceCandidate {
	
	private int serviceClassId;
	private String serviceClassName;
	private int serviceCandidateId;
	private String name;
	private QosVector qosVector = new QosVector();
	
	
	// CONSTRUCTORS
	public ServiceCandidate() {
		
	}
	
	public ServiceCandidate(int serviceClassId, String serviceClassName, 
			int serviceCandidateId, String name, QosVector qosVector) {
		this.serviceCandidateId = serviceCandidateId;
		this.serviceClassName = serviceClassName;
		this.serviceClassId = serviceClassId;
		this.name = name;
		this.qosVector = qosVector;
	}

	
	// GETTERS AND SETTERS
	public int getServiceClassId() {
		return serviceClassId;
	}
	public void setServiceClassId(int serviceClassId) {
		this.serviceClassId = serviceClassId;
	}
	public String getServiceClassName() {
		return serviceClassName;
	}
	public void setServiceClassName(String serviceClassName) {
		this.serviceClassName = serviceClassName;
	}
	public int getServiceCandidateId() {
		return serviceCandidateId;
	}
	public void setServiceCandidateId(int serviceCandidateId) {
		this.serviceCandidateId = serviceCandidateId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public QosVector getQosVector() {
		return qosVector;
	}
	public void setQosVector(QosVector qosVector) {
		this.qosVector = qosVector;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + serviceCandidateId;
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
		ServiceCandidate other = (ServiceCandidate) obj;
		if (serviceCandidateId != other.serviceCandidateId)
			return false;
		return true;
	}
	
}
