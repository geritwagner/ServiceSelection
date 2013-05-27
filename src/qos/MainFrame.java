package qos;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jsc.distributions.Beta;
import jsc.distributions.Binomial;
import jsc.distributions.Gamma;
import jsc.onesample.Ttest;


public class MainFrame {		
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				   		  VARIABLES				 		  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */

	
	// Formats
	private static SimpleDateFormat dateFormatLog = 
		new SimpleDateFormat("HH:mm:ss: ");
	
	// Lists
	private static List<ServiceClass> serviceClassesList = 
		new LinkedList<ServiceClass>();
	private static List<ServiceCandidate> serviceCandidatesList = 
		new LinkedList<ServiceCandidate>();
	
	private static double[][] antAlgorithmSettings = null;
	private static double[][] geneticAlgorithmSettings = null;
	private static Map<String, Constraint> constraintsMap = null;
	private static double[][] C = null;
	private static boolean[] S = null;
	private static int SLength;
	private static int experimentsSoFar;
	private static int sizeThetaStart;
	private static int maxRunsM;
	private static int maxInstances;
	private static int instancesSoFar;

	
	// Integer & Double
	private static int maxCosts = 10000;
	private static int maxResponseTime = 10000;
	private static int maxAvailability = 100;
	private static int minCosts = 0;
	private static int minResponseTime = 0;
	private static int minAvailability = 0;
	private static double actualParameterTuningOptimalityResult = 100;
	private static String filename;
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				   		  MAIN METHOD					  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */
	
	public static void main(String[] args) throws IOException {
		parameterTuning();
	}
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				     CONSTRAINT METHODS					  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */
	
	private static double getRelaxationMinAvailability(double r){
		return r* (minAvailability - maxAvailability) + maxAvailability;
	}
	
	private static double getRelaxationMaxResponseTime(double r){
		return r*(maxResponseTime - minResponseTime) + minResponseTime;
	}
	
	private static double getRelaxationMaxCost(double r){
		return r*(maxCosts - minCosts) + minCosts;
	}
	
		// The values are computed according to the approach of Gao et al., which
		// can be found under "4. Simulation Analysis" in their paper 
		// "Qos-aware Service Composition based on Tree-Coded Genetic 
		// Algorithm".
	private static void determineMinMaxQosComposition(){
		QosVector qosMaxComposition = determineQosMaxComposition(
				serviceClassesList);
		QosVector qosMinComposition = determineQosMinComposition(
				serviceClassesList);
		maxCosts = (int) Math.ceil(qosMaxComposition.getCosts());
		minCosts = (int) Math.floor(qosMinComposition.getCosts());
		maxResponseTime = (int) Math.ceil(qosMaxComposition.getResponseTime());
		minResponseTime = (int) Math.floor(
				qosMinComposition.getResponseTime());
		maxAvailability = (int) Math.ceil(
				qosMaxComposition.getAvailability() * 100);
		minAvailability = (int) Math.floor(
				qosMinComposition.getAvailability() * 100);
	}


	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |		 	     PARAMETER TUNING METHODS			 	  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */
	
