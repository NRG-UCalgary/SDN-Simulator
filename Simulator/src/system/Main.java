package system;

import utilities.Logger;

public class Main {
	private static Logger log;

	public static void main(String[] args) {
		log = new Logger();
		log.cleanLogFile();

		/** Creating a Simulator **/
		Simulator sim = new Simulator();

		/** Defining Topology **/
		/* createNode: 1.String label */
		sim.createSwitch("sw0", 10, 1);
		sim.createSwitch("sw1", 10, 1);
		sim.createHost("h0");
		sim.createHost("h1");

		/*
		 * createLink: 1.String src_label 2.String dst_label 3.Double delay_prob
		 * 4.Double bandwidth 5.Integer buffer_size 6.String buffer_policy)
		 */
		sim.createAccessLink("l0", "h0", "sw0", 2.0, 10, 10, Keywords.FIFO);
		sim.createLink("l1", "sw0", "sw1", 1, 100, 10, Keywords.FIFO);
		sim.createAccessLink("l2", "sw1", "h1", 2.0, 10, 10, Keywords.FIFO);

		/* Controller must be defined after the topology is complete */
		// TODO When creating the controller, maybe we can give the constructor the
		// Network and and set the delay for each node to the controller
		// TODO an alternative is to set the connection delay for all nodes to the same
		// constant by giving it to the constructor
		sim.createController("c0");

		/** Defining Traffic **/
		/*
		 * generateFlow: 1.String label 2.String type 3.String src_label 4.String
		 * dst_label 5.Integer number_packets 6.Double arrival_time
		 */
		sim.generateFlow("f1", "TCP", "s0", "d0", 50, 1.0);

		/** Running Simulation **/
		sim.run(0.0, 2000.0);

		/** Getting Results **/
		// TODO Come up with some solution to get the results and report it
	}

	public static void print(Object o) {
		System.out.println(o);
	}

	public static void debug(Object o) {
		boolean DEB = true;
		if (DEB) {
			System.out.println("Debug: " + o);
		}
	}

}
