package qos;

import java.util.LinkedList;

public class AlgorithmSolutionTier {
	
	private LinkedList<Composition> serviceCompositions;
	private int tierId;
	
	public AlgorithmSolutionTier(
			LinkedList<Composition> serviceCompositions, int tierId) {
		this.serviceCompositions = serviceCompositions;
		this.tierId = tierId;
	}
	
	public void setServiceCompositionList(
			LinkedList<Composition> serviceCompositions) {
		this.serviceCompositions = serviceCompositions;
	}
	public LinkedList<Composition> getServiceCompositionList() {
		return serviceCompositions;
	}
	public void setTierTitle(int tierId) {
		this.tierId = tierId;
	}
	public int getTierTitle() {
		return tierId;
	}

}
