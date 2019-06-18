package utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import system.Main;

public class OutputHandler {
	private final String seqNums = "output/SeqNumbers.txt";
	private final String seqTimes = "output/SeqTimes.txt";
	private final String ACKNums = "output/ACKNums.txt";
	private final String ACKTimes = "output/ACKTimes.txt";

	public OutputHandler() {
	}

	public void outOneFlow(Statistics stat) {
		ArrayList<String> output = new ArrayList<String>();
		for (int seqNum : stat.flows.get(0).dataSeqNumSendingTimes.keySet()) {
			output.add(Integer.toString(seqNum));
		}
		outOneCollum(output, seqNums);
		output = new ArrayList<String>();
		for (Double seqNumTime : stat.flows.get(0).dataSeqNumSendingTimes.values()) {
			output.add(Double.toString(seqNumTime));
		}
		outOneCollum(output, seqTimes);
		output = new ArrayList<String>();
		for (int ACKNum : stat.flows.get(0).ackSeqNumArrivalTimes.keySet()) {
			output.add(Integer.toString(ACKNum));
		}
		outOneCollum(output, ACKNums);
		output = new ArrayList<String>();

		for (Double ACKTime : stat.flows.get(0).ackSeqNumArrivalTimes.values()) {
			output.add(Double.toString(ACKTime));
		}
		outOneCollum(output, ACKTimes);
		output = new ArrayList<String>();

	}

	public void outOneCollum(ArrayList<String> output, String address) {

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
