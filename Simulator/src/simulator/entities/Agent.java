package simulator.entities;

import java.util.ArrayList;

import simulator.Network;
import utility.Keywords;
import utility.Mathematics;

public abstract class Agent {

	protected int dstHostID;
	public Flow flow;

	public float interSegmentDelay_;
	protected float mostRecentSegmentDepartureTime;

	protected ArrayList<Segment> segmentsToSend;

	protected int srcHostID;

	/* Constructor */
	public Agent(Flow flow) {
		this.flow = flow;
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Abstract methods ---------------------------------------------- */
	/* -------------------------------------------------------------------------- */

	public abstract ArrayList<Segment> recvSegment(Network net, Segment segment);

	public abstract ArrayList<Segment> sendFirst(Network net);

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	protected void sendMultipleSegments(Network net, ArrayList<Segment> segmentsToSend) {
		for (int i = 0; i < segmentsToSend.size(); i++) {
			sendSegment(net, segmentsToSend.get(i));
		}
	}

	protected void sendSegment(Network net, Segment segment) {
		/** ===== Statistical Counters ===== **/
		if (segment.getType() != Keywords.Segments.Types.ACK && segment.getType() != Keywords.Segments.Types.SYNACK) {
			flow.totalTransmissionTime = Mathematics.addFloat(flow.totalTransmissionTime,
					net.hosts.get(srcHostID).accessLink.getTransmissionDelay(segment.getSize()));
		}
		/** ================================ **/
		float bufferTime = net.hosts.get(srcHostID).accessLink.buffer.enQueue(net.getCurrentTime(),
				net.hosts.get(srcHostID).accessLink.getTransmissionDelay(segment.getSize()));
		float nextTime = Mathematics.addFloat(net.getCurrentTime(), bufferTime);
		if (nextTime < Mathematics.addFloat(interSegmentDelay_, mostRecentSegmentDepartureTime)) {
			nextTime = Mathematics.addFloat(interSegmentDelay_, mostRecentSegmentDepartureTime);
		}
		int nextNodeID = net.hosts.get(srcHostID).accessSwitchID;
		net.eventList.addDepartureFromHost(nextTime, srcHostID, nextNodeID, new Packet(segment, null));
		mostRecentSegmentDepartureTime = nextTime;
	}

}
