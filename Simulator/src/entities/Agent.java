package entities;

import java.util.ArrayList;

import events.ArrivalToSwitch;
import system.*;

public abstract class Agent {

	protected final double ACKGenerationTime = 0.5; // usually is less than 500MS

	protected int srcHostID;
	protected int dstHostID;
	protected int size;

	ArrayList<Segment> sendingBuffer;

	protected Flow flow;

	/* Constructor */
	public Agent(Flow flow) {
		this.flow = flow;
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Abstract methods ---------------------------------------------- */
	/* -------------------------------------------------------------------------- */
	public Network start(Network net) {
		return net;
	}

	public abstract Network recvSegment(Network net, Segment segment);

	/* -------------------------------------------------------------------------- */
	/* ---------- Implemented methods ------------------------------------------- */
	/* -------------------------------------------------------------------------- */

	protected Network sendSegment(Network net, Segment segment) {
		double nextTime = net.getCurrentTime() + net.hosts.get(srcHostID).accessLink.getTotalDelay(segment.getSize());
		net.eventList.addEvent(new ArrivalToSwitch(nextTime, net.hosts.get(srcHostID).accessSwitchID, segment, null));
		return net;
	}

}
