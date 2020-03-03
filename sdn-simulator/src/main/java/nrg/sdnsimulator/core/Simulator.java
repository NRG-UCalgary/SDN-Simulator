package nrg.sdnsimulator.core;

import nrg.sdnsimulator.core.entity.network.Agent;
import nrg.sdnsimulator.core.entity.network.Controller;
import nrg.sdnsimulator.core.entity.network.Host;
import nrg.sdnsimulator.core.entity.network.Link;
import nrg.sdnsimulator.core.entity.network.SDNSwitch;
import nrg.sdnsimulator.core.entity.network.agent.DefaultReceiver;
import nrg.sdnsimulator.core.entity.network.agent.DefaultSender;
import nrg.sdnsimulator.core.entity.network.agent.ESDTCPReceiverv1;
import nrg.sdnsimulator.core.entity.network.agent.ESDTCPSenderv1;
import nrg.sdnsimulator.core.entity.network.agent.Receiverv2;
import nrg.sdnsimulator.core.entity.network.agent.Senderv2;
import nrg.sdnsimulator.core.entity.network.controller.Controllerv1;
import nrg.sdnsimulator.core.entity.network.controller.Controllerv2;
import nrg.sdnsimulator.core.entity.network.controller.DefaultController;
import nrg.sdnsimulator.core.entity.network.host.DefaultHost;
import nrg.sdnsimulator.core.entity.network.link.DefaultLink;
import nrg.sdnsimulator.core.entity.network.sdnswitch.DefaultSDNSwitch;
import nrg.sdnsimulator.core.entity.network.sdnswitch.SDNSwitchv1;
import nrg.sdnsimulator.core.entity.traffic.Flow;
import nrg.sdnsimulator.core.utility.Keywords;
import nrg.sdnsimulator.core.utility.Mathematics;
import nrg.sdnsimulator.core.utility.Statistics;
import nrg.sdnsimulator.core.utility.datastructure.OneToOneMap;

public class Simulator {

	private OneToOneMap nodeLabels;
	private OneToOneMap linkLabels;
	private OneToOneMap flowLabels;
	private int hostCounter;
	private int switchCounter;
	private int controllerCounter;
	private int linkCounter;
	private int flowCounter;
	private int btllinkID = -1;
	private Network net;

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
	public void createController(String label, short controllerType, float alpha, float beta,
			float gamma, short routingAlgorithm) {
		Controller controller;
		switch (controllerType) {
		case Keywords.Entities.Controllers.Types.Controller_1:
			controller = new Controllerv1(controllerCounter, routingAlgorithm, alpha);
			break;
		case Keywords.Entities.Controllers.Types.Controller_2:
			controller = new Controllerv2(controllerCounter, routingAlgorithm, alpha, beta, gamma);
			break;
		default:
			controller = new DefaultController(controllerCounter, routingAlgorithm);
			break;
		}
		net.getControllers().put(controllerCounter, controller);
		nodeLabels.put(controllerCounter, label);
		controllerCounter++;

	}

	public void createController(String label, short controllerType, float alpha, float beta,
			float gamma) {
		createController(label, controllerType, alpha, beta, gamma,
				Keywords.RoutingAlgorithms.Dijkstra);
	}

	public void createController(String label, short routingAlgorithm) {
		createController(label, Keywords.Entities.Controllers.Types.Default, -1, -1, -1,
				routingAlgorithm);
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
		net.getSwitches().put(switchCounter, sw);
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
		net.getHosts().put(hostCounter, host);
		nodeLabels.put(hostCounter, label);
		hostCounter++;
	}

	public void createHost(String label) {
		createHost(label, Keywords.Entities.Hosts.Types.Default);
	}
	/*-------------------------------------------------------*/

