package experiments.scenarios.myScenarios;

import experiments.scenarios.NumericalFactorScenario;
import experiments.testbeds.*;
import experiments.traffic.TrafficGenerator;
import utility.*;

public class InterArrivalAndFlowSizes extends NumericalFactorScenario {

	public InterArrivalAndFlowSizes() {
		super("Flow InterArrival Time and Flow Sizes", "Flow InterArrival Time");
	}

	public void executeTest() {

		// Fixed parameters
		Float numberOfFlows = 20f;

		// Factors
		firstFactorValues.add(0f);
		for (float interArrival = 250; interArrival <= 5000; interArrival += 250) {
			firstFactorValues.add(interArrival);
		}
		secondFactorValues.add(100f);
		secondFactorValues.add(1000f);
		secondFactorValues.add(2000f);
		secondFactorValues.add(4000f);
		secondFactorValues.add(8000f);
		for (float flowSize = 2000; flowSize <= 7000; flowSize += 2000) {
			//secondFactorValues.add(flowSize);
		}

		// Nested loops for the levels of factors
		for (float flowSize : secondFactorValues) {
			Debugger.debugToConsole("Flow Size: " + flowSize);
			resetStudyStats();
			trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			testbed = new Dumbbell2(Keywords.Testbeds.Types.LAN);
			trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.Constant, flowSize, 0);
			trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
					numberOfFlows, numberOfFlows);
			for (float value : firstFactorValues) {
				trafficGen.setFlowInterArrivalTimeProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
						value, 0);
				studyStats.put(value, testbed.executeSimulation(trafficGen.generateTraffic()));
			}
			result.put(Float.toString(flowSize), studyStats);
		}
		generateOutput();
	}

}
