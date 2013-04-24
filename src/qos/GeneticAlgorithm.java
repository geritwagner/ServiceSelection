package qos;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GeneticAlgorithm extends Algorithm {
	
	private List<ServiceClass> serviceClassesList;
	private Map<String, Constraint> constraintsMap;
	
	private int populationSize;
	private int terminationCriterion;
	
	private String selectionMethod;
	private String crossoverMethod;
	private String terminationMethod;
	
	private List<Integer> numberOfDifferentSolutions;
	private List<Double> maxFitnessPerPopulation;
	private List<Double> averageFitnessPerPopulation;
	
	private int workPercentage = 0;
	private double elitismRate;
	private double crossoverRate;
	private double mutationRate;
	
	private double dynamicPenalty = 1.0;
	private int terminationCounter;
	
	private int maxDeviation;
	
	private List<AlgorithmSolutionTier> algorithmSolutionTiers = 
		new LinkedList<AlgorithmSolutionTier>();
	
	private long runtime = 0;
	
	public GeneticAlgorithm(List<ServiceClass> serviceClassesList, 
			Map<String, Constraint> constraintsMap, int populationSize, 
			int terminationCriterion, String selectionMethod, 
			int elitismRate, String crossoverMethod,
			int crossoverRate, int mutationRate,
			String terminationMethod, int maxDeviation) {
		this.serviceClassesList = serviceClassesList;
		this.constraintsMap = constraintsMap;
		this.populationSize = populationSize;
		this.terminationCriterion = terminationCriterion;
		this.selectionMethod = selectionMethod;
		this.elitismRate = elitismRate;
		this.crossoverMethod = crossoverMethod;
		this.crossoverRate = crossoverRate;
		this.mutationRate = mutationRate;
		this.terminationMethod = terminationMethod;
		this.maxDeviation = maxDeviation;
	}

	// TODO: if changes are made to this method, 
	//		 startInBenchmarkMode() has to be updated!
	@Override
	public void start() {
		crossoverRate /= 100.0;
		elitismRate /= 100.0;
		mutationRate /= 1000.0;
		workPercentage = 0;
		
		terminationCounter = terminationCriterion;
		
		numberOfDifferentSolutions = new LinkedList<Integer>();
		maxFitnessPerPopulation = new LinkedList<Double>();
		averageFitnessPerPopulation = new LinkedList<Double>();
		
		runtime = System.currentTimeMillis();
		
		List<Composition> population = generateInitialPopulation();
		setVisualizationValues(population);
		
		while (terminationCounter > 0) {
			// Temporarily save the elite compositions.
			int numberOfElites = (int) Math.round(
					populationSize * elitismRate);
			List<Composition> elites = doSelectionElitismBased(
					population, numberOfElites);
		
			// SELECTION
			List<Composition> matingPool;
			// Roulette Wheel
			if (selectionMethod.contains("Roulette Wheel")) {
				matingPool = doSelectionRouletteWheel(population);
			}
			
			// Linear Ranking
			// TODO: Linear Ranking liefert sehr schlechte Ergebnisse!
			else if (selectionMethod.contains("Linear Ranking")) {
				matingPool = doSelectionLinearRanking(population);
			}
			
			// Binary Tournament
			else {
				matingPool = doSelectionBinaryTournament(population);
			}

			// CROSSOVER
			// One-Point Crossover
			if (crossoverMethod.contains("One-Point")) {
				matingPool = doCrossoverOnePoint(matingPool, crossoverRate);
			}
			else if (crossoverMethod.contains("Two-Point")) {
				matingPool = doCrossoverTwoPoint(matingPool, crossoverRate);
			}
			else {
				matingPool = doCrossoverUniform(matingPool, crossoverRate);
			}
			

			// MUTATION
			doMutation(matingPool, mutationRate);
			
			// Replace the worst compositions with the elites.
			matingPool = doElitePreservation(matingPool, elites);
			
			boolean hasPopulationChanged = true;
			if (terminationMethod.contains("Consecutive Equal Generations")) {
				hasPopulationChanged(population, matingPool);
			}
			
			
			population.removeAll(population);
			population.addAll(matingPool);
			
			setVisualizationValues(population);
			
			// TERMINATION CRITERION
			// Number of Iterations
			if (terminationMethod.contains("Iteration")) {
				dynamicPenalty = 1 - (terminationCriterion - 
						terminationCounter) / terminationCriterion;
				terminationCounter--;
				workPercentage = (int) ((1 - 1.0 * terminationCounter / 
						terminationCriterion) * 100);
			}

			// Consecutive Equal Generations
			// TODO: Test this method later (not complete!)
			else if (terminationMethod.contains(
					"Consecutive Equal Generations")) {
				if (hasPopulationChanged) {
					terminationCounter = terminationCriterion;
				}
				else {
					terminationCounter--;
				}
				workPercentage = Math.max((int) (100.0 - 100.0 * 
						numberOfDifferentSolutions.get(
								numberOfDifferentSolutions.size() - 1) / 
								numberOfDifferentSolutions.get(0)), 
								workPercentage);
			}
			
			// Fitness Value Convergence
			// TODO: Test this method later (not complete!)
			else {
				if (maxFitnessPerPopulation.get(
						maxFitnessPerPopulation.size() - 1) <= 
						maxFitnessPerPopulation.get(
								maxFitnessPerPopulation.size() - 2 - 
								terminationCriterion + terminationCounter)) {
					terminationCounter--;
				}
				else {
					terminationCounter = terminationCriterion;
				}
				workPercentage = Math.min(Math.max((int) (100.0 * 
						terminationCriterion / numberOfDifferentSolutions.get(
								numberOfDifferentSolutions.size() - 1)), 
								workPercentage), 100);
			}
		}
		
		// Sort the population according to the fitness of the 
		// compositions. Thus, the first elements are the elite elements.
		Collections.sort(population, new Comparator<Composition>() {
			@Override
			public int compare(Composition o1, Composition o2) {
				if (computeFitness(o1) < computeFitness(o2)) {
					return 1;
				}
				else if (computeFitness(o1) > computeFitness(o2)) {
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		
		// Print the best solution.
		System.out.println("--------------");
		System.out.println("BEST COMPOSITION:");
		System.out.println(population.get(0).getServiceCandidatesAsString());
		System.out.println(population.get(0).getUtility());
		System.out.println("--------------");
		List<Composition> optimalComposition = new LinkedList<Composition>();
		optimalComposition.add(population.get(0));
		algorithmSolutionTiers.add(
				new AlgorithmSolutionTier(optimalComposition, 1));
		runtime = System.currentTimeMillis() - runtime;		
	}
	
	// TODO: Pretty much copy/paste; so if changes are made to 
	//		 the start()-method, this method has to be updated!
	public void startInBenchmarkMode() {
		crossoverRate /= 100.0;
		elitismRate /= 100.0;
		mutationRate /= 1000.0;
		
		terminationCounter = terminationCriterion;
		
		runtime = System.currentTimeMillis();	

		List<Composition> population = generateInitialPopulation();

		while (terminationCounter > 0) {
			// Temporarily save the elite compositions.
			int numberOfElites = (int) Math.round(
					populationSize * elitismRate);
			List<Composition> elites = doSelectionElitismBased(
					population, numberOfElites);
		
			// SELECTION
			List<Composition> matingPool;
			// Roulette Wheel
			matingPool = doSelectionRouletteWheel(population);
			// Linear Ranking
//			matingPool = doSelectionLinearRanking(population);
			// Binary Tournament
//			matingPool = doSelectionBinaryTournament(population);

			// CROSSOVER
			// One-Point Crossover
			matingPool = doCrossoverOnePoint(matingPool, crossoverRate);
			// Two-Point Crossover
//			matingPool = doCrossoverTwoPoint(matingPool, crossoverRate);
			// Uniform Crossover
//			matingPool = doCrossoverUniform(matingPool, crossoverRate);

			// MUTATION
			doMutation(matingPool, mutationRate);
			
			// Replace the worst compositions with the elites.
			matingPool = doElitePreservation(matingPool, elites);
			
//			boolean hasPopulationChanged = true;
//			hasPopulationChanged(population, matingPool);
			
			population.removeAll(population);
			population.addAll(matingPool);
			
			// TERMINATION CRITERION
			// Number of Iterations
			dynamicPenalty = 1 - (terminationCriterion - 
					terminationCounter) / terminationCriterion;
			terminationCounter--;
			// Consecutive Equal Generations
//			if (hasPopulationChanged) {
//				terminationCounter = terminationCriterion;
//			}
//			else {
//				terminationCounter--;
//			}
			// Fitness Value Convergence
//			if (maxFitnessPerPopulation.get(
//					maxFitnessPerPopulation.size() - 1) <= 
//					maxFitnessPerPopulation.get(
//							maxFitnessPerPopulation.size() - 2 - 
//							terminationCriterion + terminationCounter)) {
//				terminationCounter--;
//			}
//			else {
//				terminationCounter = terminationCriterion;
//			}
		}
		
		// Sort the population according to the fitness of the 
		// compositions. Thus, the first elements are the elite elements.
		Collections.sort(population, new Comparator<Composition>() {
			@Override
			public int compare(Composition o1, Composition o2) {
				if (computeFitness(o1) < computeFitness(o2)) {
					return 1;
				}
				else if (computeFitness(o1) > computeFitness(o2)) {
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		
		// Print the best solution.
		List<Composition> optimalComposition = new LinkedList<Composition>();
		optimalComposition.add(population.get(0));
		algorithmSolutionTiers.add(
				new AlgorithmSolutionTier(optimalComposition, 1));
		runtime = System.currentTimeMillis() - runtime;		
	}

	
	private List<Composition> generateInitialPopulation() {
		// Randomly select the requested number of compositions.
		List<Composition> population = new LinkedList<Composition>();
		// Loop to construct the requested number of compositions.
		for (int i = 0; i < populationSize; i++) {
			Composition composition = new Composition();
			// Loop to randomly select a service candidate for each service 
			// class.
			for (int j = 0; j < serviceClassesList.size(); j++) {
				// Generate a random number between 0 and the number of 
				// service candidates in this service class.
				int random = (int) (Math.random() * serviceClassesList.get(
						j).getServiceCandidateList().size());
				// Select the corresponding service candidate and add it to the 
				// new composition. QoS values are aggregated automatically.
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
	
	private List<Composition> doSelectionElitismBased(
			List<Composition> population, int numberOfElites) {
		List<Composition> elites = new LinkedList<Composition>();
		// Sort the population according to the fitness of the 
		// compositions. Thus, the first elements are the elite elements.
		Collections.sort(population, new Comparator<Composition>() {
			@Override
			public int compare(Composition o1, Composition o2) {
				if (computeFitness(o1) < computeFitness(o2)) {
					return 1;
				}
				else if (computeFitness(o1) > computeFitness(o2)) {
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		for (int i = 0; i < numberOfElites; i++) {
			elites.add(population.get(i));
		}
		return elites;
	}
	
	private void doMutation(List<Composition> population, 
			double mutationRate) {
		for (int i = 0; i < population.size(); i++) {
			List<ServiceCandidate> serviceCandidates = 
					population.get(i).getServiceCandidatesList();
			
			for (int j = 0; j < serviceCandidates.size(); j++) {
				if (Math.random() < mutationRate) {
					ServiceCandidate oldServiceCandidate = 
							serviceCandidates.get(j);
					// Get the service candidates from the service class 
					// that has to be mutated. "-1" is necessary because 
					// the IDs start with 1 instead of 0!
					List<ServiceCandidate> newServiceCandidates = 
							serviceClassesList.get(oldServiceCandidate.
									getServiceClassId() - 1).
									getServiceCandidateList();
					serviceCandidates.set(j, newServiceCandidates.get((int) 
							(Math.random() * newServiceCandidates.size())));
					population.get(i).buildAggregatedQosVector();
					population.get(i).computeUtilityValue();
				}
			}
		}
	}
	
	private List<Composition> doElitePreservation(
			List<Composition> matingPool, List<Composition> elites) {
		Collections.sort(matingPool, new Comparator<Composition>() {
			@Override
			public int compare(Composition o1, Composition o2) {
				if (computeFitness(o1) < computeFitness(o2)) {
					return 1;
				}
				else if (computeFitness(o1) > computeFitness(o2)) {
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		// Remove the worst compositions by using the other part of the 
		// population.
		matingPool = matingPool.subList(0, matingPool.size() - elites.size());
		// Add the elite compositions to the beginning of the list. Note that 
		// they are not necessarily the elite compositions in the new 
		// population. So they might also be added at the end.
		matingPool.addAll(0, elites);
		
		return matingPool;
	}
	
	private List<Composition> doSelectionRouletteWheel(
			List<Composition> oldPopulation) {
		double[] fitnessAreas = new double[oldPopulation.size()];
		List<Composition> newPopulation = new LinkedList<Composition>();
		// Compute cumulated fitness areas of 
		// every composition of population
		for (int i = 0; i < oldPopulation.size(); i++) {
			fitnessAreas[i] = computeFitness(oldPopulation.get(i));
			if (i != 0) {
				fitnessAreas[i] += fitnessAreas[i - 1];
			}
		}
		// Save the fitnessAreaSum
		double fitnessAreaSum = fitnessAreas[oldPopulation.size() - 1];
		// Choose every member of the new population by random
		// with respect to the fitness values of the different
		// compositions
		for (int i = 0; i < oldPopulation.size(); i++) {
			double random = Math.random() * fitnessAreaSum;
			for (int j = 0; j < oldPopulation.size(); j++) {
				if (random < fitnessAreas[j]) {
					newPopulation.add(oldPopulation.get(j));
					break;
				}
			}
		}
		return newPopulation;
	}

	// TODO: Linear Ranking liefert sehr schlechte Ergebnisse!
	private List<Composition> doSelectionLinearRanking(
			List<Composition> oldPopulation) {
		double[] fitnessRanks = new double[oldPopulation.size()];
		double selectionPressure = 2.0;
		Collections.sort(oldPopulation, new Composition());
		// Compute cumulated fitness rank areas of 
		// every composition of population
		for (int i = 0; i < oldPopulation.size(); i++) {
			fitnessRanks[i] = 2 - selectionPressure + (2 * 
				(selectionPressure - 1) * 
				(i / (oldPopulation.size() - 1)));
			if (i != 0) {
				fitnessRanks[i] += fitnessRanks[i - 1];
			}
		}
		// Save the fitnessRankSum
		double fitnessRankSum = fitnessRanks[oldPopulation.size() - 1];
		List<Composition> newPopulation = new LinkedList<Composition>();
		// Choose every member of the new population by random
		// with respect to the ranks of the different
		// compositions (like roulette wheel)
		for (int i = 0; i < oldPopulation.size(); i++) {
			double random = Math.random() * fitnessRankSum;
			for (int j = 0; j < oldPopulation.size(); j++) {
				if (random < fitnessRanks[j]) {
					newPopulation.add(oldPopulation.get(j));
					break;
				}
			}
		}
		return newPopulation;
	}

	private List<Composition> doSelectionBinaryTournament(
			List<Composition> oldPopulation) {
		List<Composition> newPopulation = 
			new LinkedList<Composition>(oldPopulation);
		// Permutation
		int[] permutationIndices = permuteIndices(oldPopulation.size());
		// Binary Tournament between two compositions, 
		// determined by the permutation above
		for (int i = 0; i < oldPopulation.size(); i++) {
			if (computeFitness(oldPopulation.get(i)) < 
					computeFitness(oldPopulation.get(
							permutationIndices[i]))) {
				newPopulation.set(i, oldPopulation.get(
						permutationIndices[i]));
			}
		}
		return newPopulation;
	}
	
	private List<Composition> doCrossoverOnePoint(
			List<Composition> matingPool, double crossoverRate) {
		List<Composition> populationNew = new LinkedList<Composition>();
		while (matingPool.size() > 0) {
			if (matingPool.size() == 1) {
				populationNew.add(matingPool.get(0));
				break;
			}
			// Pick the first composition for crossover.
			Composition compositionA = matingPool.remove(0);
			// Randomly select the second composition for crossover.
			Composition compositionB = matingPool.remove(
					(int) (Math.random() * matingPool.size()));

			if (Math.random() < crossoverRate) {
				// Randomly select the crossover point. 0 is excluded from the 
				// different possibilities because the resulting composition 
				// would be exactly the same as the first input composition. 
				// The last crossover point that is possible is included, 
				// however, because then, at least the last service candidate 
				// is changed. This is because of the definition of 
				// List.subList().
				int crossoverPoint = (int) (Math.random() * 
						(serviceClassesList.size() - 1) + 1);

				// Do the crossover.
				Composition compositionC = new Composition();
				for (ServiceCandidate serviceCandidate : compositionA.
						getServiceCandidatesList().subList(0, crossoverPoint)) {
					compositionC.addServiceCandidate(serviceCandidate);
				}
				for (ServiceCandidate serviceCandidate : compositionB.
						getServiceCandidatesList().subList(
								crossoverPoint, serviceClassesList.size())) {
					compositionC.addServiceCandidate(serviceCandidate);
				}

				Composition compositionD = new Composition();
				for (ServiceCandidate serviceCandidate : compositionB.
						getServiceCandidatesList().subList(0, crossoverPoint)) {
					compositionD.addServiceCandidate(serviceCandidate);
				}
				for (ServiceCandidate serviceCandidate : compositionA.
						getServiceCandidatesList().subList(
								crossoverPoint, serviceClassesList.size())) {
					compositionD.addServiceCandidate(serviceCandidate);
				}
				
				populationNew.add(compositionC);
				populationNew.add(compositionD);
			}
			else {
				populationNew.add(compositionA);
				populationNew.add(compositionB);
			}
		}
		return populationNew;
	}
	
	private List<Composition> doCrossoverTwoPoint(
			List<Composition> matingPool, double crossoverRate) {
		List<Composition> newPopulation = new LinkedList<Composition>();
		while (matingPool.size() > 0) {
			if (matingPool.size() == 1) {
				newPopulation.add(matingPool.get(0));
				break;
			}
			Composition composition_1 = matingPool.get(0);
			matingPool.remove(0);
			// SELECT 2ND COMPOSITION RANDOMLY
			int randomCompositionIndex = 
					(int) (Math.random() * matingPool.size());
			Composition composition_2 = matingPool.get(randomCompositionIndex);
			matingPool.remove(randomCompositionIndex);
			if (Math.random() < crossoverRate) {
				// SELECT CROSSOVER POINTS RANDOMLY
				int crossoverPoint = 
						(int) (Math.random() * serviceClassesList.size());
				int crossoverPoint_2 = 
						(int) (Math.random() * serviceClassesList.size());
				if (crossoverPoint == crossoverPoint_2) {
					if (crossoverPoint < serviceClassesList.size() - 1) {
						crossoverPoint_2++;
					}
					else {
						crossoverPoint--;
					}
				}
				else if (crossoverPoint > crossoverPoint_2) {
					int temp = crossoverPoint;
					crossoverPoint = crossoverPoint_2;
					crossoverPoint_2 = temp;
				}
				// Do the crossover
				Composition composition_3 = new Composition();
				for (ServiceCandidate serviceCandidate : composition_1.
						getServiceCandidatesList().subList(0, crossoverPoint)) {
					composition_3.addServiceCandidate(serviceCandidate);
				}
				for (ServiceCandidate serviceCandidate : composition_2.
						getServiceCandidatesList().subList(
								crossoverPoint, crossoverPoint_2)) {
					composition_3.addServiceCandidate(serviceCandidate);
				}
				for (ServiceCandidate serviceCandidate : composition_1.
						getServiceCandidatesList().subList(crossoverPoint_2, 
								serviceClassesList.size())) {
					composition_3.addServiceCandidate(serviceCandidate);
				} 

				Composition composition_4 = new Composition();
				for (ServiceCandidate serviceCandidate : composition_2.
						getServiceCandidatesList().subList(0, crossoverPoint)) {
					composition_4.addServiceCandidate(serviceCandidate);
				}
				for (ServiceCandidate serviceCandidate : composition_1.
						getServiceCandidatesList().subList(
								crossoverPoint, crossoverPoint_2)) {
					composition_4.addServiceCandidate(serviceCandidate);
				}
				for (ServiceCandidate serviceCandidate : composition_1.
						getServiceCandidatesList().subList(crossoverPoint_2, 
								serviceClassesList.size())) {
					composition_4.addServiceCandidate(serviceCandidate);
				} 

				newPopulation.add(composition_3);
				newPopulation.add(composition_4);
			}
			else {
				newPopulation.add(composition_1);
				newPopulation.add(composition_2);
			}
		}
		return newPopulation;
	}

	private List<Composition> doCrossoverUniform(
			List<Composition> matingPool, double crossoverRate) {
		List<Composition> newPopulation = new LinkedList<Composition>();
		while (matingPool.size() > 0) {
			if (matingPool.size() == 1) {
				newPopulation.add(matingPool.get(0));
				break;
			}	
			Composition composition_1 = matingPool.get(0);
			matingPool.remove(0);
			// SELECT 2ND COMPOSITION RANDOMLY
			int randomCompositionIndex = 
					(int) (Math.random() * matingPool.size());
			Composition composition_2 = matingPool.get(randomCompositionIndex);
			matingPool.remove(randomCompositionIndex);
			if (Math.random() < crossoverRate) {
				Composition composition_3 = new Composition();
				Composition composition_4 = new Composition();
				for (int count = 0; count < 
						composition_1.getServiceCandidatesList().size(); 
						count++) {
					if (Math.random() < 0.5) {
						composition_3.addServiceCandidate(
								composition_1.getServiceCandidatesList().
								get(count));
						composition_4.addServiceCandidate(
								composition_2.getServiceCandidatesList().
								get(count));
					}
					else {
						composition_4.addServiceCandidate(
								composition_2.getServiceCandidatesList().
								get(count));
						composition_3.addServiceCandidate(
								composition_1.getServiceCandidatesList().
								get(count));
					}
				}
				
				newPopulation.add(composition_3);
				newPopulation.add(composition_4);
			}
			else {
				newPopulation.add(composition_1);
				newPopulation.add(composition_2);
			}
		}
		return newPopulation;
	}
	
	private boolean hasPopulationChanged(List<Composition> population, 
			List<Composition> matingPool) {
		int deviation = 
				(int) Math.abs(maxDeviation / 100.0 * population.size());
		for (int i = 0; i < population.size(); i++) {
			if (deviation <= 0) {
				return true;
			}
			else if (population.contains(matingPool.get(i))) {
				population.remove(matingPool.get(i));
			}
			else {
				deviation--;
			}
		}
		return false;
	}

	private void setVisualizationValues(List<Composition> population) {
		List<Composition> differentSolutions = new LinkedList<Composition>();
		double maxFitness = 0.0;
		double averageFitness = 0.0;
		for (int count = 0; count < population.size(); count++) {
			if (!differentSolutions.contains(population.get(count))) {
				differentSolutions.add(population.get(count));
			}
			if (computeFitness(population.get(count)) > maxFitness) {
				maxFitness = computeFitness(population.get(count));
			}
			averageFitness += computeFitness(population.get(count));
		}
		numberOfDifferentSolutions.add(differentSolutions.size());
		maxFitnessPerPopulation.add(maxFitness);
		averageFitnessPerPopulation.add(averageFitness / population.size());
	}
	
	// TODO: Literatur nach anderen Alternativen durchsuchen.
	// Compute the distance of a composition's aggregated QoS attributes to 
	// the given constraints.
	private double computeDistanceToConstraints(Composition composition) {
		double distance = 0.0;
		if (constraintsMap.get(Constraint.COSTS) != null &&  
				composition.getQosVectorAggregated().getCosts() > 
				constraintsMap.get(Constraint.COSTS).getValue()) {
//			distance += ((composition.getQosVectorAggregated().getCosts() - 
//					constraintsMap.get(Constraint.COSTS).getValue()) / 
//					constraintsMap.get(Constraint.COSTS).getValue());
			distance += 1.0;
		}
		if (constraintsMap.get(Constraint.RESPONSE_TIME) != null &&  
				composition.getQosVectorAggregated().getResponseTime() > 
				constraintsMap.get(Constraint.RESPONSE_TIME).getValue()) {
//			distance += ((composition.getQosVectorAggregated().
//					getResponseTime() - constraintsMap.get(
//							Constraint.RESPONSE_TIME).getValue()) /
//							constraintsMap.get(
//									Constraint.RESPONSE_TIME).getValue());
			distance += 1.0;
		}
		if (constraintsMap.get(Constraint.AVAILABILITY) != null &&  
				composition.getQosVectorAggregated().getAvailability() < 
				constraintsMap.get(Constraint.AVAILABILITY).getValue()) {
//			distance += ((constraintsMap.get(
//					Constraint.AVAILABILITY).getValue() - 
//					composition.getQosVectorAggregated().
//					getAvailability()) / constraintsMap.get(
//							Constraint.AVAILABILITY).getValue());
			distance += 1.0;
		}
		return distance;
	}
	
	// Compute the fitness of a composition.
	private double computeFitness(Composition composition) {
		double fitness = composition.getUtility();
		// Penalty factor has to be considered only if the composition 
		// violates the constraints.
		if (!composition.isWithinConstraints(constraintsMap)) {
			fitness -= constraintsMap.get(
					Constraint.PENALTY_FACTOR).getWeight() * 
					computeDistanceToConstraints(composition) * dynamicPenalty;	
		}
		if (fitness < 0.0) {
			return 0.0;
		}
		return fitness;
	}
	
	private int[] permuteIndices(int populationSize) {
		int[] permutationArray = new int[populationSize];
		List<Integer> indicesList = new LinkedList<Integer>();
		for (int i = 0; i < populationSize; i++) {
			indicesList.add(i);
		}
		for (int i = 0; i < populationSize; i++) {
			int permutationIndex = 0;
			boolean swapLastIndex = false;
			int indexPosition = 0;
			do {
				indexPosition = (int) Math.round(
						(Math.random() * (indicesList.size() - 1)));
				permutationIndex = indicesList.get(indexPosition);
				if (indicesList.size() == 1 && 
						i == permutationIndex) {
					swapLastIndex = true;
					permutationArray[i] = permutationArray[i - 1];
					permutationArray[i - 1] = permutationIndex;
					break;
				}
			} while(permutationIndex == i);
			if (!swapLastIndex) {
				permutationArray[i] = permutationIndex;
			}
			indicesList.remove(indexPosition);
		}
		return permutationArray;
	}

		  
	
	// GETTERS AND SETTERS
	public List<ServiceClass> getServiceClassesList() {
		return serviceClassesList;
	}
	public void setServiceClassesList(List<ServiceClass> serviceClassesList) {
		this.serviceClassesList = serviceClassesList;
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
	public List<Integer> getNumberOfDifferentSolutions() {
		return numberOfDifferentSolutions;
	}
	public List<Double> getMaxUtilityPerPopulation() {
		return maxFitnessPerPopulation;
	}
	public List<Double> getAverageUtilityPerPopulation() {
		return averageFitnessPerPopulation;
	}
	public int getWorkPercentage() {
		return workPercentage;
	}
}