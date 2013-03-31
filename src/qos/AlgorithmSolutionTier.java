package qos;

import java.util.List;

public class AlgorithmSolutionTier {
	
	private List<Composition> serviceCompositions;
	private int tierId;
	
	public AlgorithmSolutionTier(
			List<Composition> serviceCompositions, int tierId) {
		this.serviceCompositions = serviceCompositions;
		this.tierId = tierId;
	}
	
	public boolean containsComposition(Composition composition) {
		for (Composition oldComposition : serviceCompositions) {
			if (oldComposition.equals(composition)) {
				return true;
			}
		}
		return false;
	}
	
	public void setServiceCompositionList(
			List<Composition> serviceCompositions) {
		this.serviceCompositions = serviceCompositions;
	}
	public List<Composition> getServiceCompositionList() {
		return serviceCompositions;
	}
	public void setTierTitle(int tierId) {
		this.tierId = tierId;
	}
	public int getTierTitle() {
		return tierId;
	}

}
