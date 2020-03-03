package nrg.sdnsimulator.core.entity.network;

import java.util.HashMap;

import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.traffic.Packet;
import nrg.sdnsimulator.core.entity.traffic.Segment;
import nrg.sdnsimulator.core.utility.Mathematics;

public abstract class Host extends Node {

	protected int accessLinkID;
	/* Each host should be connected to an access SDNSwitch */
	protected int accessSwitchID;
	protected float mostRecentPacketDepartureTime;
	protected Agent transportAgent;
	// For future extension
	protected HashMap<Integer, Agent> transportAgents;

	public Host(int ID) {
		super(ID);
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Network topology methods -------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public void connectToNode(int linkID, int dstNodeID, short dstNodeType) {
		this.accessLinkID = linkID;
		this.accessSwitchID = dstNodeID;
	}

	public abstract void initialize(Network net);

	protected void sendSegments(Network net) {
		for (Segment segment : transportAgent.segmentsToSend) {
			sendSegment(net, segment);
		}
		transportAgent.segmentsToSend.clear();
		if (transportAgent.segmentsToSend.size() > 0) {
		}
	}

	private void sendSegment(Network net, Segment segment) {
		/** ===== Statistical Counters ===== **/
		transportAgent.flow.setTotalTransmissionTime(Mathematics.addFloat(
				transportAgent.flow.getTotalTransmissionTime(),
				net.getLinks().get(accessLinkID).getTransmissionDelay(segment.getSize())));
		/** ================================ **/
		net.getLinks().get(accessLinkID).bufferPacket(net, new Packet(segment, null));
	}

	public void updateFlowTotalBufferTime(float bufferTime) {
		Mathematics.addFloat(transportAgent.flow.getTotalBufferTime(), bufferTime);
	}

	public void updateDataSegmentsDepartures(int seqNum, float departureTime) {
		transportAgent.flow.getDataSeqNumSendingTimes().put((float) seqNum, departureTime);
	}

	public int getAccessLinkID() {
		return accessLinkID;
	}

	public void setAccessLinkID(int accessLinkID) {
		this.accessLinkID = accessLinkID;
	}

	public int getAccessSwitchID() {
		return accessSwitchID;
	}

	public void setAccessSwitchID(int accessSwitchID) {
		this.accessSwitchID = accessSwitchID;
	}

	public float getMostRecentPacketDepartureTime() {
		return mostRecentPacketDepartureTime;
	}

	public void setMostRecentPacketDepartureTime(float mostRecentPacketDepartureTime) {
		this.mostRecentPacketDepartureTime = mostRecentPacketDepartureTime;
	}

	public Agent getTransportAgent() {
		return transportAgent;
	}

	public void setTransportAgent(Agent transportAgent) {
		this.transportAgent = transportAgent;
	}

	public HashMap<Integer, Agent> getTransportAgents() {
		return transportAgents;
	}

	public void setTransportAgents(HashMap<Integer, Agent> transportAgents) {
		this.transportAgents = transportAgents;
	}

}
