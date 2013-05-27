package qos;


import java.io.BufferedWriter;
import java.io.File;
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


import javax.swing.JFrame;
import javax.swing.JOptionPane;


import jsc.distributions.Beta;
import jsc.distributions.Binomial;
import jsc.distributions.Gamma;


public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
		
	
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
	private static List<String> saveResultList = new LinkedList<String>();
	

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
		System.out.println(dateFormatLog.format(new Date()) + " Parameter Tuning Mode");
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
		/* Parameter-tuning algorithm: BRUTUS (Birattari 2009, pp.101)
		 * F-RACE (Birattari 2009, pp.103) is a possible extension which is structurally similar to BRUTUS.
		 * Pseudo-Code: BRUTUS

					function Brutus(M)
					# Number of experiments for each candidate
					# M = [T/t] with T:= overall time available for tuning, t:= runtime for a single experiment (0.5s)
					N = floor(M/|Theta|)
					# Allocate array for storing estimated
					# expected performance of candidates
					A = allocate array(|Theta|)
					for (k = 1; k <= N; k++) do
						# Sample an instance according to PI
						i = sample instance()
							foreach theta in Theta do
								# Run candidate theta on instance i
								s = run experiment(theta, i)
								# Evaluate obtained solution
								c = evaluate solution(s)
								# Update estimate of expected
								# performance of candidate theta
								A[Theta] = update mean(A[Theta], c, k)
							done
					done
					# Select best configuration
					theta = which min(A)
					return theta
		 */
		
		boolean antAlgo = true;
		boolean geneticAlgo = false;
		
		filename = "C:\\Users\\Gerit\\Downloads\\temp\\results.csv";
		
		int sizeTheta = 10;
		int estimateIterations = 5;
		
		double[][] antAlgorithmSettings = null;
		double[][] geneticAlgorithmSettings = null;
		
		Map<String, Constraint> constraintsMap = null;
		
		// Sample/define candidate configurations/settings 
		// & Allocate array for storing estimated expected performance of candidates
		if(antAlgo){
			antAlgorithmSettings = sampleAntAlgorithmSettings(sizeTheta);
		}
		
		if(geneticAlgo){
			geneticAlgorithmSettings = sampleGeneticAlgorithmSettings(sizeTheta);
		}
		
		System.out.println("Sample algorithm parameters:");
		for(double[] row : antAlgorithmSettings){
			for(double column : row){
				System.out.print(column +";");}
			System.out.println();
		}
		
		
		// maximal tuning time in s
		long maxTuningTime = 200;
		maxTuningTime*=1000000000;
		// estimated average runtime for a single instance   (at the moment: without benchmarking)
		long estimtedRuntimeSingleInstance = 1;
		if(antAlgo){
			estimtedRuntimeSingleInstance = getEstimatedRuntimeSingleInstanceAnt(antAlgorithmSettings, estimateIterations);
			System.out.println("estimtedRuntimeSingleInstance: "+ estimtedRuntimeSingleInstance/1000000/sizeTheta + 
					"ms, estimated time one run (all instances and the set of parameter configurations)="+estimtedRuntimeSingleInstance/1000000);
		}
		if(geneticAlgo){
			estimtedRuntimeSingleInstance = getEstimatedRuntimeSingleInstanceGenetic(geneticAlgorithmSettings, estimateIterations);	
		}
		
		// determine the max. amount of tests
		long N = (long) Math.floor(maxTuningTime/estimtedRuntimeSingleInstance);
		
		// start parameter tuning
		System.out.println(dateFormatLog.format(new Date()) + " Starting Tuning Phase, Durchläufe:" +N+"*"+sizeTheta);
		long ActualTuningTime = System.currentTimeMillis();
		for (int k = 1; k <= N; k++){
			// generate random model setups
			constraintsMap = sampleModelSetup();
			
			System.out.println(dateFormatLog.format(new Date()) + ": test parameter configurations with model-setup");
			
			// run ant algorithm
			if(antAlgo){
				antAlgorithmSettings = tuneAntAlgo(antAlgorithmSettings, constraintsMap, k);
			}
			
			// run genetic algorithm
			if(geneticAlgo){
				geneticAlgorithmSettings = tuneGeneticAlgo(geneticAlgorithmSettings, constraintsMap, k);
			}
		}
		
		// save results
		long elapsed = (System.currentTimeMillis()-ActualTuningTime)/1000;
		long target = maxTuningTime/1000000000;
		System.out.println(dateFormatLog.format(new Date()) + " Finished Tuning Phase , target runtime=" + target + 
				"s, elapsed time="+elapsed);
		
		// save results
		saveTuningResults(antAlgorithmSettings, antAlgo);
		System.out.println(dateFormatLog.format(new Date()) + "saved results to "+filename);
		
		return;
	}
	
	private static long getEstimatedRuntimeSingleInstanceAnt(double[][] antAlgorithmSettings, int iterations){
		long time = 0;
		time = System.nanoTime();
		for (int i = 1; i<iterations; i++){
			Map<String, Constraint> constraintsMap = sampleModelSetup();
			@SuppressWarnings("unused")
			double[][] temp = tuneAntAlgo(antAlgorithmSettings, constraintsMap, 1);
		}
		time = (System.nanoTime() - time);
		time/=iterations;
		// instance must be tested for all parameter configuration -> no division by ParameterConfigurationList.length
		return time;
	}
	
	private static long getEstimatedRuntimeSingleInstanceGenetic(double[][] geneticAlgorithmSettings, int iterations){
		long time = 0;
		time = System.nanoTime();
		for (int i = 1; i<iterations; i++){
			Map<String, Constraint> constraintsMap = sampleModelSetup();
			@SuppressWarnings("unused")
			double[][] temp = tuneGeneticAlgo(geneticAlgorithmSettings, constraintsMap, 1);
		}
		time = (System.nanoTime() - time);
		time/=iterations;
		// instance must be tested for all parameter configuration -> no division by ParameterConfigurationList.length
		return time;
	}
	
	private static double[][] tuneGeneticAlgo(double[][] antAlgorithmSettings, Map<String, Constraint> constraintsMap, int instanceNumber){
		// to do
		return null;
	}
	
	private static double[][] tuneAntAlgo(double[][] antAlgorithmSettings, Map<String, Constraint> constraintsMap, int instanceNumber){

		// Calculate the utility value for all service candidates.
		  QosVector qosMaxServiceCandidate = determineQosMaxServiceCandidate(
		    serviceCandidatesList);
		  QosVector qosMinServiceCandidate = determineQosMinServiceCandidate(
		    serviceCandidatesList);
		  for (ServiceCandidate serviceCandidate : serviceCandidatesList) {
		   serviceCandidate.determineUtilityValue(constraintsMap, 
		     qosMaxServiceCandidate, qosMinServiceCandidate);
		  }

		int index = 0;		  
		for(double[] antAlgorithmParameterConfiguration : antAlgorithmSettings) {
			// run ant-algorithm
			int antVariant = 1;

			AntAlgorithm.setParamsAntAlgorithm(
					serviceClassesList, serviceCandidatesList, constraintsMap,
					antVariant, (int) antAlgorithmParameterConfiguration[1], (int) antAlgorithmParameterConfiguration[2], 
					antAlgorithmParameterConfiguration[3], antAlgorithmParameterConfiguration[4],
					antAlgorithmParameterConfiguration[5], antAlgorithmParameterConfiguration[6]);
			AntAlgorithm.start();
			
			double utility = AntAlgorithm.getOptimalUtility();
			// no feasible solution: utility = 0
			if(String.valueOf(utility).compareTo("NaN")==0){
				utility = 0;
			}
			
			// update estimated expected utility for parameter configuration
			antAlgorithmSettings[index][7]= updateMean(antAlgorithmSettings[index][7], 
					utility, instanceNumber);
			System.out.print("utility;"+utility);
			// update estimated expected runtime for parameter configuration
			long runtime = AntAlgorithm.getRuntime()/1000000;
			antAlgorithmSettings[index][8]= (double) updateMean(antAlgorithmSettings[index][8], 
					(double) runtime, instanceNumber);
			System.out.println(";runtime;"+runtime+";");
			index++;
		}
		return antAlgorithmSettings;
	}

	private static double updateMean(double expected,
			double additionalValue, int instanceNumber) {
		return (expected*(instanceNumber-1)+additionalValue)/instanceNumber;
	}

	private static Map<String, Constraint> sampleModelSetup() {
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
		
		return generatedConstraints;
		
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


	private static double[][] sampleAntAlgorithmSettings(int thetaSetSize) {
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
		
		int id = 1;
		for(double[] row : antAlgorithmSettings){
			row[0] = id;
			id++;
			// row[1] = (int) iterations.random();
			row[1] = iterations;
			row[2] = (int) ants.random();
			row[3] = (double) alpha.random();
			row[4] = (double) beta.random();
			row[5] = (double) dilution.random();
			row[6] = (double) piInit.random();
			//row[7] := estimated expected utility
			row[7] = 0;
			//row[8] := estimated expected runtime
			row[8] = 0;
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