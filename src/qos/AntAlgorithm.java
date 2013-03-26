package qos;

import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

public class AntAlgorithm extends Algorithm {
	
	private List<ServiceClass> serviceClassesList;
	private List<ServiceCandidate> serviceCandidatesList;
	private Map<String, Constraint> constraintsMap;
	
	private double piInit;
	private QosVector optimalQos;
	private Composition optimalComposition;
	
		
	public AntAlgorithm(List<ServiceClass> serviceClassesList,
			List<ServiceCandidate> serviceCandidatesList,
			Map<String, Constraint> constraintsMap) {
		this.serviceClassesList = serviceClassesList;
		this.serviceCandidatesList = serviceCandidatesList;
		this.constraintsMap = constraintsMap;
		
		piInit = 0.3;
	}
	
	
	public void start(JProgressBar progressBar) {
		// PHEROMONE-MATRIX INITIALISIEREN
		double[][] pi = new double[serviceCandidatesList.size()+2][serviceCandidatesList.size()+2]; // INCLUDES START- AND END-NODE
		for (int i=0; i<pi.length; i++) {
			for (int j=0; j<pi[i].length; j++) {
				pi[i][j] = piInit;
			}
		}
	}

}
