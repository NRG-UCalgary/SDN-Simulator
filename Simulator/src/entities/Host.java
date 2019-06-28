package entities;

import entities.Agents.*;
import system.*;
import system.events.ArrivalToSwitch;
import system.utility.*;

public class Host extends Node {
	/* Each host should be connected to an access SDNSwitch */
	public Link accessLink;
	public int accessSwitchID;
	public Agent transportAgent;

	/* Each host has string label */
	public String label;

	public Host(int ID) {
		super(ID, Keywords.Host);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Node) ---------- */
	/* --------------------------------------------------- */
	public Network recvSegment(Network net, Segment segment) {
		net = transportAgent.recvSegment(net, segment);
		return net;
	}

	public Network releaseSegment(Network net, Segment segment) {

		this.accessLink.buffer.deQueue();
		int nextNodeID = this.accessSwitchID;
		double nextTime = net.getCurrentTime() + this.getAccessLinkDelay(segment.getSize());
		net.eventList.addEvent(new ArrivalToSwitch(nextTime, nextNodeID, segment, null));
		if (segment.getType() == Keywords.DATA) {
			/** ===== Statistical Counters ===== **/
			this.transportAgent.flow.totalSentSegments++;
			this.transportAgent.flow.dataSeqNumSendingTimes.put(segment.getSeqNum(),
					Keywords.HostProcessDelay + net.getCurrentTime());
			/** ================================ **/
		}

		return net;
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

	/* ########## Public ################################# */
	// TODO this method must be called for initialization
	public Network initialize(Network net) {
		return transportAgent.sendSYN(net);
	}

	public double getAccessLinkDelay(int segmentSize) {
		return accessLink.getTransmissionDelay(segmentSize) + accessLink.getPropagationDelay();
	}

	public double getAccessLinkRTT() {
		return getAccessLinkDelay(Keywords.ACKSegSize) + getAccessLinkDelay(Keywords.DataSegSize);
	}

	public void connectToNetwork(int accessSwitchID, Link accessLink) {
		this.accessLink = accessLink;
		this.accessSwitchID = accessSwitchID;
	}

	public void setAgent(Agent agent) {
		this.transportAgent = agent;
	}

	/** Called in Class::Event.run() **/
	/* Objective::Showing the egress-link for the desired destination Node */
	public Link getEgressLink() {
		return this.accessLink;
	}

}
