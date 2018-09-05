package system;

public class Main {

	public static void main(String[] args) {

		/** Creating a Simulator **/
		Simulator sim = new Simulator();

		/** Defining Topology **/
		sim.createNode("s0", 10);
		sim.createNode("s1", 10);

		sim.createNode("h0", 1);
		sim.createNode("h1", 1);
		sim.createNode("h2", 1);

		sim.createLink("l1", "h0", "s0", 10.0, 2.0, 3, null);
		sim.createLink("l2", "h1", "s0", 20.0, 2.0, 3, null);
		sim.createLink("l3", "s0", "s1", 20.0, 2.0, 3, null);
		sim.createLink("l4", "s1", "h2", 10.0, 2.0, 3, null);

		/** Defining Traffic **/
		sim.generateFlow("f1", "TCP", "h0", "h2", 1000, 1.0);
		sim.generateFlow("f2", "TCP", "h1", "h2", 1000, 1.0);

		/** Running Simulation **/
		sim.run(0.0, 10.0);

		/** Getting Results **/

	}

	public static void print(Object o) {
		System.out.println(o);
	}

}
