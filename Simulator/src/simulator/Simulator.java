package simulator;

import system.Main;
import simulator.entities.network.*;
import simulator.entities.network.agents.*;
import simulator.entities.network.controllers.*;
import simulator.entities.network.hosts.*;
import simulator.entities.network.links.*;
import simulator.entities.network.switches.*;
import simulator.entities.traffic.*;
import utility.*;
import utility.dataStructures.*;

public class Simulator {

	/********* Static Programming Methods ***********/
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

	public static short getNodeType(int nodeID) {
		if (nodeID >= Keywords.HostNodeIDOffset) {
			return Keywords.Entities.Nodes.Types.Host;
		} else if (nodeID >= Keywords.SwitchNodeIDOffset) {
			return Keywords.Entities.Nodes.Types.SDNSwitch;
		} else if (nodeID >= Keywords.ControllerNodeIDOffset) {
			return Keywords.Entities.Nodes.Types.Controller;
		}
		return -1;
	}

	/************************************************/
	private OneToOneMap nodeLabels;
	private OneToOneMap linkLabels;
	private OneToOneMap flowLabels;

	private int hostCounter;
	private int switchCounter;
	private int controllerCounter;
	private int linkCounter;
	private int flowCounter;

	public final boolean OUTPUT = true;
	public int btllinkID = -1;

	/** ############### Variables ############### **/
	private Network net;

	/** ############### ID to Label Mappings ############### **/

	public Simulator() {
		/* Default Settings of the Simulator */
		nodeLabels = new OneToOneMap();
		linkLabels = new OneToOneMap();
		flowLabels = new OneToOneMap();

		controllerCounter = Keywords.ControllerNodeIDOffset;
		switchCounter = Keywords.SwitchNodeIDOffset;
		hostCounter = Keywords.HostNodeIDOffset;
		linkCounter = 0;
		flowCounter = 0;

		net = new Network();
	}

	/********** Topology Creation methods ***********/
	/* Controller Creation Method */
	public void createController(String label, short controllerType, float alpha, float beta, short routingAlgorithm) {
		Controller controller;
		switch (controllerType) {
		case Keywords.Entities.Controllers.Types.Controller_1:
			controller = new Controllerv1(controllerCounter, routingAlgorithm, alpha);
			break;
		case Keywords.Entities.Controllers.Types.Controller_2:
			controller = new Controllerv2(controllerCounter, routingAlgorithm, alpha, beta);
			break;
		default:
			controller = new DefaultController(controllerCounter, routingAlgorithm);
			break;
		}
		net.controllers.put(controllerCounter, controller);
		nodeLabels.put(controllerCounter, label);
		controllerCounter++;

	}

	public void createController(String label, short controllerType, float alpha, float beta) {
		createController(label, controllerType, alpha, beta, Keywords.RoutingAlgorithms.Dijkstra);
	}

	public void createController(String label, short routingAlgorithm) {
		createController(label, Keywords.Entities.Controllers.Types.Default, -1, routingAlgorithm);
	}

	public void createController(String label) {
		createController(label, Keywords.RoutingAlgorithms.Dijkstra);
	}

	/*-------------------------------------------------------*/

	/* Switch Creation Method */
	public void createSwitch(String label, short switchType) {
		SDNSwitch sw;
		switch (switchType) {
		case Keywords.Entities.Switches.Types.Switch_1:
			sw = new SDNSwitchv1(switchCounter);
			break;
		default:
			sw = new DefaultSDNSwitch(switchCounter);
			break;
		}
		net.switches.put(switchCounter, sw);
		nodeLabels.put(switchCounter, label);
		switchCounter++;
	}

	public void createSwitch(String label) {
		createSwitch(label, Keywords.Entities.Switches.Types.Default);
	}
	/*-------------------------------------------------------*/

	/* Host Creation Method */
	public void createHost(String label, short hostType) {
		Host host;
		switch (hostType) {
		case Keywords.Entities.Hosts.Types.Host_1:
			host = new DefaultHost(hostCounter);
			break;
		default:
			host = new DefaultHost(hostCounter);
			break;
		}
		net.hosts.put(hostCounter, host);
		nodeLabels.put(hostCounter, label);
		hostCounter++;
	}

	public void createHost(String label) {
		createHost(label, Keywords.Entities.Hosts.Types.Default);
	}
	/*-------------------------------------------------------*/

