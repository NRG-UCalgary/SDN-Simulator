package entities;

import entities.Agents.*;
import system.*;
import system.utility.*;

public class Host extends Node {
	/* Each host should be connected to an access SDNSwitch */
	public Link accessLink;
	public int accessSwitchID;
	public Agent transportAgent;

	/* Each host has string label */
	public String label;

	public Host(int ID) {
		super(ID, Keywords.Nodes.Names.Host);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Node) ---------- */
	/* --------------------------------------------------- */
	public void recvPacket(Network net, Packet packet) {
		transportAgent.recvSegment(net, packet.segment);
	}

	public void releasePacket(Network net, int dstSwitchID, Packet packet) {
		accessLink.buffer.deQueue();
		float nextTime = net.getCurrentTime() + getAccessLinkTotalDelay(packet.getSize());
		net.eventList.addArrivalToSwitch(nextTime, dstSwitchID, packet);
		// net.eventList.addEvent(new ArrivalToSwitch(nextTime, dstSwitchID, packet));
		if (packet.segment.getType() == Keywords.Segments.Types.DATA
				|| packet.segment.getType() == Keywords.Segments.Types.UncontrolledFIN) {
			/** ===== Statistical Counters ===== **/
			transportAgent.flow.totalSentSegments++;
			//transportAgent.flow.dataSeqNumSendingTimes.put((float) packet.segment.getSeqNum(), net.getCurrentTime());
			/** ================================ **/
		}
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */

	/* ########## Public ################################# */
	public void initialize(Network net) {
		transportAgent.sendSYN(net);
	}

	public float getAccessLinkTotalDelay(int segmentSize) {
		return accessLink.getTotalDelay(segmentSize);
	}

	public float getAccessLinkRtt() {
		return getAccessLinkTotalDelay(Keywords.Segments.Sizes.ACKSegSize)
				+ getAccessLinkTotalDelay(Keywords.Segments.Sizes.DataSegSize);
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
