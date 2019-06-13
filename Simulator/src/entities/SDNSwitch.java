package entities;

import java.util.HashMap;

import events.*;
import system.*;

public abstract class SDNSwitch extends Node {

	public Link controlLink;

	public HashMap<Integer, Link> accessLinks; // <HostID, Link>
	public HashMap<Integer, Link> networkLinks; // <SwitchID, Link>
	public HashMap<Integer, Integer> flowTable; // <FlowID, SwitchID(neighbors)>
	public boolean isAccessSwitch;

	public SDNSwitch(int ID, Link controlLink) {
		super(ID);
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
	public Network recvSegment(Network net, Segment segment) {
		/* # The switch decisions: # */
		/* ## Forward the segment to the Host (if connected to it) ## */
		/* ## Forward the segment to the next switch (if has the Flow entry) ## */
		/* ## Forward the segment to the controller ## */
		if (segment.getDstHostID() == Keywords.BroadcastDestination) {
			return broadcastToHosts(net, segment);
		} else if (this.isConnectedToHost(segment.getDstHostID())) {
			return forwardToHost(net, segment);
		} else if (this.hasFlowEntry(segment.getDstHostID())) {
			return forwardToSwitch(net, segment);
		} else {
			return forwardToController(net, segment);
		}
	}

	public Network releaseSegment(Network net, Segment segment) {
		int nextNodeID;
		double nextTime;
		Event nextEvent;
		double linkUtilizationTime;

		if (isConnectedToHost(segment.getDstHostID())) { // The next node is a Host
			if (segment.getType() != Keywords.ACK) { // TODO this should change later
				deQueueFromAccessLinkBuffer(segment.getDstHostID());
			}
			nextNodeID = segment.getDstHostID();
			linkUtilizationTime = getAccessLinkTotalDelay(segment.getDstHostID(), segment.getSize());
			nextTime = net.getCurrentTime() + linkUtilizationTime;
			nextEvent = new ArrivalToHost(nextTime, nextNodeID, segment);
			/** ===== Statistical Counters ===== **/
			this.accessLinks.get(segment.getDstHostID()).utilizationTimePerFlow.put(segment.getFlowID(),
					linkUtilizationTime);
			/** ================================ **/
		} else { // The next node is a Switch
			if (segment.getType() != Keywords.ACK) {// TODO this should change later
				deQueueFromNetworkLinkBuffer(segment.getFlowID());
			}
			nextNodeID = segment.getDstHostID(); // FIXME incorrect
			linkUtilizationTime = getNetworkLinkTotalDelay(segment.getFlowID(), segment.getSize());
			nextTime = net.getCurrentTime() + linkUtilizationTime;
			nextEvent = new ArrivalToSwitch(nextTime, nextNodeID, segment, null);
			/** ===== Statistical Counters ===== **/
			this.networkLinks.get(getNextSwitchID(segment.getFlowID())).utilizationTimePerFlow.put(segment.getFlowID(),
					linkUtilizationTime);
			/** ================================ **/
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
			bufferTime = accessLinks.get(hostID).buffer.getBufferTime(net.getCurrentTime(), segment.getType(),
					accessLinks.get(hostID).getTransmissionDelay(segment.getSize()));
			nextTime = net.getCurrentTime() + bufferTime;
			if (nextTime > 0) {
				Event nextEvent = new Departure(nextTime, this.ID, segment);
				net.eventList.addEvent(nextEvent);
			} else {
				// TODO segment drop happens here
			}
		}
		return net;
	}

	protected Network forwardToHost(Network net, Segment segment) {
		double bufferTime = this.accessLinks.get(segment.getDstHostID()).buffer.getBufferTime(net.getCurrentTime(),
				segment.getType(), accessLinks.get(segment.getDstHostID()).getTransmissionDelay(segment.getSize()));
		double nextTime = net.getCurrentTime() + bufferTime;
		if (nextTime > 0) {
			Event nextEvent = new Departure(nextTime, this.ID, segment);
			net.eventList.addEvent(nextEvent);
		} else {
			// TODO segment drop happens here
		}

		/** ===== Statistical Counters ===== **/
		net.hosts.get(segment.getSrcHostID()).transportAgent.flow.totalBufferTime += bufferTime;
		/** ================================ **/
		return net;
	}

	protected Network forwardToSwitch(Network net, Segment segment) {
		double bufferTime = this.networkLinks.get(getNextSwitchID(segment.getFlowID())).buffer.getBufferTime(
				net.getCurrentTime(), segment.getType(),
				accessLinks.get(segment.getDstHostID()).getTransmissionDelay(segment.getSize()));
		double nextTime = net.getCurrentTime() + bufferTime;
		if (nextTime > 0) {
			Event nextEvent = new Departure(nextTime, this.ID, segment);
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
		double nextTime = net.getCurrentTime() + controlLink.getTotalDelay(segment.getSize());
		Event nextEvent = new ArrivalToController(nextTime, this.getID(), segment);
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
