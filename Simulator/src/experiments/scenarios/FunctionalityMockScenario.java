package experiments.scenarios;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import experiments.testbeds.Dumbbell;
import experiments.testbeds.Testbed;
import experiments.traffic.TrafficGenerator;
import system.utility.Debugger;
import system.utility.Keywords;
import system.utility.Statistics;

public class FunctionalityMockScenario extends Scenario {

	public FunctionalityMockScenario() {
		super("Mock Study For Flow Size Mean", "Number Of Flows");
	}

	public void executeTest() {
		Debugger.debug("=========================================================================");
		Debugger.debug("======== Number Of Flows and Flow Size Mean =============================");
		Debugger.debug("=========================================================================");
		LinkedHashMap<String, TreeMap<Float, Statistics>> result = new LinkedHashMap<String, TreeMap<Float, Statistics>>();
		ArrayList<Float> flowSizeMeans = new ArrayList<Float>();
		flowSizeMeans.add((float) 1000);
		flowSizeMeans.add((float) 5000);
		flowSizeMeans.add((float) 10000);

		for (float flowSizeMean : flowSizeMeans) {
			TreeMap<Float, Statistics> StudyStats = new TreeMap<Float, Statistics>();
			TrafficGenerator trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			Testbed testbed = new Dumbbell(Keywords.Testbeds.Types.LAN);
			trafficGen.setFlowInterArrivalTimeProperties(Keywords.RandomVariableGenerator.Distributions.Exponential,
					100000, 10000);
			trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.LogNormal, flowSizeMean,
					10);
			for (float numberOfFlows = 1; numberOfFlows <= 10; numberOfFlows++) {
				trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
						numberOfFlows, numberOfFlows);
				StudyStats.put(numberOfFlows, testbed.executeSimulation(trafficGen.generateTraffic()));
			}
			result.put("Flow Size Mean = " + (int) flowSizeMean, StudyStats);
		}

		generateNumericalFactorOutput(result);
		Debugger.debugOutPut();
	}

}
