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
				hasPopulationChanged = 
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
			
			// Needed for Fitness Value Convergence
//			setVisualizationValues(population);
			
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
			List<Composition> populationOld) {
		double[] fitnessAreas = new double[populationOld.size()];
		List<Composition> matingPool = new LinkedList<Composition>();
		// Compute the cumulated fitness areas of every composition of the 
		// population.
		for (int i = 0; i < populationOld.size(); i++) {
			fitnessAreas[i] = computeFitness(populationOld.get(i));
			if (i != 0) {
				fitnessAreas[i] += fitnessAreas[i - 1];
			}
		}
		// Save the fitnessAreaSum.
		double fitnessAreaSum = fitnessAreas[populationOld.size() - 1];
		// Randomly select the compositions of the new population with 
		// respect to their fitness values.
		for (int i = 0; i < populationOld.size(); i++) {
			double random = Math.random() * fitnessAreaSum;
			for (int j = 0; j < populationOld.size(); j++) {
				if (random < fitnessAreas[j]) {
					matingPool.add(populationOld.get(j));
					break;
				}
			}
		}
		return matingPool;
	}

	private List<Composition> doSelectionLinearRanking(
			List<Composition> populationOld) {
		double[] fitnessRankAreas = new double[populationOld.size()];
		// sp is short for Selection Pressure; it modifies the size
		// of each rank area and can be chosen between 1.1 and 2.0.
		// The expected sampling rate of the best individual is sp,
		// the expected sampling rate of the worst individual is 2-sp
		// and the selective pressure of all other population members
		// can be interpreted by linear interpolation of the
		// selective pressure according to rank.
		double sp = 2.0;
		Collections.sort(populationOld, new Comparator<Composition>() {
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
		// Compute the accumulated fitness rank areas  
		// of every composition of the population.
		for (int i = 0; i < populationOld.size(); i++) {
			// rank(pos) = 2 - sp + (2 * (sp - 1) * (pos - 1) / (n - 1))
			fitnessRankAreas[i] = 2.0 - sp + 
					(2.0 * (sp - 1.0) * (i / (populationOld.size() - 1.0)));
			if (i != 0) {
				fitnessRankAreas[i] += fitnessRankAreas[i - 1];
			}
		}
		// Save the fitnessRankAreaSum.
		double fitnessRankAreaSum = fitnessRankAreas[populationOld.size() - 1];
		List<Composition> matingPool = new LinkedList<Composition>();
		// Randomly select the compositions of the new population 
		// with respect to their ranks (like Roulette Wheel).
		for (int i = 0; i < populationOld.size(); i++) {
			double random = Math.random() * fitnessRankAreaSum;
			for (int j = 0; j < populationOld.size(); j++) {
				if (random < fitnessRankAreas[j]) {
					matingPool.add(populationOld.get(j));
					break;
				}
			}
		}
		return matingPool;
	}

	private List<Composition> doSelectionBinaryTournament(
			List<Composition> populationOld) {
		List<Composition> matingPool = 
			new LinkedList<Composition>(populationOld);
		// Permute the indices of the population.
		int[] permutationIndices = permuteIndices(populationOld.size());
		// Pairwise comparison between two compositions. The opponents are 
		// determined by the permutation created above.
		for (int i = 0; i < populationOld.size(); i++) {
			if (computeFitness(populationOld.get(i)) < 
					computeFitness(populationOld.get(permutationIndices[i]))) {
				matingPool.set(i, populationOld.get(permutationIndices[i]));
			}
		}
		return matingPool;
	}
	
	private List<Composition> doCrossoverOnePoint(
			List<Composition> matingPool, double crossoverRate) {
		List<Composition> populationNew = new LinkedList<Composition>();
		// If there is only one composition left in the mating pool, simply 
		// add it to the new population.
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
		List<Composition> populationNew = new LinkedList<Composition>();
		// If there is only one composition left in the mating pool, simply 
		// add it to the new population.
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
				// Randomly select the crossover points.
				int crossoverPoint1 = 
						(int) ((Math.random() * 
								(serviceClassesList.size() - 2)) + 1);
				int crossoverPoint2 = (int) ((Math.random() * 
						(serviceClassesList.size() - crossoverPoint1 - 1)) + 
						(crossoverPoint1 + 1));				
				// Do the crossover.
				Composition compositionC = new Composition();
				for (ServiceCandidate serviceCandidate : compositionA.
						getServiceCandidatesList().subList(
								0, crossoverPoint1)) {
					compositionC.addServiceCandidate(serviceCandidate);
				}
				for (ServiceCandidate serviceCandidate : compositionB.
						getServiceCandidatesList().subList(
								crossoverPoint1, crossoverPoint2)) {
					compositionC.addServiceCandidate(serviceCandidate);
				}
				for (ServiceCandidate serviceCandidate : compositionA.
						getServiceCandidatesList().subList(crossoverPoint2, 
								serviceClassesList.size())) {
					compositionC.addServiceCandidate(serviceCandidate);
				} 

				Composition compositionD = new Composition();
				for (ServiceCandidate serviceCandidate : compositionB.
						getServiceCandidatesList().subList(
								0, crossoverPoint1)) {
					compositionD.addServiceCandidate(serviceCandidate);
				}
				for (ServiceCandidate serviceCandidate : compositionA.
						getServiceCandidatesList().subList(
								crossoverPoint1, crossoverPoint2)) {
					compositionD.addServiceCandidate(serviceCandidate);
				}
				for (ServiceCandidate serviceCandidate : compositionB.
						getServiceCandidatesList().subList(crossoverPoint2, 
								serviceClassesList.size())) {
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

	private List<Composition> doCrossoverUniform(
			List<Composition> matingPool, double crossoverRate) {
		List<Composition> populationNew = new LinkedList<Composition>();
		// If there is only one composition left in the mating pool, simply 
		// add it to the new population.
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
				// Do the crossover.
				Composition compositionC = new Composition();
				Composition compositionD = new Composition();
				for (int i = 0; i < 
						compositionA.getServiceCandidatesList().size(); i++) {
					if (Math.random() < 0.5) {
						compositionC.addServiceCandidate(
								compositionA.getServiceCandidatesList().get(i));
						compositionD.addServiceCandidate(
								compositionB.getServiceCandidatesList().get(i));
					}
					else {
						compositionD.addServiceCandidate(
								compositionB.getServiceCandidatesList().get(i));
						compositionC.addServiceCandidate(
								compositionA.getServiceCandidatesList().get(i));
					}
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
	
	private boolean hasPopulationChanged(List<Composition> population, 
			List<Composition> matingPool) {
		int deviation = population.size() - 
				(int) Math.round(population.size() * maxDeviation / 100.0 );
		for (int i = 0; i < matingPool.size(); i++) {
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
		if (deviation <= 0) {
			return true;
		}
		return false;
	}

	private void setVisualizationValues(List<Composition> population) {
		List<Composition> differentSolutions = new LinkedList<Composition>();
		double maxFitness = 0.0;
		double averageFitness = 0.0;
		for (int i = 0; i < population.size(); i++) {
			if (!differentSolutions.contains(population.get(i))) {
				differentSolutions.add(population.get(i));
			}
			if (computeFitness(population.get(i)) > maxFitness) {
				maxFitness = computeFitness(population.get(i));
			}
			averageFitness += computeFitness(population.get(i));
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