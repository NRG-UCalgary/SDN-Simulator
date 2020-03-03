package nrg.sdnsimulator.core.entity.network;

import java.util.HashMap;

import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.traffic.Packet;
import nrg.sdnsimulator.core.utility.Keywords;

public abstract class SDNSwitch extends Node {

	protected boolean isMonitored;
	protected HashMap<Integer, Integer> flowTable; // <FlowID, LinkID(neighbors)>
	protected int controlLinkID;
	protected HashMap<Integer, Integer> accessLinksIDs; // <NodeID(Host), LinkID>
	protected HashMap<Integer, Integer> networkLinksIDs; // <NodeID(Switch), LinkID>

	public SDNSwitch(int ID) {
		super(ID);
		isMonitored = true;
		flowTable = new HashMap<Integer, Integer>();
		accessLinksIDs = new HashMap<Integer, Integer>();
		networkLinksIDs = new HashMap<Integer, Integer>();
	}

	protected abstract void recvCtrlMessage(Network net, CtrlMessage message);

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

	protected void broadcastToHosts(Network net, Packet packet) {
		for (int dstHostID : accessLinksIDs.keySet()) {
			forwardToHost(net, dstHostID, packet);
		}
	}

	protected void forwardToHost(Network net, int dstHostID, Packet packet) {
		net.getLinks().get(accessLinksIDs.get(dstHostID)).bufferPacket(net, packet);
	}

	protected void forwardToSwitch(Network net, int flowID, Packet packet) {
		net.getLinks().get(flowTable.get(flowID)).bufferPacket(net, packet);
	}

	protected void forwardToController(Network net, Packet packet) {
		net.getLinks().get(controlLinkID).bufferPacket(net, packet);
	}

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

	public boolean isMonitored() {
		return isMonitored;
	}

	public void setMonitored(boolean isMonitored) {
		this.isMonitored = isMonitored;
	}

	public HashMap<Integer, Integer> getFlowTable() {
		return flowTable;
	}

	public void setFlowTable(HashMap<Integer, Integer> flowTable) {
		this.flowTable = flowTable;
	}

	public int getControlLinkID() {
		return controlLinkID;
	}

	public void setControlLinkID(int controlLinkID) {
		this.controlLinkID = controlLinkID;
	}

	public HashMap<Integer, Integer> getAccessLinksIDs() {
		return accessLinksIDs;
	}

	public void setAccessLinksIDs(HashMap<Integer, Integer> accessLinksIDs) {
		this.accessLinksIDs = accessLinksIDs;
	}

	public HashMap<Integer, Integer> getNetworkLinksIDs() {
		return networkLinksIDs;
	}

	public void setNetworkLinksIDs(HashMap<Integer, Integer> networkLinksIDs) {
		this.networkLinksIDs = networkLinksIDs;
	}

}
