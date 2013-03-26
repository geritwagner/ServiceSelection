package qos;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

public class AnalyticAlgorithm {
	
	private List<ServiceClass> serviceClassesList;
	private List<ServiceCandidate> serviceCandidatesList;
	private Map<String, Constraint> constraintsMap;
	
	private List<Composition> compositionsList = new LinkedList<Composition>();

	
	// CONSTRUCTORS
	public AnalyticAlgorithm() {
		
	}
	
	public AnalyticAlgorithm(List<ServiceClass> serviceClassesList, 
			List<ServiceCandidate> serviceCandidatesList, 
			Map<String, Constraint> constraintsMap) {
		this.serviceClassesList = serviceClassesList;
		this.serviceCandidatesList = serviceCandidatesList;
		this.constraintsMap = constraintsMap;
	}
	
	
	public void start(JProgressBar progressBar) {
//		printInputData();
		
		// DO COMPLETE ENUMERATION.
		for (int i = 0; i < serviceClassesList.get(0).
				getServiceCandidateList().size(); i++) {
			doCompleteEnumeration(new Composition(
					new LinkedList<ServiceCandidate>(), new QosVector(), 0.0), 
					0, i);
			//TODO: PROGRESSBAR DOESN'T WORK CORRECTLY
			progressBar.setValue((int) Math.round((
					(double) (i + 1) / ((double) serviceClassesList.get(
							0).getServiceCandidateList().size())) * 100));
		}
		computeUtilityValues();
		//printValidCompositions();
		// TODO: [MAYBE] BETTER OUTPUT FOR OPTIMAL COMPOSITION.
		System.out.println("Optimal composition: " + 
				findOptimalComposition().getServiceCandidatesAsString());
	}
	
	// COMPLETE ENUMERATION.
	// TODO: - SHOW COMPOSITIONS IN RESULT TABLE.
	//		 - [MAYBE] DO NOT CONSIDER PATHS THAT VIOLATE ANY CONSTRAINTS
	//		   ANYMORE. (OPTIMIZATION THAT COULD RESULT IN SOME WORK!) -> WOULDN'T BE A COMPLETE ENUMERATION ANYMORE
	private void doCompleteEnumeration(Composition composition, 
			int serviceClassNumber, int serviceCandidateNumber) {
		composition = forward(composition, serviceClassNumber, 
				serviceCandidateNumber);
		if (composition != null) {
			if (isComplete(composition)) {
				// TODO: ADD COMPLETE COMPOSITIONS TO LIST.
				//		 THE FOLLOWING OPERATIONS ARE NECESSARY IN ORDER TO 
				//		 CREATE NEW OBJECTS (I.E. SERVICECANDIDATELISTS AND 
				//		 QOSVECTORS). MIGHT BE NOT SO ELEGANT, THOUGH.
				List<ServiceCandidate> serviceCandidatesListNew = 
						new LinkedList<ServiceCandidate>();
				serviceCandidatesListNew.addAll(
						composition.getServiceCandidatesList());
				QosVector qos = composition.getQosVectorAggregated();
				QosVector qosVectorNew = new QosVector(qos.getCosts(), 
						qos.getResponseTime(), qos.getAvailability());
				compositionsList.add(new Composition(serviceCandidatesListNew, 
						qosVectorNew, 0.0));
				
				// TODO: THE FOLLOWING LINES ARE ONLY NEEDED IN ORDER TO 
				//		 CHECK THE WORKAROUND INTRODUCED ABOVE.
//				if (isWithinConstraints(composition)) {
//					System.out.println(composition.getQosVectorAggregated());
//				}
			}
			else {
				serviceClassNumber++;
				for (int i = 0; i < serviceClassesList.get(serviceClassNumber).
						getServiceCandidateList().size(); i++) {
					doCompleteEnumeration(composition, serviceClassNumber, i);
				}
			}
			composition = backward(composition);
			serviceClassNumber--;
		}
		return;
	}
	
	private Composition forward(Composition composition, 
			int serviceClassNumber, int serviceCandidateNumber) {
		ServiceClass serviceClass = serviceClassesList.get(serviceClassNumber);
		ServiceCandidate serviceCandidate = 
				serviceClass.getServiceCandidateList().get(
						serviceCandidateNumber);
		composition.addServiceCandidate(serviceCandidate);
		return composition;
	}
	
	private Composition backward(Composition composition) {
		composition.removeServiceCandidate();
		return composition;
	}
	
