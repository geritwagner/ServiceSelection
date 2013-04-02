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
//	private Composition optimalComposition = null;
	
	private int numberOfRequestedResultTiers;
	
	private long runtime = 0;

	
	// CONSTRUCTORS
	public AnalyticAlgorithm() {
		
	}
	
	public AnalyticAlgorithm(List<ServiceClass> serviceClassesList, 
			List<ServiceCandidate> serviceCandidatesList, 
			Map<String, Constraint> constraintsMap, 
			int numberOfRequestedResultTiers) {
		this.serviceClassesList = serviceClassesList;
		this.serviceCandidatesList = serviceCandidatesList;
		this.constraintsMap = constraintsMap;
		this.numberOfRequestedResultTiers = numberOfRequestedResultTiers;
//		this.optimalComposition = new Composition(
//				new LinkedList<ServiceCandidate>(), new QosVector(), 0.0);
	}
	
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
		runtime = System.currentTimeMillis() - runtime;
//		buildSolutionTiers();
	}
	
	// ENUMERATION
	// TODO: [MAYBE] DO NOT CONSIDER PATHS THAT VIOLATE ANY CONSTRAINTS
	//		 ANYMORE. (OPTIMIZATION THAT COULD RESULT IN SOME WORK! AND 
	//		 ACTUALLY, IT WOULDN'T BE A COMPLETE ENUMERATION ANYMORE!)
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
	
	private void changeAlgorithmSolutionTiers(Composition composition) {
		List<ServiceCandidate> serviceCandidates = 
			new LinkedList<ServiceCandidate>(
					composition.getServiceCandidatesList());						
		Collections.copy(serviceCandidates, 
				composition.getServiceCandidatesList());
		Composition newComposition = new Composition();
		newComposition.setServiceCandidateList(serviceCandidates);
		newComposition.setQosVectorAggregated(new QosVector(
				composition.getQosVectorAggregated().getCosts(),
				composition.getQosVectorAggregated().getResponseTime(),
				composition.getQosVectorAggregated().getAvailability()));
		newComposition.computeUtilityValue();
		
		// TODO: Was macht diese for-Schleife? Die if-Abfrage lieferte in 
		//		 keinem meiner Tests ein "true".
		//	-> wird benötigt, damit im sehr unwahrscheinlichen Fall, 
		//	   dass 2 Utility-Werte gleich sind, die zugehörigen 
		//     Kompositionen in den gleichen Tier eingeordnet werden
		for (AlgorithmSolutionTier tier : algorithmSolutionTiers) {
			if (tier.getServiceCompositionList().get(0).getUtility() == 
				newComposition.getUtility()) {
				tier.getServiceCompositionList().add(newComposition);
				return;
			}
		}
		if (numberOfRequestedResultTiers <= 
			algorithmSolutionTiers.size()) {
			for (int count = 0; 
			count < algorithmSolutionTiers.size(); count++) {
				if (newComposition.getUtility() > algorithmSolutionTiers.
						get(count).getServiceCompositionList().get(0).
						getUtility()) {
					List<Composition> newTier = new LinkedList<Composition>();
					newTier.add(newComposition);
					algorithmSolutionTiers.add(count, 
							new AlgorithmSolutionTier(newTier, count + 1));
					algorithmSolutionTiers.remove(
							algorithmSolutionTiers.size() - 1);
					break;
				}
			}
		}
		else {
			List<Composition> newTier = new LinkedList<Composition>();
			newTier.add(newComposition);
			algorithmSolutionTiers.add(new AlgorithmSolutionTier(newTier, 
					algorithmSolutionTiers.size() + 1));
		}
	}
			
	
	// TODO: Braucht man das noch?
	// -> zu Testzwecken noch nicht entfernen!
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
