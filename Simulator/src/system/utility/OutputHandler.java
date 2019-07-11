package system.utility;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.commons.math3.util.Pair;

import entities.Flow;
import system.utility.dataStructures.ScatterTableData;
import system.utility.dataStructures.SeqNumData;

public class OutputHandler {

	private ExcelHandler excel = new ExcelHandler();

	public OutputHandler() {
	}

	public void outSeqNumExcelFile(Statistics stat) {
		TreeMap<Integer, ScatterTableData> SeqNumDataForAllFlowIDs = new TreeMap<Integer, ScatterTableData>();
		for (Flow flow : stat.flows.values()) {
			ScatterTableData flowSeqNumData = new ScatterTableData("Time (ms)", "Sequence Number");
			ArrayList<Pair<Double, Integer>> dataSerie = new ArrayList<Pair<Double, Integer>>();
			for (Integer seqNum : flow.dataSeqNumSendingTimes.keySet()) {
				Pair<Double, Integer> singleEntry = new Pair<Double, Integer>(flow.dataSeqNumSendingTimes.get(seqNum),
						seqNum);
				dataSerie.add(singleEntry);
			}
			flowSeqNumData.data.put("Data Segments", dataSerie);

			ArrayList<Pair<Double, Integer>> ackSerie = new ArrayList<Pair<Double, Integer>>();
			for (Integer seqNum : flow.ackSeqNumArrivalTimes.keySet()) {
				Pair<Double, Integer> singleEntry = new Pair<Double, Integer>(flow.ackSeqNumArrivalTimes.get(seqNum),
						seqNum);
				ackSerie.add(singleEntry);
			}
			flowSeqNumData.data.put("ACKs", ackSerie);
			SeqNumDataForAllFlowIDs.put(flow.getID(), flowSeqNumData);
		}

		try {
			excel.createSeqNumOutput("SeqNumberGraphs", SeqNumDataForAllFlowIDs);
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
