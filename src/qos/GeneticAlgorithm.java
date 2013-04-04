package qos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

// TODO: TEST RECOMBINATION METHODS!

public class GeneticAlgorithm extends Algorithm {
	
	private List<ServiceClass> serviceClassesList;
	private List<ServiceCandidate> serviceCandidatesList;
	private Map<String, Constraint> constraintsMap;
	
	private int numberOfRequestedResultTiers;
	private double populationSize;
	private int terminationCriterion;
	
	private String crossoverMethod;
	private String terminationMethod;
	
	private List<AlgorithmSolutionTier> algorithmSolutionTiers = 
		new LinkedList<AlgorithmSolutionTier>();
	
	private long runtime = 0;
	
	public GeneticAlgorithm(List<ServiceClass> serviceClassesList, 
			List<ServiceCandidate> serviceCandidatesList, 
			Map<String, Constraint> constraintsMap, 
			int numberOfRequestedResultTiers, double populationSize,
			int terminationCriterion, String crossoverMethod, 
			String terminationMethod) {
		this.serviceClassesList = serviceClassesList;
		this.serviceCandidatesList = serviceCandidatesList;
		this.constraintsMap = constraintsMap;
		this.numberOfRequestedResultTiers = numberOfRequestedResultTiers;
		this.populationSize = populationSize;
		this.terminationCriterion = terminationCriterion;
		this.crossoverMethod = crossoverMethod;
		this.terminationMethod = terminationMethod;
	}

	@Override
	public void start(JProgressBar progressBar) {
		runtime = System.currentTimeMillis();
		List<Composition> population = generateInitialPopulation();
		System.out.println(population.size());
		
		int terminationCounter = terminationCriterion;
		updateAlgorithmSolutionTiers(population);
		
		while (terminationCounter > 0) {
//			printCurrentPopulation(population);
			List<Composition> oldPopulation = population;
			
			// MUTATION
			population = mutate(population);

			// RECOMBINATION
			if (crossoverMethod.contains("One-Point")) {
				population = doOnePointCrossover(population);
			}
			else if (crossoverMethod.contains("Two-Point")) {
				population = doTwoPointCrossover(population);
			}
			else if (crossoverMethod.contains("Half-Uniform")) {
				population = doHalfUniformCrossover(population);
			}
			else {
				population = doUniformCrossover(population);
			}
			
			// SELECTION
			population = doSelection(population);
			
			updateAlgorithmSolutionTiers(population);
//			printCurrentPopulation(population);
//			System.out.println();
			
			// CHECK TERMINATION CRITERION
			if (terminationMethod.contains("Iteration")) {
				terminationCounter--;
				if (terminationCounter < 1) {
					break;
				}
			}
			else if (terminationMethod.contains(
					"consecutive equal generations")) {
				if (hasPopulationChanged(oldPopulation, population)) {
					terminationCounter = terminationCriterion;
				}
				else {
					terminationCounter--;
					if (terminationCounter < 1) {
						break;
					}
				}
			}
			// TODO: IMPLEMENT MIN IMPROVEMENT METHOD
			// -> COULD NEED MUCH WORK...
			else {

			}
		}
		
		// TODO: Für was ist denn das setTierTitle() gut? getTierTitle() wird 
		//		 ja nie aufgerufen.
		// -> wird benötigt, um die TierID korrekt zu setzen;
		//	  diese wird bei Erstellung der ResultTables benötigt.
		//	  Es reicht aus, dies ganz am Ende zu machen.
		//    Aber: Funktion könnte in setTierId umbenannt werden.
		for (int count = 0; count < algorithmSolutionTiers.size(); count++) {
			algorithmSolutionTiers.get(count).setTierTitle(count + 1);
		}
		runtime = System.currentTimeMillis() - runtime;

	}

	
	private List<Composition> generateInitialPopulation() {
		// Randomly select the requested number of compositions.
		List<Composition> population = new LinkedList<Composition>();
		// Loop to construct the requested number of compositions.
		for (int i = 0; i < populationSize; i++) {
			List<ServiceCandidate> chosenServiceCandidatesList = 
					new LinkedList<ServiceCandidate>();
			QosVector qosVector = new QosVector();
			Composition composition = new Composition(
					chosenServiceCandidatesList, qosVector, 0.0);
			// Loop to randomly select a service candidate for each service 
			// class.
			for (int j = 0; j < serviceClassesList.size(); j++) {
				// Generate a random number between 0 and the number of 
				// service candidates in this service class.
				int random = (int) (Math.random() * serviceClassesList.get(
						j).getServiceCandidateList().size());
				// Select the corresponding service candidate and add it to the 
				// new composition. QoS values are aggregated automatically.
				// TODO: Test if this cleaner and faster way works. (--> Seems 
				//		 to be okay.)
				ServiceCandidate serviceCandidate = serviceClassesList.get(
						j).getServiceCandidateList().get((random));
				composition.addServiceCandidate(serviceCandidate);
			}
			// Check if composition has already been created.
			if (population.contains(composition)) {
				i--;
			}
			else {
				population.add(composition);
			}
		}
		return population;
	}
	
