package system;

public class Main {
	private static Logger log;

	public static void main(String[] args) {
		log = new Logger();
		log.cleanLogFile();

		/** Creating a Simulator **/
		Simulator sim = new Simulator();

		/** Defining Topology **/
		/* createNode: 1.String label */
		sim.createNode("s0");
		sim.createNode("s1");
		sim.createNode("h0");
		sim.createNode("h1");

		/*
		 * createLink: 1.String src_label 2.String dst_label 3.Double delay_prob
		 * 4.Double bandwidth 5.Integer buffer_size 6.String buffer_policy)
		 */
		sim.createLink("l1", "h0", "s0", 1.0, 1.0, 10, null);
		sim.createLink("l2", "s0", "s1", 1.0, 1.0, 10, null);
		sim.createLink("l3", "s1", "h1", 1.0, 1.0, 10, null);
		sim.createLink("l4", "s0", "h1", 4.0, 1.0, 10, null);

		/** Defining Traffic **/
		/*
		 * generateFlow: 1.String label 2.String type 3.String src_label 4.String
		 * dst_label 5.Integer number_packets 6.Double arrival_time
		 */
		sim.generateFlow("f1", "TCP", "h0", "h1", 1, 1.0);

		/** Running Simulation **/
		sim.run(0.0, 10.0);

		/** Getting Results **/

	}

	public static void print(Object o) {
		System.out.println(o);
	}

}
