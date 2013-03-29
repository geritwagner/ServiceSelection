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
		// DO COMPLETE ENUMERATION
		for (int i=0; i < serviceClassesList.get(0).
				getServiceCandidateList().size(); i++) {
			doCompleteEnumeration(new Composition(
					new LinkedList<ServiceCandidate>(), new QosVector(), 0.0), 
					0, i);
			//TODO: PROGRESSBAR DOESN'T WORK CORRECTLY
			//progressBar.setValue((int) Math.round((
			//		(double) (i + 1) / ((double) serviceClassesList.get(
			//				0).getServiceCandidateList().size())) * 100));
		}		
		runtime = System.currentTimeMillis() - runtime;
		buildSolutionTiers();
		System.out.println("Optimal composition: " + 
				optimalComposition.getServiceCandidatesAsString()+" - "+optimalComposition.getUtility());
	}
	
	// ENUMERATION
	// TODO: [MAYBE] DO NOT CONSIDER PATHS THAT VIOLATE ANY CONSTRAINTS
	//		 ANYMORE. (OPTIMIZATION THAT COULD RESULT IN SOME WORK!)
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
	
	private void buildSolutionTiers() {
		List<Composition> requestedCompositions = 
				new LinkedList<Composition>();
		requestedCompositions.add(optimalComposition);
		algorithmSolutionTiers.add(new AlgorithmSolutionTier(
				(LinkedList<Composition>) 
				requestedCompositions, 1));
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
	
	/*
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
