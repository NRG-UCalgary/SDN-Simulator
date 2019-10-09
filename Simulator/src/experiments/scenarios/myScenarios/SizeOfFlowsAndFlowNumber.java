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

public class SizeOfFlowsAndFlowNumber extends Scenario {

	public SizeOfFlowsAndFlowNumber() {
		super("Size of Flows and Flow Number", "Size of Flows");
	}

	public void executeTest() {
		Debugger.debugToConsole("================================================================");
		Debugger.debugToConsole("======== Size of Flows                   =====================");
		Debugger.debugToConsole("================================================================");
		LinkedHashMap<String, TreeMap<Float, Statistics>> result = new LinkedHashMap<String, TreeMap<Float, Statistics>>();
		ArrayList<Float> firstFactorValues = new ArrayList<Float>();
		ArrayList<Float> secondFactorValues = new ArrayList<Float>();
		firstFactorValues.add(500f);
		for (float flowSize = 1000; flowSize <= 4000; flowSize += 500) {
			firstFactorValues.add(flowSize);
		}
		secondFactorValues.add(10f);
		for (float flowNumber = 20; flowNumber <= 30; flowNumber += 10) {
			secondFactorValues.add(flowNumber);
		}

		for (float secondFactor : secondFactorValues) {
			Debugger.debugToConsole("------------ Flow Number: " + secondFactor);
			TreeMap<Float, Statistics> StudyStats = new TreeMap<Float, Statistics>();
			TrafficGenerator trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			Testbed testbed = new Dumbbell2(Keywords.Testbeds.Types.LAN);
			trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant, secondFactor,
					0);
			trafficGen.setFlowInterArrivalTimeProperties(Keywords.RandomVariableGenerator.Distributions.Constant, 1000f,
					0);
			for (float firstFactor : firstFactorValues) {
				trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.Constant, firstFactor,
						0);
				StudyStats.put(firstFactor, testbed.executeSimulation(trafficGen.generateTraffic()));
			}
			result.put(Float.toString(secondFactor), StudyStats);
		}

		generateNumericalFactorOutput(result);
		Debugger.debugOutPut();
	}

}
