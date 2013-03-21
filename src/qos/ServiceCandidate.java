package qos;

public class ServiceCandidate {
	
	private int serviceClassId;
	private String serviceClassName;
	private int serviceCandidateId;
	private String name;
	private String provider;
	private QosVector qosVector = new QosVector();
	
	
	// CONSTRUCTORS
	public ServiceCandidate() {
		
	}
	
	public ServiceCandidate(int serviceClassId, String serviceClassName, 
			int serviceCandidateId, String name, String provider, 
			QosVector qosVector) {
		this.serviceCandidateId = serviceCandidateId;
		this.serviceClassName = serviceClassName;
		this.serviceClassId = serviceClassId;
		this.name = name;
		this.provider = provider;
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
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public QosVector getQosVector() {
		return qosVector;
	}
	public void setQosVector(QosVector qosVector) {
		this.qosVector = qosVector;
	}
	
}
