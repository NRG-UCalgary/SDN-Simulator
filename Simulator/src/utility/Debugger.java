package utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import simulator.Network;
import simulator.entities.network.CtrlMessage;
import simulator.entities.traffic.Packet;
import simulator.entities.traffic.Segment;

public class Debugger {

	private static String DebugFileAddress = "debug/debug.txt";
	private static ArrayList<String> debugLines = new ArrayList<String>();;

	public static void connectivity(Network net) {
		Debugger.debug("####################	Connectivity	####################");
		Debugger.debug("############################################################");
	}

	public static void controlMessage(CtrlMessage message) {
	}

	public static void debug(String s) {
		boolean DEB = true;
		if (DEB) {
			debugLines.add(s);
		}
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

	public static void pacekt(Packet packet) {
		switch (packet.type) {
		case Keywords.Packets.Types.Segment:
			String segmentType = segmentType(packet.segment.getType());
			debug("" + segmentType);
			break;
		case Keywords.Packets.Types.SDNControl:

			break;
		default:
			break;
		}
	}

	/* ======================== Private methods ============================ */
	private static String segmentType(int type) {
		switch (type) {
		case Keywords.Segments.Types.DATA:
			return "Data";
		case Keywords.Segments.Types.ACK:
			return "ACK";
		case Keywords.Segments.Types.CTRL:
			return "Control";
		case Keywords.Segments.Types.SYN:
			return "SYN";
		case Keywords.Segments.Types.FIN:
			return "FIN";
		case Keywords.Segments.Types.SYNACK:
			return "SYNACK";
		case Keywords.Segments.Types.FINACK:
			return "FINACK";
		default:
			return null;
		}
	}

	public static void stopFlag() {
		debug("Stop Flag.");
		ArrayList<Integer> test = new ArrayList<Integer>();
		test.get(0);
	}

	public Debugger() {
	}

}
