package system;

import entities.*;
import protocols.*;
import utilities.Logger;

public class Simulator {

	/** ############### Default Variables ############### **/
	/* Controller */
	private final double CONTROLLER_RTT_DEALAY = 1.0;
	private final double CONTROLLER_PROCESS_DELAY = 1.0;
	private final String CONTROLLER_ROUTING_ALG = "Dijkstra";

	/* Buffer Algorithm */
	// private final String BUFFER_ALG = "FCFS";

	/** ################################################# **/

	private Logger log = new Logger();
	private Network net = new Network();
	private Controller controller;
	private String routing_policy;

	public Simulator() {
		routing_policy = "Dijkstra";
		controller = new Controller(net, routing_policy); // The default value of routing policy should be Dijkstra
		/* Default Settings of the Simulator */
	}

	/********** Run **********/
	public void run(Double start_time, Double end_time) {
		log.entranceToMethod("Simulator", "run");
		/* Other Default settings of the Simulator */

		/* Initializing Controller with Network Object */
		/* Network object should be initialized here */

		// This method is for creating the first arrival event (for TCP it is SYN
		// packet) for each flow.
		// This Initialization can be done through src_agent of each flow so their state
		// variables can be updated.

		// Dummy line
		controller.router.equals(null);

		/* Reading the first Event from Network Event List */

		log.endOfPhase("Initialization done.");
		int loop_counter = 0;
		/* Main Loop */
		while (net.time <= end_time) {
			log.startOfLoop("Simulator::Main Loop", loop_counter);
			/* Running the Current Event and Updating the net */
			net = net.eventList.getEvent().execute(net);

			loop_counter++;

		}
	}

	/********** Topology Creation methods ***********/

	/*-------------------------------------------------------*/
	/* Switch Creation Method */
	public void createSwitch(String label) {
		// Note that here we are assuming that we only use SDN switches.
		// Later, if needed, a mechanism for choosing type of switch should be
		// implemented
		SDNSwitch sw = new SDNSwitch(label);
		net.switches.put(label, sw);
	}

	/*-------------------------------------------------------*/
	/* Host Creation Method */
	public void createHost(String label) {
		Host host = new Host(label);
		net.hosts.put(label, host);
	}

	/*-------------------------------------------------------*/
	/* Link Creation Method */
	public void createLink(String label, String src, String dst, double propDelay, int bandwidth, int bufferSize,
			String bufferPolicy) {
		// The link from SRC to DST
		Link link = new Link(label, net.switches.get(src), net.switches.get(dst), propDelay, bandwidth, bufferSize,
				bufferPolicy);
		net.switches.get(src).neighbors.put(net.switches.get(dst), link);

		// The link from DST to SRC
		link = new Link(label, net.switches.get(dst), net.switches.get(src), propDelay, bandwidth, bufferSize,
				bufferPolicy);
		net.switches.get(dst).neighbors.put(net.switches.get(src), link);
	}

	/* Access link creation method */
	public void createAccessLink(String label, String src, String dst, double propDelay, int bandwidth, int bufferSize,
			String bufferPolicy) {

		// The link from host to switch
		Link link = new Link(label, net.hosts.get(src), net.switches.get(dst), propDelay, bandwidth, bufferSize,
				bufferPolicy);
		net.hosts.get(src).connectToNetwork(link, dst);

		// The link from switch to host
		link = new Link(label, net.switches.get(dst), net.hosts.get(src), propDelay, bandwidth, bufferSize,
				bufferPolicy);
		net.switches.get(dst).accessLinks.put(net.hosts.get(src), link);
	}

	/*-------------------------------------------------------*/
	/* Controller Creation Method */
	public void createController(String routing_alg, double rtt_delay, double process_delay) {
		Controller controller = new Controller(net, routing_alg);
		net.controller = controller;
	}

	// Overload
	public void createController(String routing_alg) {
		createController(routing_alg, CONTROLLER_RTT_DEALAY, CONTROLLER_PROCESS_DELAY);
	}

	// Overload
	public void createController() {
		createController(CONTROLLER_ROUTING_ALG);
	}
	/*-------------------------------------------------------*/

	/********* Flow Generation Methods **************/

	public void generateFlow(String label, String type, String src, String dst, int size, double arrival_time) {
		log.entranceToMethod("Simulator", "generateFlow");

		Flow flow = new Flow(label, type, net.hosts.get(src), net.hosts.get(dst), size, arrival_time);
		Agent src_agent = null;
		Agent dst_agent = null;

		// TODO Agents can be assigned in switches? In this case each agent in each node
		// (Host) can be found by the flow_label and each node has only one agent with a
		// specific flow_label in this scenario.
		/* All transport layer protocols must be added to this switch case */
		/* ^^^^^^^^^ New Architecture ^^^^^^^^^^^ */

		// TODO If the TCP agents can be two different types of sender and receiver,
		// then the switch case must be updated correspondingly
		switch (type) {
		case "TCP":
			// src_agent = new TCP(flow);
			// dst_agent = new TCP(flow);
			break;
		case "RBTCP":
			Main.print("Simulator.generateFlow()::RBTCP sender and receiver are going to be created.");
			src_agent = new RBTCPSenderv1(flow);
			dst_agent = new RBTCPReceiverv1(flow);
		case "UDP":
			// UDP udp = new UDP();
		default:
			System.out.println("Simulator.generateFlow()::Invalid type for flow.");
			break;
		}

		// Initialization
		src_agent.start(net);

		// Net update
		net.switches.get(src).agents.put(label + ".ACK", src_agent);
		net.switches.get(dst).agents.put(label, dst_agent);
		// Agents are stored with their flow's label in a Map <String,Agent> in Network
		/* ^^^^^^^^^ New Architecture ^^^^^^^^^^^ */

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