	private static void parameterTuning() throws IOException {
		System.out.println(dateFormatLog.format(new Date()) + " Parameter Tuning Mode");
		/* Parameter-tuning algorithm: BRUTUS (Birattari 2009, pp.101)
		 * F-RACE (Birattari 2009, pp.103) is a possible extension which is structurally similar to BRUTUS.
		 * Pseudo-Code: F-RACE

					function generic race(M,use test)
						# Number of experiments performed so far
						experiments soFar = 0
						# Number of instances considered so far
						instances soFar = 0
						# Allocate array for storing observed
						# performance of candidates
						C = allocate array(max instances, |Theta|)
						# Surviving candidates
						S = Theta
						while(experiments soFar + |S| <= M and
						instances soFar+ 1 <= max instances) do
						# Sample an instance according to PI
						i = sample instance()
						instances soFar+= 1
						foreach theta in S do
						# Run candidate theta on instance i
						s = run experiment(theta, i)
						experiments soFar+= 1
						# Evaluate solution and store result
						C[instances soFar, theta] = evaluate solution(s)
						done
						# Drop inferior candidates according to
						# the given statistical test
						S = drop candidates(S, C, use test)
						done

			possible improvements: "Improvement strategies for the F-Race algorithm: sampling design and iterative refinement"
		 */
		
		boolean antAlgo = true;
		boolean geneticAlgo = false;
		
		sizeThetaStart = 10;
		maxRunsM = 10000;
		maxInstances = 10000;
		filename = "C:\\Users\\Gerit\\Downloads\\temp\\results.csv";
		
		// generic dimensions: [instances][id, parameters, runtime, utility]
		
		antAlgorithmSettings = sampleAntAlgorithmSettings(maxInstances, sizeThetaStart);
		
		C = new double[(int) maxInstances][sizeThetaStart];
		
		S = new boolean[sizeThetaStart];
		for (int i = 0; i < S.length; i++) {
			S[i] = true;
		}
		
		SLength = sizeThetaStart;
		
		experimentsSoFar = 0;
		instancesSoFar = 0;
		
		// start parameter tuning
		System.out.println(dateFormatLog.format(new Date()) + " tuning phase, starting with: " + sizeThetaStart);
		long ActualTuningTime = System.currentTimeMillis();
		
		while ((experimentsSoFar + SLength <= maxRunsM) && (instancesSoFar+1 <= maxInstances)){

			// generate random model setups
			sampleModelSetup();
			
			instancesSoFar++;
			
			System.out.println(dateFormatLog.format(new Date()) + ": test parameter configurations with new model-setup");
			
			// run ant algorithm
			if(antAlgo){
				tuneAntAlgo(instancesSoFar);
			}
			
			// run genetic algorithm
//			if(geneticAlgo){
//				geneticAlgorithmSettings = tuneGeneticAlgo(geneticAlgorithmSettings);
//			}
			// alle 10 instance-durchl�ufe: Hypothesentest & bei drops SLength aktualisieren (true z�hlen)
			if(antAlgo && instancesSoFar%3==0){
				dropAntCandidates();
			}
			
//			// run genetic algorithm
//			if(geneticAlgo){
//				geneticAlgorithmSettings = dropGeneticCandidates(geneticAlgorithmSettings, constraintsMap);
//			}
		}
		
		long elapsed = (System.currentTimeMillis()-ActualTuningTime)/1000;
		System.out.println(dateFormatLog.format(new Date()) + " finished tuning phase, elapsed time="+elapsed);
		
		// save results
		saveTuningResults(antAlgorithmSettings, antAlgo);
		System.out.println(dateFormatLog.format(new Date()) + "saved results to "+filename);
		return;
	}
	
	

	private static void dropAntCandidates(){
		// find highest expected utility as a reference \mu
		double highestExpectedUtility = antAlgorithmSettings[0][7];
		for(int t = 0; t<antAlgorithmSettings.length; t++){
			if(antAlgorithmSettings[t][7]>highestExpectedUtility){
				highestExpectedUtility = antAlgorithmSettings[t][7];
			}
		}
		
		System.out.println("highestExpectedUtility"+highestExpectedUtility);
		
		// for all parameter configurations in theta
		for (int p = 0; p<C[0].length;p++){
			// only if S[p] is still a candidate
			if(S[p]!=true) {continue;}
			// get x[]
			double[] x = new double[instancesSoFar];
			for(int k = 0; k<instancesSoFar; k++){
				x[k] =C[k][p];
			}
			// get test statistic t (t-test)
			Ttest test = new Ttest(x, highestExpectedUtility);
			double tStatistic = test.getSP();
			System.out.println("t-Statistik: "+tStatistic);
		}
		
		
	}
	
	
	private static double[][] tuneGeneticAlgo(double[][] geneticAlgorithmSettings){
		// to do
		return null;
	}
	
