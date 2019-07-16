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
		accessLink.buffer.deQueue();
		float nextTime = net.getCurrentTime() + getAccessLinkTotalDelay(packet.getSize());
		net.eventList.addEvent(new ArrivalToSwitch(nextTime, dstSwitchID, packet));
		if (packet.segment.getType() == Keywords.DATA || packet.segment.getType() == Keywords.UncontrolledFIN) {
			/** ===== Statistical Counters ===== **/
			transportAgent.flow.totalSentSegments++;
			transportAgent.flow.dataSeqNumSendingTimes.put((float) packet.segment.getSeqNum(),
					Keywords.HostProcessDelay + net.getCurrentTime());
			/** ================================ **/
		}
		return net;
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

	/* ########## Public ################################# */
	public Network initialize(Network net) {
		return transportAgent.sendSYN(net);
	}

	public float getAccessLinkTotalDelay(int segmentSize) {
		return accessLink.getTotalDelay(segmentSize);
	}

	public float getAccessLinkRtt() {
		return getAccessLinkTotalDelay(Keywords.ACKSegSize) + getAccessLinkTotalDelay(Keywords.DataSegSize);
	}

	public void connectToNetwork(int accessSwitchID, Link accessLink) {
		this.accessLink = accessLink;
		this.accessSwitchID = accessSwitchID;
	}

	public void setAgent(Agent transportAgent) {
		this.transportAgent = transportAgent;
	}

	public Link getEgressLink() {
		return accessLink;
	}

}
