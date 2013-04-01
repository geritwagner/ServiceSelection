package qos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

public class AntAlgorithm extends Algorithm {
	
	private List<ServiceClass> serviceClassesList;
	private List<ServiceCandidate> serviceCandidatesList;
	
	// TODO: Diese 3 Variablen sollten nach Erledigung der zwei unten 
	//		 eingefügten TODOs nicht mehr benötigt werden.
	private Map<String, Constraint> constraintsMap;
	private QosVector qosMax;
	private QosVector qosMin;
	
	private List<AlgorithmSolutionTier> algorithmSolutionTiers = 
			new LinkedList<AlgorithmSolutionTier>();
		
	private double piInit;
	private int ants;
	private int iterations;
	private double alpha;
	private double beta;
	private double dilution;	
	private Composition optimalComposition;
	private double[] nj;
	private double[][] pi;
	int counter = 0;
	
	private long runtime = 0;
			
	public AntAlgorithm(List<ServiceClass> serviceClassesList,
			List<ServiceCandidate> serviceCandidatesList,
			Map<String, Constraint> constraintsMap, QosVector max, QosVector min,
			int iterations, int ants, double alpha, double beta, double dilution, double piInit) {
		this.serviceClassesList = new LinkedList<ServiceClass>(serviceClassesList);
		this.serviceCandidatesList = new LinkedList<ServiceCandidate>(serviceCandidatesList);
		Collections.copy(this.serviceClassesList, serviceClassesList);
		Collections.copy(this.serviceCandidatesList, serviceCandidatesList);		
		this.constraintsMap = constraintsMap;
		this.qosMax = max;
		this.qosMin = min;		
				
		optimalComposition = null;
		this.piInit = piInit;
		this.ants = ants;
		this.iterations = iterations;
		this.alpha = alpha;
		this.beta = beta;
		this.dilution = dilution;
	}
		
	public void start(JProgressBar progressBar) {			
		runtime = System.currentTimeMillis();
		initAlgo();
				
		// RUN ITERATIONS
		for (int i=1; i<=iterations; i++) {	
			doIteration();
			
			// PROGRESSBAR
			//progressBar.setValue((int) Math.round((double) (i + 1) / iterations));
		}	
		runtime = System.currentTimeMillis() - runtime;
		if (optimalComposition != null) {
			List<ServiceCandidate> sCList = new LinkedList<ServiceCandidate>();
			for (int i=1; i<optimalComposition.getServiceCandidatesList().size()-1; i++) {
				sCList.add(optimalComposition.getServiceCandidatesList().get(i));
			}		
			optimalComposition.setServiceCandidateList(sCList);
		}
		else {
			optimalComposition = new Composition(new LinkedList<ServiceCandidate>(), new QosVector(), 0.0);
			optimalComposition.addServiceCandidate(new ServiceCandidate(0, 0, "keine Lösung", new QosVector()));			
		}		
		buildSolutionTiers();
		System.out.println(counter);
	}
	
	private void initAlgo() {		
		// ADD PSEUDO NODES AT THE BEGINNING AND AT THE END
		List<ServiceCandidate> tempServiceCandidateList = 
				new LinkedList<ServiceCandidate>();
		ServiceCandidate tempServiceCandidate = new ServiceCandidate(0, 0, "S", new QosVector());
		tempServiceCandidateList.add(tempServiceCandidate);
		ServiceClass tempServiceClass = new ServiceClass(0, "StartServiceClass", tempServiceCandidateList);
		serviceCandidatesList.add(0, tempServiceCandidate);
		serviceClassesList.add(0, tempServiceClass);
		
		tempServiceCandidateList = new LinkedList<ServiceCandidate>();
		tempServiceCandidate = new ServiceCandidate(serviceClassesList.size(), 
				serviceCandidatesList.size(), "T", new QosVector());
		tempServiceCandidateList.add(tempServiceCandidate);
		tempServiceClass = new ServiceClass(serviceClassesList.size(), 
				"EndServiceClass", tempServiceCandidateList);
		serviceCandidatesList.add(tempServiceCandidate);
		serviceClassesList.add(tempServiceClass);		
		
		// INITIALIZE PHEROMONE-MATRIX
		pi = new double[serviceCandidatesList.size()][serviceCandidatesList.size()];
		for (int i=0; i<pi.length; i++) {
			for (int j=0; j<pi[i].length; j++) {
				pi[i][j] = piInit;
			}
		}
		
		normalizeQos();
	}
	
	private void doIteration() {
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
				for (int a=1; a < composition.getServiceCandidatesList().size()-1; a++) {
					int id = composition.getServiceCandidatesList().get(a).getServiceCandidateId();
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
		
	
	}
	
	private void normalizeQos() {
		// CONSIDER START AND END NODE
		nj = new double[serviceCandidatesList.size()];
		// SET UTILITY OF START AND END NODE TO NULL
		nj[0] = 1;
		nj[serviceCandidatesList.size()-1] = 1;
		
		for (int i=1; i<serviceCandidatesList.size()-1; i++) {
			// TODO: Vorschlag, wie ihr die Utility-Werte nutzen könnt. Müsste 
			//		 so funktionieren. Evtl. könnt ihr euch ja sogar das 
			//		 Kopieren ins Array sparen. Das könntet ihr euch noch 
			//		 anschauen.
			nj[i] = serviceCandidatesList.get(i).getUtility();
			
			
			
			// (Q_Max - Q_i) / (Q_max - Q_min) * W		negative criteria
			// (Q_i - Q_min) / (Q_max - Q_min) * W		positive criteria
//			QosVector qos = serviceCandidatesList.get(i).getQosVector();
//			Constraint costs = constraintsMap.get(Constraint.COSTS);
//			Constraint responseTime = constraintsMap.get(Constraint.RESPONSE_TIME);
//			Constraint availability = constraintsMap.get(Constraint.AVAILABILITY);
//			nj[i] = (((qosMax.getCosts() - qos.getCosts()) / 
//					(qosMax.getCosts() - qosMin.getCosts())) * costs.getWeight()/100) + 
//					(((qosMax.getResponseTime() - qos.getResponseTime()) / 
//					(qosMax.getResponseTime() - qosMin.getResponseTime())) * responseTime.getWeight()/100) + 
//					(((qos.getAvailability() - qosMin.getAvailability()) / 
//					(qosMax.getAvailability() - qosMin.getAvailability())) * availability.getWeight()/100);			
		}
	}
	
	// TODO: Funktion aus Klasse Composition verwenden!
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
	
	private void buildSolutionTiers() {
		List<Composition> requestedCompositions = 
				new LinkedList<Composition>();
		requestedCompositions.add(optimalComposition);
		algorithmSolutionTiers.add(new AlgorithmSolutionTier(
				(LinkedList<Composition>) 
				requestedCompositions, 1));
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

	public void setQosMax(QosVector qosMax) {
		this.qosMax = qosMax;
	}
	
	public void setQosMin(QosVector qosMin) {
		this.qosMin = qosMin;
	}

	public long getRuntime() {
		return runtime;
	}

	public List<AlgorithmSolutionTier> getAlgorithmSolutionTiers() {
		return algorithmSolutionTiers;
	}

	public void setAlgorithmSolutionTiers(
			List<AlgorithmSolutionTier> algorithmSolutionTiers) {
		this.algorithmSolutionTiers = algorithmSolutionTiers;
	}	
	
}
