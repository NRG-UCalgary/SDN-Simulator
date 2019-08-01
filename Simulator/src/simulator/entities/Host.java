package simulator.entities;

import java.util.ArrayList;

import simulator.Network;
import utility.Keywords;
import utility.Mathematics;

public abstract class Host extends Node {
	/* Each host should be connected to an access SDNSwitch */
	public Link accessLink;
	public int accessSwitchID;
	protected float mostRecentPacketDepartureTime;

	public Agent transportAgent;

	public Host(int ID) {
		super(ID, Keywords.Nodes.Names.Host);
	}

	public void connectToNetwork(int accessSwitchID, Link accessLink) {
		this.accessLink = accessLink;
		this.accessSwitchID = accessSwitchID;
	}

	public float getAccessLinkRtt() {
		return Mathematics.addFloat(getAccessLinkTotalDelay(Keywords.Segments.Sizes.ACKSegSize),
				getAccessLinkTotalDelay(Keywords.Segments.Sizes.DataSegSize));
	}

	public float getAccessLinkTotalDelay(int segmentSize) {
		return accessLink.getTotalDelay(segmentSize);
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */
	public abstract void initialize(Network net);

	private void sendSegment(Network net, Segment segment) {
		float bufferTime = accessLink.bufferPacket(net.getCurrentTime(), new Packet(segment, null));
		float nextTime = Mathematics.addFloat(net.getCurrentTime(), bufferTime);
		if (nextTime < Mathematics.addFloat(transportAgent.interSegmentDelay_, mostRecentPacketDepartureTime)) {
			nextTime = Mathematics.addFloat(transportAgent.interSegmentDelay_, mostRecentPacketDepartureTime);
		}
		net.eventList.addDepartureFromHost(nextTime, ID, accessSwitchID, new Packet(segment, null));
	}

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */
	protected void sendSegments(Network net, ArrayList<Segment> segmentsToSend) {
		for (int i = 0; i < segmentsToSend.size(); i++) {
			sendSegment(net, segmentsToSend.get(i));
		}
	}

	public void setAgent(Agent transportAgent) {
		this.transportAgent = transportAgent;
	}

	/*---------- Statistical counter methods ---------- */
	public void updateFlowTotalBufferTime(float bufferTime) {
		Mathematics.addFloat(transportAgent.flow.totalBufferTime, bufferTime);
	}
}
