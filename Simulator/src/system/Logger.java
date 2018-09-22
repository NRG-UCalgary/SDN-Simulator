/* Handles simulation log for experimental results */
package system;

import java.io.*;

public class Logger {
	private final String GENERAL_LOG = "general_log.txt";
	private final String NETWORK_LOG = "network_log.txt";
	private final String LINE = "****************************************";
	private FileWriter file_writer;
	private BufferedWriter buffered_writer;
	private PrintWriter print_writer;

	public Logger() {
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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

}
