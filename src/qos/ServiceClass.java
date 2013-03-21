package qos;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	
	
	private void determineQosVectorMin() {
		double priceMin = 0;
		double costsMin = 0;
		double responseTimeMin = 0;
		double availabilityMin = 0;
		double reliabilityMin = 0;
		for (ServiceCandidate serviceCandidate : serviceCandidateList) {
			if (serviceCandidate.getQosVector().getPrice() < priceMin) {
				priceMin = serviceCandidate.getQosVector().getPrice();
			}
			if (serviceCandidate.getQosVector().getCosts() < costsMin) {
				costsMin = serviceCandidate.getQosVector().getCosts();
			}
			if (serviceCandidate.getQosVector().getResponseTime() < responseTimeMin) {
				responseTimeMin = serviceCandidate.getQosVector().getResponseTime();
			}
			if (serviceCandidate.getQosVector().getAvailability() < availabilityMin) {
				availabilityMin = serviceCandidate.getQosVector().getAvailability();
			}
			if (serviceCandidate.getQosVector().getReliability() < reliabilityMin) {
				reliabilityMin = serviceCandidate.getQosVector().getReliability();
			}
		}
		qosVectorMin = new QosVector(priceMin, costsMin, responseTimeMin, 
				availabilityMin, reliabilityMin);
	}
	
	private void determineQosVectorMax() {
		double priceMax = 0;
		double costsMax = 0;
		double responseTimeMax = 0;
		double availabilityMax = 0;
		double reliabilityMax = 0;
		for (ServiceCandidate serviceCandidate : serviceCandidateList) {
			if (serviceCandidate.getQosVector().getPrice() > priceMax) {
				priceMax = serviceCandidate.getQosVector().getPrice();
			}
			if (serviceCandidate.getQosVector().getCosts() > costsMax) {
				costsMax = serviceCandidate.getQosVector().getCosts();
			}
			if (serviceCandidate.getQosVector().getResponseTime() > responseTimeMax) {
				responseTimeMax = serviceCandidate.getQosVector().getResponseTime();
			}
			if (serviceCandidate.getQosVector().getAvailability() > availabilityMax) {
				availabilityMax = serviceCandidate.getQosVector().getAvailability();
			}
			if (serviceCandidate.getQosVector().getReliability() > reliabilityMax) {
				reliabilityMax = serviceCandidate.getQosVector().getReliability();
			}
		}
		qosVectorMax = new QosVector(priceMax, costsMax, responseTimeMax, 
				availabilityMax, reliabilityMax);
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

}
