package qos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

public class AnalyticAlgorithm extends Algorithm {
	
	private List<ServiceClass> serviceClassesList;
	private List<ServiceCandidate> serviceCandidatesList;
	private Map<String, Constraint> constraintsMap;
	
	private List<Composition> compositionsList = new LinkedList<Composition>();
	private List<AlgorithmSolutionTier> algorithmSolutionTiers = 
		new LinkedList<AlgorithmSolutionTier>();
	private Composition optimalComposition = null;
	private QosVector qosMax;
	private QosVector qosMin;
	
	private int numberOfRequestedResultTiers;
	
	private long runtime = 0;

	
	// CONSTRUCTORS
	public AnalyticAlgorithm() {
		
	}
	
	public AnalyticAlgorithm(List<ServiceClass> serviceClassesList, 
			List<ServiceCandidate> serviceCandidatesList, 
			Map<String, Constraint> constraintsMap, 
			int numberOfRequestedResultTiers, QosVector max, QosVector min) {
		this.serviceClassesList = serviceClassesList;
		this.serviceCandidatesList = serviceCandidatesList;
		this.constraintsMap = constraintsMap;
		this.numberOfRequestedResultTiers = numberOfRequestedResultTiers;
		this.qosMax = max;
		this.qosMin = min;
		this.optimalComposition = new Composition(new LinkedList<ServiceCandidate>(), new QosVector(), 0.0);
	}
	
	
	@Override
	public void start(JProgressBar progressBar) {
		runtime = System.currentTimeMillis();
		for (int i=0; i < serviceClassesList.get(0).
				getServiceCandidateList().size(); i++) {
			doCompleteEnumeration(new Composition(
					new LinkedList<ServiceCandidate>(), new QosVector(), 0.0), 
					0, i);
		}		
		runtime = System.currentTimeMillis() - runtime;
		System.out.println("Optimal composition: " + 
				optimalComposition.getServiceCandidatesAsString()+" - "+optimalComposition.getUtility());
	}
	
