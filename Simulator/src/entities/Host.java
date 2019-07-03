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
	public Network recvPacket(Network net, Packet packet) {
		net = transportAgent.recvSegment(net, packet.segment);
		return net;
	}

	public Network releasePacket(Network net, int dstSwitchID, Packet packet) {
		this.accessLink.buffer.deQueue();
		double nextTime = net.getCurrentTime() + this.getAccessLinkDelay(packet.getSize());
		net.eventList.addEvent(new ArrivalToSwitch(nextTime, dstSwitchID, packet));
		if (packet.segment.getType() == Keywords.DATA) {
			/** ===== Statistical Counters ===== **/
			this.transportAgent.flow.totalSentSegments++;
			this.transportAgent.flow.dataSeqNumSendingTimes.put(packet.segment.getSeqNum(),
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

	public double getAccessLinkRtt() {
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