	private static void tuneAntAlgo(int instancesSoFar){

		for (int i = 0; i< sizeThetaStart; i++){
			
			if(S[i] != true) {continue;}

			int antVariant = 1;

			AntAlgorithm.setParamsAntAlgorithm(
					serviceClassesList, serviceCandidatesList, constraintsMap,
					antVariant, (int) antAlgorithmSettings[i][1], (int) antAlgorithmSettings[i][2], 
					antAlgorithmSettings[i][3], antAlgorithmSettings[i][4],
					antAlgorithmSettings[i][5], antAlgorithmSettings[i][6]);
			AntAlgorithm.start();
			
			double utility = AntAlgorithm.getOptimalUtility();
			// no feasible solution: utility = 0
			if(String.valueOf(utility).compareTo("NaN")==0){
				utility = 0;
			}
			
			// update estimated expected utility for parameter configuration
			antAlgorithmSettings[instancesSoFar][7]= updateMean(antAlgorithmSettings[i][7], 
					utility, instancesSoFar);
			System.out.print("utility;"+utility);
			// update estimated expected runtime for parameter configuration
			long runtime = AntAlgorithm.getRuntime()/1000000;
			antAlgorithmSettings[instancesSoFar][8]= (double) updateMean(antAlgorithmSettings[i][8], 
					(double) runtime, instancesSoFar);
			System.out.println(";runtime;"+runtime+";");
			experimentsSoFar++;
			
			C[instancesSoFar][i] = utility;
			
		}

	}
	

	private static double updateMean(double expected,
			double additionalValue, int instanceNumber) {
		return (expected*(instanceNumber-1)+additionalValue)/instanceNumber;
	}

	private static void sampleModelSetup() {
		 /* Model setup distributions:
		 * 		(no analytic solution, few classes with many candidates would also be feasible)
		 * 		number of classes in [1;20], discrete: Binomial(20, 0.5)
		 * 															>Mean: 10
		 * 		number of candidates in [1;20], discrete: Binomial(20, 0.5) 
		 * 															>Mean: 10
		 * 		restrictions in [0.6; 1.0], continuous: Beta(2,2) -> (linear transformation: 2/5*Beta+0,6) 
		 * 															>Mean: 0.8
		 */
		// Classes & Candidates
		// with analytic solution:
//		Binomial numberOfServiceClasses = new Binomial(10, 0.7);
//		Binomial numberOfWebServices = new Binomial(10, 0.7);	
		
		Binomial numberOfServiceClasses = new Binomial(20, 0.5);
		Binomial numberOfWebServices = new Binomial(20, 0.5);
		serviceClassesList = new RandomSetGenerator().generateSet(
				(int) numberOfServiceClasses.random(), (int) numberOfWebServices.random());
		serviceCandidatesList.clear(); 
		for (int i = 0 ; i < serviceClassesList.size() ; i++) {
			ServiceClass serviceClass = serviceClassesList.get(i);  
		    for (ServiceCandidate serviceCandidate : serviceClass.getServiceCandidateList()) {
		    	serviceCandidatesList.add(serviceCandidate);
		    }
		}
	    
		determineMinMaxQosComposition();		
		
		// restrictions
		Beta relaxation = new Beta(2,2);
		Map<String, Constraint> generatedConstraints = new HashMap<String, Constraint>();
		// linear transformation: 2/5*Beta+0,6
		double relaxationValue = relaxation.random()*2/5+0.6;
		
		double maxCost = getRelaxationMaxCost(relaxationValue);
		double maxResponseTime = getRelaxationMaxResponseTime(relaxationValue);
		double minAvailability = getRelaxationMinAvailability(relaxationValue)/100;
		
		// sample weights
		double weightCost = Math.random();
		double weightResponseTime = Math.random();
		double weightAvailability = Math.random();
		double weightSum = weightCost+weightResponseTime+weightAvailability;
		//normalize weights
		weightCost = (weightCost/weightSum)*100;
		weightResponseTime = (weightResponseTime/weightSum)*100;
		weightAvailability = (weightAvailability/weightSum)*100;
		
		
		// generate constraints
		Constraint constraintCosts = new Constraint(Constraint.COSTS, 
				maxCost, 
				weightCost);
		generatedConstraints.put(constraintCosts.getTitle(), constraintCosts);
		
		Constraint constraintResponseTime = new Constraint(Constraint.RESPONSE_TIME, 
				maxResponseTime, 
				weightResponseTime);
		generatedConstraints.put(constraintResponseTime.getTitle(), constraintResponseTime);
		
		Constraint constraintAvailability = new Constraint(Constraint.AVAILABILITY, 
				minAvailability, 
				weightAvailability);
		generatedConstraints.put(constraintAvailability.getTitle(), constraintAvailability);
		
//		System.out.println(dateFormatLog.format(new Date()) + ": Sample model-setup (relaxation="+relaxationValue+")");		
//		System.out.println(constraintAvailability.toString()+";"+constraintCosts+";"+constraintResponseTime+";");
		
		constraintsMap = generatedConstraints;
		
		// Calculate the utility value for all service candidates.
		  QosVector qosMaxServiceCandidate = determineQosMaxServiceCandidate(
		    serviceCandidatesList);
		  QosVector qosMinServiceCandidate = determineQosMinServiceCandidate(
		    serviceCandidatesList);
		  for (ServiceCandidate serviceCandidate : serviceCandidatesList) {
		   serviceCandidate.determineUtilityValue(constraintsMap, 
		     qosMaxServiceCandidate, qosMinServiceCandidate);
		  }
		
	}


