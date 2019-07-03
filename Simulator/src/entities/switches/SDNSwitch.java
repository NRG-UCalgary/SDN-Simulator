package entities.switches;

import java.util.HashMap;

import entities.*;
import system.*;
import system.events.*;
import system.utility.*;

public abstract class SDNSwitch extends Node {

	public Link controlLink;
	public int controllerID = 0; // Assuming there is only 1 controller

	public HashMap<Integer, Link> accessLinks; // <HostID, Link>
	public HashMap<Integer, Link> networkLinks; // <SwitchID, Link>
	public HashMap<Integer, Integer> flowTable; // <FlowID, SwitchID(neighbors)>
	public boolean isAccessSwitch;

	public SDNSwitch(int ID, Link controlLink) {
		super(ID, Keywords.Switch);
		this.controlLink = controlLink;
		accessLinks = new HashMap<Integer, Link>();
		networkLinks = new HashMap<Integer, Link>();
		flowTable = new HashMap<Integer, Integer>();
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */
	public abstract Network recvCtrlMessage(Network net, CtrlMessage message);

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Node) ---------- */
	/* --------------------------------------------------- */
	public Network recvPacket(Network net, Packet packet) {
		Segment segment = packet.segment;
		if (packet.type == Keywords.SDNControl) {
			return recvCtrlMessage(net, packet.controlMessage);
		} else if (segment.getDstHostID() == Keywords.BroadcastDestination) {
			return broadcastToHosts(net, segment);
		} else if (this.isConnectedToHost(segment.getDstHostID())) {
			return forwardToHost(net, segment.getDstHostID(), segment);
		} else if (this.hasFlowEntry(segment.getFlowID())) {
			return forwardToSwitch(net, getNextSwitchID(segment.getFlowID()), segment);
		} else {
			return forwardToController(net, segment);
		}
	}

	public Network releasePacket(Network net, int dstNodeID, Packet packet) {
		Segment segment = packet.segment;
		double nextTime;
		Event nextEvent;
		double linkUtilizationTime;
		if (isConnectedToHost(segment.getDstHostID())) { // The next node is a Host
			if (segment.getType() != Keywords.ACK) { // TODO this should change later
				deQueueFromAccessLinkBuffer(dstNodeID);
			}
			linkUtilizationTime = accessLinks.get(dstNodeID).getTransmissionDelay(segment.getSize());
			nextTime = net.getCurrentTime() + getAccessLinkTotalDelay(dstNodeID, segment.getSize());
			nextEvent = new ArrivalToHost(nextTime, dstNodeID, packet);
			/** ===== Statistical Counters ===== **/
			this.accessLinks.get(segment.getDstHostID()).utilizationTimePerFlow.put(segment.getFlowID(),
					linkUtilizationTime);
			/** ================================ **/
		} else if (this.hasFlowEntry(segment.getFlowID())) { // The next node is a Switch
			if (segment.getType() != Keywords.ACK) {// TODO this should change later
				deQueueFromNetworkLinkBuffer(segment.getFlowID());
			}
			linkUtilizationTime = networkLinks.get(dstNodeID).getTransmissionDelay(segment.getSize());
			nextTime = net.getCurrentTime() + getNetworkLinkTotalDelay(segment.getFlowID(), segment.getSize());
			nextEvent = new ArrivalToSwitch(nextTime, dstNodeID, packet);
			/** ===== Statistical Counters ===== **/
			this.networkLinks.get(getNextSwitchID(segment.getFlowID())).utilizationTimePerFlow.put(segment.getFlowID(),
					linkUtilizationTime);
			/** ================================ **/
		} else { // The next node is the controller
			nextTime = net.getCurrentTime() + controlLink.getTotalDelay(segment.getSize());
			nextEvent = new ArrivalToController(nextTime, this.getID(), packet);
		}
		net.eventList.addEvent(nextEvent);
		return net;
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

	/* ########## Protected ############################## */
	protected boolean hasFlowEntry(int flowID) {
		if (flowTable.containsKey(flowID)) {
			return true;
		} else {
			return false;
		}
	}

	protected Network broadcastToHosts(Network net, Segment segment) {
		double nextTime = 0;
		double bufferTime = 0;
		for (int hostID : accessLinks.keySet()) {
			bufferTime = accessLinks.get(hostID).buffer.getBufferTime(net.getCurrentTime(),
					accessLinks.get(hostID).getTransmissionDelay(segment.getSize()));
			nextTime = net.getCurrentTime() + bufferTime;
			if (nextTime > 0) {
				segment.setDstHostID(hostID);
				Event nextEvent = new DepartureFromSwitch(nextTime, this.ID, hostID, new Packet(segment, null));
				net.eventList.addEvent(nextEvent);
			} else {
				// TODO segment drop happens here
			}
		}
		return net;
	}

	protected Network forwardToHost(Network net, int hostID, Segment segment) {
		double bufferTime = this.accessLinks.get(hostID).buffer.getBufferTime(net.getCurrentTime(),
				accessLinks.get(hostID).getTransmissionDelay(segment.getSize()));
		double nextTime = net.getCurrentTime() + bufferTime;
		if (nextTime > 0) {
			Event nextEvent = new DepartureFromSwitch(nextTime, this.ID, hostID, new Packet(segment, null));
			net.eventList.addEvent(nextEvent);
		} else {
			// TODO segment drop happens here
		}

		/** ===== Statistical Counters ===== **/
		net.hosts.get(segment.getSrcHostID()).transportAgent.flow.totalBufferTime += bufferTime;
		/** ================================ **/
		return net;
	}

	protected Network forwardToSwitch(Network net, int switchID, Segment segment) {
		double bufferTime = this.networkLinks.get(switchID).buffer.getBufferTime(net.getCurrentTime(),
				networkLinks.get(switchID).getTransmissionDelay(segment.getSize()));
		double nextTime = net.getCurrentTime() + bufferTime;
		if (nextTime > 0) {
			Event nextEvent = new DepartureFromSwitch(nextTime, this.getID(), switchID, new Packet(segment, null));
			net.eventList.addEvent(nextEvent);
		} else {
			// TODO segment drop happens here
		}

		/** ===== Statistical Counters ===== **/
		net.hosts.get(segment.getSrcHostID()).transportAgent.flow.totalBufferTime += bufferTime;
		/** ================================ **/
		return net;
	}

	protected Network forwardToController(Network net, Segment segment) {
		double nextTime = net.getCurrentTime() + controlLink.buffer.getBufferTime(net.getCurrentTime(),
				controlLink.getTransmissionDelay(segment.getSize()));
		Event nextEvent = new DepartureFromSwitch(nextTime, this.getID(), controllerID, new Packet(segment, null));
		net.eventList.addEvent(nextEvent);
		return net;
	}

	protected int getNextSwitchID(int flowID) {
		return this.flowTable.get(flowID);
	}

	protected double getAccessLinkTotalDelay(int hostID, int segmentSize) {
		return accessLinks.get(hostID).getTotalDelay(segmentSize);
	}

	protected double getNetworkLinkTotalDelay(int flowID, int segmentSize) {
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

	public void addFlowTableEntry(int flowID, int neighborID) {
		this.flowTable.put(flowID, neighborID);
	}

	public boolean isAccessSwitch() {
		if (this.accessLinks.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/* ########## Private ################################# */

}
