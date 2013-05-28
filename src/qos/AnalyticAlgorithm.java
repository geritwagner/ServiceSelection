package qos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnalyticAlgorithm {
	
	private List<ServiceClass> serviceClassesList;
	private Map<String, Constraint> constraintsMap;
	
	private List<Composition> compositionsList = new LinkedList<Composition>();
	private List<AlgorithmSolutionTier> algorithmSolutionTiers = 
		new LinkedList<AlgorithmSolutionTier>();
	
	private int numberOfRequestedResultTiers;
	
	private long runtime = 0;
	
	private int workPercentage;

	
	// CONSTRUCTORS
	public AnalyticAlgorithm() {
		
	}
	
	public AnalyticAlgorithm(List<ServiceClass> serviceClassesList, 
			Map<String, Constraint> constraintsMap, 
			int numberOfRequestedResultTiers) {
		this.serviceClassesList = serviceClassesList;
		this.constraintsMap = constraintsMap;
		this.numberOfRequestedResultTiers = numberOfRequestedResultTiers;
	}
	
	public void start() {
		runtime = System.nanoTime();
		workPercentage = 0;
		// Do complete enumeration.
		for (int i = 0; i < serviceClassesList.get(0).
				getServiceCandidateList().size(); i++) {
			doCompleteEnumeration(new Composition(), 0, i);
			workPercentage = (int) (100.0 * i / serviceClassesList.size());
		}	
		for (int i = 0; i < algorithmSolutionTiers.size(); i++) {
			algorithmSolutionTiers.get(i).setTierTitle(i + 1);
		}
		runtime = System.nanoTime() - runtime;
	}
	
	public void startInBenchmarkMode() {
		runtime = System.nanoTime();
		// Do complete enumeration.
		for (int i = 0; i < serviceClassesList.get(0).
				getServiceCandidateList().size(); i++) {
			doCompleteEnumeration(new Composition(), 0, i);
		}	
		for (int i = 0; i < algorithmSolutionTiers.size(); i++) {
			algorithmSolutionTiers.get(i).setTierTitle(i + 1);
		}
		runtime = System.nanoTime() - runtime;
	}
	
	// ENUMERATION
	private void doCompleteEnumeration(Composition composition, 
			int serviceClassNumber, int serviceCandidateNumber) {
		composition = forward(composition, serviceClassNumber, 
				serviceCandidateNumber);
		if (composition != null) {
			if (isComplete(composition)) {
				if (composition.isWithinConstraints(constraintsMap)) {
					changeAlgorithmSolutionTiers(composition);
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
	
	// Check is done by comparing the size of the composition with the number
	// of available service classes.
	private boolean isComplete(Composition composition) {
		if (composition.getServiceCandidatesList().size() == 
				serviceClassesList.size()) {
			return true;
		}
		return false;
	}
	
	private void changeAlgorithmSolutionTiers(Composition composition) {
		List<ServiceCandidate> serviceCandidates = 
			new LinkedList<ServiceCandidate>(
					composition.getServiceCandidatesList());						
		Collections.copy(serviceCandidates, 
				composition.getServiceCandidatesList());
		Composition newComposition = new Composition();
		newComposition.setServiceCandidateList(serviceCandidates);
		
		// Loop is needed when two compositions have the same utility value.
		for (AlgorithmSolutionTier tier : algorithmSolutionTiers) {
			if (tier.getServiceCompositionList().get(0).getUtility() == 
				newComposition.getUtility()) {
				tier.getServiceCompositionList().add(newComposition);
				return;
			}
		}
		
		if (numberOfRequestedResultTiers <= 
			algorithmSolutionTiers.size()) {
			for (int i = 0; i < algorithmSolutionTiers.size(); i++) {
				if (newComposition.getUtility() > algorithmSolutionTiers.
						get(i).getServiceCompositionList().get(0).
						getUtility()) {
					List<Composition> newTier = new LinkedList<Composition>();
					newTier.add(newComposition);
					algorithmSolutionTiers.add(
							i, new AlgorithmSolutionTier(newTier, i + 1));
					algorithmSolutionTiers.remove(
							algorithmSolutionTiers.size() - 1);
					break;
				}
			}
		}
		else {
			List<Composition> newTier = new LinkedList<Composition>();
			newTier.add(newComposition);
			int tierRank = 0;
			for (AlgorithmSolutionTier tier : algorithmSolutionTiers) {
				if (newComposition.getUtility() < 
						tier.getServiceCompositionList().get(0).getUtility()) {
					tierRank++;
				}
			}
			algorithmSolutionTiers.add(tierRank, new AlgorithmSolutionTier(
					newTier, tierRank + 1));
		}
	}
			
	
	// Print service classes and their service candidates.
	// Do not remove! Might be useful for tests!
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

	// GETTERS AND SETTERS
	public List<ServiceClass> getServiceClassesList() {
		return serviceClassesList;
	}
	public void setServiceClassesList(List<ServiceClass> serviceClassesList) {
		this.serviceClassesList = serviceClassesList;
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
	public int getWorkPercentage() {
		return workPercentage;
	}
	public double getOptimalUtility(){
		return algorithmSolutionTiers.get(0).
				getServiceCompositionList().get(0).getUtility();
	}
}