	// Use the selection operator. Calculating the fitness values is 
	// contained in here.
	private List<Composition> doSelection(List<Composition> population) {
		List<Composition> newPopulation = new LinkedList<Composition>();
		double[] fitnessArray = new double[population.size()];
		double fitnessSum = 0.0;
		
		// Compute the fitness for all compositions as follows:
		// - Sum up the fitness values of all service candidates contained
		// - Add penalty if constraints are violated
		// Store the fitness values of all of the population's compositions 
		// in an array.
		for (int count = 0; count < population.size(); count++) {	
			fitnessArray[count] = computeAggregatedFitness(
					population.get(count));
			fitnessSum += fitnessArray[count];
		}
		
		// Do the actual selection (Survival of the Fittest)
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
			
			// Version with constant population size
			newPopulation.add(population.get(selectionIndex));
			
			// Alternatively: Declining population size
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
	
	// Use the mutation operator.
	private List<Composition> mutate(List<Composition> initialPopulation) {
		for (int count = 0; count < initialPopulation.size(); count++) {
			for (int innerCount = 0; innerCount < 
			serviceClassesList.size(); innerCount++) {
				// PROBABILITY = (1 / NUMBER OF ALL SERVICE CLASSES)
				if (Math.random() < (1.0 / serviceClassesList.size())) {
					// INSERT NEW RANDOMLY SELECTED SERVICE CANDIDATE
					// AND REMOVE OLD SERVICE CANDIDATE
					initialPopulation.get(count).getServiceCandidatesList().
					set(innerCount,getRandomComposition(initialPopulation).
							getServiceCandidatesList().get(innerCount));
				}
			}
		}
		return initialPopulation;
	}
	
	private List<Composition> doOnePointCrossover(
			List<Composition> population) {
		List<Composition> newPopulation = new LinkedList<Composition>();
		while (population.size() > 0) {
			if (population.size() == 1) {
				newPopulation.add(population.get(0));
				break;
			}	
			Composition composition_1 = population.get(0);
			population.remove(composition_1);
			// SELECT 2ND COMPOSITION RANDOMLY
			Composition composition_2 = getRandomComposition(population);
			population.remove(composition_2);
			List<ServiceCandidate> newServiceCandidateList_1 = 
				new LinkedList<ServiceCandidate>();
			List<ServiceCandidate> newServiceCandidateList_2 = 
				new LinkedList<ServiceCandidate>();
			
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
			composition_1.setServiceCandidateList(newServiceCandidateList_1);
			composition_2.setServiceCandidateList(newServiceCandidateList_2);
			newPopulation.add(composition_1);
			newPopulation.add(composition_2);
		}
		return newPopulation;
	}
	private List<Composition> doTwoPointCrossover(
			List<Composition> population) {
		List<Composition> newPopulation = new LinkedList<Composition>();
		while (population.size() > 0) {
			if (population.size() == 1) {
				newPopulation.add(population.get(0));
				break;
			}	
			Composition composition_1 = population.get(0);
			population.remove(composition_1);
			// SELECT 2ND COMPOSITION RANDOMLY
			Composition composition_2 = getRandomComposition(population);
			population.remove(composition_2);
			List<ServiceCandidate> newServiceCandidateList_1 = 
				new LinkedList<ServiceCandidate>();
			List<ServiceCandidate> newServiceCandidateList_2 = 
				new LinkedList<ServiceCandidate>();

			// SELECT CROSSOVER POINTS RANDOMLY
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
			composition_1.setServiceCandidateList(newServiceCandidateList_1);
			composition_2.setServiceCandidateList(newServiceCandidateList_2);
			newPopulation.add(composition_1);
			newPopulation.add(composition_2);
		}
		return newPopulation;
	}

	private List<Composition> doUniformCrossover(
			List<Composition> population) {
		List<Composition> newPopulation = new LinkedList<Composition>();
		while (population.size() > 0) {
			if (population.size() == 1) {
				newPopulation.add(population.get(0));
				break;
			}	
			Composition composition_1 = population.get(0);
			population.remove(composition_1);
			// SELECT 2ND COMPOSITION RANDOMLY
			Composition composition_2 = getRandomComposition(population);
			population.remove(composition_2);
			List<ServiceCandidate> newServiceCandidateList_1 = 
					new LinkedList<ServiceCandidate>();
			List<ServiceCandidate> newServiceCandidateList_2 = 
					new LinkedList<ServiceCandidate>();
			for (int count = 0; 
					count < composition_1.getServiceCandidatesList().size(); 
					count++) {
				if (Math.random() > 0.5) {
					newServiceCandidateList_1.add(
							composition_1.getServiceCandidatesList().
							get(count));
					newServiceCandidateList_2.add(
							composition_2.getServiceCandidatesList().
							get(count));
				}
				else {
					newServiceCandidateList_1.add(
							composition_2.getServiceCandidatesList().
							get(count));
					newServiceCandidateList_2.add(
							composition_1.getServiceCandidatesList().
							get(count));
				}
			}
			composition_1.setServiceCandidateList(newServiceCandidateList_1);
			composition_2.setServiceCandidateList(newServiceCandidateList_2);
			newPopulation.add(composition_1);
			newPopulation.add(composition_2);
		}
		return newPopulation;
	}
	
	private List<Composition> doHalfUniformCrossover(
			List<Composition> population) {
		List<Composition> newPopulation = new LinkedList<Composition>();
		while (population.size() > 0) {
			if (population.size() == 1) {
				newPopulation.add(population.get(0));
				break;
			}
			Composition composition_1 = population.get(0);
			population.remove(composition_1);
			// SELECT 2ND COMPOSITION RANDOMLY
			Composition composition_2 = getRandomComposition(population);
			population.remove(composition_2);
			int numberOfNonMatchingCandidates = 0;
			for (int count = 0; 
					count < composition_1.getServiceCandidatesList().size(); 
					count++) {
				if (!composition_1.getServiceCandidatesList().get(count).
						equals(composition_2.getServiceCandidatesList().
								get(count))) {
					numberOfNonMatchingCandidates++;
				}
			}
			int numberOfChangedCandidates = 0;
			int numberOfCandidatesWhichHaveToBeChanged = 
					(int) Math.round(numberOfNonMatchingCandidates / 2.0);
			List<ServiceCandidate> newServiceCandidateList_1 = 
					new LinkedList<ServiceCandidate>();
			List<ServiceCandidate> newServiceCandidateList_2 = 
					new LinkedList<ServiceCandidate>();
			for (int count = 0; 
					count < composition_1.getServiceCandidatesList().size(); 
					count++) {
				if (composition_1.getServiceCandidatesList().get(count).
						equals(composition_2.getServiceCandidatesList().
								get(count))) {
					newServiceCandidateList_1.add(
							composition_1.getServiceCandidatesList().
							get(count));
					newServiceCandidateList_2.add(
							composition_2.getServiceCandidatesList().
							get(count));
				}
				else {
					if (numberOfNonMatchingCandidates - 
							numberOfChangedCandidates <= 
							numberOfCandidatesWhichHaveToBeChanged) {
						newServiceCandidateList_1.add(
								composition_2.getServiceCandidatesList().
								get(count));
						newServiceCandidateList_2.add(
								composition_1.getServiceCandidatesList().
								get(count));
					}
					else {
						if (Math.random() > 0.5) {
							newServiceCandidateList_1.add(
									composition_2.getServiceCandidatesList().
									get(count));
							newServiceCandidateList_2.add(
									composition_1.getServiceCandidatesList().
									get(count));
							numberOfChangedCandidates++;
						}
						else {
							newServiceCandidateList_1.add(
									composition_1.getServiceCandidatesList().
									get(count));
							newServiceCandidateList_2.add(
									composition_2.getServiceCandidatesList().
									get(count));
						}
					}
					numberOfNonMatchingCandidates--;
				}
			}
			composition_1.setServiceCandidateList(newServiceCandidateList_1);
			composition_2.setServiceCandidateList(newServiceCandidateList_2);
			newPopulation.add(composition_1);
			newPopulation.add(composition_2);
		}
		return newPopulation;
	}
	
	// Computes the fitness of a single service candidate.
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
	//		 --> Anders gesagt hängt es von der Fitness ab. Im Endeffekt ist es 
	//			 aber (zumindest bei hohem Penalty Factor) das Gleiche. Oder?
	//		 --> Seh ich auch so. Bin mir aber nicht ganz sicher.
	private void updateAlgorithmSolutionTiers(
			List<Composition> newPopulation) {
		List<Composition> differentSolutions = new LinkedList<Composition>(
				getDifferentSolutions(newPopulation));
		for (Composition composition : differentSolutions) {
			// Check if the composition is already contained in the list of 
			// algorithm solution tiers.
			boolean isNewComposition = true;
			for (AlgorithmSolutionTier tier : algorithmSolutionTiers) {
				if (tier.getServiceCompositionList().contains(composition)) {
					isNewComposition = false;
					break;
				}
			}
			if (isNewComposition) {
				// Create the new composition to be added to the list of 
				// algorithm solution tiers.
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
						composition.getQosVectorAggregated().getAvailability())
				);
				newComposition.computeUtilityValue();
				
				// Determine the position of the new composition in the 
				// result table. (According to its rank, which is evaluated by 
				// comparing the utility values.)
				int tierRank = 0;
				boolean equalValues = false;
				for (AlgorithmSolutionTier tier : algorithmSolutionTiers) {
					if (tier.getServiceCompositionList().get(0).getUtility() > 
					newComposition.getUtility()) {
						tierRank++;
						if (tierRank >= numberOfRequestedResultTiers) {
							break;
						}
					}
					else if (tier.getServiceCompositionList().get(0).
							getUtility() == newComposition.getUtility()) {
						equalValues = true;
						break;
					}
				}
				if (equalValues) {
					algorithmSolutionTiers.get(tierRank).
					getServiceCompositionList().add(newComposition);
				}
				else {
					// Add the new composition to the right position.
					List<Composition> newEntry = new LinkedList<Composition>();
					newEntry.add(newComposition);
					algorithmSolutionTiers.add(tierRank, 
							new AlgorithmSolutionTier(newEntry, tierRank + 1));
					// Remove the worst composition if the number of maximum 
					// solution tiers would be exceeded.
					if (algorithmSolutionTiers.size() > 
					numberOfRequestedResultTiers) {
						algorithmSolutionTiers.remove(
								algorithmSolutionTiers.size() - 1);
					}
				}	
			}	
		}
	}
	
	private List<Composition> getDifferentSolutions(
			List<Composition> population) {
		List<Composition> differentSolutions = new LinkedList<Composition>();
		for (int count = 0; count < population.size(); count++) {
			boolean newComposition = true;
			for (int innerCount = 0; innerCount < population.size(); 
			innerCount++) {
				if (innerCount != count && population.get(count).equals(
						population.get(innerCount))) {
					newComposition = false;
					break;
				}
			}
			if (newComposition) {
				differentSolutions.add(population.get(count));
			}
		}
		return differentSolutions;
	}
	
	// Compute the distance of a composition's aggregated QoS attributes to 
	// the given constraints.
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
	
	// Compute the fitness of a composition.
	private double computeAggregatedFitness(Composition composition) {
		double aggregatedFitness = 0.0;
		int numberOfServiceCandidates = 0;
		for (ServiceCandidate candidate : 
			composition.getServiceCandidatesList()) {
			aggregatedFitness += computeFitness(candidate);
			numberOfServiceCandidates++;
		}
		// Penalty factor has to be considered only if the composition 
		// violates the constraints.
		if (!composition.isWithinConstraints(constraintsMap)) {
			aggregatedFitness += constraintsMap.get(
					Constraint.PENALTY_FACTOR).getWeight() * 
					computeDistanceToConstraints(composition);	
		}
		return (aggregatedFitness / numberOfServiceCandidates);
	}
	
	private Composition getRandomComposition(List<Composition> population) {
		double random = Math.random();
		// Avoid getting MAX_SIZE (Out of Bounds)
		if (random == 1) {
			random -= 0.01;
		}
		return population.get((int) (random * population.size()));
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
