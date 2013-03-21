package qos;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

public class AnalyticAlgorithm {
	
	private List<ServiceClass> serviceClassesList;
	private List<ServiceCandidate> serviceCandidatesList;
	private Map<String, Constraint> constraintsMap;
	
	// TODO: REMOVE FLAGS IF NOT NEEDED.
//	private List<String> triedCompositions = new LinkedList<String>();
	
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
		// PRINT SERVICE CLASSES AND THEIR SERVICE CANDIDATES. 
		for (ServiceClass serviceClass : serviceClassesList) {
			System.out.println("\n" + serviceClass.getName());
			for (ServiceCandidate serviceCandidate : serviceClass.getServiceCandidateList()) {
				System.out.println(serviceCandidate.getName());
			}
		}
		System.out.println("\n\n");
		
		// DO COMPLETE ENUMERATION.
		for (int i = 0; i < serviceClassesList.get(0).getServiceCandidateList().size(); i++) {
			solve(new Composition(0, "Test", new LinkedList<ServiceCandidate>()), 0, i);
//			System.out.println(Math.round((
//			(double) (i + 1) / ((double) serviceClassesList.get(
//					0).getServiceCandidateList().size())) * 100));
			progressBar.setValue((int) Math.round((
					(double) (i + 1) / ((double) serviceClassesList.get(
							0).getServiceCandidateList().size())) * 100));
		}
		
		// TODO: REMOVE DEBUG OUTPUT.
//		System.out.println(triedCompositions.size());
	}
	
	// COMPLETE ENUMERATION.
	// TODO: - SEARCH FOR THE OPTIMAL COMPOSITION. [UTILITY FUNCTION NEEDED!]
	// 		 - [ONLY PRINT OUT THE OPTIMAL COMPOSITION.]
	//		 - SHOW COMPOSITIONS IN RESULT TABLE.
	//		 - STORE ALL COMPOSITIONS IN A LIST.
	//		 - DO NOT CONSIDER PATHS THAT VIOLATE ANY CONSTRAINTS ANYMORE.
	private void solve(Composition composition, int serviceClassNumber, int serviceCandidateNumber) {
		composition = forward(composition, serviceClassNumber, serviceCandidateNumber);
		if (composition != null) {
			if (isComplete(composition)) {
				
				// ONLY PRINT OUT VALID COMPOSITIONS.
				if (isWithinConstraints(composition)) {
					System.out.println(composition.getQosVectorAggregated());
				}
			}
			else {
				serviceClassNumber++;
				for (int i = 0; i < serviceClassesList.get(serviceClassNumber).getServiceCandidateList().size(); i++) {
					solve(composition, serviceClassNumber, i);
				}
			}
			composition = backward(composition);
			serviceClassNumber--;
		}
		return;
	}
	
	private boolean isWithinConstraints(Composition composition) {
		// TODO: HANDLE THE DIFFERENT CONSTRAINT VIOLATIONS IN A 
		//		 REASONABLE WAY.
		
		boolean isWithinConstraints = true;
		QosVector qosVector = composition.getQosVectorAggregated();
		if (qosVector.getPrice() 
				> constraintsMap.get(Constraint.PRICE).getValue()) {
			isWithinConstraints = false;
		}
		if (qosVector.getCosts() 
				> constraintsMap.get(Constraint.COSTS).getValue()) {
			isWithinConstraints = false;
		}
		if (qosVector.getResponseTime() 
				> constraintsMap.get(Constraint.RESPONSE_TIME).getValue()) {
			isWithinConstraints = false;
		}
		if (qosVector.getAvailability() 
				< constraintsMap.get(Constraint.AVAILABILITY).getValue()) {
			isWithinConstraints = false;
		}
		if (qosVector.getReliability() 
				< constraintsMap.get(Constraint.RELIABILITY).getValue()) {
			isWithinConstraints = false;
		}
		return isWithinConstraints;
	}
	
	private Composition forward(Composition composition, int serviceClassNumber, int serviceCandidateNumber) {
		ServiceClass serviceClass = serviceClassesList.get(serviceClassNumber);
		ServiceCandidate serviceCandidate = serviceClass.getServiceCandidateList().get(serviceCandidateNumber);
		composition.addServiceCandidate(serviceCandidate);
		
		// TODO: REMOVE FLAGS IF NOT NEEDED.
//		String flag = createFlag(composition);
//		if (! isNewComposition(flag)) {
//			return null;
//		}
//		triedCompositions.add(flag);
		
		return composition;
	}
	
	private Composition backward(Composition composition) {
		composition.removeServiceCandidate();
		return composition;
	}
	
	// CHECK IS DONE BY COMPARING THE SIZE OF THE COMPOSITION WITH THE NUMBER 
	// OF AVAILABLE SERVICE CLASSES.
	private boolean isComplete(Composition composition) {
		if (composition.getServiceCandidatesList().size() == serviceClassesList.size()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	// FLAGS ARE CREATED BY CONCATENATING THE ID'S OF THE USED SERVICE
	// CANDIDATES. MIGHT BE NOT SO GOOD, BUT WORKS.
//	private String createFlag(Composition composition) {
//		String flag = "";
//		for (ServiceCandidate serviceCandidate : composition.getServiceCandidatesList()) {
//			flag = flag + "-" + serviceCandidate.getServiceCandidateId();
//		}
//		return flag;
//	}
	
//	private boolean isNewComposition(String flag) {
//		for (String triedComposition : triedCompositions) {
//			if (flag.equals(triedComposition)) {
//				return false;
//			}
//		}
//		return true;
//	}
	
	
	// SIMPLE METHOD FOR CALCULATING THE NUMBER OF POSSIBLE PERMUTATIONS. 
	// MIGHT BE USEFUL.
//	private int getNumberOfPermutations() {
//		int permutations = 1;
//		for (ServiceClass serviceClass : serviceClassesList) {
//			permutations *= serviceClass.getSize();
//		}
//		System.out.println(permutations);
//		return permutations;
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
	
}
