package qos;

import java.util.Map;

public class ServiceCandidate {
	
	private String serviceCandidateId;
	private String name;
	private QosVector qosVector = new QosVector();
	private double utility;
	
	
	// CONSTRUCTORS
	public ServiceCandidate() {
		
	}
	
	public ServiceCandidate(String serviceCandidateId, 
			String name, QosVector qosVector) {
		this.serviceCandidateId = serviceCandidateId;
		this.name = name;
		this.qosVector = qosVector;
	}
	
	public void determineUtilityValue(Map<String, Constraint> constraintsMap, 
			QosVector max, QosVector min) {
		// (Q_Max - Q_i) / (Q_max - Q_min) * Weight		
		//  -> QoS to be minimized
		// (Q_i - Q_min) / (Q_max - Q_min) * Weight		
		//  -> QoS to be maximized
		if (max == min) {
			
		}
		double utility = 0.0;
		if (constraintsMap.get(Constraint.COSTS) != null) {
			if (max.getCosts() - min.getCosts() == 0.0) {
				utility += (constraintsMap.get(
						Constraint.COSTS).getWeight() / 100);
			}
			else {
				utility += ((max.getCosts() - qosVector.getCosts()) / 
						(max.getCosts() - min.getCosts())) * 
						constraintsMap.get(Constraint.COSTS).getWeight() / 100;
			}
			
		}
		if (constraintsMap.get(Constraint.RESPONSE_TIME) != null) { 
			if (max.getResponseTime() - min.getResponseTime() == 0.0) {
				utility += (constraintsMap.get(
								Constraint.RESPONSE_TIME).getWeight() / 100);
			}
			else {
				utility += ((max.getResponseTime() - 
						qosVector.getResponseTime()) / 
						(max.getResponseTime() - min.getResponseTime())) * 
						constraintsMap.get(
								Constraint.RESPONSE_TIME).getWeight() / 100;
			}
			
		}
		if (constraintsMap.get(Constraint.AVAILABILITY) != null) {
			if (max.getAvailability() - min.getAvailability() == 0.0) {
				utility += (constraintsMap.get(
						Constraint.AVAILABILITY).getWeight() / 100);
			}
			else {
				utility += ((qosVector.getAvailability() - 
						min.getAvailability()) / 
						(max.getAvailability() - min.getAvailability())) * 
						constraintsMap.get(
								Constraint.AVAILABILITY).getWeight() / 100;
			}
		}	
		setUtility(utility);
	}

	
	// GETTERS AND SETTERS
	public String getServiceCandidateId() {
		return serviceCandidateId;
	}
	public void setServiceCandidateId(String serviceCandidateId) {
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
	public double getUtility() {
		return utility;
	}
	public void setUtility(double utility) {
		this.utility = utility;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) Double.parseDouble(serviceCandidateId);
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
		if (!serviceCandidateId.equals(other.serviceCandidateId))
			return false;
		return true;
	}
	
}
