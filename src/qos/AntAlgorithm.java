package qos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AntAlgorithm extends Algorithm{

	private static List<ServiceClass> serviceClassesList;
	private static List<ServiceCandidate> serviceCandidatesList;
	private static Map<String, Constraint> constraintsMap;	
	private static List<AlgorithmSolutionTier> algorithmSolutionTiers =
			new LinkedList<AlgorithmSolutionTier>();

	private static double piInit;
	private static int ants;
	private static int iterations;
	private static double alpha;
	private static double beta;
	private static double dilution;	
	private static Composition optimalComposition;
	private static double[] nj;
	private static double[][] pi;		
	// variant: Ant System = 1, Ant Colony System = 2, MAX-MIN Ant System = 3
	// (Qiqing et al. 2009) = 4, Convergent Variant = 5, (Li und Yan-xiang 2011) = 6	
	private static int variant = 4;
	private static boolean convergent = false;
	private static double lambda = 0.4;
	private static double chaosFactor = 4;
	private static double piMax = 1;
	private static double piMin = 0;	

	private static long runtime = 0;
	

	public static void setParamsAntAlgorithm(List<ServiceClass> setServiceClassesList,
			List<ServiceCandidate> setServiceCandidatesList, Map<String, Constraint> setConstraintsMap, int setVariant,
			int setIterations, int setAnts, double setAlpha, double setBeta, double setDilution, double setPiInit) {
		
		serviceClassesList = new LinkedList<ServiceClass>(setServiceClassesList);
		serviceCandidatesList = new LinkedList<ServiceCandidate>(setServiceCandidatesList);
		Collections.copy(serviceClassesList, setServiceClassesList);
		Collections.copy(serviceCandidatesList, setServiceCandidatesList);	
		constraintsMap = setConstraintsMap;	

		optimalComposition = null;
		variant = setVariant;
		piInit = setPiInit;
		ants = setAnts;
		iterations = setIterations;
		alpha = setAlpha;
		beta = setBeta;
		dilution = setDilution;
		nj = null;
		pi = null;
		convergent = false;
		lambda = 0.4;
		chaosFactor = 4;
		piMax = 1;
		piMin = 0;
	}
	

	public static void start() {

		runtime = System.nanoTime();		
		initAlgo();
				
		if (variant == 5) {
			int it = 1;
			while (!convergent && (it < 10000 )) {
				doIterationV5();
				it++;
			}						
		} else if (variant == 6) {
			int it = 1;			
			while (!convergent && (it < 10000 )) {
				doIterationV6();								
				it++;
			}			
		} else {
			// RUN ITERATIONS
			for (int i=1; i<=iterations; i++) {
				if (variant == 1) {
					doIteration();
				} else if (variant == 2) {
					doIterationV2();
				} else if (variant == 3) {
					if (optimalComposition != null) {
						piMax = optimalComposition.getUtility() / dilution;
					} else {
						piMax = 0.6 / dilution;
					}					
					piMin = piMax / (double) serviceClassesList.size();
					piInit = piMax;					
					doIterationV3();
				} else if (variant == 4) {					
					doIterationV4();
				}												
			}	
		}
		
		runtime = System.nanoTime() - runtime;
		if (optimalComposition != null) {
			List<ServiceCandidate> sCList = new LinkedList<ServiceCandidate>();
			for (int i=1; i<optimalComposition.getServiceCandidatesList().size()-1; i++) {
				sCList.add(optimalComposition.getServiceCandidatesList().get(i));
			}	
			optimalComposition.setServiceCandidateList(sCList);
			buildSolutionTiers();
		}
		else {
			optimalComposition = new Composition();
			optimalComposition.addServiceCandidate(new ServiceCandidate(0, 0, "keine Lösung", new QosVector()));	
		}
		
	}

	private static void initAlgo() {
		
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
		
		initPheromoneMatrix();
		setUtilityArray();
	}
	
	private static void initPheromoneMatrix() {
		// INITIALIZE PHEROMONE-MATRIX
		pi = new double[serviceCandidatesList.size()][serviceCandidatesList.size()];
		for (int i=0; i<pi.length; i++) {
			for (int j=0; j<pi[i].length; j++) {
				pi[i][j] = piInit;
			}
		}
	}
	
	private static void setUtilityArray() {
		// CONSIDER START AND END NODE
		nj = new double[serviceCandidatesList.size()];
		// SET UTILITY OF START AND END NODE TO ONE
		nj[0] = 1;
		nj[serviceCandidatesList.size()-1] = 1;

		for (int i=1; i<serviceCandidatesList.size()-1; i++) {	
			nj[i] = serviceCandidatesList.get(i).getUtility();	
		}
	}	

	private static void buildSolutionTiers() {
		List<Composition> requestedCompositions =
				new LinkedList<Composition>();
		requestedCompositions.add(optimalComposition);
		algorithmSolutionTiers.add(new AlgorithmSolutionTier(
				(LinkedList<Composition>)
				requestedCompositions, 1));
	}
	
	/*
	 *  ANT SYSTEM
	 */

	private static void doIteration() {
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
				
				double utility = 0;
				for (int a=1; a < composition.getServiceCandidatesList().size()-1; a++) {	
					utility += composition.getServiceCandidatesList().get(a).getUtility();	
				}
				utility = utility / (composition.getServiceCandidatesList().size()-2);
				composition.setUtility(utility);

				if ((optimalComposition == null)||
						(composition.getUtility() > optimalComposition.getUtility())) {
					optimalComposition = composition;					
					//System.out.println(optimalComposition.getServiceCandidatesAsString() +
					//		": "+optimalComposition.getUtility());
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
			// IN THIS VARIANT COMPOSITIONS WHICH ARE OUT OF CONSTRAINTS ARE NOT CONSIDERED			
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
	
	/*
	 *  ANT COLONY SYSTEM
	 */
	
	private static void doIterationV2() {
		List<Composition> antCompositions = new LinkedList<Composition>();
		double q0 = 0.5;
		for (int k=0; k<ants; k++) {
			int currentClass = 0;
			int currentService = 0;				
			antCompositions.add(new Composition());
			antCompositions.get(k).addServiceCandidate(serviceCandidatesList.get(currentService));
			while (currentClass != (serviceClassesList.size()-1)) {
				// CALCULATE PROBABILITY FOR ALL POSSIBLE SERVICES
				int actualService = currentService;
				List<ServiceCandidate> nextServiceCandidatesList =
						serviceClassesList.get(currentClass+1).getServiceCandidateList();
				double[] value = new double[nextServiceCandidatesList.size()];
				for (int x=0; x<nextServiceCandidatesList.size(); x++) {
					int nextID = nextServiceCandidatesList.get(x).getServiceCandidateId();
					value[x] = pi[currentService][nextID] * Math.pow(nj[nextID], beta);
				}
				double q = Math.random();
				if (q <= q0) {
					// CHOOSE HIGHEST UTILITY
					int y = 0;
					double use = 0;
					for (int x=0; x<value.length; x++) {
						if (value[x] > use) {
							use = value[x];
							y = x;
						}
					}
					currentService = nextServiceCandidatesList.get(y).getServiceCandidateId();
				} else {
					double[] p = new double[nextServiceCandidatesList.size()];
					double nenner = 0;
					for (int x=0; x<nextServiceCandidatesList.size(); x++) {
						int nextID = nextServiceCandidatesList.get(x).getServiceCandidateId();
						nenner += pi[currentService][nextID] * Math.pow(nj[nextID], beta);
					}
					for (int x=0; x<nextServiceCandidatesList.size(); x++) {
						int nextID = nextServiceCandidatesList.get(x).getServiceCandidateId();
						p[x] = pi[currentService][nextID] * Math.pow(nj[nextID], beta)
								/ nenner;						
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
				}				
				
				antCompositions.get(k).addServiceCandidate(serviceCandidatesList.get(currentService));
				currentClass++;
				// IMMEDIATE PHEROMONE UPDATE				
				pi[actualService][currentService] = (1-dilution)*pi[actualService][currentService] + piInit*dilution;
				//System.out.println("pi["+actualService+"]["+currentService+"]: "+pi[actualService][currentService]);
			}
		}

		// LOOK FOR BEST COMPOSITION
		for (int k=0; k<ants; k++) {
			Composition composition = antCompositions.get(k);
			if (composition.isWithinConstraints(constraintsMap)) {
				
				double utility = 0;
				for (int a=1; a < composition.getServiceCandidatesList().size()-1; a++) {	
					utility += composition.getServiceCandidatesList().get(a).getUtility();	
				}
				utility = utility / (composition.getServiceCandidatesList().size()-2);
				composition.setUtility(utility);

				if ((optimalComposition == null)||
						(composition.getUtility() > optimalComposition.getUtility())) {
					optimalComposition = composition;					
					//System.out.println(optimalComposition.getServiceCandidatesAsString() +
					//		": "+optimalComposition.getUtility());
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
		
		boolean pheromomeAlreadySet = false;
		for (int k=0; k<ants; k++) {
			Composition composition = antCompositions.get(k);			
			if (composition.isWithinConstraints(constraintsMap) && 
					composition.getUtility() == optimalComposition.getUtility() && !pheromomeAlreadySet) {
				double ratio = composition.getUtility();
				for (int a=0; a<composition.getServiceCandidatesList().size()-1; a++) {
					int currentID = composition.getServiceCandidatesList().get(a).getServiceCandidateId();
					int nextID = composition.getServiceCandidatesList().get(a+1).getServiceCandidateId();
					deltaPi[currentID][nextID] += ratio;
					pi[currentID][nextID] = (1-dilution)*pi[currentID][nextID] + deltaPi[currentID][nextID]*dilution;
				}				
				pheromomeAlreadySet = true;
			}
		}			
	}
	
	/*
	 *  MAX-MIN ANT SYSTEM
	 */	
	
	private static void doIterationV3() {
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
				
				double utility = 0;
				for (int a=1; a < composition.getServiceCandidatesList().size()-1; a++) {	
					utility += composition.getServiceCandidatesList().get(a).getUtility();	
				}
				utility = utility / (composition.getServiceCandidatesList().size()-2);
				composition.setUtility(utility);

				if ((optimalComposition == null)||
						(composition.getUtility() > optimalComposition.getUtility())) {
					optimalComposition = composition;					
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
		boolean pheromomeAlreadySet = false;
		for (int k=0; k<ants; k++) {
			Composition composition = antCompositions.get(k);			
			if (composition.isWithinConstraints(constraintsMap) && 
					composition.getUtility() == optimalComposition.getUtility() && !pheromomeAlreadySet) {
				double ratio = composition.getUtility();
				for (int a=0; a<composition.getServiceCandidatesList().size()-1; a++) {
					int currentID = composition.getServiceCandidatesList().get(a).getServiceCandidateId();
					int nextID = composition.getServiceCandidatesList().get(a+1).getServiceCandidateId();
					deltaPi[currentID][nextID] += ratio;					
				}				
				pheromomeAlreadySet = true;
			}
		}	
		for (int a=0; a<pi.length; a++) {
			for (int b=0; b<pi[a].length; b++) {
				pi[a][b] = (1-dilution)*pi[a][b] + deltaPi[a][b];
				if (pi[a][b] < piMin) {
					pi[a][b] = piMin;
				} else if (pi[a][b] > piMax) {
					pi[a][b] = piMax;
				}
				//System.out.println("pi["+a+"]["+b+"]: "+pi[a][b]);
			}
		}		
	}
	

	/*
	 *  (Qiqing et al. 2009)
	 */

	private static void doIterationV4() {
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
		boolean newSolution = false;
		for (int k=0; k<ants; k++) {
			Composition composition = antCompositions.get(k);
			if (composition.isWithinConstraints(constraintsMap)) {

				double utility = 0;
				for (int a=1; a < composition.getServiceCandidatesList().size()-1; a++) {	
					utility += composition.getServiceCandidatesList().get(a).getUtility();	
				}
				utility = utility / (composition.getServiceCandidatesList().size()-2);
				composition.setUtility(utility);

				if ((optimalComposition == null)||
						(composition.getUtility() > optimalComposition.getUtility())) {
					optimalComposition = composition;
					newSolution = true;					
				}
			}
		}
		if (newSolution) {
			// RESET PHEROMONE-MATRIX
			initPheromoneMatrix();			
		}
		else {
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
					double ratio = composition.getUtility();
					for (int a=0; a<composition.getServiceCandidatesList().size()-1; a++) {
						int currentID = composition.getServiceCandidatesList().get(a).getServiceCandidateId();
						int nextID = composition.getServiceCandidatesList().get(a+1).getServiceCandidateId();
						deltaPi[currentID][nextID] += ratio;
					}
				}
			}
			for (int a=0; a<pi.length; a++) {
				for (int b=0; b<pi[a].length; b++) {
					pi[a][b] = (1-dilution)*pi[a][b] + deltaPi[a][b]*dilution;
				}
			}
		}		
	}

	/*
	 *  CONVERGENT VARIANT
	 */

	private static void doIterationV5() {
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
		convergent = true;
		double commonUtility = 0;
		for (int k=0; k<ants; k++) {
			Composition composition = antCompositions.get(k);
			double utility = 0;
			for (int a=1; a < composition.getServiceCandidatesList().size()-1; a++) {	
				utility += composition.getServiceCandidatesList().get(a).getUtility();	
			}
			utility = utility / (composition.getServiceCandidatesList().size()-2);
			composition.setUtility(utility);

			if (commonUtility < 0.01) {
				commonUtility = composition.getUtility();
			}
			if ((commonUtility != composition.getUtility()) || 
					(!composition.isWithinConstraints(constraintsMap))) {
				convergent = false;
			}
			if (composition.isWithinConstraints(constraintsMap)) {

				if ((optimalComposition == null)||
						(composition.getUtility() > optimalComposition.getUtility())) {
					optimalComposition = composition;					
					//System.out.println(optimalComposition.getServiceCandidatesAsString() +
					//		": "+optimalComposition.getUtility());
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

	/*
	 *  (Li und Yan-xiang 2011)
	 */
	
	private static void doIterationV6() {
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
		convergent = true;
		double commonUtility = 0;
		for (int k=0; k<ants; k++) {
			Composition composition = antCompositions.get(k);
			double utility = 0;
			for (int a=1; a < composition.getServiceCandidatesList().size()-1; a++) {	
				utility += composition.getServiceCandidatesList().get(a).getUtility();	
			}
			utility = utility / (composition.getServiceCandidatesList().size()-2);
			composition.setUtility(utility);
			
			if (commonUtility < 0.01) {
				commonUtility = composition.getUtility();
			}
			if ((commonUtility != composition.getUtility()) || 
					(!composition.isWithinConstraints(constraintsMap))) {
				convergent = false;
			}
			if (composition.isWithinConstraints(constraintsMap)) {
				
				if ((optimalComposition == null)||
						(composition.getUtility() > optimalComposition.getUtility())) {
					optimalComposition = composition;								
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
				lambda = chaosFactor * lambda * (1 - lambda);
				pi[a][b] = (1-dilution)*pi[a][b] + deltaPi[a][b] + lambda;
			}
		}		
	}
	
	

	// GETTER AND SETTER
	public static List<ServiceClass> getServiceClassesList() {
		return serviceClassesList;
	}

	public static void setServiceClassesList(List<ServiceClass> setserviceClassesList) {
		serviceClassesList = setserviceClassesList;
	}

	public static List<ServiceCandidate> getServiceCandidatesList() {
		return serviceCandidatesList;
	}

	public static void setServiceCandidatesList(
			List<ServiceCandidate> setserviceCandidatesList) {
		serviceCandidatesList = setserviceCandidatesList;
	}

	public static Map<String, Constraint> getConstraintsMap() {
		return constraintsMap;
	}

	public static void setConstraintsMap(Map<String, Constraint> setconstraintsMap) {
		constraintsMap = setconstraintsMap;
	}

	public static double getPiInit() {
		return piInit;
	}

	public static void setPiInit(double setpiInit) {
		piInit = setpiInit;
	}

	public static int getAnts() {
		return ants;
	}

	public static void setAnts(int setants) {
		ants = setants;
	}

	public static int getIterations() {
		return iterations;
	}

	public static void setIterations(int setiterations) {
		iterations = setiterations;
	}

	public static double getAlpha() {
		return alpha;
	}

	public static void setAlpha(double setalpha) {
		alpha = setalpha;
	}

	public static double getBeta() {
		return beta;
	}

	public static void setBeta(double setbeta) {
		beta = setbeta;
	}

	public static double getDilution() {
		return dilution;
	}

	public static void setDilution(double setdilution) {
		dilution = setdilution;
	}	

	public static Composition getOptimalComposition() {
		return optimalComposition;
	}

	public static void setOptimalComposition(Composition setoptimalComposition) {
		optimalComposition = setoptimalComposition;
	}

	public static double[] getNj() {
		return nj;
	}

	public static void setNj(double[] setnj) {
		nj = setnj;
	}	

	public static long getRuntime() {
		return runtime;
	}

	public static double getOptimalUtility() {
		return optimalComposition.getUtility();
	}
	
	public static List<AlgorithmSolutionTier> getAlgorithmSolutionTiers() {
		return algorithmSolutionTiers;
	}

	public static void setAlgorithmSolutionTiers(
			List<AlgorithmSolutionTier> setalgorithmSolutionTiers) {
		algorithmSolutionTiers = setalgorithmSolutionTiers;
	}

}