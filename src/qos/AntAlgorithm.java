package qos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AntAlgorithm extends Algorithm {

	private List<ServiceClass> serviceClassesList;
	private List<ServiceCandidate> serviceCandidatesList;
	private Map<String, Constraint> constraintsMap;	
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
	private int workPercentage;

	public AntAlgorithm(List<ServiceClass> serviceClassesList,
			List<ServiceCandidate> serviceCandidatesList,
			Map<String, Constraint> constraintsMap,
			int iterations, int ants, double alpha, double beta, double dilution, double piInit) {
		this.serviceClassesList = new LinkedList<ServiceClass>(serviceClassesList);
		this.serviceCandidatesList = new LinkedList<ServiceCandidate>(serviceCandidatesList);
		Collections.copy(this.serviceClassesList, serviceClassesList);
		Collections.copy(this.serviceCandidatesList, serviceCandidatesList);	
		this.constraintsMap = constraintsMap;	

		optimalComposition = null;
		this.piInit = piInit;
		this.ants = ants;
		this.iterations = iterations;
		this.alpha = alpha;
		this.beta = beta;
		this.dilution = dilution;
	}

	public void start() {	
		runtime = System.currentTimeMillis();
		workPercentage = 0;
		initAlgo();

		// RUN ITERATIONS
		for (int i=1; i<=iterations; i++) {	
			doIteration();
			// PROGRESSBAR			
			workPercentage = (int) (100 * (i / iterations));
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
			optimalComposition = new Composition();
			optimalComposition.addServiceCandidate(new ServiceCandidate(0, 0, "keine L�sung", new QosVector()));	
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
			antCompositions.add(new Composition());
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
			if (composition.isWithinConstraints(constraintsMap)) {
				counter++;

				double utility = 0;
				for (int a=1; a < composition.getServiceCandidatesList().size()-1; a++) {	
					utility += composition.getServiceCandidatesList().get(a).getUtility();	
				}
				utility = utility / (composition.getServiceCandidatesList().size()-2);
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
			if (composition.isWithinConstraints(constraintsMap)) {
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
		// SET UTILITY OF START AND END NODE TO ONE
		nj[0] = 1;
		nj[serviceCandidatesList.size()-1] = 1;

		for (int i=1; i<serviceCandidatesList.size()-1; i++) {	
			nj[i] = serviceCandidatesList.get(i).getUtility();	
		}
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

	public int getWorkPercentage() {
		return workPercentage;
	}	

}