	/* Link Creation Method */
	public void createLink(String label, String srcNodeLabel, String dstNodeLabel, short linkType,
			float propDelay, float bandwidth, short bufferType, int bufferSize, int bufferPolicy,
			boolean isMonitored) {

		int srcNodeID = nodeLabels.getID(srcNodeLabel);
		int dstNodeID = nodeLabels.getID(dstNodeLabel);

		Link link;
		Link reverseLink;
		switch (linkType) {
		case Keywords.Entities.Links.Types.Link_1:
			link = new DefaultLink(linkCounter, srcNodeID, dstNodeID,
					(float) Mathematics.baseToMicro(propDelay),
					(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferType,
					bufferSize, bufferPolicy);
			reverseLink = new DefaultLink(reverseLinkID(linkCounter), dstNodeID, srcNodeID,
					(float) Mathematics.baseToMicro(propDelay),
					(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferType,
					bufferSize, bufferPolicy);
			break;
		default:
			link = new DefaultLink(linkCounter, srcNodeID, dstNodeID,
					(float) Mathematics.baseToMicro(propDelay),
					(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferType,
					bufferSize, bufferPolicy);
			reverseLink = new DefaultLink(reverseLinkID(linkCounter), dstNodeID, srcNodeID,
					(float) Mathematics.baseToMicro(propDelay),
					(float) Mathematics.bitPerSecondTobitPerMicroSecond(bandwidth), bufferType,
					bufferSize, bufferPolicy);
			break;
		}
		link.setMonitored(isMonitored);
		net.getLinks().put(link.getID(), link);
		net.getLinks().put(reverseLink.getID(), reverseLink);
		linkLabels.put(linkCounter, label);
		linkCounter++;

		if (isMonitored) {
			btllinkID = linkCounter;
		}

		switch (getNodeType(srcNodeID)) {
		case Keywords.Entities.Nodes.Types.SDNSwitch:
			net.getSwitches().get(srcNodeID).connectToNode(link.getID(), dstNodeID,
					getNodeType(dstNodeID));
			break;
		case Keywords.Entities.Nodes.Types.Host:
			net.getHosts().get(srcNodeID).connectToNode(link.getID(), dstNodeID,
					getNodeType(dstNodeID));
			break;
		case Keywords.Entities.Nodes.Types.Controller:
			net.getControllers().get(srcNodeID).connectToNode(link.getID(), dstNodeID,
					getNodeType(dstNodeID));
			break;
		default:
			break;
		}
		switch (getNodeType(dstNodeID)) {
		case Keywords.Entities.Nodes.Types.SDNSwitch:
			net.getSwitches().get(dstNodeID).connectToNode(reverseLink.getID(), srcNodeID,
					getNodeType(srcNodeID));
			break;
		case Keywords.Entities.Nodes.Types.Host:
			net.getHosts().get(dstNodeID).connectToNode(reverseLink.getID(), srcNodeID,
					getNodeType(srcNodeID));
			break;
		case Keywords.Entities.Nodes.Types.Controller:
			net.getControllers().get(dstNodeID).connectToNode(reverseLink.getID(), srcNodeID,
					getNodeType(srcNodeID));
			break;
		default:
			break;
		}

	}

	public void createLink(String label, String srcNodeLabel, String dstNodeLabel, float propDelay,
			float bandwidth, int bufferSize, boolean isMonitored) {
		createLink(label, srcNodeLabel, dstNodeLabel, Keywords.Entities.Links.Types.Default,
				propDelay, bandwidth, Keywords.Entities.Buffers.Types.Default, bufferSize,
				Keywords.Entities.Buffers.Policy.FIFO, isMonitored);

	}

	/********* Traffic Generation Methods **************/
	public void generateFlow(String label, String srcHostLabel, String dstHostLabel, int size,
			float arrivalTime, short agentType, int initialSWnd) {
		int srcHostID = nodeLabels.getID(srcHostLabel);
		int dstHostID = nodeLabels.getID(dstHostLabel);
		Flow flow = new Flow(flowCounter, srcHostID, dstHostID, size, arrivalTime);
		Flow reverseFlow = new Flow(reverseFlowStreamID(flow.getID()), dstHostID, srcHostID, size,
				arrivalTime);
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

		net.getHosts().get(srcHostID).setTransportAgent(srcAgent);
		net.getHosts().get(dstHostID).setTransportAgent(dstAgent);
		net.getHosts().get(srcHostID).initialize(net);
	}

	public void generateFlow(String label, String srcHostLabel, String dstHostLabel, int size,
			float arrivalTime, short agentType) {
		generateFlow(label, srcHostLabel, dstHostLabel, size, arrivalTime, agentType, 1);
	}

	/********** Run **********/
	private void initialize() {
		// Initialize everyThing here
		for (Controller controller : net.getControllers().values()) {
			controller.setNetwokInformation(net);
			controller.setBottleneckLinkID(btllinkID); // TODO resolve this issue
		}
	}

	public Statistics run(float start_time, float end_time) {
		/* Other Default settings of the Simulator */
		initialize();
		/* Reading the first Event from Network Event List */
		/* Main Loop */
		double timeCheck = 0;
		while (net.getCurrentTime() <= end_time && net.getEventList().size() > 0) {
			/* Running the Current Event and Updating the net */
			net.getEventList().getEvent().execute(net);
			net.getEventList().removeEvent();
			timeCheck = net.getCurrentTime();
		}

		return new Statistics(net, btllinkID);
	}

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

}
