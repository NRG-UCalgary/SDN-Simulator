package entities.switches;

import java.util.HashMap;

import entities.*;
import system.*;
import system.utility.*;

public abstract class SDNSwitch extends Node {

	public boolean isMonitored;

	public Link controlLink;
	public int controllerID = 0; // Assuming there is only 1 controller

	public HashMap<Integer, Link> accessLinks; // <HostID, Link>
	public HashMap<Integer, Link> networkLinks; // <SwitchID, Link>
	private HashMap<Integer, Integer> flowTable; // <FlowID, SwitchID(neighbors)>

	public SDNSwitch(int ID, Link controlLink) {
		super(ID, Keywords.Nodes.Names.Switch);
		this.controlLink = controlLink;
		isMonitored = true;
		accessLinks = new HashMap<Integer, Link>();
		networkLinks = new HashMap<Integer, Link>();
		flowTable = new HashMap<Integer, Integer>();
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */
	public abstract void recvCtrlMessage(Network net, CtrlMessage message);

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Node) ---------- */
	/* --------------------------------------------------- */
	public void recvPacket(Network net, Packet packet) {
		/* ================================================ */
		/* ========== Control message stays in switch ===== */
		/* ================================================ */
		if (packet.type == Keywords.Packets.Types.SDNControl) {
			recvCtrlMessage(net, packet.controlMessage);
		}
		/* ================================================ */
		/* ========== Broadcast segment to hosts ========== */
		/* ================================================ */
		else if (packet.segment.getDstHostID() == Keywords.BroadcastDestination) {
			broadcastToHosts(net, packet.segment);
		}
		/* ================================================ */
		/* ========== Segment to end host ================= */
		/* ================================================ */
		else if (isConnectedToHost(packet.segment.getDstHostID())) {
			forwardToHost(net, packet.segment.getDstHostID(), packet.segment);
		}
		/* ================================================ */
		/* ========== Segment to next switch ============== */
		/* ================================================ */
		else if (hasFlowEntry(packet.segment.getFlowID())) {
			if (packet.segment.getType() == Keywords.Segments.Types.UncontrolledFIN) {
				/* Uncontrolled FIN goes to controller */
				forwardToController(net, packet.segment);
			} else {
				forwardToSwitch(net, getNextSwitchID(packet.segment.getFlowID()), packet.segment);
			}
		}
		/* ================================================ */
		/* ========== Segment to controller =============== */
		/* ================================================ */
		else {
			forwardToController(net, packet.segment);
		}
	}