	private static double[][] sampleGeneticAlgorithmSettings(int thetaSetSize) {
		 /* Genetic algorithm - parameter distribution details:
		  * 	population size
		  * 	elitism rate
		  * 	crossover-rate
		  * 	mutation rate
		  * 
		*/	 
		
		// Birattari p.85: sampling/discretizing Theta
		// Birattari p.140:sample-values for Theta		
		
		double[][] geneticAlgorithmSettings = new double[thetaSetSize][7];
		
		// define parameter distributions
/*
		int id = 1;
		for(double[] row : geneticAlgorithmSettings){
			row[0] = id;
			id++;
			// row[1] = (int) iterations.random();
			row[1] = (int) populationSize.random();
			row[2] = elitismRate.random();
			row[3] = crossoverRate.random();
			row[4] = mutationRate.random();
			//row[5] := estimated expected utility
			//row[6] := estimated expected runtime
		}	
*/
		return geneticAlgorithmSettings;
	}


	private static double[][] sampleAntAlgorithmSettings(long maxInsances, int thetaSetSize) {
		 /* Ant algorithm - parameter distribution details:
		 * 		iterations in [100; 10000], discrete: Binomial(10000, 0.01)
		 * 															>Mean: 1,000	[Graf 2003, p.86]
		 * 		ants in [1, 30], discrete: Binomial(1000;0.015)		>Mean: 15		[Dorigo und Gambardella 1997, p.57/58]
		 * 		alpha in [0; infinite], continuous: Gamma(2, 1.33) 	>Mean: 1.5		[Yuan et al 2011, p.85]
		 * 		beta in [0; infinite], continuous: Gamma(2, 1)		>Mean: 2		[Yuan et al 2011, p.85]
		 * 		dilution in [0;1], continuous Beta(2,5) 			>Mean: 0.1 		[Graf 2003, p.85]
		 * 		pi in [0; infinite], continuous Gamma(5, 1)			>Mean: 5		[Quelle??]
		*/	 
		

		// Birattari p.85: sampling/discretizing Theta
		// Birattari p.140:sample-values for Theta		
		
		
		double[][] antAlgorithmSettings = new double[thetaSetSize][9];
		
		// Exponential iterations = new Exponential(150);
		int iterations = 100;
		Binomial ants = new Binomial(1000, 0.015);
		Gamma alpha = new Gamma(2, 2);
		Gamma beta = new Gamma(2,2);
		Beta dilution = new Beta(2, 5);
		Gamma piInit = new Gamma(5, 1);
		
		// to do : does rounding alpha and beta to integers make a difference? (inefficient taylor series) 
		
		int setAnts;
		double setAlpha;
		double setBeta;
		double setDilution;
		double setPiInit;
		
		
		for(int r = 0; r< thetaSetSize; r=r+2){
				setAnts = (int) ants.random();
				setAlpha = (double) alpha.random();
				setBeta = (double) beta.random();
				setDilution = (double) dilution.random();
				setPiInit = (double) piInit.random();		
				
				antAlgorithmSettings[r][0] = r;
				// row[1] = (int) iterations.random();
				antAlgorithmSettings[r][1] = iterations;
				antAlgorithmSettings[r][2] = setAnts;
				antAlgorithmSettings[r][3] = setAlpha;
				antAlgorithmSettings[r][4] = setBeta;
				antAlgorithmSettings[r][5] = setDilution;
				antAlgorithmSettings[r][6] = setPiInit;
				//row[7] := estimated expected utility
				antAlgorithmSettings[r][7] = 0;
				//row[8] := estimated expected runtime
				antAlgorithmSettings[r][8] = 0;
				// round alpha and beta
				antAlgorithmSettings[r+1][0] = r+1;
				// row[1] = (int) iterations.random();
				antAlgorithmSettings[r+1][1] = iterations;
				antAlgorithmSettings[r+1][2] = setAnts;
				antAlgorithmSettings[r+1][3] = Math.round(setAlpha);
				antAlgorithmSettings[r+1][4] = Math.round(setBeta);
				antAlgorithmSettings[r+1][5] = setDilution;
				antAlgorithmSettings[r+1][6] = setPiInit;
				//row[7] := estimated expected utility
				antAlgorithmSettings[r+1][7] = 0;
				//row[8] := estimated expected runtime
				antAlgorithmSettings[r+1][8] = 0;
		}
		return antAlgorithmSettings;
	}
	
