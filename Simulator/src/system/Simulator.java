package system;

import switches.*;
import controllers.*;
import entities.*;
import transport_protocols.*;
import utilities.Logger;
import utilities.OneToOneMap;

public class Simulator {

	/** ############### Default Variables ############### **/
	/* Controller */
	private final double CONTROLLER_RTT_DEALAY = 1.0;
	private final double CONTROLLER_PROCESS_DELAY = 1.0;
	private final int CONTROLLER_ROUTING_ALG = 1;

	/* Buffer Algorithm */
	// private final String BUFFER_ALG = "FCFS";

	/** ############### ID to Label Mappings ############### **/
	private OneToOneMap switchLabels;
	private OneToOneMap hostLabels;
	private OneToOneMap linkLabels;
	private OneToOneMap controllerLabels;
	private OneToOneMap flowLabels;

	private int switchCounter;
	private int hostCounter;
	private int linkCounter;
	private int controllerCounter;
	private int flowCounter;

	/** ############### Variables ############### **/

	private Logger log = new Logger();
	private Network net = new Network();

	public Simulator() {
		/* Default Settings of the Simulator */
		switchLabels = new OneToOneMap();
		hostLabels = new OneToOneMap();
		linkLabels = new OneToOneMap();
		controllerLabels = new OneToOneMap();
		flowLabels = new OneToOneMap();
		switchCounter = 0;
		hostCounter = 0;
		linkCounter = 0;
		controllerCounter = 0;
		flowCounter = 0;
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

		/* Reading the first Event from Network Event List */

		log.endOfPhase("Initialization done.");
		int loopCounter = 0;
		/* Main Loop */
		while (net.getCurrentTime() <= end_time) {
			log.startOfLoop("Simulator::Main Loop", loopCounter);
			/* Running the Current Event and Updating the net */
			net = net.eventList.getEvent().execute(net);

			loopCounter++;

		}
	}

	/********** Topology Creation methods ***********/

	/*-------------------------------------------------------*/
	/* Switch Creation Method */
	public void createSwitch(String label, double ctrlLinkPropagationDelay, int ctrlLinkBandwidth) {
		// Note that here we are assuming that we only use SDN switches.
		// Later, if needed, a mechanism for choosing type of switch should be
		// implemented

		Link controlLink = new Link(Keywords.ControllerID, switchCounter, Keywords.ControllerID,
				ctrlLinkPropagationDelay, ctrlLinkBandwidth, 1, Keywords.FIFO);
		SDNSwitch sw = new SDNSwitchv1(switchCounter, controlLink);
		net.switches.put(switchCounter, sw);

		// Handling labeling
		switchLabels.put(switchCounter, label);
		switchCounter++;
	}

	/*-------------------------------------------------------*/
	/* Host Creation Method */
	public void createHost(String label) {
		Host host = new Host(hostCounter);
		net.hosts.put(hostCounter, host);

		// Handling labeling
		hostLabels.put(hostCounter, label);
		hostCounter++;

	}

	/*-------------------------------------------------------*/
	/* Link Creation Method */
	public void createLink(String label, String src, String dst, double propDelay, int bandwidth, int bufferSize,
			int bufferPolicy) {
		// The link from SRC to DST
		Link link = new Link(linkCounter, switchLabels.getKey(src), switchLabels.getKey(dst), propDelay, bandwidth,
				bufferSize, bufferPolicy);
		net.switches.get(switchLabels.getKey(src)).networkLinks.put(switchLabels.getKey(dst), link);

		// Handling Labeling
		linkLabels.put(linkCounter, label);
		linkCounter++;

		// The link from DST to SRC
		link = new Link(linkCounter, switchLabels.getKey(dst), switchLabels.getKey(src), propDelay, bandwidth,
				bufferSize, bufferPolicy);
		net.switches.get(switchLabels.getKey(dst)).networkLinks.put(switchLabels.getKey(src), link);

		// Handling Labeling
		linkLabels.put(linkCounter, label + "r"); // TODO There should be a mechanism for handling different labels for
													// reverse links
		linkCounter++;
	}

	/* Access link creation method */
	public void createAccessLink(String label, String src, String dst, double propDelay, int bandwidth, int bufferSize,
			int bufferPolicy) {
		// The link from host to switch
		Link link = new Link(linkCounter, hostLabels.getKey(src), switchLabels.getKey(dst), propDelay, bandwidth,
				bufferSize, bufferPolicy);
		net.hosts.get(hostLabels.getKey(src)).connectToNetwork(switchLabels.getKey(dst), link);

		// Handling Labeling
		linkLabels.put(linkCounter, label);
		linkCounter++;

		// The link from switch to host
		link = new Link(linkCounter, switchLabels.getKey(dst), hostLabels.getKey(dst), propDelay, bandwidth, bufferSize,
				bufferPolicy);
		net.switches.get(switchLabels.getKey(dst)).accessLinks.put(hostLabels.getKey(src), link);

		// Handling Labeling
		linkLabels.put(linkCounter, label + "r"); // TODO There should be a mechanism for handling different labels for
													// reverse links
		linkCounter++;
	}

	/*-------------------------------------------------------*/
	/* Controller Creation Method */
	public void createController(String label, int routing_alg, double rtt_delay, double process_delay) {
		Controller controller = new Controllerv1(controllerCounter, net, routing_alg);
		net.controller = controller;

		// Handling Labeling
		controllerLabels.put(controllerCounter, label);
		controllerCounter++;
	}

	// Overload
	public void createController(String label, int routing_alg) {
		createController(label, routing_alg, CONTROLLER_RTT_DEALAY, CONTROLLER_PROCESS_DELAY);
	}

	// Overload
	public void createController(String label) {
		createController(label, CONTROLLER_ROUTING_ALG);
	}
	/*-------------------------------------------------------*/

	/********* Flow Generation Methods **************/

	public void generateFlow(String label, String type, String src, String dst, int size, double arrival_time) {
		log.entranceToMethod("Simulator", "generateFlow");

		Flow flow = new Flow(flowCounter, type, net.hosts.get(hostLabels.getKey(src)),
				net.hosts.get(hostLabels.getKey(dst)), size, arrival_time);

		// Handling Labeling
		flowLabels.put(flowCounter, label);
		flowCounter++;

		Agent src_agent = null;
		Agent dst_agent = null;

		switch (type) {
		case Keywords.TCP:
			src_agent = new TCPSender(flow);
			dst_agent = new TCPReceiver(flow);
			break;
		case Keywords.RBTCP:
			Main.print("Simulator.generateFlow()::RBTCP sender and receiver are going to be created.");
			src_agent = new RBTCPSenderv1(flow);
			dst_agent = new RBTCPReceiverv1(flow);
		case Keywords.SDTCP:

		default:
			System.out.println("Simulator.generateFlow()::Invalid type for flow.");
			break;
		}

		// TODO should it be here?!
		// src_agent.start(net);

		// Net update
		net.hosts.get(hostLabels.getKey(src)).transportAgent = src_agent;
		net.hosts.get(hostLabels.getKey(dst)).transportAgent = dst_agent;

	}

	/********* General Programming Methods **********/

}
