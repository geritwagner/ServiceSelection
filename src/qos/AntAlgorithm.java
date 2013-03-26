package qos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

public class AntAlgorithm extends Algorithm {
	
	private List<ServiceClass> serviceClassesList;
	private List<ServiceCandidate> serviceCandidatesList;
	private Map<String, Constraint> constraintsMap;
		
	private double piInit;
	private int ants;
	private int iterations;
	private double alpha;
	private double beta;
	private double dilution;
	private QosVector optimalQos;
	private Composition optimalComposition;
	private double[] nj;
			
	public AntAlgorithm(List<ServiceClass> serviceClassesList,
			List<ServiceCandidate> serviceCandidatesList,
			Map<String, Constraint> constraintsMap) {
		this.serviceClassesList = new LinkedList<ServiceClass>(serviceClassesList);
		this.serviceCandidatesList = new LinkedList<ServiceCandidate>(serviceCandidatesList);
		Collections.copy(this.serviceClassesList, serviceClassesList);
		Collections.copy(this.serviceCandidatesList, serviceCandidatesList);		
		this.constraintsMap = constraintsMap;
				
		optimalComposition = null;
		piInit = 1;
		ants = 10;
		iterations = 100;
		alpha = 0;
		beta = 1;
		dilution = 0.1;
	}
		
	public void start(JProgressBar progressBar) {
		// ADD PSEUDO NODES AT THE BEGINNING AND AT THE END
		List<ServiceCandidate> tempServiceCandidateList = 
				new LinkedList<ServiceCandidate>();
		ServiceCandidate tempServiceCandidate = new ServiceCandidate(0, "StartServiceClass", 0, "S", new QosVector());
		tempServiceCandidateList.add(tempServiceCandidate);
		ServiceClass tempServiceClass = new ServiceClass(0, "StartServiceClass", tempServiceCandidateList);
		serviceCandidatesList.add(0, tempServiceCandidate);
		serviceClassesList.add(0, tempServiceClass);
		
		tempServiceCandidateList = new LinkedList<ServiceCandidate>();
		tempServiceCandidate = new ServiceCandidate(serviceClassesList.size(), 
				"EndServiceClass", serviceCandidatesList.size(), "T", new QosVector());
		tempServiceCandidateList.add(tempServiceCandidate);
		tempServiceClass = new ServiceClass(serviceClassesList.size(), 
				"EndServiceClass", tempServiceCandidateList);
		serviceCandidatesList.add(tempServiceCandidate);
		serviceClassesList.add(tempServiceClass);		
		
		// INITIALIZE PHEROMONE-MATRIX
		double[][] pi = new double[serviceCandidatesList.size()][serviceCandidatesList.size()];
		for (int i=0; i<pi.length; i++) {
			for (int j=0; j<pi[i].length; j++) {
				pi[i][j] = piInit;
			}
		}
		
		optimalQos = null;
		normalizeQos();
		for (double n : nj) {
			//System.out.println(n);
		}
		
		int counter = 0;
		
		// RUN ITERATIONS
		for (int i=1; i<=iterations; i++) {			
			List<Composition> antCompositions = new LinkedList<Composition>();
			for (int k=0; k<ants; k++) {
				int currentClass = 0;
				int currentService = 0;				
				antCompositions.add(new Composition(
						new LinkedList<ServiceCandidate>(), new QosVector(), 0.0));
				antCompositions.get(k).addServiceCandidate(serviceCandidatesList.get(currentService));
				while (currentClass != (serviceClassesList.size()-1)) {
					// CALCULATE PROBABILITY FOR ALL POSSIBLE SERVICES
					List<ServiceCandidate> nextServiceCandidatesList = 
							serviceClassesList.get(currentClass+1).getServiceCandidateList();
					double[] p = new double[nextServiceCandidatesList.size()];
					double nenner = 0;
					for (int x=0; x<nextServiceCandidatesList.size(); x++) {
						int nextID = nextServiceCandidatesList.get(x).getServiceCandidateId(); 
						nenner += Math.pow(pi[currentService][nextID], alpha) * Math.pow(nj[nextID], beta);
					}
					for (int x=0; x<nextServiceCandidatesList.size(); x++) {
						int nextID = nextServiceCandidatesList.get(x).getServiceCandidateId(); 
						p[x] = (Math.pow(pi[currentService][nextID], alpha) * Math.pow(nj[nextID], beta))
								/ nenner;
						//System.out.println("Iteration:"+i+",ant:"+k+",p["+currentService+"]["+nextID+"]:"+p[x]);
					}
					double randomNumber = Math.random();
					double temp = 0;
					for (int x=0; x<nextServiceCandidatesList.size(); x++) {
						temp += p[x];
						if (randomNumber <= temp) {
							currentService = nextServiceCandidatesList.get(x).getServiceCandidateId();
							break;
						}
					}
					antCompositions.get(k).addServiceCandidate(serviceCandidatesList.get(currentService));
					currentClass++;
				}
			}
			
			// LOOK FOR BEST COMPOSITION
			for (int k=0; k<ants; k++) {
				Composition composition = antCompositions.get(k);
				if (isWithinConstraints(composition)) {
					counter++;
					
					double utility = 0;
					for (ServiceCandidate sc : composition.getServiceCandidatesList()) {
						int id = sc.getServiceCandidateId();
						utility += nj[id];
					}
					composition.setUtility(utility);
					
					if ((optimalComposition == null)||
							(composition.getUtility() > optimalComposition.getUtility())) {
						optimalComposition = composition;
						System.out.println(optimalComposition.getServiceCandidatesAsString() +
								": "+optimalComposition.getUtility());
					}
				}
			}
			// PHEROMONE UPDATE FUNCTION
			double[][] deltaPi = new double[serviceCandidatesList.size()][serviceCandidatesList.size()];
			for (int a=0; a<deltaPi.length; a++) {
				for (int b=0; b<deltaPi[a].length; b++) {
					deltaPi[a][b] = 0;
				}
			}
			for (int k=0; k<ants; k++) {
				Composition composition = antCompositions.get(k);
				if (isWithinConstraints(composition)) {
					double ratio = composition.getUtility() / optimalComposition.getUtility();
					for (int a=0; a<composition.getServiceCandidatesList().size()-1; a++) {
						int currentID = composition.getServiceCandidatesList().get(a).getServiceCandidateId();
						int nextID = composition.getServiceCandidatesList().get(a+1).getServiceCandidateId();
						deltaPi[currentID][nextID] += ratio;
					}
					
				}
			}
			for (int a=0; a<pi.length; a++) {
				for (int b=0; b<pi[a].length; b++) {
					pi[a][b] = (1-dilution)*pi[a][b] + deltaPi[a][b];
				}
			}
			// PROGRESSBAR
			//progressBar.setValue((int) Math.round((double) (i + 1) / iterations));
		}
		
		System.out.println(counter);
	}
	
