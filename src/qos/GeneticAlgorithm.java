package qos;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

public class GeneticAlgorithm extends Algorithm {
	
	private List<ServiceClass> serviceClassesList;
	private List<ServiceCandidate> serviceCandidatesList;
	private Map<String, Constraint> constraintsMap;
	
	private int numberOfRequestedResultTiers;
	private double initialPopulationSize;
	private int terminationCriteria;
	private int terminationCounter;
	
	private String geneticOperators;
	private String chosenTerminationMethod;
	
	private QosVector qosMax;
	private QosVector qosMin;
	
	
	
	private List<Composition> compositionsList = new LinkedList<Composition>();
	private List<AlgorithmSolutionTier> algorithmSolutionTiers = 
		new LinkedList<AlgorithmSolutionTier>();
	
	private long runtime = 0;
	
	public GeneticAlgorithm(List<ServiceClass> serviceClassesList, 
			List<ServiceCandidate> serviceCandidatesList, 
			Map<String, Constraint> constraintsMap, 
			int numberOfRequestedResultTiers, double initialPopulationSize,
			int terminationCriteria, String geneticOperators, 
			String chosenTerminationCriteria, QosVector qosMax,
			QosVector qosMin) {
		this.serviceClassesList = serviceClassesList;
		this.serviceCandidatesList = serviceCandidatesList;
		this.constraintsMap = constraintsMap;
		this.numberOfRequestedResultTiers = numberOfRequestedResultTiers;
		this.initialPopulationSize = initialPopulationSize;
		this.terminationCriteria = terminationCriteria;
		this.geneticOperators = geneticOperators;
		this.chosenTerminationMethod = chosenTerminationCriteria;
		this.qosMax = qosMax;
		this.qosMin = qosMin;
	}

	@Override
	public void start(JProgressBar progressBar) {
		List<Composition> population = generateInitialPopulation();
		runtime = System.currentTimeMillis();
		terminationCounter = terminationCriteria;
		
		while (terminationCounter > 0) {
//			printCurrentPopulation(population);
			List<Composition> oldPopulation = population;
			
			// MUTATION
			population = mutate(population);

			// RECOMBINATION
			if (geneticOperators.contains("One-Point")) {
				population = doOnePointCrossover(population);
			}
			else if (geneticOperators.contains("Two-Point")) {
				population = doTwoPointCrossover(population);
			}
			else if (geneticOperators.contains("Half-Uniform")) {
				// INSERT FUNCTION
			}
			else {
				// INSERT FUNCTION
			}
			
			// SELECTION
			population = doSelection(population);
			
			updateAlgorithmSolutionTiers(population);
//			printCurrentPopulation(population);
//			System.out.println();
			
			if (chosenTerminationMethod.contains("Iteration")) {
				terminationCounter--;
				if (terminationCounter < 1) {
					break;
				}
			}
			else if (chosenTerminationMethod.contains(
					"consecutive equal generations")) {
				if (hasPopulationChanged(oldPopulation, population)) {
					terminationCounter = terminationCriteria;
				}
				else {
					terminationCounter--;
					if (terminationCounter < 1) {
						break;
					}
				}
			}
			// TODO: IMPLEMENT MIN IMPROVEMENT METHOD
			else {
				
			}
		}
		runtime = System.currentTimeMillis() - runtime;
		for (AlgorithmSolutionTier tier : algorithmSolutionTiers) {
			for (Composition composition : tier.getServiceCompositionList()) {
				composition.computeUtilityValue();
			}
		}
		
	}
		
