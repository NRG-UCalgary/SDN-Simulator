package com.nrg.sdnsimulator.core.entity.network;

import java.util.HashMap;

import com.nrg.sdnsimulator.core.Network;
import com.nrg.sdnsimulator.core.entity.traffic.Packet;
import com.nrg.sdnsimulator.core.entity.traffic.Segment;
import com.nrg.sdnsimulator.core.utility.Debugger;
import com.nrg.sdnsimulator.core.utility.Mathematics;

public abstract class Host extends Node {

	// New design
	public int accessLinkID;

	/* Each host should be connected to an access SDNSwitch */
	public int accessSwitchID;
	protected float mostRecentPacketDepartureTime;

	public Agent transportAgent;

	// For future extension
	public HashMap<Integer, Agent> transportAgents;

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

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */
	public abstract void initialize(Network net);

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */
	protected void sendSegments(Network net) {
		for (Segment segment : transportAgent.segmentsToSend) {
			sendSegment(net, segment);
		}
		transportAgent.segmentsToSend.clear();
		if (transportAgent.segmentsToSend.size() > 0) {
			Debugger.error("Host", "sendSegments", "The sendingBuffer of host is not empty after sending.");
		}
	}

	private void sendSegment(Network net, Segment segment) {
		/** ===== Statistical Counters ===== **/
		transportAgent.flow.totalTransmissionTime = Mathematics.addFloat(transportAgent.flow.totalTransmissionTime,
				net.links.get(accessLinkID).getTransmissionDelay(segment.getSize()));
		/** ================================ **/
		net.links.get(accessLinkID).bufferPacket(net, new Packet(segment, null));
	}

	/*---------- Statistical counter methods ---------- */
	public void updateFlowTotalBufferTime(float bufferTime) {
		Mathematics.addFloat(transportAgent.flow.totalBufferTime, bufferTime);
	}

	public void updateDataSegmentsDepartures(int seqNum, float departureTime) {
		transportAgent.flow.dataSeqNumSendingTimes.put((float) seqNum, departureTime);
	}

}
