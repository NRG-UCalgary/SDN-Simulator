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
		super(ID, Keywords.Operations.Nodes.Names.Host);
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
		if (packet.segment.getType() == Keywords.Operations.Segments.Types.DATA
				|| packet.segment.getType() == Keywords.Operations.Segments.Types.UncontrolledFIN) {
			/** ===== Statistical Counters ===== **/
			transportAgent.flow.totalSentSegments++;
			transportAgent.flow.dataSeqNumSendingTimes.put((float) packet.segment.getSeqNum(), net.getCurrentTime());
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
		return getAccessLinkTotalDelay(Keywords.Operations.Segments.Sizes.ACKSegSize)
				+ getAccessLinkTotalDelay(Keywords.Operations.Segments.Sizes.DataSegSize);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((accessLink == null) ? 0 : accessLink.hashCode());
		result = prime * result + accessSwitchID;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((transportAgent == null) ? 0 : transportAgent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Host other = (Host) obj;
		if (accessLink == null) {
			if (other.accessLink != null)
				return false;
		} else if (!accessLink.equals(other.accessLink))
			return false;
		if (accessSwitchID != other.accessSwitchID)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (transportAgent == null) {
			if (other.transportAgent != null)
				return false;
		} else if (!transportAgent.equals(other.transportAgent))
			return false;
		return true;
	}

}
