package qos;

import java.util.LinkedList;
import java.util.List;

public class RandomSetGenerator {
	
	public static List<ServiceClass> generateSet(int numClasses, int numCandidates) {		
		List<ServiceClass> serviceClassList = new LinkedList<ServiceClass>();		
		for (int i=0; i<numClasses; i++) {
			List<ServiceCandidate> serviceCandidateList = new LinkedList<ServiceCandidate>();
			// GENERATE SERVICE CANDIDATES
			for (int j=0; j<numCandidates; j++) {
				int serviceID = (i+1)*(j+1);
				
				double cost = 0;
				double time = 0;
				double availability = 0;
				
				QosVector qosVector = new QosVector(0, cost, time, availability, 0);				
				serviceCandidateList.add(new ServiceCandidate(i+1, "ServiceClass"+(i+1), 
						serviceID, "WebService"+serviceID, "", qosVector));
			}
			
			serviceClassList.add(new ServiceClass(i+1, "ServiceClass"+(i+1)+"", serviceCandidateList));
		}
		
		
		return serviceClassList;		
	}

}
