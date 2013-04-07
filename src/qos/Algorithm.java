package qos;

import java.util.List;

public abstract class Algorithm {
	
	public abstract void start();
	public abstract List<AlgorithmSolutionTier> getAlgorithmSolutionTiers();
	public abstract long getRuntime();

}
