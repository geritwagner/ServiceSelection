package qos;

import java.util.List;

public class AntAlgorithm {
	
	private List<ServiceClass> serviceClassesList;
	private List<ServiceCandidate> serviceCandidatesList;
	
	private double piInit;
	private QosVector optimalQos;
	private Composition optimalComposition;
	
		
	public AntAlgorithm(List<ServiceClass> serviceClassesList,
			List<ServiceCandidate> serviceCandidatesList) {
		this.serviceClassesList = serviceClassesList;
		this.serviceCandidatesList = serviceCandidatesList;
		
		piInit = 0.3;
	}
	
	
	public void start() {
		// PHEROMONE-MATRIX INITIALISIEREN
		double[][] pi = new double[serviceCandidatesList.size()+2][serviceCandidatesList.size()+2]; // INCLUDES START- AND END-NODE
		for (int i=0; i<pi.length; i++) {
			for (int j=0; j<pi[i].length; j++) {
				pi[i][j] = piInit;
			}
		}
	}

}
