package qos;

import java.util.LinkedList;
import java.util.List;

public class RandomSetGenerator {
	
	private static final double CORRELATION_COST_TIME = -0.8;
	
	private static final double MIN_COST = 0.0;
	private static final double MAX_COST = 100.0;
	private static final double MIN_TIME = 0.0;
	private static final double MAX_TIME = 100.0;
	private static final double MIN_AVAILABILITY = 0.9;
	private static final double MAX_AVAILABILITY = 0.99;
	
	public List<ServiceClass> generateSet(
			int numClasses, int numCandidates) {		
		List<ServiceClass> serviceClassList = new LinkedList<ServiceClass>();
		// GENERATE SERVICE CLASSES
		for (int i = 0; i < numClasses; i++) {
			List<ServiceCandidate> serviceCandidateList = 
					new LinkedList<ServiceCandidate>();
			// GENERATE SERVICE CANDIDATES
			for (int j = 0; j < numCandidates; j++) {				
				int serviceID = (j + 1) + (numCandidates * i);
				
				QosVector qosVector = generateQosVector();
				serviceCandidateList.add(new ServiceCandidate(
						i+1, serviceID, 
						"WebService"+serviceID, qosVector));
			}
			
			serviceClassList.add(new ServiceClass(i + 1, 
					"ServiceClass" + (i + 1) + "", serviceCandidateList));
		}
		
		
		return serviceClassList;		
	}
	
	private QosVector generateQosVector() {
		// Choose costs factor randomly
		double costs = Math.random();
		// Take the inverse value because of negative correlation
		double time = 1.0 - costs;
		// Loop as long as value is out of bounds
		do {
			// Case 1: Value equal/bigger
			if (Math.random() < 0.5) {
				time *= (1.0 + Math.random() * (
						1.0 - Math.abs(CORRELATION_COST_TIME)));
			}
			// Case 2: Value equal/smaller
			else {
				time *= (1.0 - Math.random() * (
						1.0 - Math.abs(CORRELATION_COST_TIME)));
			}
		} while (time < 0.0 || time > 1.0);
		
		// Determine final values for constraints 
		// which are saved in a QosVector object
		costs = costs * (MAX_COST - MIN_COST) + MIN_COST;
		time = time * (MAX_TIME - MIN_TIME) + MIN_TIME;
		double availability = Math.random() * (
				MAX_AVAILABILITY - MIN_AVAILABILITY) + MIN_AVAILABILITY;
		
		return new QosVector(costs, time, availability);
	}
}
