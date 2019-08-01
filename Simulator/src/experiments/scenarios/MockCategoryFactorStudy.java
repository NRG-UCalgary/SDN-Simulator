package experiments.scenarios;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import experiments.testbeds.Dumbbell;
import experiments.testbeds.Testbed;
import experiments.traffic.TrafficGenerator;
import utility.Debugger;
import utility.Keywords;
import utility.Statistics;

public class MockCategoryFactorStudy extends Scenario {

	public MockCategoryFactorStudy() {
		super("Mock Category Study", "Test Category Factor");
	}

	public void executeTest() {
		LinkedHashMap<String, LinkedHashMap<String, Statistics>> result = new LinkedHashMap<String, LinkedHashMap<String, Statistics>>();
		ArrayList<Short> secondFactorValues = new ArrayList<Short>();
		secondFactorValues.add(Keywords.Testbeds.Types.LAN);
		secondFactorValues.add(Keywords.Testbeds.Types.WAN);
		for (short networkDelayType : secondFactorValues) {
			Debugger.debugToConsole("------------ Network Type is: " + networkDelayType);
			LinkedHashMap<String, Statistics> StudyStats = new LinkedHashMap<String, Statistics>();
			TrafficGenerator trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			Testbed testbed = new Dumbbell(networkDelayType);
			trafficGen.setFlowInterArrivalTimeProperties(Keywords.RandomVariableGenerator.Distributions.Exponential,
					100000, 10000);
			trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.Guassian, 10000, 10);
			for (float numberOfFlows = 1; numberOfFlows <= 5; numberOfFlows++) {
				Debugger.debugToConsole("-------------------- Number Of Flows: " + numberOfFlows);
				trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
						numberOfFlows, numberOfFlows);
				StudyStats.put(Float.toString(numberOfFlows), testbed.executeSimulation(trafficGen.generateTraffic()));
			}
			switch (networkDelayType) {
			case Keywords.Testbeds.Types.LAN:
				result.put("LAN", StudyStats);
				break;
			case Keywords.Testbeds.Types.WAN:
				result.put("WAN", StudyStats);
				break;
			default:
				break;
			}

		}

		generateCategoryFactorOutput(result);
		Debugger.debugOutPut();
	}

}
