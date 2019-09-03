package simulator.entities.network;

import java.util.HashMap;

import simulator.Network;
import simulator.entities.traffic.Packet;
import utility.*;

public abstract class SDNSwitch extends Node {
	public boolean isMonitored;

	public HashMap<Integer, Integer> flowTable; // <FlowID, LinkID(neighbors)>

	protected int controlLinkID;
	protected HashMap<Integer, Integer> accessLinksIDs; // <NodeID(Host), LinkID>
	protected HashMap<Integer, Integer> networkLinksIDs; // <NodeID(Switch), LinkID>

	public SDNSwitch(int ID) {
		super(ID);

		isMonitored = true;
		flowTable = new HashMap<Integer, Integer>();
		// New design
		accessLinksIDs = new HashMap<Integer, Integer>();
		networkLinksIDs = new HashMap<Integer, Integer>();

	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */
	protected abstract void recvCtrlMessage(Network net, CtrlMessage message);

	/* -------------------------------------------------------------------------- */
	/* ---------- Network topology methods -------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public void connectToNode(int linkID, int dstNodeID, short dstNodeType) {

		switch (dstNodeType) {
		case Keywords.Entities.Nodes.Types.Controller:
			controlLinkID = linkID;
			break;
		case Keywords.Entities.Nodes.Types.Host:
			accessLinksIDs.put(dstNodeID, linkID);
			break;
		case Keywords.Entities.Nodes.Types.SDNSwitch:
			networkLinksIDs.put(dstNodeID, linkID);
			break;

		default:
			break;
		}
	}
	/* -------------------------------------------------------------------------- */

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

	/* ========== Forwarding methods =========================== */
	protected void broadcastToHosts(Network net, Packet packet) {
		for (int dstHostID : accessLinksIDs.keySet()) {
			forwardToHost(net, dstHostID, packet);
		}
	}

	protected void forwardToHost(Network net, int dstHostID, Packet packet) {
		net.links.get(accessLinksIDs.get(dstHostID)).bufferPacket(net, packet);
	}

	protected void forwardToSwitch(Network net, int flowID, Packet packet) {
		net.links.get(flowTable.get(flowID)).bufferPacket(net, packet);
	}

	protected void forwardToController(Network net, Packet packet) {
		net.links.get(controlLinkID).bufferPacket(net, packet);
	}

	/* ========================================================= */
	protected void addFlowTableEntry(int flowID, int egressLinkID) {
		flowTable.put(flowID, egressLinkID);
	}

	protected boolean hasFlowEntry(int flowID) {
		if (flowTable.containsKey(flowID)) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean isConnectedToHost(int hostID) {
		if (accessLinksIDs.get(hostID) != null) {
			return true;
		} else {
			return false;
		}
	}

	/* ========================= Getters ============================ */
	public HashMap<Integer, Integer> getNetworkLinksIDs() {
		return networkLinksIDs;
	}

}
