/* Handles simulation log for experimental results */
package system.utility;

import java.io.*;

public class Logger {

	/** ######## List of characters ######## **/
	private final String END_OF_PHASE = "*";
	private final String LOOP_START = "-";
	private final String CASE_CAPTURE = "=";
	private final String BOX = "+";
	private final String ARRIVED = "*";

	/** ######## Number of characters before and after the content ######## **/
	private final int PRELOAD = 10;
	private String LINE = "####################";

	private final String GENERAL_LOG = "log/general_log.txt";
	private final String NETWORK_LOG = "log/network_log.txt";
	private FileWriter file_writer;
	private BufferedWriter buffered_writer;
	private PrintWriter print_writer;

	/* Constructor */
	public Logger() {
	}

	/*********************************************/
	public void entranceToMethod(String class_name, String method_name) {
		generalLog("Entered " + class_name + "." + method_name + "()");
	}

	public void endOfPhase(String content) {
		generalLog(banner(END_OF_PHASE, content));

	}

	public void startOfLoop(String title, int counter) {
		String content = title + " (#" + Integer.toString(counter) + ")";
		generalLog(banner(LOOP_START, content));
	}

	public void captureCase(String class_name, String method_name, String cap_case) {
		String content = class_name + "." + method_name + "(): " + cap_case;
		generalLog(lineWithContent(CASE_CAPTURE, content));
	}

	public void eventInfo(int flowID, int segmentType, int nodeID, Double eventTime) {
		String[] content = new String[4];
		content[0] = "EventType: " + segmentType;
		content[1] = "FlowID: " + flowID;
		content[2] = "NodeID: " + nodeID;
		content[3] = "Time: " + Double.toString(eventTime);
		generalLog(box(BOX, content));
	}

	public void arrivalOfPaket(int packet_seq_num, String node_label) {
		// generalLog(lineWithContent(ARRIVED, "Packet #" + packet_seq_num + " delivered
		// to node " + node_label));
		String content = "Packet #" + packet_seq_num + " delivered to node " + node_label;
		generalLog(box(ARRIVED, content));
	}

	/*********************************************/
	/* Generates a box for information */
	private String box(String character, String[] content) {
		int max = 0;
		for (int i = 0; i < content.length; i++) {
			if (content[i].length() >= max) {
				max = content[i].length();
			}
		}
		String result = simpleLine(character, PRELOAD + max) + "\n";

		for (int i = 0; i < content.length; i++) {
			result = result + character + simpleLine(" ", PRELOAD / 2) + content[i]
					+ simpleLine(" ", (PRELOAD + max) - ((PRELOAD / 2) + 2 + content[i].length())) + character + "\n";
		}

		result = result + simpleLine(character, PRELOAD + max);
		return result;
	}

	// Overload
	private String box(String character, String content) {
		String[] content_array = new String[1];
		content_array[0] = content;
		return box(character, content_array);
	}

	/* Generates a banner with desired character and content in it */
	private String banner(String character, String content) {
		String line = simpleLine(character, (content.length() + PRELOAD * 2) + 2);
		return (line + "\n" + lineWithContent(character, content) + "\n" + line);
	}

	/* Generates a line with desired character and custom content in it */
	private String lineWithContent(String character, String content) {
		return (simpleLine(character, PRELOAD) + " " + content + " " + simpleLine(character, PRELOAD));
	}

	/* Generates a simple line with desired character and size */
	private String simpleLine(String character, int size) {
		String result = "";
		for (int i = 0; i < size; i++) {
			result = result + character;
		}
		return result;
	}

	/*********************************************/
	public void generalLog(String s) {
		try {
			file_writer = new FileWriter(this.GENERAL_LOG, true);
			buffered_writer = new BufferedWriter(file_writer);
			print_writer = new PrintWriter(buffered_writer);
			print_writer.println(s);
			// print_writer.println(LINE);
			print_writer.close();
			buffered_writer.close();
			file_writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (print_writer != null) {
				print_writer.close();
			}
			try {
				if (buffered_writer != null) {
					buffered_writer.close();
				}
			} catch (IOException e) {

			}
			try {
				if (file_writer != null) {
					file_writer.close();
				}
			} catch (IOException e) {

			}
		}
	}

	public void networkLog(String s) {
		try {
			file_writer = new FileWriter(this.NETWORK_LOG, true);
			buffered_writer = new BufferedWriter(file_writer);
			print_writer = new PrintWriter(buffered_writer);
			print_writer.println(s);
			print_writer.println(LINE);
			print_writer.close();
			buffered_writer.close();
			file_writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (print_writer != null) {
				print_writer.close();
			}
			try {
				if (buffered_writer != null) {
					buffered_writer.close();
				}
			} catch (IOException e) {

			}
			try {
				if (file_writer != null) {
					file_writer.close();
				}
			} catch (IOException e) {

			}
		}
	}

	public void cleanLogFile() {
		try {
			file_writer = new FileWriter(this.GENERAL_LOG);
			buffered_writer = new BufferedWriter(file_writer);
			print_writer = new PrintWriter(buffered_writer);
			print_writer.flush();
			print_writer.close();
			buffered_writer.close();
			file_writer.close();

			file_writer = new FileWriter(this.NETWORK_LOG);
			buffered_writer = new BufferedWriter(file_writer);
			print_writer = new PrintWriter(buffered_writer);
			print_writer.flush();
			print_writer.close();
			buffered_writer.close();
			file_writer.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
