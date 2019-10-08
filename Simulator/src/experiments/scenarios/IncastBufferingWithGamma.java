package experiments.scenarios;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;

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

public class IncastBufferingWithGamma extends Scenario {

	public IncastBufferingWithGamma() {
		super("IncastBufferingWithGamma", "Number of Flows");
	}

	public void executeTest() {
		Debugger.debugToConsole("================================================================");
		Debugger.debugToConsole("======== Number of Flows                   =====================");
		Debugger.debugToConsole("================================================================");
		LinkedHashMap<String, TreeMap<Float, Statistics>> result = new LinkedHashMap<String, TreeMap<Float, Statistics>>();
		ArrayList<Float> firstFactorValues = new ArrayList<Float>();
		ArrayList<Float> secondFactorValues = new ArrayList<Float>();
		firstFactorValues.add(1f);
		for (float numberOfFLows = 10; numberOfFLows <= 40; numberOfFLows += 5) {
			firstFactorValues.add(numberOfFLows);
		}
		secondFactorValues.add(1.5f);
		for (float gamma = 2f; gamma <= 2.5f; gamma = Mathematics.addFloat(gamma, 0.5f)) {
			secondFactorValues.add(gamma);
		}

		for (float secondFactor : secondFactorValues) {
			Debugger.debugToConsole("------------ Gamma: " + secondFactor);
			TreeMap<Float, Statistics> StudyStats = new TreeMap<Float, Statistics>();
			TrafficGenerator trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			Testbed testbed = new Dumbbell2(Keywords.Testbeds.Types.Custom);
			trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.Constant, 1000f, 0);
			trafficGen.setFlowInterArrivalTimeProperties(Keywords.RandomVariableGenerator.Distributions.Constant, 0f,
					0);
			testbed.gamma = secondFactor;
			for (float firstFactor : firstFactorValues) {
				trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
						firstFactor, 0);
				StudyStats.put(firstFactor, testbed.executeSimulation(trafficGen.generateTraffic()));
			}
			result.put(Float.toString(secondFactor), StudyStats);
		}

		generateNumericalFactorOutput(result);
		Debugger.debugOutPut();
	}

	private void generateOutput(String testName, Statistics stat) {
		String studyOutputPath = "validation/" + testName + "/";
		new File(studyOutputPath).mkdirs();
		LinkedHashMap<String, NumericFactorScatterTableData> outputData = new LinkedHashMap<String, NumericFactorScatterTableData>();
		for (Flow flow : stat.flows.values()) {
			NumericFactorScatterTableData flowSeqNumData = new NumericFactorScatterTableData("Time (us)",
					"Sequence Number");
			ArrayList<Pair<Float, Float>> dataSerie = new ArrayList<Pair<Float, Float>>();
			for (float seqNum : flow.dataSeqNumSendingTimes.keySet()) {
				Pair<Float, Float> singleEntry = new Pair<Float, Float>(flow.dataSeqNumSendingTimes.get(seqNum),
						seqNum);
				dataSerie.add(singleEntry);
			}
			flowSeqNumData.data.put("Data Segments", dataSerie);

			ArrayList<Pair<Float, Float>> ackSerie = new ArrayList<Pair<Float, Float>>();
			for (float seqNum : flow.ackSeqNumArrivalTimes.keySet()) {
				Pair<Float, Float> singleEntry = new Pair<Float, Float>(flow.ackSeqNumArrivalTimes.get(seqNum), seqNum);
				ackSerie.add(singleEntry);
			}
			flowSeqNumData.data.put("ACKs", ackSerie);
			outputData.put("Flow_" + Integer.toString((int) flow.getID()), flowSeqNumData);
		}
		try {
			ExcelHandler.createValidationOutput(studyOutputPath, "SeqNumPlots", outputData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
