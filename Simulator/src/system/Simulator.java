package system;

import switches.*;
import controllers.*;
import entities.*;
import transport_protocols.*;
import utilities.*;

public class Simulator {

	public final boolean OUTPUT = true;
	public OutputHandler outputHandle = new OutputHandler();

	/** ############### Default Variables ############### **/
	/* Controller */
	private final double CONTROLLER_RTT_DEALAY = 1.0;
	private final double CONTROLLER_PROCESS_DELAY = 1.0;
	private final int CONTROLLER_ROUTING_ALG = 1;
	private final int alpha = 1;

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

	private Network net;
	public Statistics stats;

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

		net = new Network();
	}

	/********** Run **********/
	public void run(Double start_time, Double end_time) {

		Debugger.connectivity(net);
		/* Other Default settings of the Simulator */

		/* Reading the first Event from Network Event List */
		/* Main Loop */
		double timeCheck = 0;
		while (net.getCurrentTime() <= end_time && net.eventList.size() > 0) {
			if (net.getCurrentTime() < timeCheck) {
				Debugger.debug("Error in simulator Time management: " + net.getCurrentTime());
			}
			/* Running the Current Event and Updating the net */
			net = net.eventList.getEvent().execute(net);
			timeCheck = net.getCurrentTime();
		}
		stats = new Statistics(net);
		Debugger.debug("Size of SeqNumbers: " + stats.flows.get(0).dataSeqNumSendingTimes.size());
		Debugger.debug("Size of ACKNumbers: " + stats.flows.get(0).ackSeqNumArrivalTimes.size());

		// Generating output files (Temporary)
		if (OUTPUT) {
			outputHandle.outOneFlow(stats);
		}
	}

	/********** Topology Creation methods ***********/

	/*-------------------------------------------------------*/
	/* Switch Creation Method */
	public void createSwitch(String label, double ctrlLinkPropagationDelay, double ctrlLinkBandwidth) {
		// Note that here we are assuming that we only use SDN switches.
		// Later, if needed, a mechanism for choosing type of switch should be
		// implemented
		Link controlLink = new Link(Keywords.ControllerID, switchCounter, Keywords.ControllerID,
				ctrlLinkPropagationDelay, Mathematics.MegabitPerSecondTobitPerMsecond(ctrlLinkBandwidth), 10,
				Keywords.FIFO);
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
	public void createLink(String label, String src, String dst, double propDelay, double bandwidth, int bufferSize,
			int bufferPolicy) {
		// The link from SRC to DST
		Link link = new Link(linkCounter, switchLabels.getKey(src), switchLabels.getKey(dst), propDelay,
				Mathematics.MegabitPerSecondTobitPerMsecond(bandwidth), bufferSize, bufferPolicy);
		net.switches.get(switchLabels.getKey(src)).networkLinks.put(switchLabels.getKey(dst), link);
		// Handling Labeling
		linkLabels.put(linkCounter, label);

		// The link from DST to SRC
		link = new Link(linkCounter + 10000, switchLabels.getKey(dst), switchLabels.getKey(src), propDelay,
				Mathematics.MegabitPerSecondTobitPerMsecond(bandwidth), bufferSize, bufferPolicy);
		net.switches.get(switchLabels.getKey(dst)).networkLinks.put(switchLabels.getKey(src), link);

		// Handling Labeling
		linkLabels.put(linkCounter + 10000, label + "r"); // TODO There should be a mechanism for handling different
															// labels for
		linkCounter++;
	}

	/* Access link creation method */
	public void createAccessLink(String label, String src, String dst, double propDelay, double bandwidth,
			int bufferSize, int bufferPolicy) {
		// The link from host to switch
		Link link = new Link(linkCounter, hostLabels.getKey(src), switchLabels.getKey(dst), propDelay,
				Mathematics.MegabitPerSecondTobitPerMsecond(bandwidth), bufferSize, bufferPolicy);
		net.hosts.get(hostLabels.getKey(src)).connectToNetwork(switchLabels.getKey(dst), link);

		// Handling Labeling
		linkLabels.put(linkCounter, label);

		// The link from switch to host
		link = new Link(linkCounter + 10000, switchLabels.getKey(dst), hostLabels.getKey(src), propDelay,
				Mathematics.MegabitPerSecondTobitPerMsecond(bandwidth), bufferSize, bufferPolicy);
		net.switches.get(switchLabels.getKey(dst)).accessLinks.put(hostLabels.getKey(src), link);
		net.switches.get(switchLabels.getKey(dst)).isAccessSwitch = true;

		// Handling Labeling
		linkLabels.put(linkCounter + 10000, label + "r"); // TODO There should be a mechanism for handling different
															// labels for
															// reverse links
		linkCounter++;
	}

	/*-------------------------------------------------------*/
	/* Controller Creation Method */
	public void createController(String label, int routing_alg, double rtt_delay, double process_delay) {
		net.controller = new Controllerv1(controllerCounter, net, routing_alg, alpha);

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

		Flow flow = new Flow(flowCounter, net.hosts.get(hostLabels.getKey(src)), net.hosts.get(hostLabels.getKey(dst)),
				size, arrival_time);
		// Handling Labeling
		flowLabels.put(flowCounter, label);

		Agent src_agent = null;
		Agent dst_agent = null;

		switch (type) {
		case Keywords.TCP:
			break;
		case Keywords.SDTCP:
			src_agent = new SDTCPSenderv1(flow);
			flow = new Flow(ACKStreamID(flowCounter), net.hosts.get(hostLabels.getKey(dst)),
					net.hosts.get(hostLabels.getKey(src)), size, arrival_time);
			dst_agent = new SDTCPReceiverv1(flow);
			break;
		case Keywords.RBTCP:
			break;
		default:
			Debugger.debug("Simulator.generateFlow()::Invalid type for flow.");
			break;
		}

		flowCounter++;
		// Net update
		net.hosts.get(hostLabels.getKey(src)).transportAgent = src_agent;
		net.hosts.get(hostLabels.getKey(dst)).transportAgent = dst_agent;

		// Creating initial ArrivalToSwitch event for the flow
		net = net.hosts.get(hostLabels.getKey(src)).initialize(net);

	}

	/********* General Programming Methods **********/

	public static int ACKStreamID(int dataStreamID) {
		return dataStreamID + 10000;
	}
}