	public void normalizeQos() {
		QosVector max = new QosVector(0.0, 0.0, 0.0);
		for (ServiceCandidate serviceCandidate : serviceCandidatesList) {
			QosVector qos = serviceCandidate.getQosVector();
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
		QosVector min = new QosVector(100000.0, 100000.0, 1.0);
		for (ServiceCandidate serviceCandidate : serviceCandidatesList) {
			QosVector qos = serviceCandidate.getQosVector();
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
		
		nj = new double[serviceCandidatesList.size()];
		
		for (int i=0; i<serviceCandidatesList.size(); i++) {
			// (Q_Max - Q_i) / (Q_max - Q_min) * W		negative criteria
			// (Q_i - Q_min) / (Q_max - Q_min) * W		positive criteria
			QosVector qos = serviceCandidatesList.get(i).getQosVector();
			Constraint costs = constraintsMap.get(Constraint.COSTS);
			Constraint responseTime = constraintsMap.get(Constraint.RESPONSE_TIME);
			Constraint availability = constraintsMap.get(Constraint.AVAILABILITY);
			nj[i] = (((max.getCosts() - qos.getCosts()) / 
					(max.getCosts() - min.getCosts())) * costs.getWeight()/100) + 
					(((max.getResponseTime() - qos.getResponseTime()) / 
					(max.getResponseTime() - min.getResponseTime())) * responseTime.getWeight()/100) + 
					(((qos.getAvailability() - min.getAvailability()) / 
					(max.getAvailability() - min.getAvailability())) * availability.getWeight()/100);			
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

	// GETTER AND SETTER
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

	public Map<String, Constraint> getConstraintsMap() {
		return constraintsMap;
	}

	public void setConstraintsMap(Map<String, Constraint> constraintsMap) {
		this.constraintsMap = constraintsMap;
	}

	public double getPiInit() {
		return piInit;
	}

	public void setPiInit(double piInit) {
		this.piInit = piInit;
	}

	public int getAnts() {
		return ants;
	}

	public void setAnts(int ants) {
		this.ants = ants;
	}

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public double getDilution() {
		return dilution;
	}

	public void setDilution(double dilution) {
		this.dilution = dilution;
	}

	public QosVector getOptimalQos() {
		return optimalQos;
	}

	public void setOptimalQos(QosVector optimalQos) {
		this.optimalQos = optimalQos;
	}

	public Composition getOptimalComposition() {
		return optimalComposition;
	}

	public void setOptimalComposition(Composition optimalComposition) {
		this.optimalComposition = optimalComposition;
	}

	public double[] getNj() {
		return nj;
	}

	public void setNj(double[] nj) {
		this.nj = nj;
	}

	
	
}
