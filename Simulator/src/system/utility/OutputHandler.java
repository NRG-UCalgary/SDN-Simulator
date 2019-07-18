package system.utility;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;

import entities.Flow;
import system.utility.dataStructures.NumberOFFlowsOutputData;
import system.utility.dataStructures.ScatterTableData;

public class OutputHandler {
	public static boolean FunctionalityOutput = false;

	public OutputHandler() {
	}

	public static void outStatsforNumberOfFlows(TreeMap<Integer, Statistics> mySimStats,
			TreeMap<Integer, Statistics> nsStats) {
		NumberOFFlowsOutputData outputData = new NumberOFFlowsOutputData();
		String studyOutputDirectory = "output/NumberOfFlows/";
		new File(studyOutputDirectory).mkdir();
		for (int numberOfFlows : mySimStats.keySet()) {
			if (FunctionalityOutput) {
				String directoryPathPerFctor = "output/NumberOfFlows/" + numberOfFlows + "_flows/";
				new File(directoryPathPerFctor).mkdir();
				outSegmentArrivalToBottleneckData(directoryPathPerFctor, mySimStats.get(numberOfFlows));
				outSequenceNumberData(directoryPathPerFctor, mySimStats.get(numberOfFlows));
			}
			outputData.prepareOutputMetrics(numberOfFlows, mySimStats.get(numberOfFlows));
		}
		outputData.prepareOutputSheets();
		try {
			ExcelHandler.createNumberOfFlowsStudyOutput(studyOutputDirectory, outputData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outSegmentArrivalToBottleneckData(String outputPath, Statistics stat) {
		ScatterTableData bottleneckArrivals = new ScatterTableData("Time (ms)", "FlowID");
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

		try {
			ExcelHandler.createSegmentArrivalToBottleneckOutput(outputPath, bottleneckArrivals);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outSequenceNumberData(String outputPath, Statistics stat) {
		TreeMap<Integer, ScatterTableData> SeqNumDataForAllFlowIDs = new TreeMap<Integer, ScatterTableData>();
		for (Flow flow : stat.flows.values()) {
			ScatterTableData flowSeqNumData = new ScatterTableData("Time (us)", "SeqNum");
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

		try {
			ExcelHandler.createSeqNumOutput(outputPath, SeqNumDataForAllFlowIDs);
		} catch (IOException e) {
			e.printStackTrace();
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
