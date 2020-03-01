package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network;

import java.util.HashMap;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Network;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Simulator;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.controller.ControlDatabase;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.controller.router.Dijkstra;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network.controller.router.Router;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Packet;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Segment;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.utility.Keywords;


public abstract class Controller extends Node {

	/** ========================================== **/
	// New design
	protected HashMap<Integer, Integer> controlLinksIDs; // <NodeID(Switch), LinkID>

	protected Segment recvdSegment;
	protected Network currentNetwork;
	public float numberOfAdmittedFlows;

	/** ========== Statistical Counters ========== **/
	public float numberOfDistinctSYNs;
	public float numberOfRejectedFlows;

	// Modules
	protected Router router;
	protected ControlDatabase database;

	public Controller(int ID, short routingAlgorithm) {
		super(ID);
		/** ========== Statistical Counters ========== **/
		numberOfDistinctSYNs = 0;
		numberOfAdmittedFlows = 0;
		numberOfRejectedFlows = 0;
		/** ========================================== **/

		controlLinksIDs = new HashMap<Integer, Integer>();

		switch (routingAlgorithm) {
		case Keywords.RoutingAlgorithms.Dijkstra:
			router = new Dijkstra();
			break;
		case Keywords.RoutingAlgorithms.Routing1:
			break;
		default:
			break;
		}
		recvdSegment = new Segment(-1, -1, -1, -1, -1, -1);

	}

	public void setNetwokInformation(Network net) {
		database = new ControlDatabase(net);
		router.setNodeInformation(net.switches);
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Network topology methods -------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public void connectToNode(int linkID, int dstNodeID, short dstNodeType) {
		this.controlLinksIDs.put(dstNodeID, linkID);
	}
	/* -------------------------------------------------------------------------- */

	protected void handleRouting(Network net, int srcAccessSwitchID, int dstAccessSwitchID) {
		HashMap<Integer, Integer> dataStreamPath = router.run(net, srcAccessSwitchID, dstAccessSwitchID);
		//Debugger.debugToConsole("Router has found the shortest path...");
		for (int switchID : dataStreamPath.keySet()) {
			//Debugger.debugToConsole(" From Switch: " + switchID + " to Switch: " + dataStreamPath.get(switchID));
		}
		HashMap<Integer, CtrlMessage> messagesToSwitchID = new HashMap<Integer, CtrlMessage>();

		// Preparing flow setup messages
		for (int switchID : dataStreamPath.keySet()) {
			// Data stream entry
			int nextSwitchID = dataStreamPath.get(switchID);
			if (!messagesToSwitchID.containsKey(switchID)) {
				messagesToSwitchID.put(switchID, new CtrlMessage(Keywords.SDNMessages.Types.FlowSetUp));
			}
			messagesToSwitchID.get(switchID).addFlowSetUpEntry(recvdSegment.getFlowID(),
					net.switches.get(switchID).getNetworkLinksIDs().get(nextSwitchID));
			// ACK stream entry
			if (!messagesToSwitchID.containsKey(nextSwitchID)) {
				messagesToSwitchID.put(nextSwitchID, new CtrlMessage(Keywords.SDNMessages.Types.FlowSetUp));
			}
			messagesToSwitchID.get(nextSwitchID).addFlowSetUpEntry(
					Simulator.reverseFlowStreamID(recvdSegment.getFlowID()),
					net.switches.get(nextSwitchID).getNetworkLinksIDs().get(switchID));
		}

		// Sending flow setup messages to the switches
		for (int switchID : messagesToSwitchID.keySet()) {
			//Debugger.debugToConsole("Sending flow setup message to switch: " + switchID);
			sendPacketToSwitch(net, switchID, new Packet(null, messagesToSwitchID.get(switchID)));
		}

		// Update the database pathOfFlowID
		database.pathOfFlowID.put(recvdSegment.getFlowID(), dataStreamPath);

		// FIXME Update sharedEgressLink for the single access switch
		database.sharedEgressLinkBw = net.links
				.get(net.switches.get(srcAccessSwitchID).networkLinksIDs.get(dataStreamPath.get(srcAccessSwitchID)))
				.getBandwidth();
		//Debugger.debugToConsole("=============================================================");
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	protected void sendControlMessageToAccessSwitches(Network net, HashMap<Integer, CtrlMessage> messages) {
		for (int switchID : database.getAccessSwitchIDsSet()) {
			sendPacketToSwitch(net, switchID, new Packet(null, messages.get(switchID)));
		}
	}

	/* =========================================== */
	/* ========== Switch Communication =========== */
	/* =========================================== */
	protected void sendPacketToSwitch(Network net, int switchID, Packet packet) {
		net.links.get((controlLinksIDs.get(switchID))).bufferPacket(net, packet);
	}

	/* =========================================== */
	/* ========== Utility ======================== */
	/* =========================================== */
	protected int getAccessSwitchID(Network net, int hostID) {
		return net.hosts.get(hostID).accessSwitchID;
	}

	public int getBottleneckLinkID() {
		return database.bottleneckLinkID;
	}

	public void setBottleneckLinkID(int id) {
		database.bottleneckLinkID = id;
	}

}
