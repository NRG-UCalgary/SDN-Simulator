package system;

import entities.*;
import protocols.*;

public class Simulator {
	private Logger log = new Logger();

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
		log.generalLog("Entered Simulator.run().");
		/* Other Default settings of the Simulator */

		/* Initializing Controller with Network Object */
		/* Network object should be initialized here */

		// This method is for creating the first arrival event (for TCP it is SYN
		// packet) for each flow.
		// This Initialization can be done through src_agent of each flow so their state
		// variables can be updated.
		initialize();

		// Dummy line
		controller.router.equals(null);

		/* Reading the first Event from Network Event List */

		/* Main Loop */
		while (net.time <= end_time) {
			/* Running the Current Event and Updating the net */
			net = net.event_List.getEvent().execute(net);

			log.generalLog("************************************");
		}
	}

	/********** Initialization methods ***********/
	public void initialize() {

		// Initializing first packets
		// for each flow in the network, the method start() is called from their
		// src_agent. Based on the type of the agent (TCP, UDP, BBR, etc.), the
		// implementation of start() method may differ.
		for (Agent agent : net.agents.values()) {
			// flow.src_agent.start(net);
			agent.start(net);
		}

	}

	/********** Topology Creation methods ***********/

	/* Node Creation Method */
	public void createNode(String label) {
		log.generalLog("Entered Simulator.createNode().");

		Node node = new Node(label);
		net.nodes.put(label, node);
	}

	/* Link Creation Method */
	public void createLink(String label, String src, String dst, Double d_prop, Double BndWdt, int buffer_size,
			String buffer_policy) {
		log.generalLog("Entered Simulator.createLink().");

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
		log.generalLog("Entered Simulator.generateFlow().");

		Flow flow = new Flow(label, type, net.nodes.get(src), net.nodes.get(dst), size, arrival_time);
		Agent agent = null;

		/* All transport layer protocols must be added to this switch case */
		switch (type) {
		case "TCP":
			agent = new TCP(flow);
			break;
		case "RBTCP":
			agent = new RBTCP(flow);
		case "UDP":
			UDP udp = new UDP();
		default:
			System.out.println("Simulator.generateFlow()::Invalid type for flow.");
			break;
		}

		// Agents are stored with their flow's label in a Map <String,Agent> in Network
		net.agents.put(label, agent);

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
