package system;

import entities.*;

public class Simulator {

	private Network net = new Network();
	private Controller controller;
	private Double time;

	private String routing_policy;

	public Simulator() {
		routing_policy = "Dijkstra";
		controller = new Controller(net, routing_policy); // Thte deafaul value of routing policy should be Dijkstra
		/* Default Settings of the Simulator */
		time = 0.0;
	}

	/********** Run **********/
	public void run(Double start_time, Double end_time) {
		/* Other Default settings of the Simulator */

		/* Initializing Controller with Network Object */
		/* Network object should be initialized here */
		this.net.initialize();

		print("Hello, I'm the simulator!");
		
		/* Reading the first Event from Network Event List */

		/* Main Loop */
		while (time <= end_time) {
			/* Running the Current Event and Updating the net */
			net = net.event_List.getEvent().execute(net);

		}

	}

	/********** Topology Creation methods **********/

	/* Node Creation Method */
	public void createNode(String label, int buffer) {
		Node node = new Node(label, buffer);
		net.nodes.addElement(label, node);

	}

	/* Link Creation Method */
	public void createLink(String label, String src, String dst, Double d_prop, Double BndWdt) {
		Link link = new Link(label, net.nodes.getByLabel(src), net.nodes.getByLabel(dst), d_prop, BndWdt);
		net.links.addElement(label, link);

	}

	/* Controller Creation Method */
	public void createController() {

	}

	/********** Flow Generation Methods **********/

	public void generateFlow(String label, String type, String src, String dst, int size, double arrival_time) {
		Flow flow = new Flow(label, type, net.nodes.getByLabel(src), net.nodes.getByLabel(dst), size, arrival_time);
		net.flows.addElement(label, flow);

		/* Creating Initial Events */
		Event e = new Event(arrival_time, "Arrival", (net.flows.size() - 1), 0,
				net.nodes.indexOf(net.nodes.getByLabel(src)));
		net.event_List.addEvent(e);
	}

	/********** General Programming Methods **********/
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
