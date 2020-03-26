package nrg.sdnsimulator.core.entity.network;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import nrg.sdnsimulator.core.Network;
import nrg.sdnsimulator.core.entity.traffic.Packet;
import nrg.sdnsimulator.core.entity.traffic.Segment;
import nrg.sdnsimulator.core.utility.Mathematics;

@Getter
@Setter
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
		transportAgent.flow
				.setTotalTransmissionTime(Mathematics.addFloat(transportAgent.flow.getTotalTransmissionTime(),
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

}
