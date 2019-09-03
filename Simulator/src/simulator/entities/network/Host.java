package simulator.entities.network;

import java.util.ArrayList;
import java.util.HashMap;

import simulator.Network;
import simulator.entities.traffic.*;
import utility.*;

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
	protected void sendSegments(Network net, ArrayList<Segment> segmentsToSend) {
		for (int i = 0; i < segmentsToSend.size(); i++) {
			sendSegment(net, segmentsToSend.get(i));
		}
	}

	private void sendSegment(Network net, Segment segment) {
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
