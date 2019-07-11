package system;

import system.utility.*;

public class Main {
	static boolean OneFlowScenario = false;
	static boolean TwoFlowScenario = false;
	static boolean ThreeFlowScenario = false;

	public static void main(String[] args) {

		 OneFlowScenario = true;
		// TwoFlowScenario = true;
		//ThreeFlowScenario = true;

		//oneFlowScenario();
		//twoFlowScenario();
		threeFlowScenario();
	}

	public static void print(Object o) {
		System.out.println(o);
	}

	public static void threeFlowScenario() {
		/** Creating a Simulator **/
		Simulator sim = new Simulator();

		/** Defining Topology **/
		/* createNode: 1.String label 2.PropagationDelay 3.Bandwidth */
		sim.createSwitch("sw0", 5, 8);
		sim.createSwitch("sw1", 5, 8);
		sim.createSwitch("sw2", 5, 8);
		sim.createHost("h0");
		sim.createHost("h1");
		sim.createHost("h2");
		sim.createHost("d0");
		sim.createHost("d1");
		sim.createHost("d2");

		/*
		 * createLink: 1.String src_label 2.String dst_label 3.Double delay_prob
		 * 4.Double bandwidth 5.Integer buffer_size 6.String buffer_policy)
		 */
		sim.createAccessLink("al0", "h0", "sw0", 10, 8, 1000, Keywords.FIFO);
		sim.createAccessLink("al1", "h1", "sw0", 10, 8, 1000, Keywords.FIFO);
		sim.createAccessLink("al2", "h2", "sw0", 10, 8, 1000, Keywords.FIFO);
		sim.createLink("l0", "sw0", "sw1", 10, 4, 1000, Keywords.FIFO); // btl
		sim.createLink("l1", "sw1", "sw2", 10, 8, 1000, Keywords.FIFO);
		sim.createAccessLink("dal0", "d0", "sw2", 10, 8, 1000, Keywords.FIFO);
		sim.createAccessLink("dal1", "d1", "sw2", 10, 8, 1000, Keywords.FIFO);
		sim.createAccessLink("dal2", "d2", "sw2", 10, 8, 1000, Keywords.FIFO);

		/* Controller must be defined after the topology is complete */
		sim.createController("c0", Keywords.Dijkstra);

		/** Defining Traffic **/
		/*
		 * generateFlow: 1.String label 2.String type 3.String src_label 4.String
		 * dst_label 5.Integer number_packets 6.Double arrival_time
		 */
		sim.generateFlow("f0", Keywords.SDTCP, "h0", "d0", 200, 0.0);
		sim.generateFlow("f1", Keywords.SDTCP, "h1", "d1", 120, 30);
		sim.generateFlow("f2", Keywords.SDTCP, "h2", "d2", 80, 200);

		/** Running Simulation: Times are in millisecond **/
		sim.run(0.0, 200000.0);

		/** Getting Results **/

		// Debugger Output
		Debugger.debugOutPut();
	}

	public static void twoFlowScenario() {
		/** Creating a Simulator **/
		Simulator sim = new Simulator();

		/** Defining Topology **/
		/* createNode: 1.String label 2.PropagationDelay 3.Bandwidth */
		sim.createSwitch("sw0", 10, 8);
		sim.createSwitch("sw1", 5, 8);
		sim.createSwitch("sw2", 5, 8);
		sim.createHost("h0");
		sim.createHost("h1");
		sim.createHost("d0");
		sim.createHost("d1");

		/*
		 * createLink: 1.String src_label 2.String dst_label 3.Double delay_prob
		 * 4.Double bandwidth 5.Integer buffer_size 6.String buffer_policy)
		 */
		sim.createAccessLink("al0", "h0", "sw0", 10, 8, 1000, Keywords.FIFO);
		sim.createAccessLink("al1", "h1", "sw0", 5, 8, 1000, Keywords.FIFO);
		sim.createLink("l0", "sw0", "sw1", 10, 4, 1000, Keywords.FIFO); // btl
		sim.createLink("l1", "sw1", "sw2", 10, 8, 1000, Keywords.FIFO);
		sim.createAccessLink("al2", "d0", "sw2", 10, 8, 1000, Keywords.FIFO);
		sim.createAccessLink("al3", "d1", "sw2", 10, 8, 1000, Keywords.FIFO);

		/* Controller must be defined after the topology is complete */
		sim.createController("c0", Keywords.Dijkstra);

		/** Defining Traffic **/
		/*
		 * generateFlow: 1.String label 2.String type 3.String src_label 4.String
		 * dst_label 5.Integer number_packets 6.Double arrival_time
		 */
		sim.generateFlow("f0", Keywords.SDTCP, "h0", "d0", 200, 0.0);
		sim.generateFlow("f1", Keywords.SDTCP, "h1", "d1", 50, 200);

		/** Running Simulation: Times are in millisecond **/
		sim.run(0.0, 200000.0);

		/** Getting Results **/

		// Debugger Output
		Debugger.debugOutPut();
	}

	public static void oneFlowScenario() {
		/** Creating a Simulator **/
		Simulator sim = new Simulator();

		/** Defining Topology **/
		/* createNode: 1.String label 2.PropagationDelay 3.Bandwidth */
		sim.createSwitch("sw0", 10, 8);
		sim.createSwitch("sw1", 5, 8);
		sim.createSwitch("sw2", 5, 8);
		sim.createHost("h0");
		sim.createHost("d0");

		/*
		 * createLink: 1.String src_label 2.String dst_label 3.Double delay_prob
		 * 4.Double bandwidth 5.Integer buffer_size 6.String buffer_policy)
		 */
		sim.createAccessLink("al0", "h0", "sw0", 10, 8, 1000, Keywords.FIFO);
		sim.createLink("l0", "sw0", "sw1", 10, 4, 1000, Keywords.FIFO); // btl
		sim.createLink("l1", "sw1", "sw2", 10, 8, 1000, Keywords.FIFO);
		sim.createAccessLink("al2", "d0", "sw2", 10, 8, 1000, Keywords.FIFO);

		/* Controller must be defined after the topology is complete */
		sim.createController("c0", Keywords.Dijkstra);

		/** Defining Traffic **/
		/*
		 * generateFlow: 1.String label 2.String type 3.String src_label 4.String
		 * dst_label 5.Integer number_packets 6.Double arrival_time
		 */
		sim.generateFlow("f0", Keywords.SDTCP, "h0", "d0", 200, 0.0);

		/** Running Simulation: Times are in millisecond **/
		sim.run(0.0, 200000.0);

		/** Getting Results **/

		// Debugger Output
		Debugger.debugOutPut();
	}

}
