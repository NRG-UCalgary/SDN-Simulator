package system;

import entities.*;

public class Simulator {

	private Network net = new Network();
	private Controller controller;

	private String routing_policy;

	public Simulator() {
		routing_policy = "Dijkstra";
		controller = new Controller(net, routing_policy); // Thte deafaul value of routing policy should be Dijkstra
		/* Default Settings of the Simulator */
	}

	/********** Run **********/
	public void run(Double start_time, Double end_time) {
		/* Other Default settings of the Simulator */

		/* Initializing Controller with Network Object */
		/* Network object should be initialized here */

		// Initial packet arrivals can be created here by calling Agents method
		for (Flow flow : net.flows.values()) {
			net = flow.src_agent.send(net);
		}

		// Dummy line
		controller.router.equals(null);

		print("Hello, I'm the simulator!");

		/* Reading the first Event from Network Event List */

		/* Main Loop */
		while (net.time <= end_time) {
			/* Running the Current Event and Updating the net */
			net = net.event_List.getEvent().execute(net);
		}
	}

	/********** Topology Creation methods ***********/

	/* Node Creation Method */
	public void createNode(String label) {
		Node node = new Node(label);
		net.nodes.put(label, node);
	}

	/* Link Creation Method */
	public void createLink(String label, String src, String dst, Double d_prop, Double BndWdt, int buffer_size,
			String buffer_policy) {

		Link link = new Link(label, net.nodes.get(src), net.nodes.get(dst), d_prop, BndWdt, buffer_size, buffer_policy);
		net.nodes.get(src).neighbors.put(net.nodes.get(dst), link);
		link = new Link(label, net.nodes.get(dst), net.nodes.get(src), d_prop, BndWdt, buffer_size, buffer_policy);
		net.nodes.get(dst).neighbors.put(net.nodes.get(src), link);

	}

	/* Controller Creation Method */
	public void createController() {

	}

	/********* Flow Generation Methods **************/

	public void generateFlow(String label, String type, String src, String dst, int size, double arrival_time) {
		Flow flow = new Flow(label, type, net.nodes.get(src), net.nodes.get(dst), size, arrival_time);
		net.flows.put(label, flow);
	}

	/********* General Programming Methods **********/
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