	// Determine the maximum value for each QoS attribute over all 
	// service candidates given to the method.
	// Note that "maximum" really means "maximum" and not "best".
	private static QosVector determineQosMaxServiceCandidate(
			List<ServiceCandidate> serviceCandidates) {
		QosVector max = new QosVector(0.0, 0.0, 0.0);
		for (ServiceCandidate serviceCandidate : serviceCandidates) {
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
		return max;
	}
	
	// Determine the minimum value for each QoS attribute over all 
	// service candidates given to the method.
	// Note that "minimum" really means "minimum" and not "worst".
	private static QosVector determineQosMinServiceCandidate(
			List<ServiceCandidate> serviceCandidates) {
		QosVector min = new QosVector(100000.0, 100000.0, 1.0);
		for (ServiceCandidate serviceCandidate : serviceCandidates) {
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
		return min;
	}
	
	// Determine the maximum value for each QoS attribute that can 
	// be obtained by always selecting the maximum values of the 
	// service candidates of each service class.
	// Note that "maximum" really means "maximum" and not "best".
	private static QosVector determineQosMaxComposition(
			List<ServiceClass> serviceClasses) {
		double costs = 0.0;
		double responseTime = 0.0;
		double availability = 1.0;
		QosVector maxServiceCandidate;
		for (ServiceClass serviceClass : serviceClasses) {
			maxServiceCandidate = determineQosMaxServiceCandidate(
					serviceClass.getServiceCandidateList());
			costs += maxServiceCandidate.getCosts();
			responseTime += maxServiceCandidate.getResponseTime();
			availability *= maxServiceCandidate.getAvailability();
		}
		return new QosVector(costs, responseTime, availability);
	}
	
	// Determine the minimum value for each QoS attribute that can 
	// be obtained by always selecting the minimum values of the 
	// service candidates of each service class.
	// Note that "minimum" really means "minimum" and not "worst".
	private static QosVector determineQosMinComposition(
			List<ServiceClass> serviceClasses) {
		double costs = 0.0;
		double responseTime = 0.0;
		double availability = 1.0;
		QosVector minServiceCandidate;
		for (ServiceClass serviceClass : serviceClasses) {
			minServiceCandidate = determineQosMinServiceCandidate(
					serviceClass.getServiceCandidateList());
			costs += minServiceCandidate.getCosts();
			responseTime += minServiceCandidate.getResponseTime();
			availability *= minServiceCandidate.getAvailability();
		}
		return new QosVector(costs, responseTime, availability);
	}
	
	public static double getActualParameterTuningOptimalityResult() {
		return actualParameterTuningOptimalityResult;
	}

	public static void setActualParameterTuningOptimalityResult(
			double utility) {
		actualParameterTuningOptimalityResult = utility;
	}
	
	private static void saveTuningResults(double[][] results, boolean antAlgo) throws IOException{
        FileWriter fw = new FileWriter(filename);
    	BufferedWriter bufferedWriter = new BufferedWriter(fw);	
    	if(antAlgo){bufferedWriter.write("id;iterations;ants;alpha;beta;dilution;piInit;e(utility);e(runtime in ms);");}
    	if(!antAlgo){bufferedWriter.write("id; INSERT COLUMN DESCRIPTIONS");}
    	for (double[] row : results) {	
	    	String line = "";
	    	for (double cell: row){
		    	DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
		    	dfs.setDecimalSeparator(',');
		    	DecimalFormat dFormat = new DecimalFormat("0.0000", dfs);
		    	String zahlString = dFormat.format(cell);
		
		    	line+= zahlString +";";
	    	}
	    	bufferedWriter.newLine();
	    	bufferedWriter.write(line);
    	}
	    bufferedWriter.close();
	}

}