package qos;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import jsc.distributions.Beta;
import jsc.distributions.Binomial;
import jsc.distributions.DiscreteUniform;
import jsc.distributions.Gamma;
import jsc.distributions.Uniform;


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
	private static SimpleDateFormat dateFormaFile =
			new SimpleDateFormat("HH_mm_ss");
	private static SimpleDateFormat runDescription =
			new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

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
	private static String filepath;
	private static String filename;
	private static String comment = "";
	private static int sizeTheta;
	private static long N;
	private static int classes;
	private static int candidates;
	
	/*	+-----------------------------------------------------------+
	 * 	| +-------------------------------------------------------+ |
	 * 	| |														  | |
	 * 	| |				   		  MAIN METHOD					  | |
	 * 	| |														  | |
	 * 	| +-------------------------------------------------------+ |
	 * 	+-----------------------------------------------------------+
	 */

	public static void main(String[] args) throws Exception {
		System.out.println(dateFormatLog.format(new Date()) + " Parameter Tuning Mode");
		for (int i=2; i<=9; i++) {
			for (int j=2; j<=9; j++) {
				classes = i;
				candidates = j;
				parameterTuning();
			}
		}		
		//benchmarking();
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
	
	private static void benchmarking() throws Exception {
		//filepath = "/home/ubuntu/";
		filepath = "C:\\temp\\";
		
		if (!new File(filepath).exists())
		{
		   throw new Exception("filepath does not exist.");
		}

		serviceClassesList = new RandomSetGenerator().generateSet(
				(int) 10, (int) 10);
		serviceCandidatesList.clear();
		for (int i = 0 ; i < serviceClassesList.size() ; i++) {
			ServiceClass serviceClass = serviceClassesList.get(i);
		    for (ServiceCandidate serviceCandidate : serviceClass.getServiceCandidateList()) {
		    	serviceCandidatesList.add(serviceCandidate);
		    }
		}

		determineMinMaxQosComposition();

		
		Map<String, Constraint> generatedConstraints = new HashMap<String, Constraint>();
		// linear transformation: 0,35*Beta+0,45
		double relaxationValue = 0.5;

		double maxCost = getRelaxationMaxCost(relaxationValue);
		double maxResponseTime = getRelaxationMaxResponseTime(relaxationValue);
		double minAvailability = getRelaxationMinAvailability(relaxationValue)/100;

				
		// sample weights
		double weightCost = 1;
		double weightResponseTime = 1;
		double weightAvailability = 1;
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
		
		//////////////////////////////////////////////////////
		//////////////////////////////////////////////////////
		
		double[] antAlgorithmSettings = new double[10];

		// Exponential iterations = new Exponential(150);
		int iterations = 100;
		int setAnts = 20;
		double setAlpha = 1;
		int setVariant = 2;
		double setBeta = 1;
		double setDilution = 0.05;
		double setPiInit = 5;
		
		antAlgorithmSettings[0] = 0;
		// row[1] = (int) iterations.random();
		antAlgorithmSettings[1] = iterations;
		antAlgorithmSettings[2] = setAnts;
		antAlgorithmSettings[3] = setVariant;
		antAlgorithmSettings[4] = setAlpha;
		antAlgorithmSettings[5] = setBeta;
		antAlgorithmSettings[6] = setDilution;
		antAlgorithmSettings[7] = setPiInit;
		//row[7] := estimated expected utility
		antAlgorithmSettings[8] = 0;
		//row[8] := estimated expected runtime
		antAlgorithmSettings[9] = 0;

		//////////////////////////////////////////////
		//////////////////////////////////////////////
		
		// Calculate the utility value for all service candidates.
		QosVector qosMaxServiceCandidate = determineQosMaxServiceCandidate(
				serviceCandidatesList);
		QosVector qosMinServiceCandidate = determineQosMinServiceCandidate(
				serviceCandidatesList);
		for (ServiceCandidate serviceCandidate : serviceCandidatesList) {
			serviceCandidate.determineUtilityValue(generatedConstraints,
					qosMaxServiceCandidate, qosMinServiceCandidate);
		}
		
		List<Double> utilityList = new LinkedList<Double>();
		List<Long> runtimeList = new LinkedList<Long>();

		
		long ActualTuningTime = System.currentTimeMillis();
		int N = 1000;
		System.out.println(dateFormatLog.format(new Date()) + " Starting Benchmarking, " +
				"" +N+" runs");
		
		for (int i=0; i<N; i++) {
			// run ant-algorithm

			AntAlgorithm.setParamsAntAlgorithm(
					serviceClassesList, serviceCandidatesList, generatedConstraints,
					(int) antAlgorithmSettings[3], (int) antAlgorithmSettings[1], (int) antAlgorithmSettings[2],
					antAlgorithmSettings[4], antAlgorithmSettings[5],
					antAlgorithmSettings[6], antAlgorithmSettings[7]);
			AntAlgorithm.start();

			double utility = AntAlgorithm.getOptimalUtility();
			// no feasible solution: utility = 0
			if(String.valueOf(utility).compareTo("NaN")==0){
				utility = 0;
			}

			// update estimated expected utility for parameter configuration
			utilityList.add(utility);
//			System.out.print("utility;"+utility);
			// update estimated expected runtime for parameter configuration
			long runtime = AntAlgorithm.getRuntime()/1000000;
			runtimeList.add(runtime);
//			System.out.println(";runtime;"+runtime+";");
			
			// progress
			double progress = (double) i/ (double) N;
			if((progress*100)%10 == 0) {				
				System.out.println("    " + dateFormatLog.format(new Date()) + " Progress = " + (int)(progress*100) + " %");
			}			
		}
		
		
		long elapsed = (System.currentTimeMillis()-ActualTuningTime)/1000;		
		System.out.println(dateFormatLog.format(new Date()) + " Finished Tuning Phase, elapsed time = "+ elapsed + "s");

		// save results		
		filename = filepath + "results " + dateFormaFile.format(new Date())+".csv";
        FileWriter fw = new FileWriter(filename);
    	BufferedWriter bufferedWriter = new BufferedWriter(fw);
    	bufferedWriter.write("run;utility;runtime in ms;");
    	
    	for (int i=0; i<utilityList.size(); i++) {
	    	String line = "";	    	
		    DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
		    dfs.setDecimalSeparator(',');
		    DecimalFormat dFormat = new DecimalFormat("0.0000", dfs);
		    String zahlString = dFormat.format(i);
	    	line+= zahlString +";";
	    	zahlString = dFormat.format(utilityList.get(i));
	    	line+= zahlString +";";
	    	zahlString = dFormat.format(runtimeList.get(i));
	    	line+= zahlString +";";	    	
	    	bufferedWriter.newLine();
	    	bufferedWriter.write(line);
    	}
	    bufferedWriter.close();
		
		System.out.println(dateFormatLog.format(new Date()) + " Saved results to "+filename);

		return;
		
	}

	private static void parameterTuning() throws Exception {
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

		
		filepath = "C:\\temp\\";

		// create filepath-directory
		// cmd: go to path with cd ... , java -jar NAMEOFJARFILE.jar
		// set java filepath
		// for large thetasize the estimateIterations must be reduced, apparently 1 does not work
		

		if (!new File(filepath).exists())
		{
		   throw new Exception("filepath does not exist.");
		}

		comment = "";
				
		sizeTheta = 1;
		int estimateIterations = 10;
		long maxTuningTime = 120;

		// maxTuningTime in h
		// maxTuningTime *= 3600;


		double[][] antAlgorithmSettings = null;
		double[][] antAlgorithmSettingsTemp = new double[sizeTheta][];
		double[] analyticSettings = new double[2];
		 

		Map<String, Constraint> constraintsMap = null;

		System.out.println(dateFormatLog.format(new Date()) + " filepath = " + filepath);

		// Sample/define candidate configurations/settings
		// & Allocate array for storing estimated expected performance of candidates
		if(antAlgo){
				antAlgorithmSettings = sampleAntAlgorithmSettings(sizeTheta);
				for (int i=0; i<antAlgorithmSettings.length; i++) {
					antAlgorithmSettingsTemp[i] = antAlgorithmSettings[i].clone();
				}
		}	

		System.out.println(dateFormatLog.format(new Date()) + " Sample algorithm parameters ("+sizeTheta +")");

		// maximal tuning time in s
		maxTuningTime*=1000000000;
		// estimated average runtime for a single instance   (at the moment: without benchmarking)
		long estimtedRuntimeSingleInstance = 1;
		if(antAlgo){
			estimtedRuntimeSingleInstance = getEstimatedRuntimeSingleInstanceAnt(antAlgorithmSettingsTemp, estimateIterations);
		}		

		long estimatedRuntimeOneRun = estimtedRuntimeSingleInstance/1000000;

		System.out.println(dateFormatLog.format(new Date()) + " EstimtedRuntimeSingleInstance: "+ estimtedRuntimeSingleInstance/1000000/sizeTheta +
				"ms, estimated time for one run (one instance and the whole set of parameter configurations) = "+ estimatedRuntimeOneRun/1000 + "s");

		// determine the max. amount of tests
		N = 100;

		// start parameter tuning
		System.out.println(dateFormatLog.format(new Date()) + " Starting Tuning Phase, " +N+" runs with AntAlgo and AnalyticAlgo");
		System.out.println(dateFormatLog.format(new Date()) + " Size: classes="+classes+", candidates="+candidates);
		long ActualTuningTime = System.currentTimeMillis();
		long progressTimer = ActualTuningTime;
		double progress;
		comment += runDescription.format(new Date())+";";
		for (int k = 1; k <= N; k++){
			// generate random model setups
			constraintsMap = sampleModelSetup();

//			System.out.println(dateFormatLog.format(new Date()) + ": test parameter configurations with model-setup");

			// run ant algorithm
			if(antAlgo){
				antAlgorithmSettings = tuneAntAlgo(antAlgorithmSettings, constraintsMap, k);
				analyticSettings = tuneAnalytic(analyticSettings, constraintsMap, k);
			}
			

			// progress
			if(Math.abs((System.currentTimeMillis()-progressTimer)) > (maxTuningTime/20)/1000000  ) {
				progress = (double)k/(double)N;
				progressTimer = System.currentTimeMillis();
				System.out.println("    " + dateFormatLog.format(new Date()) + " Progress = " + (int)(progress*100) + " %");
			}
		}

		// save results
		long elapsed = (System.currentTimeMillis()-ActualTuningTime)/1000;
		long target = maxTuningTime/1000000000;
		System.out.println(dateFormatLog.format(new Date()) + " Finished Tuning Phase, target runtime = " + target +
				"s, elapsed time = "+ elapsed + "s");
		comment +=runDescription.format(new Date())+";";
		
		
		// save results
		if(antAlgo){
			saveTuningResults(antAlgorithmSettings, analyticSettings, N);
		}
		System.out.println(dateFormatLog.format(new Date()) + " Saved results to "+filename);

		return;
	}

	private static long getEstimatedRuntimeSingleInstanceAnt(double[][] antAlgorithmSettings, int iterations){
		long time = 0;		
		time = System.nanoTime();
		for (int i = 1; i<(iterations+1); i++){
			Map<String, Constraint> constraintsMap = sampleModelSetup();
			@SuppressWarnings("unused")
			double[][] temp = tuneAntAlgo(antAlgorithmSettings, constraintsMap, 1);	
			QosVector qosMaxServiceCandidate = determineQosMaxServiceCandidate(
					serviceCandidatesList);
			QosVector qosMinServiceCandidate = determineQosMinServiceCandidate(
					serviceCandidatesList);
			for (ServiceCandidate serviceCandidate : serviceCandidatesList) {
				serviceCandidate.determineUtilityValue(constraintsMap,
						qosMaxServiceCandidate, qosMinServiceCandidate);
			}

			AnalyticAlgorithm.initAnalytic(
					serviceClassesList, constraintsMap);
			AnalyticAlgorithm.startInBenchmarkMode();
		}
		time = (System.nanoTime() - time);
		time/=iterations;
		// instance must be tested for all parameter configuration -> no division by ParameterConfigurationList.length
		return time;
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

			AntAlgorithm.setParamsAntAlgorithm(
					serviceClassesList, serviceCandidatesList, constraintsMap,
					(int) antAlgorithmParameterConfiguration[3], (int) antAlgorithmParameterConfiguration[1], (int) antAlgorithmParameterConfiguration[2],
					antAlgorithmParameterConfiguration[4], antAlgorithmParameterConfiguration[5],
					antAlgorithmParameterConfiguration[6], antAlgorithmParameterConfiguration[7]);
			AntAlgorithm.start();

			double utility = AntAlgorithm.getOptimalUtility();
			// no feasible solution: utility = 0
			if(String.valueOf(utility).compareTo("NaN")==0){
				utility = 0;
			}

			// update estimated expected utility for parameter configuration
			antAlgorithmSettings[index][8]= updateMean(antAlgorithmSettings[index][8],
					utility, instanceNumber);
			//System.out.print("Ants: utility:"+utility);
			// update estimated expected runtime for parameter configuration
			double runtime = AntAlgorithm.getRuntime()/1000000;
			antAlgorithmSettings[index][9]= (double) updateMean(antAlgorithmSettings[index][9],
					(double) runtime, instanceNumber);
			//System.out.println(";runtime:"+runtime+";");
			index++;			
		}
		return antAlgorithmSettings;
	}
	
	private static double[] tuneAnalytic(double[] analyticSettings, Map<String, Constraint> constraintsMap, int instanceNumber){

		// Calculate the utility value for all service candidates.
		QosVector qosMaxServiceCandidate = determineQosMaxServiceCandidate(
				serviceCandidatesList);
		QosVector qosMinServiceCandidate = determineQosMinServiceCandidate(
				serviceCandidatesList);
		for (ServiceCandidate serviceCandidate : serviceCandidatesList) {
			serviceCandidate.determineUtilityValue(constraintsMap,
					qosMaxServiceCandidate, qosMinServiceCandidate);
		}

		AnalyticAlgorithm.initAnalytic(
				serviceClassesList, constraintsMap);
		AnalyticAlgorithm.startInBenchmarkMode();

		double utility = AnalyticAlgorithm.getOptimalUtility();
		// no feasible solution: utility = 0
		if(String.valueOf(utility).compareTo("NaN")==0){
			utility = 0;
		}

		// update estimated expected utility for parameter configuration
		analyticSettings[0]= updateMean(analyticSettings[0],
				utility, instanceNumber);
		//System.out.print("Analytic: utility:"+utility);
		// update estimated expected runtime for parameter configuration
		double runtime = AnalyticAlgorithm.getRuntime()/1000000;
		analyticSettings[1]= (double) updateMean(analyticSettings[1],
				(double) runtime, instanceNumber);
		//System.out.println(";runtime:"+AnalyticAlgorithm.getRuntime()+";");

		return analyticSettings;
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

		
		serviceClassesList = new RandomSetGenerator().generateSet(
				classes, candidates);
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
		// linear transformation: 0,35*Beta+0,45
		double relaxationValue = relaxation.random()*0.35+0.45;

		double maxCost = getRelaxationMaxCost(relaxationValue);
		double maxResponseTime = getRelaxationMaxResponseTime(relaxationValue);
		double minAvailability = getRelaxationMinAvailability(relaxationValue)/100;

		Uniform randomVariable = new Uniform (0,1);
		
		// sample weights
		double weightCost = randomVariable.random();
		double weightResponseTime = randomVariable.random();
		double weightAvailability = randomVariable.random();
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
	
	private static double[][] sampleAntAlgorithmSettings(int thetaSetSize) {
		 /* Ant algorithm - parameter distribution details:
		 * 		iterations = 100													[Graf 2003, p.86]
		 * 		ants = 20															[Dorigo und Gambardella 1997, p.57/58]
		 * 		variants = [1,6], discrete: DiscreteUniform
		 * 		alpha in [0; infinite], continuous: Gamma(1, 1.5) 	>Mean: 1.5		[Yuan et al 2011, p.85]
		 * 		beta in [0; infinite], continuous: Gamma(1, 2)		>Mean: 2		[Yuan et al 2011, p.85]
		 * 		dilution in [0;1], continuous Beta(2,5) 			>Mean: 0.1 		[Graf 2003, p.85]
		 * 		piInit in [0; infinite], continuous Gamma(2.5, 2)	>Mean: 5		[Quelle??]
		*/


		// Birattari p.85: sampling/discretizing Theta
		// Birattari p.140:sample-values for Theta


		double[][] antAlgorithmSettings = new double[thetaSetSize][10];

		// Exponential iterations = new Exponential(150);
		int iterations = 100;
		Gamma alpha = new Gamma(1,1.5);
		DiscreteUniform variant = new DiscreteUniform (1,4);
		Gamma beta = new Gamma(1,2);
		Beta dilution = new Beta(1,8);
		Uniform piInit = new Uniform(0,10);

		int setAnts = 20;
		double setAlpha;
		int setVariant;
		double setBeta;
		double setDilution;
		double setPiInit;

		for(int r = 0; r<antAlgorithmSettings.length; r++){
			setVariant = 1;
			setAlpha = 1;
			setBeta = 2;
			setDilution = 0.05;
			setPiInit = 5.0;

			antAlgorithmSettings[r][0] = r;
			// row[1] = (int) iterations.random();
			antAlgorithmSettings[r][1] = iterations;
			antAlgorithmSettings[r][2] = setAnts;
			antAlgorithmSettings[r][3] = setVariant;
			antAlgorithmSettings[r][4] = setAlpha;
			antAlgorithmSettings[r][5] = setBeta;
			antAlgorithmSettings[r][6] = setDilution;
			antAlgorithmSettings[r][7] = setPiInit;
			//row[7] := estimated expected utility
			antAlgorithmSettings[r][8] = 0;
			//row[8] := estimated expected runtime
			antAlgorithmSettings[r][9] = 0;					
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


	private static void saveTuningResults(double[][] antResults, double[] analyticResults, 
			long N) throws IOException{
		filename = filepath + "results " + classes+"_"+candidates+".csv";
        FileWriter fw = new FileWriter(filename);
    	BufferedWriter bufferedWriter = new BufferedWriter(fw);
    	bufferedWriter.write(comment+";runs:"+N+";classes: "+classes+";candidates: "+candidates);
    	bufferedWriter.newLine();    	
    	bufferedWriter.write("Algorithm;e(utility);e(runtime in ms);");
    	
    	// AntAlgo
    	String line = "";
    	line += "AntAlgorithm;";
    	DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
    	dfs.setDecimalSeparator(',');
    	DecimalFormat dFormat = new DecimalFormat("0.0000", dfs);
    	String zahlString = dFormat.format(antResults[0][8]);
    	line+= zahlString +";";
    	zahlString = dFormat.format(antResults[0][9]);
    	line+= zahlString +";";
    	bufferedWriter.newLine();
    	bufferedWriter.write(line);
    	
    	// AnalyticAlgo
    	line = "AnalyticAlgo;";    	
    	zahlString = dFormat.format(analyticResults[0]);
    	line+= zahlString +";";
    	zahlString = dFormat.format(analyticResults[1]);
    	line+= zahlString +";";
    	bufferedWriter.newLine();
    	bufferedWriter.write(line);

    	bufferedWriter.close();
}
}
