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

public class AlphaAndBeta extends Scenario {

	public AlphaAndBeta() {
		super("alpha and beta", "alpha and beta");
	}

	public void executeTest() {
		Debugger.debugToConsole("=============================================");
		Debugger.debugToConsole("======== alpha	and beta		==============");
		Debugger.debugToConsole("=============================================");
		LinkedHashMap<String, TreeMap<Float, Statistics>> result = new LinkedHashMap<String, TreeMap<Float, Statistics>>();
		ArrayList<Float> secondFactorValues = new ArrayList<Float>();
		for (float beta = 1; beta <= 2; beta += 1) {
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
			trafficGen.setNumberOfFlowsProperties(Keywords.RandomVariableGenerator.Distributions.Constant, 15, 15);
			for (float alpha = 0.1f; alpha <= 1; alpha = Mathematics.addFloat(alpha, 0.1f)) {
				testbed.alpha = alpha;
				StudyStats.put(alpha, testbed.executeSimulation(trafficGen.generateTraffic()));
			}
			result.put("beta = " + Float.toString(beta), StudyStats);
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
