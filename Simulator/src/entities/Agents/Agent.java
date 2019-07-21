package entities.Agents;

import java.util.ArrayList;

import entities.*;
import system.*;
import system.utility.Keywords;

public abstract class Agent {

	protected int srcHostID;
	protected int dstHostID;

	protected float mostRecentSegmentDepartureTime;
	protected float interSegmentDelay_;

	public Flow flow;

	/* Constructor */
	public Agent(Flow flow) {
		this.flow = flow;
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Abstract methods ---------------------------------------------- */
	/* -------------------------------------------------------------------------- */

	public abstract void recvSegment(Network net, Segment segment);

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public void sendSYN(Network net) {
		Main.error("Agent", "sendSYN", "incorrect method call.");
	}

	protected void sendMultipleSegments(Network net, ArrayList<Segment> segmentsToSend) {
		for (int i = 0; i < segmentsToSend.size(); i++) {
			sendSegment(net, segmentsToSend.get(i));
		}
	}

	protected void sendSegment(Network net, Segment segment) {
		/** ===== Statistical Counters ===== **/
		if (segment.getType() != Keywords.Segments.Types.ACK && segment.getType() != Keywords.Segments.Types.SYNACK) {
			flow.totalTransmissionTime += net.hosts.get(srcHostID).accessLink.getTransmissionDelay(segment.getSize());
		}
		/** ================================ **/
		float bufferTime = net.hosts.get(srcHostID).accessLink.buffer.getBufferTime(net.getCurrentTime(),
				net.hosts.get(srcHostID).accessLink.getTransmissionDelay(segment.getSize()));

		float nextTime = net.getCurrentTime() + bufferTime;
		if (nextTime < interSegmentDelay_ + mostRecentSegmentDepartureTime) {
			nextTime = interSegmentDelay_ + mostRecentSegmentDepartureTime;
		} else {

		}
		int nextNodeID = net.hosts.get(srcHostID).accessSwitchID;
		net.eventList.addDepartureFromHost(nextTime, srcHostID, nextNodeID, new Packet(segment, null));
		mostRecentSegmentDepartureTime = nextTime;
	}

}
