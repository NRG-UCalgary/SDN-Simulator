package experiments.scenarios;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;

import entities.Flow;
import system.charts.datastructures.*;
import system.utility.Statistics;
import system.utility.excel.ExcelHandler;

public abstract class Scenario {
	protected boolean functionalityOutput = false;

	private String studyName;
	private String mainFactorName;

	public Scenario(String studyName, String mainFactorName) {
		this.studyName = studyName;
		this.mainFactorName = mainFactorName;
	}

	public void executeTest() {

	}

	/* ======================================================================== */
	/* ============ Performance outputs ======================================= */
	/* ======================================================================== */

	public void generateNumericalFactorOutput(LinkedHashMap<String, TreeMap<Float, Statistics>> result) {
		String studyOutputPath = "output/" + studyName + "/";
		new File(studyOutputPath).mkdirs();
		NumericFactorOutputData outputData = new NumericFactorOutputData(mainFactorName, result);
		try {
			ExcelHandler.createNumericFactorStudyOutput(studyOutputPath, studyName, outputData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateCategoryFactorOutput(LinkedHashMap<String, LinkedHashMap<String, Statistics>> result) {
		String studyOutputPath = "output/" + studyName + "/";
		new File(studyOutputPath).mkdirs();
		CategoryFactorOutputData outputData = new CategoryFactorOutputData(mainFactorName, result);
		try {
			ExcelHandler.createCategoryFactorStudyOutput(studyOutputPath, studyName, outputData);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/* ======================================================================== */
	/* ============ Functionality outputs ===================================== */
	/* ======================================================================== */

	public void outSegmentArrivalToBottleneckData(String outputPath, Statistics stat) {
		NumericFactorScatterTableData bottleneckArrivals = new NumericFactorScatterTableData("Time (ms)", "FlowID");
		for (Pair<Float, Float> entry : stat.links.get(stat.bottleneckLinkID).segmentArrivalTimeOfFlowID) {
			String key = "Flow_" + entry.getSecond().intValue();
			if (bottleneckArrivals.data.containsKey(key)) {
				bottleneckArrivals.data.get(key).add(entry);
			} else {
				ArrayList<Pair<Float, Float>> array = new ArrayList<Pair<Float, Float>>();
				array.add(entry);
				bottleneckArrivals.data.put(key, array);
			}
		}

	}

	public void outSequenceNumberData(String outputPath, Statistics stat) {
		TreeMap<Integer, NumericFactorScatterTableData> SeqNumDataForAllFlowIDs = new TreeMap<Integer, NumericFactorScatterTableData>();
		for (Flow flow : stat.flows.values()) {
			NumericFactorScatterTableData flowSeqNumData = new NumericFactorScatterTableData("Time (us)", "SeqNum");
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
			SeqNumDataForAllFlowIDs.put(flow.getID(), flowSeqNumData);
		}

	}

	public void outOneCollumTextFile(ArrayList<String> output, String address) {

		FileWriter file_writer;
		BufferedWriter buffered_writer;
		PrintWriter print_writer;
		try {
			file_writer = new FileWriter(address);
			buffered_writer = new BufferedWriter(file_writer);
			print_writer = new PrintWriter(buffered_writer);

			for (String s : output) {
				print_writer.println(s);
			}
			print_writer.close();
			buffered_writer.close();
			file_writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
