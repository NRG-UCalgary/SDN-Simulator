package utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import entities.*;
import system.*;

public class Debugger {

	private static String DebugFileAddress = "debug/debug.txt";
	private static ArrayList<String> debugLines = new ArrayList<String>();;

	public Debugger() {
	}

	public static void debugOutPut() {
		try {
			FileWriter fileWriter = new FileWriter(DebugFileAddress);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			PrintWriter printWriter = new PrintWriter(bufferedWriter);

			for (String line : debugLines) {
				printWriter.println(line);
			}
			printWriter.close();
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static void debugToConsole(String s) {
		boolean DEB = true;
		if (DEB) {
			System.out.println(s);
		}
	}

	public static void debug(String s) {
		boolean DEB = true;
		if (DEB) {
			debugLines.add(s);
		}
	}

	public static void connectivity(Network net) {
		Debugger.debug("####################	Connectivity	####################");
		for (SDNSwitch s : net.switches.values()) {
			Debugger.debug("=======================");
			Debugger.debug("The switch ID = " + s.getID());
			Debugger.debug(" These are the access links: ");
			for (Link l : s.accessLinks.values()) {
				Debugger.debug("  link ID: " + l.getID() + " connected to hostID: " + l.getDstID());
			}
			Debugger.debug(" These are the network links: ");
			for (Link l : s.networkLinks.values()) {
				Debugger.debug("  link ID: " + l.getID() + " connected to switchID: " + l.getDstID());
			}
			Debugger.debug("=======================");
		}
		Debugger.debug("############################################################");
	}

	public static void event(String eventType, double eventTime, int nodeID, Segment segment, CtrlMessage message) {
		debug("====================================================");
		debug("+ Event: " + eventType + " " + nodeID + " --- time " + Double.toString(eventTime) + "ms");
		if (segment != null) {
			debug("++	FlowID: " + segment.getFlowID() + ", " + " SeqNumber: " + segment.getSeqNum() + ", dst: Host "
					+ segment.getDstHostID() + " (segment type: " + segmentType(segment.getType()) + ")");
		} else {
			debug("++	Control Message");
		}
		debug("====================================================");

	}

	public static void controlMessage(CtrlMessage message) {
	}

	public static void segment(Segment segment) {

	}

	public static void stopFlag() {
		debug("Stop Flag.");
		ArrayList<Integer> test = new ArrayList<Integer>();
		try {
			test.get(0);
		} catch (Exception e) {
			System.out.println("Debugger Stop Flag.");
		}

	}

	/* ======================== Private methods ============================ */
	private static String segmentType(int type) {
		switch (type) {
		case Keywords.DATA:
			return "Data";
		case Keywords.ACK:
			return "ACK";
		case Keywords.CTRL:
			return "Control";
		case Keywords.SYN:
			return "SYN";
		case Keywords.FIN:
			return "FIN";
		case Keywords.SYNACK:
			return "SYNACK";
		case Keywords.FINACK:
			return "FINACK";
		default:
			return null;
		}
	}
}
