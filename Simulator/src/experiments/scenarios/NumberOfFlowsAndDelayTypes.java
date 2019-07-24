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

public class NumberOfFlowsAndDelayTypes extends Scenario {

	public NumberOfFlowsAndDelayTypes() {
		super("Number Of Flows for WAN and LAN", "Number Of Flows");
	}

	public void executeTest() {
		Debugger.debugToConsole("=========================================================================");
		Debugger.debugToConsole("======== Number Of Flows in LAN and WAN =================================");
		Debugger.debugToConsole("=========================================================================");
		LinkedHashMap<String, TreeMap<Float, Statistics>> result = new LinkedHashMap<String, TreeMap<Float, Statistics>>();
		ArrayList<Short> secondFactorValues = new ArrayList<Short>();
		secondFactorValues.add(Keywords.Testbeds.Types.LAN);
		secondFactorValues.add(Keywords.Testbeds.Types.WAN);
		for (short networkDelayType : secondFactorValues) {
			Debugger.debugToConsole("------------ Network Type is: " + networkDelayType);
			TreeMap<Float, Statistics> StudyStats = new TreeMap<Float, Statistics>();
			TrafficGenerator trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			Testbed testbed = new Dumbbell(networkDelayType);
			trafficGen.setFlowInterArrivalTimeProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
					0, 0);
			trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.Constant, 10000, 10);
			for (float numberOfFlows = 1; numberOfFlows <= 21; numberOfFlows+=2) {
				Debugger.debugToConsole("-------------------- Number Of Flows: " + numberOfFlows);
				trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
						numberOfFlows, numberOfFlows);
				StudyStats.put(numberOfFlows, testbed.executeSimulation(trafficGen.generateTraffic()));
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

		generateNumericalFactorOutput(result);
		Debugger.debugOutPut();
	}

}
