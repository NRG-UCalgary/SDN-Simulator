package utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import simulator.entities.network.CtrlMessage;

public class Debugger {
	

	private static String DebugFileAddress = "debug/debug.txt";
	private static ArrayList<String> debugLines = new ArrayList<String>();;

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

	public static void debugEvent(String eventType, float eventTime, Object note) {
		boolean eventDEB = true;
		if (eventDEB) {
			printLine("=", 80);
			System.out.println("===== " + eventType + " at Time: " + eventTime);
			debugLines.add("===== " + eventType + " at Time: " + eventTime);
			if (note != null) {
				System.out.println("===== Note: " + note);
				debugLines.add("===== Note: " + note);
			}
			printLine("=", 80);
		}
	}

	public static void methodEntrance(String className, String methodName, String note) {
		boolean DEB = true;
		if (DEB) {
			System.out.println("++++++++ " + className + "." + methodName + "(): Entrance");
			debugLines.add("++++++++ " + className + "." + methodName + "(): Entrance");
			System.out.println("------------ Note: " + note);
			debugLines.add("------------ Note: " + note);
		}
	}

	public static void methodExit(String className, String methodName, Object note) {
		boolean DEB = true;
		if (DEB) {
			System.out.println("^^^^^^^^ Exiting " + className + "." + methodName + "()");
			debugLines.add("^^^^^^^^ Exiting " + className + "." + methodName + "()");
		}
	}

	public static void interMethodDebug(Object o) {
		boolean interMethodDEB = true;
		if (interMethodDEB) {
			System.out.println("               " + o);
			debugLines.add("               " + o);
		}
	}

	public static void error(String className, String methodName, String errorMessage) {
		boolean DEB = true;
		if (DEB) {
			printLine("#", 80);
			printLine("#", 80);
			System.out.println("Error--inClass--" + className + "." + methodName + "()::" + errorMessage);
			debugLines.add("Error--inClass--" + className + "." + methodName + "()::" + errorMessage);
			printLine("#", 80);
			printLine("#", 80);
		}
	}

	public static void printLine(String character, int size) {
		boolean DEB = true;
		if (DEB) {
			String line = "";
			for (int i = 0; i < size; i++) {
				line += character;
			}
			System.out.println(line);
			debugLines.add(line);
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
