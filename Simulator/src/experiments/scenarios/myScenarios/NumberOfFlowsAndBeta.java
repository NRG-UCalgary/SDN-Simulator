package experiments.scenarios.myScenarios;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;

import experiments.scenarios.Scenario;
import experiments.testbeds.Dumbbell;
import experiments.testbeds.Dumbbell2;
import experiments.testbeds.Testbed;
import experiments.traffic.TrafficGenerator;
import simulator.entities.traffic.Flow;
import utility.Debugger;
import utility.Keywords;
import utility.Mathematics;
import utility.Statistics;
import utility.excel.ExcelHandler;
import utility.excel.charts.datastructures.NumericFactorScatterTableData;

public class NumberOfFlowsAndBeta extends Scenario {

	public NumberOfFlowsAndBeta() {
		super("Number of Flows and Beta", "Number of Flows");
	}

	public void executeTest() {
		Debugger.debugToConsole("=========================================================================");
		Debugger.debugToConsole("======== Number Of Flows and Beta			==============================");
		Debugger.debugToConsole("=========================================================================");
		LinkedHashMap<String, TreeMap<Float, Statistics>> result = new LinkedHashMap<String, TreeMap<Float, Statistics>>();
		ArrayList<Float> secondFactorValues = new ArrayList<Float>();
		for (float beta = 1; beta <= 2; beta += 0.2f) {
			secondFactorValues.add(beta);
		}
		for (float beta : secondFactorValues) {
			Debugger.debugToConsole("------------ beta is: " + beta);
			TreeMap<Float, Statistics> StudyStats = new TreeMap<Float, Statistics>();
			TrafficGenerator trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			Testbed testbed = new Dumbbell2(Keywords.Testbeds.Types.LAN);
			testbed.beta = beta;
			// Flow inter-arrival = 1 ms
			trafficGen.setFlowInterArrivalTimeProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
					(float) Mathematics.milliToMicro(1), 0);
			trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.Constant, 2000, 2000);
			trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant, 1, 1);
			StudyStats.put(1f, testbed.executeSimulation(trafficGen.generateTraffic()));
			for (float numberOfFlows = 5; numberOfFlows <= 15; numberOfFlows += 5) {
				trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
						numberOfFlows, numberOfFlows);
				StudyStats.put(numberOfFlows, testbed.executeSimulation(trafficGen.generateTraffic()));
			}
			result.put(Float.toString(beta), StudyStats);
		}

		generateNumericalFactorOutput(result);
		Debugger.debugOutPut();
	}


}
