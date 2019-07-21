package system;

import entities.*;
import entities.Agents.*;
import entities.controllers.*;
import entities.switches.*;
import system.utility.*;
import system.utility.dataStructures.OneToOneMap;

public class Simulator {

	public final boolean OUTPUT = true;

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
	public Statistics run(float start_time, float end_time) {
		/* Other Default settings of the Simulator */
		// Debugger.connectivity(net);
		/* Reading the first Event from Network Event List */
		/* Main Loop */
		double timeCheck = 0;
		while (net.getCurrentTime() <= end_time && net.eventList.size() > 0) {
			if (net.getCurrentTime() < timeCheck) {
				Main.error("Simulator", "run", "Invalid time progression.");
			}
			/* Running the Current Event and Updating the net */
			net.eventList.getEvent().execute(net);
			net.eventList.removeEvent();
			timeCheck = net.getCurrentTime();
		}
		return new Statistics(net);
	}

	/********** Topology Creation methods ***********/

	/*-------------------------------------------------------*/
	/* Switch Creation Method */
	public void createSwitch(String label, float ctrlLinkPropagationDelay, float ctrlLinkBandwidth) {
		Link controlLink = new Link(Keywords.ControllerID, switchCounter, Keywords.ControllerID,
				ctrlLinkPropagationDelay,
				(float) Mathematics.bitPerSecondTobitPerMicroSecond((double) ctrlLinkBandwidth), Integer.MAX_VALUE,
				Keywords.Buffers.Policy.FIFO);
		SDNSwitch sw = new SDNSwitchv1(switchCounter, controlLink);
		net.switches.put(switchCounter, sw);
		switchLabels.put(switchCounter, label);
		switchCounter++;
	}

	/*-------------------------------------------------------*/
	/* Host Creation Method */
	public void createHost(String label) {
		Host host = new Host(hostCounter);
		net.hosts.put(hostCounter, host);
		hostLabels.put(hostCounter, label);
		hostCounter++;
	}

	/*-------------------------------------------------------*/
	/* Link Creation Method */
	public void createLink(String label, String src, String dst, float propDelay, float bandwidth, int bufferSize,
			int bufferPolicy, boolean isMonitored) {
		Link link = new Link(linkCounter, switchLabels.getKey(src), switchLabels.getKey(dst), propDelay,
				(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferSize, bufferPolicy);
		net.switches.get(switchLabels.getKey(src)).networkLinks.put(switchLabels.getKey(dst), link);
		linkLabels.put(linkCounter, label);
		link.isMonitored = isMonitored;
		link = new Link(reverseLinkID(linkCounter), switchLabels.getKey(dst), switchLabels.getKey(src), propDelay,
				(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferSize, bufferPolicy);
		net.switches.get(switchLabels.getKey(dst)).networkLinks.put(switchLabels.getKey(src), link);
		linkLabels.put(reverseLinkID(linkCounter), label + "r"); // TODO There should be a mechanism for handling
		linkCounter++;
	}

	/* Access link creation method */
	public void createAccessLink(String label, String src, String dst, float propDelay, float bandwidth, int bufferSize,
			int bufferPolicy) {
		Link link = new Link(linkCounter, hostLabels.getKey(src), switchLabels.getKey(dst), propDelay,
				(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferSize, bufferPolicy);
		net.hosts.get(hostLabels.getKey(src)).connectToNetwork(switchLabels.getKey(dst), link);
		linkLabels.put(linkCounter, label);
		link = new Link(reverseLinkID(linkCounter), switchLabels.getKey(dst), hostLabels.getKey(src), propDelay,
				(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferSize, bufferPolicy);
		net.switches.get(switchLabels.getKey(dst)).accessLinks.put(hostLabels.getKey(src), link);
		linkLabels.put(reverseLinkID(linkCounter), label + "r"); // TODO There should be a mechanism for handling
		linkCounter++;
	}

	/*-------------------------------------------------------*/
	/* Controller Creation Method */
	public void createController(String label, float alpha, int routingAlgorithm) {
		net.controller = new Controllerv1(controllerCounter, net, routingAlgorithm, alpha);
		for (SDNSwitch sdnSwitch : net.switches.values()) {
			net.controller.connectSwitch(sdnSwitch.getID(), sdnSwitch.controlLink);
		}
		controllerLabels.put(controllerCounter, label);
		controllerCounter++;
	}
	/*-------------------------------------------------------*/

	/********* Flow Generation Methods **************/

	public void generateFlow(String label, String type, String src, String dst, int size, float arrival_time) {

		Flow flow = new Flow(flowCounter, hostLabels.getKey(src), hostLabels.getKey(dst), size, arrival_time);
		flowLabels.put(flowCounter, label);
		Agent src_agent = null;
		Agent dst_agent = null;
		switch (type) {
		case Keywords.Agents.Types.SDTCP:
			src_agent = new SDTCPSenderv1(flow);
			flow = new Flow(reverseFlowStreamID(flowCounter), hostLabels.getKey(dst), hostLabels.getKey(src), size,
					arrival_time);
			dst_agent = new SDTCPReceiverv1(flow);
			break;
		default:
			Main.error("Simulator", "generateFlow", "Invalid flow type (" + type + ").");
			break;
		}
		flowCounter++;
		net.hosts.get(hostLabels.getKey(src)).transportAgent = src_agent;
		net.hosts.get(hostLabels.getKey(dst)).transportAgent = dst_agent;
		net.hosts.get(hostLabels.getKey(src)).initialize(net);
	}

	/********* General Programming Methods **********/

	public static int reverseFlowStreamID(int streamID) {
		int offset = Keywords.ACKStreamIDOffSet;
		if (streamID < offset) {
			return streamID + offset;
		} else {
			return streamID - offset;
		}
	}

	public static int reverseLinkID(int linkID) {
		int offset = Keywords.ReverseLinkIDOffSet;
		if (linkID < offset) {
			return linkID + offset;
		} else {
			return linkID - offset;
		}
	}

}