	// TODO: IF INITIALPOPULATIONSIZE > 50% 
	//        -> DO ENUMERATION AND SELECT RANDOMLY
	//        -> FIRST PART HAS TO BE OPTIMIZED
	private List<Composition> generateInitialPopulation() {
		
		// GET THE NUMBER OF ALL POSSIBLE COMPOSITIONS 
		// WITH RESPECT TO THE GIVEN CONSTRAINTS (NOT DONE)
		int maxSize = 1;
		for (int count = 0; count < serviceClassesList.size(); count++) {
			maxSize *= serviceClassesList.get(count).getSize();
		}
		// COMPUTE NUMBER OF REQUESTED COMPOSITIONS
		// INITIALPOPULATIONSIZE * RESULT FROM THAT LIST
		int sizeOfInitialPopulation = (int) Math.round(
				maxSize * initialPopulationSize / 100);
		// RANDOM SELECTION OF REQUESTED NUMBER OF COMPOSITIONS
		List<Composition> initialPopulation = new LinkedList<Composition>();
		for (int count = 0; count < sizeOfInitialPopulation; count++) {
			List<ServiceCandidate> chosenServiceCandidatesList = 
				new LinkedList<ServiceCandidate>();
			QosVector qosVector = new QosVector();
			for (int innerCount = 0; 
					innerCount < serviceClassesList.size(); innerCount++) {
				chosenServiceCandidatesList.add(serviceClassesList.get(
						innerCount).getServiceCandidateList().get(
								(int) (Math.random() * 
										serviceCandidatesList.size() / 
										serviceClassesList.size())));
				qosVector.addCosts(
						chosenServiceCandidatesList.get(
								innerCount).getQosVector().getCosts());
				qosVector.addResponseTime(
						chosenServiceCandidatesList.get(
								innerCount).getQosVector().getResponseTime());
				qosVector.addAvailability(
						chosenServiceCandidatesList.get(
								innerCount).getQosVector().getAvailability());
			}
			Composition composition = new Composition(
					chosenServiceCandidatesList, qosVector, 0.0);
			if (chosenServiceCandidatesList.contains(composition)) {
				count--;
			}
			else {
				initialPopulation.add(composition);
			}
		}
		return initialPopulation;
	}
	
	// CALCULATE FITNESS VALUE OF EACH OBJECT 
	// WITHIN START POPULATION
	private List<Composition> doSelection(
			List<Composition> population) {
		List<Composition> newPopulation = new LinkedList<Composition>();
		double[] fitnessArray = new double[population.size()];
		double fitnessSum = 0.0;
		// COMPUTE ALL FITNESS VALUES & THEIR SUM
		for (int count = 0; count < population.size(); count++) {	
			fitnessArray[count] = computeAggregatedFitness(
					population.get(count));
			fitnessSum += fitnessArray[count];
		}
		// DO SELECTION (SURVIVAL OF THE FITTEST)
		for (int count = 0; count < population.size(); count++) {
			double randomValue = Math.random();
			double distance = Double.MAX_VALUE;
			int selectionIndex = 0;
			for (int innerCount = 0; 
			innerCount < population.size(); innerCount++) {
				if (Math.abs(1 - (fitnessArray[innerCount] / fitnessSum) - 
						randomValue) < distance) {
					selectionIndex = innerCount;
					distance = Math.abs(1 - (fitnessArray[innerCount] / 
							fitnessSum) - randomValue);
				}
			}
			
			// VERSION WITH CONSTANT POPULATION SIZE
			newPopulation.add(population.get(selectionIndex));
			
			// ALTERNATIVELY: DECLINING POPULATION SIZE
//			boolean newComposition = true;
//			for (Composition composition : newPopulation) {
//				if (population.get(selectionIndex).equals(composition)) {
//					newComposition = false;
//					break;
//				}
//			}
//			if (newComposition) {
//				newPopulation.add(population.get(selectionIndex));
//			}
		}
		
		return newPopulation;
	}
	