	// CHECK IS DONE BY COMPARING THE SIZE OF THE COMPOSITION WITH THE NUMBER 
	// OF AVAILABLE SERVICE CLASSES.
	private boolean isComplete(Composition composition) {
		if (composition.getServiceCandidatesList().size() == 
				serviceClassesList.size()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void computeUtilityValues() {
		QosVector max = determineQosVectorMax();
		QosVector min = determineQosVectorMin();
		
		for (Composition composition : compositionsList) {
			// (Q_Max - Q_i) / (Q_max - Q_min) * W		negative criteria
			// (Q_i - Q_min) / (Q_max - Q_min) * W		positive criteria
			QosVector qos = composition.getQosVectorAggregated();			
			Constraint costs = constraintsMap.get(Constraint.COSTS);
			Constraint responseTime = constraintsMap.get(Constraint.RESPONSE_TIME);
			Constraint availability = constraintsMap.get(Constraint.AVAILABILITY);
			double utility = (((max.getCosts() - qos.getCosts()) / 
					(max.getCosts() - min.getCosts())) * costs.getWeight()/100) + 
					(((max.getResponseTime() - qos.getResponseTime()) / 
					(max.getResponseTime() - min.getResponseTime())) * responseTime.getWeight()/100) + 
					(((qos.getAvailability() - min.getAvailability()) / 
					(max.getAvailability() - min.getAvailability())) * availability.getWeight()/100);
			composition.setUtility(utility);
		}
		
	}
	
	private QosVector determineQosVectorMax() {
		QosVector max = new QosVector(0.0, 0.0, 0.0);
		for (Composition composition : compositionsList) {
			QosVector qos = composition.getQosVectorAggregated();
			if (qos.getCosts() > max.getCosts()) {
				max.setCosts(qos.getCosts());
			}
			if (qos.getResponseTime() > max.getResponseTime()) {
				max.setResponseTime(qos.getResponseTime());
			}
			if (qos.getAvailability() > max.getAvailability()) {
				max.setAvailability(qos.getAvailability());
			}
		}
		return max;
	}
	
	private QosVector determineQosVectorMin() {
		QosVector min = new QosVector(100000.0, 100000.0, 1.0);
		for (Composition composition : compositionsList) {
			QosVector qos = composition.getQosVectorAggregated();
			if (qos.getCosts() < min.getCosts()) {
				min.setCosts(qos.getCosts());
			}
			if (qos.getResponseTime() < min.getResponseTime()) {
				min.setResponseTime(qos.getResponseTime());
			}
			if (qos.getAvailability() < min.getAvailability()) {
				min.setAvailability(qos.getAvailability());
			}
		}
		return min;
	}
	
	// PRINT SERVICE CLASSES AND THEIR SERVICE CANDIDATES.
	private void printInputData() {
		for (ServiceClass serviceClass : serviceClassesList) {
			System.out.println("\n" + serviceClass.getName());
			for (ServiceCandidate serviceCandidate : 
				serviceClass.getServiceCandidateList()) {
				System.out.println(serviceCandidate.getName());
			}
		}
		System.out.println("\n\n");
	}
	
	private void printValidCompositions() {
		for (Composition composition : compositionsList) {
			if (isWithinConstraints(composition)) {
				System.out.println(composition.getServiceCandidatesAsString()
						+ "\t" + composition.getUtility() + "\t" + 
						composition.getQosVectorAggregated());
			}
		}
	}
	
	private boolean isWithinConstraints(Composition composition) {
		boolean isWithinConstraints = true;
		QosVector qosVector = composition.getQosVectorAggregated();
		Constraint costs = constraintsMap.get(Constraint.COSTS);
		Constraint responseTime = constraintsMap.get(Constraint.RESPONSE_TIME);
		Constraint availability = constraintsMap.get(Constraint.AVAILABILITY);
		if (costs != null && qosVector.getCosts() > costs.getValue()) {
			isWithinConstraints = false;
		}
		if (responseTime != null && 
				qosVector.getResponseTime() > responseTime.getValue()) {
			isWithinConstraints = false;
		}
		if (availability != null && 
				qosVector.getAvailability() < availability.getValue()) {
			isWithinConstraints = false;
		}
		return isWithinConstraints;
	}
	
	private Composition findOptimalComposition() {
		double utilityMax = 0.0;
		Composition optimalComposition = new Composition();
		for (Composition composition : compositionsList) {
			if (isWithinConstraints(composition) && 
					composition.getUtility() > utilityMax) {
				utilityMax = composition.getUtility();
				optimalComposition = composition;
			}
		}
		return optimalComposition;
	}
	

	// GETTERS AND SETTERS
	public List<ServiceClass> getServiceClassesList() {
		return serviceClassesList;
	}
	public void setServiceClassesList(List<ServiceClass> serviceClassesList) {
		this.serviceClassesList = serviceClassesList;
	}
	public List<ServiceCandidate> getServiceCandidatesList() {
		return serviceCandidatesList;
	}
	public void setServiceCandidatesList(
			List<ServiceCandidate> serviceCandidatesList) {
		this.serviceCandidatesList = serviceCandidatesList;
	}
	public Map<String, Constraint> getConstraintList() {
		return constraintsMap;
	}
	public void setConstraintList(Map<String, Constraint> constraintsMap) {
		this.constraintsMap = constraintsMap;
	}
	public List<Composition> getCompositionsList() {
		return compositionsList;
	}
	public void setCompositionsList(List<Composition> compositionsList) {
		this.compositionsList = compositionsList;
	}
	
}
