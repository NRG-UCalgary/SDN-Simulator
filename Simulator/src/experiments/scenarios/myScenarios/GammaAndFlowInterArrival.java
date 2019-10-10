package experiments.scenarios.myScenarios;

import experiments.scenarios.NumericalFactorScenario;
import experiments.testbeds.*;
import experiments.traffic.TrafficGenerator;
import utility.*;

public class GammaAndFlowInterArrival extends NumericalFactorScenario {

	public GammaAndFlowInterArrival() {
		super("Gamma and Flow Inter-Arrival Time", "Gamma");
	}

	public void executeTest() {

		// Fixed parameters
		float numberOfFlows = 10f;
		float flowSize = 1000f; 
		float alpha = 1f;
		float beta = 1f;

		// Factors
		firstFactorValues.add(1f);
		for (float gamma = 2f; gamma <= 200; gamma = Mathematics.addFloat(gamma, 1f)) {
			firstFactorValues.add(gamma);
		}
		// secondFactorValues.add(100f);
		secondFactorValues.add(0f);
		secondFactorValues.add(200f);
		secondFactorValues.add(500f);
		secondFactorValues.add(1000f);
		for (float flowInterArrival = 1000f; flowInterArrival <= 2000d; flowInterArrival += 1000d) {
			// secondFactorValues.add(flowSize);
		}

		// Nested loops for the levels of factors
		for (float secondValue : secondFactorValues) {
			Debugger.debugToConsole("flowInterArrival: " + secondValue);
			resetStudyStats();
			trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			testbed = new Dumbbell2(Keywords.Testbeds.Types.LAN);
			testbed.alpha = alpha;
			testbed.beta = beta;

			trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.Constant, flowSize,
					flowSize);
			trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
					numberOfFlows, numberOfFlows);
			trafficGen.setFlowInterArrivalTimeProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
					secondValue, secondValue);
			for (float value : firstFactorValues) {
				testbed.gamma = value;
				studyStats.put(value, testbed.executeSimulation(trafficGen.generateTraffic()));
			}
			result.put(Float.toString(secondValue), studyStats);
		}
		generateOutput();
	}

}