	// MUTATION
	private List<Composition> mutate(List<Composition> initialPopulation) {
		for (int count = 0; count < initialPopulation.size(); count++) {
			for (int innerCount = 0; innerCount < 
			serviceClassesList.size(); innerCount++) {
				// PROBABILITY = (1 / NUMBER OF ALL SERVICE CLASSES)
				if (Math.random() < (1.0 / serviceClassesList.size())) {
					double random = Math.random();
					// AVOID GETTING MAX_SIZE (OUT OF BOUNDS)
					if (random * initialPopulation.size() == 
						initialPopulation.size()) {
						random -= 0.01;
					}
					// INSERT NEW RANDOMLY SELECTED SERVICE CANDIDATE
					// AND REMOVE OLD SERVICE CANDIDATE
					initialPopulation.get(count).getServiceCandidatesList().
					set(innerCount, initialPopulation.get((int)(random * 
							initialPopulation.size())).
							getServiceCandidatesList().get(innerCount));
				}
			}
		}
		return initialPopulation;
	}
	
	private List<Composition> doOnePointCrossover(
			List<Composition> population) {
		List<Composition> newPopulation = 
			new LinkedList<Composition>();
		while (population.size() > 0) {
			if (population.size() == 1) {
				newPopulation.add(population.get(0));
				break;
			}	
			Composition composition_1 = population.get(0);
			population.remove(composition_1);
			double random = Math.random();
			// AVOID GETTING MAX_SIZE (OUT OF BOUNDS)
			
			// SELECT 2ND COMPOSITION RANDOMLY
			Composition composition_2 = population.get(
					(int)(random * population.size()));
			population.remove(composition_2);
			List<ServiceCandidate> newServiceCandidateList_1 = 
				new LinkedList<ServiceCandidate>();
			List<ServiceCandidate> newServiceCandidateList_2 = 
				new LinkedList<ServiceCandidate>();
			random = Math.random();
			// AVOID GETTING MAX_SIZE (OUT OF BOUNDS)
			if (random * serviceClassesList.size() == 
				serviceClassesList.size()) {
				random -= 0.01;
			}
			// SELECT CROSSOVER POINT RANDOMLY
			int crossoverPoint = 
				(int) (Math.random() * serviceClassesList.size());
			// CROSS OVER THE CHOSEN COMPOSITIONS
			for (int count = 0; count < crossoverPoint; count++) {
				newServiceCandidateList_1.add(composition_1.
						getServiceCandidatesList().get(count));
				newServiceCandidateList_2.add(composition_2.
						getServiceCandidatesList().get(count));
			}
			for (int count = crossoverPoint; 
			count < serviceClassesList.size(); count++) {
				newServiceCandidateList_1.add(composition_2.
						getServiceCandidatesList().get(count));
				newServiceCandidateList_2.add(composition_1.
						getServiceCandidatesList().get(count));
			}
			composition_1.setServiceCandidateList(
					newServiceCandidateList_1);
			composition_2.setServiceCandidateList(
					newServiceCandidateList_2);
			newPopulation.add(composition_1);
			newPopulation.add(composition_2);
		}
		return newPopulation;
	}
	private List<Composition> doTwoPointCrossover(
			List<Composition> population) {
		List<Composition> newPopulation = 
			new LinkedList<Composition>();
		while (population.size() > 0) {
			if (population.size() == 1) {
				newPopulation.add(population.get(0));
				break;
			}	
			Composition composition_1 = population.get(0);
			population.remove(composition_1);
			double random = Math.random();
			// AVOID GETTING MAX_SIZE (OUT OF BOUNDS)
			if (random * serviceClassesList.size() == 
				serviceClassesList.size()) {
				random -= 0.01;
			}
			
			// SELECT 2ND COMPOSITION RANDOMLY
			Composition composition_2 = population.get(
					(int)(random * population.size()));
			population.remove(composition_2);
			List<ServiceCandidate> newServiceCandidateList_1 = 
				new LinkedList<ServiceCandidate>();
			List<ServiceCandidate> newServiceCandidateList_2 = 
				new LinkedList<ServiceCandidate>();

			// SELECT CROSSOVER POINT RANDOMLY
			int crossoverPoint = 
				(int) (Math.random() * serviceClassesList.size());
			int crossoverPoint_2 = 
				(int) (Math.random() * serviceClassesList.size());
			if (crossoverPoint == crossoverPoint_2) {
				if (crossoverPoint < serviceClassesList.size()) {
					crossoverPoint_2++;
				}
				else {
					crossoverPoint--;
				}
			}
			// CROSS OVER THE CHOSEN COMPOSITIONS
			for (int count = 0; count < crossoverPoint; count++) {
				newServiceCandidateList_1.add(composition_1.
						getServiceCandidatesList().get(count));
				newServiceCandidateList_2.add(composition_2.
						getServiceCandidatesList().get(count));
			}
			for (int count = crossoverPoint; 
			count < crossoverPoint_2; count++) {
				newServiceCandidateList_1.add(composition_2.
						getServiceCandidatesList().get(count));
				newServiceCandidateList_2.add(composition_1.
						getServiceCandidatesList().get(count));
			}
			for (int count = crossoverPoint_2; 
			count < serviceClassesList.size(); count++) {
				newServiceCandidateList_1.add(composition_1.
						getServiceCandidatesList().get(count));
				newServiceCandidateList_2.add(composition_2.
						getServiceCandidatesList().get(count));
			}
			composition_1.setServiceCandidateList(
					newServiceCandidateList_1);
			composition_2.setServiceCandidateList(
					newServiceCandidateList_2);
			newPopulation.add(composition_1);
			newPopulation.add(composition_2);
		}
		return newPopulation;
	}
	
