package simulator.entities.network;

import java.util.ArrayList;

import simulator.Network;
import simulator.entities.Entity;
import simulator.entities.traffic.Flow;
import simulator.entities.traffic.Segment;

public abstract class Agent extends Entity {

	protected int srcHostID;
	protected int dstHostID;
	public Flow flow;

	public float interSegmentDelay_;
	protected float mostRecentSegmentDepartureTime;

	public ArrayList<Segment> segmentsToSend;

	/* Constructor */
	public Agent(Flow flow) {
		super(-1);
		this.flow = flow;
		srcHostID = flow.getSrcHostID();
		dstHostID = flow.getDstHostID();
		segmentsToSend = new ArrayList<Segment>();
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Abstract methods ---------------------------------------------- */
	/* -------------------------------------------------------------------------- */

	public abstract void recvSegment(Network net, Segment segment);

	public abstract void sendFirst(Network net);

	public abstract void timeout(Network net, int timerID);

}
