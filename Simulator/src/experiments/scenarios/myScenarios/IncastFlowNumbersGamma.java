package experiments.scenarios.myScenarios;

import experiments.scenarios.NumericalFactorScenario;
import experiments.testbeds.*;
import experiments.traffic.TrafficGenerator;
import utility.*;

public class IncastFlowNumbersGamma extends NumericalFactorScenario {

	public IncastFlowNumbersGamma() {
		super("Incast, Flow Number and Gamma", "Number of Flows");
	}

	public void executeTest() {

		// Fixed parameters
		float flowSize = 100f;
		float interArrivalTime = 0f;
		float alpha = 1f;
		float beta = 2f;

		// Factors
		firstFactorValues.add(1f);
		for (float numberOfFlows = 5; numberOfFlows <= 100; numberOfFlows += 5) {
			firstFactorValues.add(numberOfFlows);
		}
		secondFactorValues.add(1f);
		secondFactorValues.add(1.2f);
		secondFactorValues.add(1.5f);
		secondFactorValues.add(1.6f);
		secondFactorValues.add(1.8f);
		secondFactorValues.add(2f);
		secondFactorValues.add(2.5f);


		// Nested loops for the levels of factors
		for (float secondValue : secondFactorValues) {
			Debugger.debugToConsole(" Gamma: " + secondValue);
			resetStudyStats();
			trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			testbed = new Dumbbell2(Keywords.Testbeds.Types.LAN);
			testbed.alpha = alpha;
			testbed.beta = beta;
			testbed.gamma = secondValue;
			trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.Constant, flowSize, 0);
			trafficGen.setFlowInterArrivalTimeProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
					interArrivalTime, 0);
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
