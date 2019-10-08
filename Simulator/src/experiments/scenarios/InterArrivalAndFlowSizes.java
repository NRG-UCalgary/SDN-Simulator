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

public class InterArrivalAndFlowSizes extends Scenario {

	public InterArrivalAndFlowSizes() {
		super("Flow InterArrival Time and Flow Sizes", "Flow InterArrival Time");
	}

	public void executeTest() {
		Debugger.debugToConsole("=========================================================================");
		Debugger.debugToConsole("======== Flow InterArrival Time                    =====================");
		Debugger.debugToConsole("=========================================================================");
		LinkedHashMap<String, TreeMap<Float, Statistics>> result = new LinkedHashMap<String, TreeMap<Float, Statistics>>();
		ArrayList<Float> firstFactorValues = new ArrayList<Float>();
		ArrayList<Float> secondFactorValues = new ArrayList<Float>();
		firstFactorValues.add(0f);
		for (float interArrival = 500; interArrival <= 10000; interArrival += 500) {
			firstFactorValues.add(interArrival);
		}
		secondFactorValues.add(1000f);
		for (float flowSize = 2000; flowSize <= 5000; flowSize += 1000) {
			secondFactorValues.add(flowSize);
		}

		for (float flowSize : secondFactorValues) {
			Debugger.debugToConsole("------------ Flow Size: " + flowSize);
			TreeMap<Float, Statistics> StudyStats = new TreeMap<Float, Statistics>();
			TrafficGenerator trafficGen = new TrafficGenerator(Keywords.Traffics.Types.GeneralTraffic, 0);
			Testbed testbed = new Dumbbell2(Keywords.Testbeds.Types.LAN);
			trafficGen.setFlowSizeProperties(Keywords.RandomVariableGenerator.Distributions.Constant, flowSize, 0);
			trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant, 20, 0);
			for (float value : firstFactorValues) {
				trafficGen.setFlowInterArrivalTimeProperties(Keywords.RandomVariableGenerator.Distributions.Constant,
						value, 0);
				StudyStats.put(value, testbed.executeSimulation(trafficGen.generateTraffic()));
			}
			result.put(Float.toString(flowSize), StudyStats);
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
