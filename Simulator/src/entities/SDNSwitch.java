package entities;

import java.util.HashMap;

import events.ArrivalToController;
import events.Departure;
import system.Event;
import system.Network;

public abstract class SDNSwitch extends Node {

	public Link controlLink;

	// Only for DIjkstra (temporary) // TODO Must be filled by simulator
	public HashMap<SDNSwitch, Link> neighbors;
	public HashMap<Integer, Link> accessLinks; // <HostID, Link>
	public HashMap<Integer, Link> networkLinks; // <SwitchID, Link>
	public HashMap<Integer, Integer> flowTable; // <FlowID, SwitchID(neighbors)>

	public SDNSwitch(int ID, Link controlLink) {
		super(ID);
		this.controlLink = controlLink;
		neighbors = new HashMap<SDNSwitch, Link>();
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
		if (this.isConnectedToHost(segment.getDstHostID())) {
			return forwardToHost(net, segment);
		} else if (this.hasFlowEntry(segment.getDstHostID())) {
			return forwardToSwitch(net, segment);
		} else {
			return forwardToController(net, segment);
		}
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

	/* ########## Protected ############################## */
	/* Objective::Checking if the flow table has the flowID entry */
	protected boolean hasFlowEntry(int flowID) {
		if (flowTable.containsKey(flowID)) {
			return true;
		} else {
			return false;
		}
	}

	/* Objective::Forwarding the segment to the Destination Host */
	protected Network forwardToHost(Network net, Segment segment) {
		double nextTime = net.getCurrentTime()
				+ this.accessLinks.get(segment.getDstHostID()).getBufferTime(net.getCurrentTime(), segment);
		if (nextTime > 0) {
			Event nextEvent = new Departure(nextTime, this.ID, segment);
			net.eventList.addEvent(nextEvent);
		} else {
			// TODO segment drop happens here
		}
		return net;
	}

	/* Objective::Forwarding the segment to the next switch in the path */
	protected Network forwardToSwitch(Network net, Segment segment) {
		double nextTime = net.getCurrentTime() + this.networkLinks.get(getNextSwitchID(segment.getFlowID()))
				.getBufferTime(net.getCurrentTime(), segment);
		if (nextTime > 0) {
			Event nextEvent = new Departure(nextTime, this.ID, segment);
			net.eventList.addEvent(nextEvent);
		} else {
			// TODO segment drop happens here
		}
		return net;
	}

	/* Objective::Forwarding the segment to the controller */
	protected Network forwardToController(Network net, Segment segment) {
		double nextTime = net.getCurrentTime() + net.controller.getControlLinkDelay(this.getID(), segment.getSize());
		Event nextEvent = new ArrivalToController(nextTime, this.getID(), segment);
		net.eventList.addEvent(nextEvent);
		return net;
	}

	/* Objective::Returning the next switchID in the path */
	protected int getNextSwitchID(int flowID) {
		return this.flowTable.get(flowID);
	}

	/* ########## Public ################################# */
	public double getAccessLinkDelay(int hostID, int segmentSize) {
		return accessLinks.get(hostID).getTransmissionDelay(segmentSize)
				+ accessLinks.get(hostID).getPropagationDelay();
	}

	// TODO We may not need this method
	public void addFlowTableEntry(int flowID, int neighborID) {
		this.flowTable.put(flowID, neighborID);
	}

	public boolean isConnectedToHost(int hostID) {
		if (accessLinks.get(hostID) != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isAccessSwitch() {
		if (this.accessLinks.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

}
