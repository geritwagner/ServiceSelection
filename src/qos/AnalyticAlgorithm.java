package qos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnalyticAlgorithm {
	
	private static List<ServiceClass> serviceClassesList;
	private static Map<String, Constraint> constraintsMap;
	
	private static List<Composition> compositionsList = new LinkedList<Composition>();
	private static List<AlgorithmSolutionTier> algorithmSolutionTiers = 
		new LinkedList<AlgorithmSolutionTier>();
	
	private static int numberOfRequestedResultTiers = 1;	
	private static long runtime = 0;	
	

	
	
	public static void initAnalytic(List<ServiceClass> setServiceClassesList, 
			Map<String, Constraint> setConstraintsMap) {
		serviceClassesList = setServiceClassesList;
		constraintsMap = setConstraintsMap;
		compositionsList = new LinkedList<Composition>();
		algorithmSolutionTiers = new LinkedList<AlgorithmSolutionTier>();
		runtime = 0;
	}	
	
	
	public static void startInBenchmarkMode() {
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
	private static void doCompleteEnumeration(Composition composition, 
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
	
	private static Composition forward(Composition composition, 
			int serviceClassNumber, int serviceCandidateNumber) {
		ServiceClass serviceClass = serviceClassesList.get(serviceClassNumber);
		ServiceCandidate serviceCandidate = 
				serviceClass.getServiceCandidateList().get(
						serviceCandidateNumber);
		composition.addServiceCandidate(serviceCandidate);
		return composition;
	}
	
	private static Composition backward(Composition composition) {
		composition.removeServiceCandidate();
		return composition;
	}
	
	// Check is done by comparing the size of the composition with the number
	// of available service classes.
	private static boolean isComplete(Composition composition) {
		if (composition.getServiceCandidatesList().size() == 
				serviceClassesList.size()) {
			return true;
		}
		return false;
	}
	
	private static void changeAlgorithmSolutionTiers(Composition composition) {
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
	public static List<ServiceClass> getServiceClassesList() {
		return serviceClassesList;
	}
	public static void setServiceClassesList(List<ServiceClass> setServiceClassesList) {
		serviceClassesList = setServiceClassesList;
	}
	public static Map<String, Constraint> getConstraintList() {
		return constraintsMap;
	}
	public static void setConstraintList(Map<String, Constraint> setConstraintsMap) {
		constraintsMap = setConstraintsMap;
	}
	public static List<Composition> getCompositionsList() {
		return compositionsList;
	}
	public static void setCompositionsList(List<Composition> setCompositionsList) {
		compositionsList = setCompositionsList;
	}
	public static List<AlgorithmSolutionTier> getAlgorithmSolutionTiers() {
		return algorithmSolutionTiers;
	}
	public static void setAlgorithmSolutionTiers(
			List<AlgorithmSolutionTier> setAlgorithmSolutionTiers) {
		algorithmSolutionTiers = setAlgorithmSolutionTiers;
	}
	public static long getRuntime() {
		return runtime;
	}
	
	public static double getOptimalUtility(){
		if (algorithmSolutionTiers.isEmpty()) {
			return 0.0;
		}
		return algorithmSolutionTiers.get(0).
				getServiceCompositionList().get(0).getUtility();
	}
}
