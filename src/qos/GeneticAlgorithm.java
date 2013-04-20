package qos;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// TODO: TEST DIFFERENT SELECTION METHODS
// TODO: TEST RECOMBINATION METHODS
// TODO: IMPLEMENT DYNAMIC PENALTY
// TODO: IMPLEMENT DIFFERENT TERMINATION CRITERIA

public class GeneticAlgorithm extends Algorithm {
	
	private List<ServiceClass> serviceClassesList;
	private Map<String, Constraint> constraintsMap;
	
	private int populationSize;
	private int terminationCriterion;
	
	private String selectionMethod;
	private String crossoverMethod;
	private String terminationMethod;
	
	private int[] startPopulationVisualization;
	private List<Integer> numberOfDifferentSolutions;
	private List<Double> maxFitnessPerPopulation;
	private List<Double> averageFitnessPerPopulation;
	
	private int workPercentage = 0;
	private double elitismRate;
	
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
			String terminationMethod, int maxDeviation) {
		this.serviceClassesList = serviceClassesList;
		this.constraintsMap = constraintsMap;
		this.populationSize = populationSize;
		this.terminationCriterion = terminationCriterion;
		this.selectionMethod = selectionMethod;
		this.elitismRate = elitismRate;
		this.crossoverMethod = crossoverMethod;
		this.terminationMethod = terminationMethod;
		this.maxDeviation = maxDeviation;
	}

	// TODO: if changes are made to this method, 
	//		 startInBenchmarkMode() has to be updated!
	@Override
	public void start() {
		runtime = System.currentTimeMillis();
		List<Composition> population = generateInitialPopulation();
		setStartPopulationVisualization(population);

		numberOfDifferentSolutions = new LinkedList<Integer>();
		maxFitnessPerPopulation = new LinkedList<Double>();
		averageFitnessPerPopulation = new LinkedList<Double>();
		setVisualizationValues(population);
		workPercentage = 0;
		terminationCounter = terminationCriterion;
		elitismRate /= 100.0;
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
				matingPool = doSelectionRouletteWheel(
						population, populationSize - numberOfElites);
			}
			
			// Linear Ranking
			// TODO: Linear Ranking liefert sehr schlechte Ergebnisse!
			else if (selectionMethod.contains("Linear Ranking")) {
				matingPool = doSelectionLinearRanking(
						population, populationSize - numberOfElites);
			}
			
			// Binary Tournament
			// TODO: Tournament funktioniert nicht (mehr)!
			else {
				// TODO: Das mit der subList braucht man jetzt nicht mehr oder? 
				//		 Außerdem glaube ich, dass das " - 1" falsch ist. Der 
				//		 end index ist nämlich exclusive.
				matingPool = new LinkedList<Composition>(population.subList(
						numberOfElites, population.size() - 1));
				matingPool = doSelectionBinaryTournament(matingPool);
			}

			// CROSSOVER
			// One-Point Crossover
			// TODO: jTextField für crossoverRate in MainFrame!
			double crossoverRate = 0.7;
			// matingPool is used for crossover. The result is saved directly
			// into population and therefore updates it.
			population = doCrossoverOnePoint(matingPool, crossoverRate);
			
			// MUTATION
			// TODO: Möglichkeit anbieten, mutationRate selbst festzulegen.
			//		 Dazu jTextField für mutationRate in MainFrame!
			double mutationRate = 1.0 / serviceClassesList.size();
			// Mutation is performed directly on the new population.
			doMutation(population, mutationRate);
			
			// Replace the worst compositions with the elites.
			doElitePreservation(population, elites);
			
			// TODO: hasPopulationChanged muss man überarbeiten.
			boolean hasPopulationChanged = hasPopulationChanged(
					population, elites, matingPool);
			
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
		runtime = System.currentTimeMillis();
		List<Composition> population = generateInitialPopulation();

		terminationCounter = terminationCriterion;
		elitismRate /= 100.0;
		while (terminationCounter > 0) {
			// SELECTION (Elitism Based)
			int numberOfElites = (int) Math.round(
					populationSize * elitismRate);
			List<Composition> population1 = doSelectionElitismBased(
					population, numberOfElites);
			List<Composition> population2;
			if (selectionMethod.contains("Roulette Wheel")) {
				population2 = 
						doSelectionRouletteWheel(population, 
								populationSize - numberOfElites);
			}
			else if (selectionMethod.contains("Linear Ranking")) {
				population2 = 
				doSelectionLinearRanking(population, 
						populationSize - numberOfElites);
			}
			else {
				population2 = 
				new LinkedList<Composition>(population.subList(
						numberOfElites, population.size() - 1));
			population2 = 
				doSelectionBinaryTournament(population2);
			}
			
			// RECOMBINATION
			// CROSSOVER (One-Point Crossover)
			int numberOfCrossovers = (int) Math.round((
					(populationSize - numberOfElites) / 2.0));
			population2 = doCrossoverOnePoint(
					population, numberOfCrossovers);
			
			// MUTATION
			doMutation(population2, numberOfCrossovers);
			
			boolean hasPopulationChanged = hasPopulationChanged(
					population, population1, population2);
			
			// UPDATE
			population.removeAll(population);
			population.addAll(population1);
			population.addAll(population2);
			
			// TERMINATION CRITERION
			if (terminationMethod.contains("Iteration")) {
				dynamicPenalty = 1 - (terminationCriterion - 
						terminationCounter) / terminationCriterion;
				terminationCounter--;
			}
			// TODO: Test this method later (not complete!)
			else if (terminationMethod.contains(
					"Consecutive Equal Generations")) {
				if (hasPopulationChanged) {
					terminationCounter = terminationCriterion;
				}
				else {
					terminationCounter--;
				}
			}
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
				composition.computeUtilityValue();
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
	
	private List<Composition> doCrossoverOnePoint(
			List<Composition> matingPool, double crossoverRate) {
		List<Composition> populationNew = new LinkedList<Composition>();
		for (int i = 0; i < matingPool.size(); i++) {
			// Randomly select two compositions for crossover.
			int a = (int) (Math.random() * matingPool.size());
			int b = (int) (Math.random() * matingPool.size());
			while (b == a) {
				b = (int) (Math.random() * matingPool.size());
			}
			Composition compositionA = matingPool.get(a);
			Composition compositionB = matingPool.get(b);

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
				compositionC.computeUtilityValue();

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
				compositionD.computeUtilityValue();

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
	
	private void doMutation(List<Composition> population, double mutationRate) {
		for (int i = 0; i < population.size(); i++) {
			
			// TODO: Evtl. sollte man die Composition gar nicht zufällig 
			// auswählen?! Dann hätte man folgendes...
//			List<ServiceCandidate> serviceCandidates = 
//					population.get(i).getServiceCandidatesList();
			
			Composition composition = population.get(
					(int) (Math.random() * population.size()));
			List<ServiceCandidate> serviceCandidates = 
					composition.getServiceCandidatesList();
			
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
					composition.buildAggregatedQosVector();
					composition.computeUtilityValue();
				}
			}
		}
	}
	
	private void doElitePreservation(
			List<Composition> population, List<Composition> elites) {
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
		// Remove the worst compositions by using the other part of the 
		// population.
		population = population.subList(0, population.size() - elites.size());
		// Add the elite compositions to the beginning of the list. Note that 
		// they are not necessarily the elite compositions in the new 
		// population. So they might also be added at the end.
		population.addAll(0, elites);
	}
	
	private List<Composition> doSelectionRouletteWheel(
			List<Composition> oldPopulation, int numberOfSpins) {
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
		for (int i = 0; i < numberOfSpins; i++) {
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
			List<Composition> oldPopulation, int numberOfSpins) {
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
		for (int i = 0; i < numberOfSpins; i++) {
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

	// TODO: Tournament funktioniert nicht (mehr)!
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
	
	// TODO: hasPopulationChanged() Überarbeiten!
	private boolean hasPopulationChanged(List<Composition> population, 
			List<Composition> population1, List<Composition> population2) {
		int deviation = 
				(int) Math.abs(maxDeviation / 100.0 * population.size());
		for (int i = 0; i < population1.size(); i++) {
			if (deviation <= 0) {
				return true;
			}
			else if (population.contains(population1.get(i))) {
				population.remove(population1.get(i));
			}
			else {
				deviation--;
			}
		}
		for (int i = 0; i < population2.size(); i++) {
			if (deviation <= 0) {
				return true;
			}
			else if (population.contains(population2.get(i))) {
				population.remove(population2.get(i));
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
	
	// Compute the distance of a composition's aggregated QoS attributes to 
	// the given constraints.
	private double computeDistanceToConstraints(Composition composition) {
		double distance = 0.0;
		if (constraintsMap.get(Constraint.COSTS) != null &&  
				composition.getQosVectorAggregated().getCosts() > 
				constraintsMap.get(Constraint.COSTS).getValue()) {
			distance += composition.getQosVectorAggregated().getCosts() - 
					constraintsMap.get(Constraint.COSTS).getValue();
		}
		if (constraintsMap.get(Constraint.RESPONSE_TIME) != null &&  
				composition.getQosVectorAggregated().getResponseTime() > 
				constraintsMap.get(Constraint.RESPONSE_TIME).getValue()) {
			distance += composition.getQosVectorAggregated().
					getResponseTime() - constraintsMap.get(
							Constraint.RESPONSE_TIME).getValue();
		}
		if (constraintsMap.get(Constraint.AVAILABILITY) != null &&  
				composition.getQosVectorAggregated().getAvailability() < 
				constraintsMap.get(Constraint.AVAILABILITY).getValue()) {
			distance += composition.getQosVectorAggregated().
					getAvailability() - constraintsMap.get(
							Constraint.AVAILABILITY).getValue();
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
	
	private Composition getRandomComposition(List<Composition> population) {
		double random = Math.random();
		// Avoid getting MAX_SIZE (Out of Bounds)
		if (random == 1) {
			random -= 0.01;
		}
		return population.get((int) (random * population.size()));
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
	
	private void setStartPopulationVisualization(
			List<Composition> population) {
		List <ServiceCandidate> serviceCandidates = 
			new LinkedList<ServiceCandidate>();
		startPopulationVisualization = new int[serviceClassesList.size()];
		for (int i = 0; i < serviceClassesList.size(); i++) {
			startPopulationVisualization[i] = 0;
		}
		for (Composition composition : population) {
			int serviceClassNumber = 0;
			for (ServiceCandidate candidate : 
				composition.getServiceCandidatesList()) {
				if (!serviceCandidates.contains(candidate)){
					serviceCandidates.add(candidate);
					startPopulationVisualization[serviceClassNumber]++;
				}
				serviceClassNumber++;
			}
		}
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
	public int[] getStartPopulationVisualization() {
		return startPopulationVisualization;
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