package qos;

import java.util.LinkedList;
import java.util.List;

import jsc.distributions.Beta;

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
		
		Beta RandomDistribution = new Beta(2, 2);
		// Choose costs factor randomly
		double costs = RandomDistribution.random();
		
		// http://www.sitmo.com/article/generating-correlated-random-numbers/
		// min/max, spread for standardization
		double maxRandomTime = Math.sqrt(1-Math.pow(CORRELATION_COST_TIME, 2))*1;
		if(CORRELATION_COST_TIME > 0){
			maxRandomTime+= CORRELATION_COST_TIME;
		}
		double minRandomTime = CORRELATION_COST_TIME*1;
		if(CORRELATION_COST_TIME > 0){
			minRandomTime = 0;
		}
		double spread = maxRandomTime - minRandomTime;
		
		double time = CORRELATION_COST_TIME*costs+Math.sqrt(1-Math.pow(CORRELATION_COST_TIME, 2))*RandomDistribution.random();
		// standardization:
		time = time/spread + maxRandomTime;
		
		// Determine final values for constraints 
		// which are saved in a QosVector object
		costs = costs * (MAX_COST - MIN_COST) + MIN_COST;
		time = time * (MAX_TIME - MIN_TIME) + MIN_TIME;
		double availability = RandomDistribution.random() * (
				MAX_AVAILABILITY - MIN_AVAILABILITY) + MIN_AVAILABILITY;

		return new QosVector(costs, time, availability);
	}
}