	/* Link Creation Method */
	public void createLink(String label, String srcNodeLabel, String dstNodeLabel, short linkType, float propDelay,
			float bandwidth, short bufferType, int bufferSize, int bufferPolicy, boolean isMonitored) {

		int srcNodeID = nodeLabels.getID(srcNodeLabel);
		int dstNodeID = nodeLabels.getID(dstNodeLabel);

		Link link;
		Link reverseLink;
		switch (linkType) {
		case Keywords.Entities.Links.Types.Link_1:
			link = new DefaultLink(linkCounter, srcNodeID, dstNodeID, (float) Mathematics.baseToMicro(propDelay),
					(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferType, bufferSize,
					bufferPolicy);
			reverseLink = new DefaultLink(reverseLinkID(linkCounter), dstNodeID, srcNodeID,
					(float) Mathematics.baseToMicro(propDelay),
					(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferType, bufferSize,
					bufferPolicy);
			break;
		default:
			link = new DefaultLink(linkCounter, srcNodeID, dstNodeID, (float) Mathematics.baseToMicro(propDelay),
					(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferType, bufferSize,
					bufferPolicy);
			reverseLink = new DefaultLink(reverseLinkID(linkCounter), dstNodeID, srcNodeID,
					(float) Mathematics.baseToMicro(propDelay),
					(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferType, bufferSize,
					bufferPolicy);
			break;
		}
		link.isMonitored = isMonitored;
		net.links.put(link.getID(), link);
		net.links.put(reverseLink.getID(), reverseLink);
		linkLabels.put(linkCounter, label);
		linkCounter++;

		if (isMonitored) {
			btllinkID = linkCounter;
		}

		switch (getNodeType(srcNodeID)) {
		case Keywords.Entities.Nodes.Types.SDNSwitch:
			net.switches.get(srcNodeID).connectToNode(link.getID(), dstNodeID, getNodeType(dstNodeID));
			break;
		case Keywords.Entities.Nodes.Types.Host:
			net.hosts.get(srcNodeID).connectToNode(link.getID(), dstNodeID, getNodeType(dstNodeID));
			break;
		case Keywords.Entities.Nodes.Types.Controller:
			net.controllers.get(srcNodeID).connectToNode(link.getID(), dstNodeID, getNodeType(dstNodeID));
			break;
		default:
			break;
		}
		switch (getNodeType(dstNodeID)) {
		case Keywords.Entities.Nodes.Types.SDNSwitch:
			net.switches.get(dstNodeID).connectToNode(reverseLink.getID(), srcNodeID, getNodeType(srcNodeID));
			break;
		case Keywords.Entities.Nodes.Types.Host:
			net.hosts.get(dstNodeID).connectToNode(reverseLink.getID(), srcNodeID, getNodeType(srcNodeID));
			break;
		case Keywords.Entities.Nodes.Types.Controller:
			net.controllers.get(dstNodeID).connectToNode(reverseLink.getID(), srcNodeID, getNodeType(srcNodeID));
			break;
		default:
			break;
		}

	}

	public void createLink(String label, String srcNodeLabel, String dstNodeLabel, float propDelay, float bandwidth,
			int bufferSize, boolean isMonitored) {
		createLink(label, srcNodeLabel, dstNodeLabel, Keywords.Entities.Links.Types.Default, propDelay, bandwidth,
				Keywords.Entities.Buffers.Types.Default, bufferSize, Keywords.Entities.Buffers.Policy.FIFO,
				isMonitored);

	}

	/********* Traffic Generation Methods **************/
	public void generateFlow(String label, String srcHostLabel, String dstHostLabel, int size, float arrivalTime,
			short agentType, int initialSWnd) {
		int srcHostID = nodeLabels.getID(srcHostLabel);
		int dstHostID = nodeLabels.getID(dstHostLabel);
		Flow flow = new Flow(flowCounter, srcHostID, dstHostID, size, arrivalTime);
		Flow reverseFlow = new Flow(reverseFlowStreamID(flow.getID()), dstHostID, srcHostID, size, arrivalTime);
		flowCounter++;
		flowLabels.put(flow.getID(), label);

		Agent srcAgent;
		Agent dstAgent;
		switch (agentType) {
		case Keywords.Entities.Agents.Types.SDTCP:
			srcAgent = new ESDTCPSenderv1(flow);
			dstAgent = new ESDTCPReceiverv1(reverseFlow);
			break;
		case Keywords.Entities.Agents.Types.v2:
			srcAgent = new Senderv2(flow);
			dstAgent = new Receiverv2(reverseFlow);
			break;
		default:
			srcAgent = new DefaultSender(flow, initialSWnd);
			dstAgent = new DefaultReceiver(reverseFlow);
			break;
		}

		net.hosts.get(srcHostID).transportAgent = srcAgent;
		net.hosts.get(dstHostID).transportAgent = dstAgent;
		net.hosts.get(srcHostID).initialize(net);
	}

	public void generateFlow(String label, String srcHostLabel, String dstHostLabel, int size, float arrivalTime,
			short agentType) {
		generateFlow(label, srcHostLabel, dstHostLabel, size, arrivalTime, agentType, 1);
	}

	/********** Run **********/
	private void initialize() {
		// Initialize everyThing here
		for (Controller controller : net.controllers.values()) {
			controller.setNetwokInformation(net);
			controller.setBottleneckLinkID(btllinkID); // TODO resolve this issue
		}
	}

	public Statistics run(float start_time, float end_time) {
		/* Other Default settings of the Simulator */
		initialize();
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

		return new Statistics(net, btllinkID);
	}

}
