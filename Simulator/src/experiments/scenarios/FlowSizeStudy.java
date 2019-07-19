package experiments.scenarios;

import system.utility.Keywords;

public class FlowSizeStudy extends Scenario {

	public FlowSizeStudy() {
		super(Keywords.Outputs.Scenarios.Names.FlowSizeStudy,
				Keywords.Outputs.Charts.MainFactors.Titles.FlowSizeFactor);
	}

	public void executeTest(int flowSizeDistribution, double averageFlowSize, double minFlowSize, double maxFlowSize,
			double hops, int networkType, int networkTopology, int trafficType) {
	}

}
