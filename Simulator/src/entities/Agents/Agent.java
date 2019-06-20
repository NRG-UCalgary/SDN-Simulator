package entities.Agents;

import java.util.ArrayList;

import entities.*;
import system.*;
import system.events.*;
import system.utility.*;

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

	public abstract Network recvSegment(Network net, Segment segment);

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public Network sendSYN(Network net) {
		Debugger.debug("SDRCPReceiver.start()::We should never get here");
		return null;
	}

	protected Network sendMultipleSegments(Network net, ArrayList<Segment> segmentsToSend) {
		Link accessLink = net.hosts.get(srcHostID).accessLink;
		double interSegmentDelay = 0;
		// TODO all segments arrive to the host buffer at the same time?! or they have
		// transmission Delay (for buffering)
		for (int i = 0; i < segmentsToSend.size(); i++) {
			net = sendSegment(net, segmentsToSend.get(i), i * interSegmentDelay);
			interSegmentDelay = accessLink.getTransmissionDelay((segmentsToSend.get(i).getSize()));
		}
		return net;
	}

	protected Network sendSegment(Network net, Segment segment, double sendTimeOffset) {
		double nextTime;
		 nextTime = sendTimeOffset + net.getCurrentTime()
		 + net.hosts.get(srcHostID).accessLink.getTotalDelay(segment.getSize());
		 net.eventList.addEvent(new ArrivalToSwitch(nextTime,
		 net.hosts.get(srcHostID).accessSwitchID, segment, null));
		
		//double transmissionDelay = net.hosts.get(srcHostID).accessLink.getTransmissionDelay(segment.getSize());
		//double bufferTime = net.hosts.get(srcHostID).accessLink.buffer.getBufferTime(net.getCurrentTime(),
			//	segment.getType(), transmissionDelay, false);
		//nextTime = net.getCurrentTime() + bufferTime;
		//net.eventList.addEvent(new Departure(nextTime, this.srcHostID, segment));
		if (segment.getType() == Keywords.DATA) {
			/** ===== Statistical Counters ===== **/
			this.flow.totalSentSegments++;
			this.flow.dataSeqNumSendingTimes.put(segment.getSeqNum(), sendTimeOffset+net.getCurrentTime());
			/** ================================ **/
		}
		return net;
	}

}
