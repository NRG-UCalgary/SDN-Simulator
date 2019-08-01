package simulator.entities;

import java.util.HashMap;

import simulator.Network;
import system.Main;
import utility.Keywords;
import utility.Mathematics;

public abstract class SDNSwitch extends Node {

	public HashMap<Integer, Link> accessLinks; // <HostID, Link>

	public int controllerID = 0; // Assuming there is only 1 controller
	public Link controlLink;

	public HashMap<Integer, Integer> flowTable; // <FlowID, SwitchID(neighbors)>
	public boolean isMonitored;
	public HashMap<Integer, Link> networkLinks; // <SwitchID, Link>

	public SDNSwitch(int ID, Link controlLink) {
		super(ID, Keywords.Nodes.Names.Switch);
		this.controlLink = controlLink;
		isMonitored = true;
		accessLinks = new HashMap<Integer, Link>();
		networkLinks = new HashMap<Integer, Link>();
		flowTable = new HashMap<Integer, Integer>();
	}

	public void addFlowTableEntry(int flowID, int neighborID) {
		flowTable.put(flowID, neighborID);
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

	/* ========== Forwarding methods =========================== */
	public void broadcastToHosts(Network net, Packet packet) {
		float nextTime = 0;
		float bufferTime = 0;
		for (int hostID : accessLinks.keySet()) {
			bufferTime = accessLinks.get(hostID).buffer.enQueue(net.getCurrentTime(),
					accessLinks.get(hostID).getTransmissionDelay(packet.getSize()));
			nextTime = Mathematics.addFloat(net.getCurrentTime(), bufferTime);
			if (nextTime > 0) {
				packet.segment.setDstHostID(hostID);
				net.eventList.addDepartureFromSwitch(nextTime, this.ID, hostID, packet);
			} else {
				Main.error("SDNSwitch", "broadcastToHosts", "Packe Drop happened at switch_" + ID);
			}
		}
	}

	public void deQueueFromAccessLinkBuffer(int dstHostID) {
		accessLinks.get(dstHostID).buffer.deQueue();
	}

	public void deQueueFromNetworkLinkBuffer(int flowID) {
		networkLinks.get(getNextSwitchID(flowID)).buffer.deQueue();
	}

	public void forwardToController(Network net, Packet packet) {
		float bufferTime = controlLink.bufferPacket(net.getCurrentTime(), packet);
		float nextTime = Mathematics.addFloat(net.getCurrentTime(), bufferTime);
		net.eventList.addDepartureFromSwitch(nextTime, this.ID, controllerID, packet);
	}

	/* ========================================================= */

	public void forwardToHost(Network net, Packet packet) {
		int dstHostID = packet.segment.getDstHostID();
		float bufferTime = accessLinks.get(dstHostID).bufferPacket(net.getCurrentTime(), packet);
		float nextTime = Mathematics.addFloat(net.getCurrentTime(), bufferTime);
		if (nextTime > 0) {
			net.eventList.addDepartureFromSwitch(nextTime, this.ID, dstHostID, packet);
		} else {
			Main.error("SDNSwitch", "forwardToHost", "Packe Drop happened at switch_" + ID);
		}
		/** ===== Statistical Counters ===== **/
		if (isMonitored) {
			net.hosts.get(packet.segment.getSrcHostID()).updateFlowTotalBufferTime(bufferTime);
		}
		/** ================================ **/
	}

	public void forwardToSwitch(Network net, int nextSwitchID, Packet packet) {
		float bufferTime = networkLinks.get(nextSwitchID).bufferPacket(net.getCurrentTime(), packet);
		float nextTime = Mathematics.addFloat(net.getCurrentTime(), bufferTime);
		if (nextTime > 0) {
			net.eventList.addDepartureFromSwitch(nextTime, this.ID, nextSwitchID, packet);
		} else {
			Main.error("SDNSwitch", "forwardToSwitch", "Packe Drop happened at switch_" + ID);
		}
	}

	public float getAccessLinkTotalDelay(int hostID, int segmentSize) {
		return accessLinks.get(hostID).getTotalDelay(segmentSize);
	}

	public float getNetworkLinkTotalDelay(int flowID, int segmentSize) {
		return networkLinks.get(getNextSwitchID(flowID)).getTotalDelay(segmentSize);
	}

	public int getNextSwitchID(int flowID) {
		return flowTable.get(flowID);
	}

	public boolean hasFlowEntry(int flowID) {
		if (flowTable.containsKey(flowID)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isConnectedToHost(int hostID) {
		if (accessLinks.get(hostID) != null) {
			return true;
		} else {
			return false;
		}
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */
	public abstract void recvCtrlMessage(Network net, CtrlMessage message);

}
