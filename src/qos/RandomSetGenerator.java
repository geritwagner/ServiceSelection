package qos;

import java.util.LinkedList;
import java.util.List;

public class RandomSetGenerator {
	
	// TODO: Set realistic correlation values
	private static final double CORRELATION_COST_TIME = 0.8;
	private static final double CORRELATION_COST_AVAILABILITY = 0.8;
	private static final double CORRELATION_TIME_AVAILABILITY = 0.2;
	
	// TODO: Set realistic extreme values for qos attributes. Especially
	//		 MIN_AVAILABILITY has to be discussed.
	private static final double MIN_COST = 0.0;
	private static final double MAX_COST = 100.0;
	private static final double MIN_TIME = 0.0;
	private static final double MAX_TIME = 100.0;
	private static final double MIN_AVAILABILITY = 0.9;
	private static final double MAX_AVAILABILITY = 0.99;
	
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
				// TODO: Consider correlations and realistic values.
				double cost = myRandom(MIN_COST, MAX_COST);
				double time;
				double availability;
				if (Math.random() < CORRELATION_COST_TIME) {
					time = randomWithCorrelation(MAX_COST - cost, 
							MAX_TIME, MIN_TIME);
				}
				else {
					time = myRandom(MIN_TIME, MAX_TIME);
				}
				if (Math.random() < CORRELATION_COST_AVAILABILITY) {
					availability = randomWithCorrelation(cost / 100 * 
							(MAX_AVAILABILITY - MIN_AVAILABILITY) + 
							MIN_AVAILABILITY, MAX_AVAILABILITY, 
							MIN_AVAILABILITY);
				}
				// TODO: Is there any correlation between time and 
				//		 availability? If not, delete this else if!
				else if (Math.random() < CORRELATION_TIME_AVAILABILITY) {
					availability = randomWithCorrelation(time / 100 * 
							(MAX_AVAILABILITY - MIN_AVAILABILITY) + 
							MIN_AVAILABILITY, MAX_AVAILABILITY, 
							MIN_AVAILABILITY);
				}
				else {
					 availability = myRandom(
							 MIN_AVAILABILITY, MAX_AVAILABILITY);
				}
				QosVector qosVector = new QosVector(cost, time, availability);				
				serviceCandidateList.add(new ServiceCandidate(
						i+1, serviceID, 
						"WebService"+serviceID, qosVector));
			}
			
			serviceClassList.add(new ServiceClass(i + 1, 
					"ServiceClass" + (i + 1) + "", serviceCandidateList));
		}
		
		
		return serviceClassList;		
	}
	
	private static double myRandom(double low, double high) {
		return Math.random() * (high - low) + low;
	}
	
	private static double randomWithCorrelation(double correlationValue, 
			double maxValue, double minValue) {
		double returnValue = 0.0;
		do {
			if (Math.random() > 0.5) {
				returnValue = correlationValue + 
						correlationValue * Math.random() * 0.1;
			}
			else {
				returnValue = correlationValue - 
						correlationValue * Math.random() * 0.1;
			}
		}
		while (returnValue < minValue || returnValue > maxValue);
		return returnValue;
	}
}