	public void releasePacket(Network net, int dstNodeID, Packet packet) {
		float nextTime, linkUtilizationTime;
		/* ================================================ */
		/* ========== Next node is a Host ================= */
		/* ================================================ */
		if (isConnectedToHost(packet.segment.getDstHostID())) {
			linkUtilizationTime = accessLinks.get(dstNodeID).getTransmissionDelay(packet.segment.getSize());
			nextTime = net.getCurrentTime() + getAccessLinkTotalDelay(dstNodeID, packet.segment.getSize());
			net.eventList.addArrivalToHost(nextTime, dstNodeID, packet);
			deQueueFromAccessLinkBuffer(dstNodeID);
			/** ===== Statistical Counters ===== **/
			if (isMonitored) {
				accessLinks.get(dstNodeID).updateUtilizationCounters(net.getCurrentTime(), packet.segment.getFlowID(),
						linkUtilizationTime);
			}
			/** ================================ **/
		}
		/* ================================================ */
		/* ========== Next node is a Switch =============== */
		/* ================================================ */
		else if (hasFlowEntry(packet.segment.getFlowID())) {

			if (packet.segment.getType() == Keywords.Segments.Types.UncontrolledFIN) {
				/* Uncontrolled FIN goes to controller */
				nextTime = net.getCurrentTime() + controlLink.getTotalDelay(packet.segment.getSize());
				net.eventList.addArrivalToController(nextTime, this.ID, packet);
				controlLink.buffer.deQueue();
			} else {
				linkUtilizationTime = networkLinks.get(dstNodeID).getTransmissionDelay(packet.segment.getSize());
				nextTime = net.getCurrentTime()
						+ getNetworkLinkTotalDelay(packet.segment.getFlowID(), packet.segment.getSize());
				net.eventList.addArrivalToSwitch(nextTime, dstNodeID, packet);
				deQueueFromNetworkLinkBuffer(packet.segment.getFlowID());
				/** ===== Statistical Counters ===== **/
				if (isMonitored) {
					networkLinks.get(getNextSwitchID(packet.segment.getFlowID())).updateUtilizationCounters(
							net.getCurrentTime(), packet.segment.getFlowID(), linkUtilizationTime);
				}
				/** ================================ **/
			}
		}
		/* ================================================ */
		/* ========== Next node is a Controller =========== */
		/* ================================================ */
		else {
			nextTime = net.getCurrentTime() + controlLink.getTotalDelay(packet.segment.getSize());
			net.eventList.addArrivalToController(nextTime, this.ID, packet);
			controlLink.buffer.deQueue();
		}
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */
	/* ########## Protected ############################## */
	protected void addFlowTableEntry(int flowID, int neighborID) {
		flowTable.put(flowID, neighborID);
	}

	protected boolean hasFlowEntry(int flowID) {
		if (flowTable.containsKey(flowID)) {
			return true;
		} else {
			return false;
		}
	}

	protected void broadcastToHosts(Network net, Segment segment) {
		float nextTime = 0;
		float bufferTime = 0;
		for (int hostID : accessLinks.keySet()) {
			bufferTime = accessLinks.get(hostID).buffer.getBufferTime(net.getCurrentTime(),
					accessLinks.get(hostID).getTransmissionDelay(segment.getSize()));
			nextTime = net.getCurrentTime() + bufferTime;
			if (nextTime > 0) {
				segment.setDstHostID(hostID);
				net.eventList.addDepartureFromSwitch(nextTime, this.ID, hostID, new Packet(segment, null));
			} else {
				// Packet Drop happens here
				Main.error("SDNSwitch", "broadcastToHosts", "Packe Drop happened at switch_" + ID);
			}
		}
	}

	protected void forwardToHost(Network net, int hostID, Segment segment) {
		float bufferTime = accessLinks.get(hostID).buffer.getBufferTime(net.getCurrentTime(),
				accessLinks.get(hostID).getTransmissionDelay(segment.getSize()));
		float nextTime = net.getCurrentTime() + bufferTime;
		if (nextTime > 0) {
			net.eventList.addDepartureFromSwitch(nextTime, this.ID, hostID, new Packet(segment, null));
		} else {
			// Packet Drop happens here
			Main.error("SDNSwitch", "forwardToHost", "Packe Drop happened at switch_" + ID);
		}

		/** ===== Statistical Counters ===== **/
		if (isMonitored)
			net.hosts.get(segment.getSrcHostID()).transportAgent.flow.totalBufferTime += bufferTime;
		/** ================================ **/
	}

	protected void forwardToSwitch(Network net, int switchID, Segment segment) {
		float bufferTime = networkLinks.get(switchID).buffer.getBufferTime(net.getCurrentTime(),
				networkLinks.get(switchID).getTransmissionDelay(segment.getSize()));
		float nextTime = net.getCurrentTime() + bufferTime;
		if (nextTime > 0) {
			net.eventList.addDepartureFromSwitch(nextTime, this.ID, switchID, new Packet(segment, null));
		} else {
			// Packet Drop happens here
			Main.error("SDNSwitch", "forwardToSwitch", "Packe Drop happened at switch_" + ID);
		}
		/** ===== Statistical Counters ===== **/
		if (isMonitored) {
			networkLinks.get(switchID).updateSegementArrivalToLinkCounters(segment.getFlowID(), net.getCurrentTime());
		}
		/** ================================ **/
	}

	protected void forwardToController(Network net, Segment segment) {
		float bufferTime = controlLink.buffer.getBufferTime(net.getCurrentTime(),
				controlLink.getTransmissionDelay(segment.getSize()));
		float nextTime = net.getCurrentTime() + bufferTime;
		net.eventList.addDepartureFromSwitch(nextTime, this.ID, controllerID, new Packet(segment, null));

	}

	protected int getNextSwitchID(int flowID) {
		return flowTable.get(flowID);
	}

	protected float getAccessLinkTotalDelay(int hostID, int segmentSize) {
		return accessLinks.get(hostID).getTotalDelay(segmentSize);
	}

	protected float getNetworkLinkTotalDelay(int flowID, int segmentSize) {
		return networkLinks.get(getNextSwitchID(flowID)).getTotalDelay(segmentSize);
	}

	protected boolean isConnectedToHost(int hostID) {
		if (accessLinks.get(hostID) != null) {
			return true;
		} else {
			return false;
		}
	}

	protected void deQueueFromAccessLinkBuffer(int dstHostID) {
		accessLinks.get(dstHostID).buffer.deQueue();
	}

	protected void deQueueFromNetworkLinkBuffer(int flowID) {
		networkLinks.get(getNextSwitchID(flowID)).buffer.deQueue();
	}

	/* ########## Public ################################# */
	public boolean isAccessSwitch() {
		if (accessLinks.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	/* ########## Private ################################# */

}
