package entities;

import java.util.ArrayList;

import events.ArrivalToSwitch;
import system.*;

public abstract class Agent {

	protected int srcHostID;
	protected int dstHostID;

	public Flow flow;

	/* Constructor */
	public Agent(Flow flow) {
		this.flow = flow;
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Abstract methods ---------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public abstract Network start(Network net);

	public abstract Network recvSegment(Network net, Segment segment);

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	protected Network sendMultipleSegments(Network net, ArrayList<Segment> segments) {
		double interSegmentDelay = net.hosts.get(srcHostID).accessLink
				.getTransmissionDelay((segments.get(0).getSize()));
		for (int i = 0; i < segments.size(); i++) {
			net = sendSegment(net, segments.get(i), i * interSegmentDelay);
		}
		return net;
	}

	protected Network sendSegment(Network net, Segment segment, double sendTimeOffset) {
		double nextTime = sendTimeOffset + net.getCurrentTime()
				+ net.hosts.get(srcHostID).accessLink.getTotalDelay(segment.getSize());
		net.eventList.addEvent(new ArrivalToSwitch(nextTime, net.hosts.get(srcHostID).accessSwitchID, segment, null));

		/** ===== Statistical Counters ===== **/
		this.flow.totalSentSegments++;
		this.flow.dataSeqNumSendingTimes.put(segment.getSeqNum(), sendTimeOffset + net.getCurrentTime());
		/** ================================ **/
		return net;
	}

}
