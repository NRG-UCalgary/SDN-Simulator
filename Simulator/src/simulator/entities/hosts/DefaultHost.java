package simulator.entities.hosts;

import java.util.ArrayList;

import simulator.Network;
import simulator.entities.Host;
import simulator.entities.Packet;
import simulator.entities.Segment;
import utility.Keywords;
import utility.Mathematics;

public class DefaultHost extends Host {

	public DefaultHost(int ID) {
		super(ID);
	}

	public float getAccessLinkRtt() {
		return Mathematics.addFloat(getAccessLinkTotalDelay(Keywords.Segments.Sizes.ACKSegSize),
				getAccessLinkTotalDelay(Keywords.Segments.Sizes.DataSegSize));
	}

	public float getAccessLinkTotalDelay(int segmentSize) {
		return accessLink.getTotalDelay(segmentSize);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Host) ---------- */
	/* --------------------------------------------------- */
	public void initialize(Network net) {
		sendSegments(net, transportAgent.sendFirst(net));
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Node) ---------- */
	/* --------------------------------------------------- */
	public void recvPacket(Network net, Packet packet) {
		ArrayList<Segment> segmentsToSend = transportAgent.recvSegment(net, packet.segment);
		sendSegments(net, segmentsToSend);
	}

	public void releasePacket(Network net, int dstSwitchID, Packet packet) {
		accessLink.buffer.deQueue();
		float nextTime = Mathematics.addFloat(net.getCurrentTime(), getAccessLinkTotalDelay(packet.getSize()));
		net.eventList.addArrivalToSwitch(nextTime, dstSwitchID, packet);
		if (packet.segment.getType() == Keywords.Segments.Types.DATA
				|| packet.segment.getType() == Keywords.Segments.Types.UncontrolledFIN
				|| packet.segment.getType() == Keywords.Segments.Types.SYN) {
			/** ===== Statistical Counters ===== **/
			transportAgent.flow.totalSentSegments++;
			transportAgent.flow.dataSeqNumSendingTimes.put((float) packet.segment.getSeqNum(), net.getCurrentTime());
			/** ================================ **/
		}
	}

}
