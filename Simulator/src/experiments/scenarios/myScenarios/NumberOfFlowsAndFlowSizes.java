package experiments.scenarios.myScenarios;

import experiments.scenarios.NumericalFactorScenario;
import experiments.testbeds.*;
import experiments.traffic.TrafficGenerator;
import utility.*;

public class NumberOfFlowsAndFlowSizes extends NumericalFactorScenario {

	public NumberOfFlowsAndFlowSizes() {
		super("Flow Numbers and Flow Sizes", "Flow Numbers (N)");
	}

	public void executeTest() {

		// Fixed parameters
		float interArrival = 100f;
		float alpha = 1f;
		float beta = 1f;
		float gamma = 1f;

		// Factors
		firstFactorValues.add(1f);
		for (float numberOfFlows = 5; numberOfFlows <= 100; numberOfFlows += 5) {
			firstFactorValues.add(numberOfFlows);
		}
		//secondFactorValues.add(0f);
		secondFactorValues.add(100f);
		//secondFactorValues.add(200f);
		secondFactorValues.add(500f);
		//secondFactorValues.add(400f);
		secondFactorValues.add(1000f);
		//secondFactorValues.add(1000f);

		// Nested loops for the levels of factors
		for (float secondValue : secondFactorValues) {
			Debugger.debugToConsole(" Flow Sizes: " + secondValue);
			resetStudyStats();
			trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			testbed = new Dumbbell2(Keywords.Testbeds.Types.LAN);
			testbed.alpha = alpha;
			testbed.beta = beta;
			testbed.gamma = gamma;
			trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.Constant, secondValue, 0);
			trafficGen.setFlowInterArrivalTimeProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
					interArrival, 0);
			for (float value : firstFactorValues) {
				trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant, value,
						value);
				studyStats.put(value, testbed.executeSimulation(trafficGen.generateTraffic()));
			}
			result.put(Float.toString(secondValue), studyStats);
		}
		generateOutput();
	}

}