	private void doCompleteEnumeration(Composition composition, 
			int serviceClassNumber, int serviceCandidateNumber) {
		composition = forward(composition, serviceClassNumber, 
				serviceCandidateNumber);
		if (composition != null) {
			if (isComplete(composition)) {
				if (isWithinConstraints(composition)) {
					if (isNewOptimalComposition(composition)) {
						List<ServiceCandidate> tempServiceCandidatesList = 
								new LinkedList<ServiceCandidate>(composition.getServiceCandidatesList());						
						Collections.copy(tempServiceCandidatesList, composition.getServiceCandidatesList());
						QosVector qos = new QosVector(composition.getQosVectorAggregated().getCosts(),
								composition.getQosVectorAggregated().getResponseTime(),
								composition.getQosVectorAggregated().getAvailability());
						optimalComposition = new Composition(tempServiceCandidatesList,
								qos, composition.getUtility());
					}
				}						
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
	
	private boolean isNewOptimalComposition(Composition composition) {		
		double utility = 0;
		for (ServiceCandidate sc : composition.getServiceCandidatesList()) {
			QosVector qos = sc.getQosVector();
			utility += ((qosMax.getCosts() - qos.getCosts()) / 
					(qosMax.getCosts() - qosMin.getCosts())) * constraintsMap.get(
							Constraint.COSTS).getWeight() / 100;
			utility += ((qosMax.getResponseTime() - qos.getResponseTime()) / 
					(qosMax.getResponseTime() - qosMin.getResponseTime())) * 
					constraintsMap.get(
							Constraint.RESPONSE_TIME).getWeight() / 100;
			utility += ((qos.getAvailability() - qosMin.getAvailability(
					)) / (qosMax.getAvailability() - qosMin.getAvailability(
							))) * constraintsMap.get(
									Constraint.AVAILABILITY).getWeight() / 100;
		}
		composition.setUtility(utility);
		if (composition.getUtility() > optimalComposition.getUtility()) {
			return true;
		}
		return false;
	}
	
	/*
	@Override
	public void start(JProgressBar progressBar) {
//		printInputData();
		runtime = System.currentTimeMillis();
		// DO COMPLETE ENUMERATION.
		for (int i = 0; i < serviceClassesList.get(0).
				getServiceCandidateList().size(); i++) {
			doCompleteEnumeration(new Composition(
					new LinkedList<ServiceCandidate>(), new QosVector(), 0.0), 
					0, i);
			//TODO: PROGRESSBAR DOESN'T WORK CORRECTLY
			//progressBar.setValue((int) Math.round((
			//		(double) (i + 1) / ((double) serviceClassesList.get(
			//				0).getServiceCandidateList().size())) * 100));
		}
		computeUtilityValues();
		runtime = System.currentTimeMillis() - runtime;
		printValidCompositions();

//		System.out.println("Optimal composition: " + 
//				findOptimalComposition().getServiceCandidatesAsString());
//		int count = 1;
//		for (AlgorithmSolutionTier tier : algorithmSolutionTiers) {
//			System.out.println("Tier " + count++ + "\n");
//			for (Composition composition : tier.getServiceCompositionList()) {
//				System.out.println(composition.getServiceCandidatesAsString());
//			}
//			System.out.println("\n\n");
//		}
	}
	
	// COMPLETE ENUMERATION.
	// TODO: [MAYBE] DO NOT CONSIDER PATHS THAT VIOLATE ANY CONSTRAINTS
	//		 ANYMORE. (OPTIMIZATION THAT COULD RESULT IN SOME WORK!)
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
	*/
	
	
	/*
	private void computeUtilityValues() {
		QosVector max = determineQosVectorMax();
		QosVector min = determineQosVectorMin();
		
		for (Composition composition : compositionsList) {
			// (Q_Max - Q_i) / (Q_max - Q_min) * W		negative criteria
			// (Q_i - Q_min) / (Q_max - Q_min) * W		positive criteria
			QosVector qos = composition.getQosVectorAggregated();
			double utility = ((max.getCosts() - qos.getCosts()) / 
					(max.getCosts() - min.getCosts())) * constraintsMap.get(
							Constraint.COSTS).getWeight() / 100;
			utility += ((max.getResponseTime() - qos.getResponseTime()) / 
					(max.getResponseTime() - min.getResponseTime())) * 
					constraintsMap.get(
							Constraint.RESPONSE_TIME).getWeight() / 100;
			utility += ((qos.getAvailability() - min.getAvailability(
					)) / (max.getAvailability() - min.getAvailability(
							))) * constraintsMap.get(
									Constraint.AVAILABILITY).getWeight() / 100;
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
//	private void printInputData() {
//		for (ServiceClass serviceClass : serviceClassesList) {
//			System.out.println("\n" + serviceClass.getName());
//			for (ServiceCandidate serviceCandidate : 
//				serviceClass.getServiceCandidateList()) {
//				System.out.println(serviceCandidate.getName());
//			}
//		}
//		System.out.println("\n\n");
//	}
	
	private void printValidCompositions() {
		for (Composition composition : compositionsList) {
			if (isWithinConstraints(composition)) {
				if (numberOfRequestedResultTiers <= 
					algorithmSolutionTiers.size()) {
					for (int count = 0; count < algorithmSolutionTiers.size(
							); count++) {
						if (composition.getUtility() > 
						algorithmSolutionTiers.get(
								count).getServiceCompositionList().get(
										0).getUtility()) {
							List<Composition> requestedCompositions = 
								new LinkedList<Composition>();
							requestedCompositions.add(composition);
							algorithmSolutionTiers.add(count, 
									new AlgorithmSolutionTier(
											(LinkedList<Composition>) 
											requestedCompositions, count));
							algorithmSolutionTiers.remove(
									numberOfRequestedResultTiers);
							break;
						}
						else if (composition.getUtility() == 
						algorithmSolutionTiers.get(
								count).getServiceCompositionList().get(
										0).getUtility()) {
							algorithmSolutionTiers.get(
									count).getServiceCompositionList(
											).add(composition);
							break;
						}
					}
				}
				else {
					List<Composition> requestedCompositions = 
						new LinkedList<Composition>();
					requestedCompositions.add(composition);
					algorithmSolutionTiers.add(new AlgorithmSolutionTier(
							(LinkedList<Composition>) 
							requestedCompositions, 
							algorithmSolutionTiers.size() + 1));
				}
//				System.out.println(composition.getServiceCandidatesAsString()
//						+ "\t" + composition.getUtility() + "\t" + 
//						composition.getQosVectorAggregated());
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
	
//	private Composition findOptimalComposition() {
//		double utilityMax = 0.0;
//		Composition optimalComposition = new Composition();
//		for (Composition composition : compositionsList) {
//			if (isWithinConstraints(composition) && 
//					composition.getUtility() > utilityMax) {
//				utilityMax = composition.getUtility();
//				optimalComposition = composition;
//			}
//		}
//		return optimalComposition;
//	}
	*/

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
	public List<AlgorithmSolutionTier> getAlgorithmSolutionTiers() {
		return algorithmSolutionTiers;
	}
	public void setAlgorithmSolutionTiers(
			List<AlgorithmSolutionTier> algorithmSolutionTiers) {
		this.algorithmSolutionTiers = algorithmSolutionTiers;
	}
	public long getRuntime() {
		return runtime;
	}
	
}
