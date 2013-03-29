package qos;

import java.util.List;

import javax.swing.JProgressBar;

public abstract class Algorithm {
	
	public abstract void start(JProgressBar progressBar);
	public abstract List<AlgorithmSolutionTier> getAlgorithmSolutionTiers();

}