	// TODO: IMPLEMENT RECOMBINATION METHODS
//	private List<Composition> doUniformCrossover(
//			List<Composition> population) {
//		return null;
//	}
//	private List<Composition> doHalfUniformCrossover(
//			List<Composition> population) {
//		return null;
//	}
	
	private double computeFitness(ServiceCandidate candidate) {
		double fitness = 0.0;
		if (constraintsMap.get(Constraint.COSTS) != null) {
			fitness += constraintsMap.get(
					Constraint.COSTS).getWeight() / 100 * 
					candidate.getQosVector().getCosts();
		}
		if (constraintsMap.get(Constraint.RESPONSE_TIME) != null) {
			fitness += constraintsMap.get(
					Constraint.RESPONSE_TIME).getWeight() / 100 * 
					candidate.getQosVector().getResponseTime();
		}
		if (fitness == 0.0) {
			fitness = 1.0;
		}
		if (constraintsMap.get(Constraint.AVAILABILITY) != null && 
				constraintsMap.get(
						Constraint.AVAILABILITY).getWeight() != 0.0) {
			fitness /= constraintsMap.get(
					Constraint.AVAILABILITY).getWeight() / 100 * 
					candidate.getQosVector().getAvailability();
		}
		return fitness;
	}
	
	private boolean hasPopulationChanged(
			List<Composition> oldPopulation, List<Composition> newPopulation) {
		for (int count = 0; count < oldPopulation.size(); count++) {
			if (!oldPopulation.get(count).equals(newPopulation.get(count))) {
				return true;
			}
		}
		return false;
	}
	
