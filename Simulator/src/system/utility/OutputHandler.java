package system.utility;

import java.io.*;
import java.util.ArrayList;

import entities.Flow;
import system.utility.dataStructures.SeqNumData;

public class OutputHandler {

	private ExcelHandler excel = new ExcelHandler();

	public OutputHandler() {
	}

	public void outSeqNumExcelFile(Statistics stat) throws IOException {
		ArrayList<SeqNumData> dataOfFlows = new ArrayList<SeqNumData>();
		for (Flow flow : stat.flows.values()) {
			SeqNumData flowData = new SeqNumData();
			flowData.ackNumbers = flow.ackSeqNumArrivalTimes;
			flowData.seqNumbers = flow.dataSeqNumSendingTimes;
			flowData.flowName = "Flow " + Integer.toString(flow.getID());
			dataOfFlows.add(flowData);
		}
		excel.createSeqNumOutput("SeqNumberGraph", dataOfFlows);

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
