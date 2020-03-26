package nrg.sdnsimulator.core.entity.network;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.Simulator;
import nrg.sdnsimulator.core.entity.network.controller.ControlDatabase;
import nrg.sdnsimulator.core.entity.network.controller.router.Dijkstra;
import nrg.sdnsimulator.core.entity.network.controller.router.Router;
import nrg.sdnsimulator.core.entity.traffic.Packet;
import nrg.sdnsimulator.core.entity.traffic.Segment;
import nrg.sdnsimulator.core.utility.Keywords;

@Getter
@Setter
public abstract class Controller extends Node {

	protected HashMap<Integer, Integer> controlLinksIDs; // <NodeID(Switch), LinkID>
	protected Segment recvdSegment;
	protected Network currentNetwork;
	protected float numberOfAdmittedFlows;
	protected float numberOfDistinctSYNs;
	protected float numberOfRejectedFlows;
	protected Router router;
	protected ControlDatabase database;

	public Controller(int ID, short routingAlgorithm) {
		super(ID);
		numberOfDistinctSYNs = 0;
		numberOfAdmittedFlows = 0;
		numberOfRejectedFlows = 0;
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
		router.setNodeInformation(net.getSwitches());
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
		HashMap<Integer, CtrlMessage> messagesToSwitchID = new HashMap<Integer, CtrlMessage>();

		// Preparing flow setup messages
		for (int switchID : dataStreamPath.keySet()) {
			// Data stream entry
			int nextSwitchID = dataStreamPath.get(switchID);
			if (!messagesToSwitchID.containsKey(switchID)) {
				messagesToSwitchID.put(switchID, new CtrlMessage(Keywords.SDNMessages.Types.FlowSetUp));
			}
			messagesToSwitchID.get(switchID).addFlowSetUpEntry(recvdSegment.getFlowID(),
					net.getSwitches().get(switchID).getNetworkLinksIDs().get(nextSwitchID));
			// ACK stream entry
			if (!messagesToSwitchID.containsKey(nextSwitchID)) {
				messagesToSwitchID.put(nextSwitchID, new CtrlMessage(Keywords.SDNMessages.Types.FlowSetUp));
			}
			messagesToSwitchID.get(nextSwitchID).addFlowSetUpEntry(
					Simulator.reverseFlowStreamID(recvdSegment.getFlowID()),
					net.getSwitches().get(nextSwitchID).getNetworkLinksIDs().get(switchID));
		}

		// Sending flow setup messages to the switches
		for (int switchID : messagesToSwitchID.keySet()) {
			// Debugger.debugToConsole("Sending flow setup message to switch: " + switchID);
			sendPacketToSwitch(net, switchID, new Packet(null, messagesToSwitchID.get(switchID)));
		}

		// Update the database pathOfFlowID
		database.getPathOfFlowID().put(recvdSegment.getFlowID(), dataStreamPath);

		// FIXME Update sharedEgressLink for the single access switch
		database.setSharedEgressLinkBw(net.getLinks().get(
				net.getSwitches().get(srcAccessSwitchID).networkLinksIDs.get(dataStreamPath.get(srcAccessSwitchID)))
				.getBandwidth());
		// Debugger.debugToConsole("=============================================================");
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
		net.getLinks().get((controlLinksIDs.get(switchID))).bufferPacket(net, packet);
	}

	/* =========================================== */
	/* ========== Utility ======================== */
	/* =========================================== */
	protected int getAccessSwitchID(Network net, int hostID) {
		return net.getHosts().get(hostID).accessSwitchID;
	}

	public int getBottleneckLinkID() {
		return database.bottleneckLinkID;
	}

	public void setBottleneckLinkID(int id) {
		database.bottleneckLinkID = id;
	}

}