	// TODO: TIER BUILDING DEPENDS ON UTILITY VALUE (?)
	private void updateAlgorithmSolutionTiers(
			List<Composition> newPopulation) {
		for (Composition composition : newPopulation) {
			if (!composition.isWithinConstraints(constraintsMap)) {
				return;
			}
			composition.computeUtilityValue();
			boolean equalValues = false;
			int compositionRank = 0;
			if (algorithmSolutionTiers.size() == 0) {
				List<Composition> newTier = new LinkedList<Composition>();
				newTier.add(composition);
				algorithmSolutionTiers.add(compositionRank, 
						new AlgorithmSolutionTier(newTier, compositionRank));
				continue;
			}
			else if (algorithmSolutionTiers.size() < 
					numberOfRequestedResultTiers) {
				for (AlgorithmSolutionTier tier : algorithmSolutionTiers) {
					if (composition.getUtility() == tier.
							getServiceCompositionList().get(0).getUtility()) {
						tier.getServiceCompositionList().add(composition);
						equalValues = true;
						break;
					}
				}
				if (!equalValues) {
					List<Composition> newTier = new LinkedList<Composition>();
					newTier.add(composition);
					algorithmSolutionTiers.add(compositionRank, 
							new AlgorithmSolutionTier(
									newTier, compositionRank));
				}
				continue;
			}
			for (AlgorithmSolutionTier tier : algorithmSolutionTiers) {
				if (composition.getUtility() > 
						tier.getServiceCompositionList().get(0).getUtility()) {
					compositionRank++;
				}
				else if (composition.getUtility() > 
				tier.getServiceCompositionList().get(0).getUtility()) {
					tier.getServiceCompositionList().add(composition);
					equalValues = true;
				}
			}
			if (compositionRank < numberOfRequestedResultTiers && 
					!equalValues) {
				List<Composition> newTier = new LinkedList<Composition>();
				newTier.add(composition);
				algorithmSolutionTiers.add(compositionRank, 
						new AlgorithmSolutionTier(newTier, compositionRank));
				algorithmSolutionTiers.remove(
						algorithmSolutionTiers.size() - 1);
				
			}
		}
	}
	
	private double computeDistanceToConstraints(Composition composition) {
		double distance = 0.0;
		if (constraintsMap.get(Constraint.COSTS) != null &&  
				composition.getQosVectorAggregated().getCosts() > 
				constraintsMap.get(Constraint.COSTS).getValue()) {
			distance += 1.0;

		}
		if (constraintsMap.get(Constraint.RESPONSE_TIME) != null &&  
				composition.getQosVectorAggregated().getResponseTime() > 
				constraintsMap.get(Constraint.RESPONSE_TIME).getValue()) {
			distance += 1.0;
		}

		if (constraintsMap.get(Constraint.AVAILABILITY) != null &&  
				composition.getQosVectorAggregated().getAvailability() > 
				constraintsMap.get(Constraint.AVAILABILITY).getValue()) {
			distance += 1.0;
		}
		return distance;
	}
	
	private double computeAggregatedFitness(
			Composition composition) {
		double aggregatedFitness = 0.0;
		int numberOfServiceCandidates = 0;
		for (ServiceCandidate candidate : 
			composition.getServiceCandidatesList()) {
			aggregatedFitness += computeFitness(candidate);
			numberOfServiceCandidates++;
		}
		if (!composition.isWithinConstraints(constraintsMap)) {
			aggregatedFitness += constraintsMap.get(
					Constraint.PENALTY_FACTOR).getWeight() * 
					computeDistanceToConstraints(composition);	
		}
		
		return aggregatedFitness /= numberOfServiceCandidates;
	}
	
	private void printCurrentPopulation(
			List<Composition> population) {
		for (Composition composition : population) {
			System.out.println(composition.getServiceCandidatesAsString());
		}
		System.out.println("\n");
	}
	
	private double computeUtilityValue(Composition composition) {
		double utility = 0.0;
		if (constraintsMap.get(Constraint.COSTS) != null) {
			utility += constraintsMap.get(Constraint.COSTS).
			getWeight() * composition.getQosVectorAggregated().getCosts();
		}
		if (constraintsMap.get(Constraint.RESPONSE_TIME) != null) {
			utility += constraintsMap.get(Constraint.RESPONSE_TIME).
			getWeight() * composition.getQosVectorAggregated().
			getResponseTime();
		}
		if (constraintsMap.get(Constraint.AVAILABILITY) != null) {
			utility += constraintsMap.get(Constraint.AVAILABILITY).
			getWeight() * composition.getQosVectorAggregated().
			getAvailability();
		}
		return utility;
	}

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
	public void setRuntime(long runtime) {
		this.runtime = runtime;
	}
}
