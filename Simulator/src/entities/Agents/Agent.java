package entities.Agents;

import java.util.ArrayList;

import entities.*;
import system.*;
import system.events.*;
import system.utility.*;

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

	public abstract Network recvSegment(Network net, Segment segment);

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public Network sendSYN(Network net) {
		Main.error("Agent", "sendSYN", "incorrect method call.");
		return null;
	}

	protected Network sendMultipleSegments(Network net, ArrayList<Segment> segmentsToSend) {
		for (int i = 0; i < segmentsToSend.size(); i++) {
			net = sendSegment(net, segmentsToSend.get(i));
		}
		return net;
	}

	protected Network sendSegment(Network net, Segment segment) {
		float bufferTime = net.hosts.get(srcHostID).accessLink.buffer.getBufferTime(net.getCurrentTime(),
				net.hosts.get(srcHostID).accessLink.getTransmissionDelay(segment.getSize()));

		float nextTime = net.getCurrentTime() + bufferTime + Keywords.HostProcessDelay;
		if (nextTime < interSegmentDelay_ + mostRecentSegmentDepartureTime) {
			nextTime = interSegmentDelay_ + mostRecentSegmentDepartureTime;
		} else {

		}
		int nextNodeID = net.hosts.get(srcHostID).accessSwitchID;
		net.eventList.addEvent(new DepartureFromHost(nextTime, srcHostID, nextNodeID, new Packet(segment, null)));
		mostRecentSegmentDepartureTime = nextTime;
		return net;
	}

}
