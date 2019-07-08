package entities.Agents;

import java.util.ArrayList;

import entities.*;
import system.*;
import system.events.*;
import system.utility.*;

public abstract class Agent {

	protected int srcHostID;
	protected int dstHostID;

	protected double mostRecentSegmentDepartureTime;
	protected double interSegmentDelay_;

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
		for (int i = 0; i < segmentsToSend.size(); i++) {
			net = sendSegment(net, segmentsToSend.get(i));
		}
		return net;
	}

	protected Network sendSegment(Network net, Segment segment) {
		double bufferTime = net.hosts.get(srcHostID).accessLink.buffer.getBufferTime(net.getCurrentTime(),
				net.hosts.get(srcHostID).accessLink.getTransmissionDelay(segment.getSize()));

		double nextTime = net.getCurrentTime() + bufferTime + Keywords.HostProcessDelay;
		if (nextTime < this.interSegmentDelay_ + mostRecentSegmentDepartureTime) {
			nextTime = this.interSegmentDelay_ + mostRecentSegmentDepartureTime;
		} else {

		}
		int nextNodeID = net.hosts.get(this.srcHostID).accessSwitchID;
		net.eventList.addEvent(new DepartureFromHost(nextTime, this.srcHostID, nextNodeID, new Packet(segment, null)));
		mostRecentSegmentDepartureTime = nextTime;
		return net;
	}

}
