package experiments.scenarios.onefactor;

import experiments.scenarios.Scenario;

public abstract class OneFactorScenario extends Scenario {

	public OneFactorScenario() {
	}

	public abstract void executeTest(int distributionOfFactor, double averageValueOfFactor, double minValueOfFactor,
			double maxValueOfFactor, double hops, int networkType, int networkTopology, int trafficType);
}
