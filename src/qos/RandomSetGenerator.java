package qos;

import java.util.LinkedList;
import java.util.List;

public class RandomSetGenerator {
	
	public static List<ServiceClass> generateSet(
			int numClasses, int numCandidates) {		
		List<ServiceClass> serviceClassList = new LinkedList<ServiceClass>();
		// GENERATE SERVICE CLASSES
		for (int i = 0; i < numClasses; i++) {
			List<ServiceCandidate> serviceCandidateList = 
					new LinkedList<ServiceCandidate>();
			// GENERATE SERVICE CANDIDATES
			for (int j = 0; j < numCandidates; j++) {				
				int serviceID = (j + 1) + (numCandidates * i);
				// TODO: CONSIDER CORRELATIONS AND REALISTIC VALUES
				double cost = myRandom(0, 100);
				double time = myRandom(0, 100);
				double availability = myRandom(0.85, 0.99);
				
				QosVector qosVector = new QosVector(cost, time, availability);				
				serviceCandidateList.add(new ServiceCandidate(
						i+1, "ServiceClass"+(i+1), serviceID, 
						"WebService"+serviceID, qosVector));
			}
			
			serviceClassList.add(new ServiceClass(i + 1, 
					"ServiceClass" + (i + 1) + "", serviceCandidateList));
		}
		
		
		return serviceClassList;		
	}
	
	public static double myRandom(double low, double high) {
		return Math.random() * (high - low) + low;
	}

}
