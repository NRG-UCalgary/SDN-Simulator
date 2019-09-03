package simulator.entities.network;

import java.util.ArrayList;

import simulator.Network;
import simulator.entities.traffic.Flow;
import simulator.entities.traffic.Segment;

public abstract class Agent {

	protected int srcHostID;
	protected int dstHostID;
	public Flow flow;

	public float interSegmentDelay_;
	protected float mostRecentSegmentDepartureTime;

	protected ArrayList<Segment> segmentsToSend;

	

	/* Constructor */
	public Agent(Flow flow) {
		this.flow = flow;
		segmentsToSend = new ArrayList<Segment>();
	}

	/* -------------------------------------------------------------------------- */
	/* ---------- Abstract methods ---------------------------------------------- */
	/* -------------------------------------------------------------------------- */

	public abstract ArrayList<Segment> recvSegment(Network net, Segment segment);

	public abstract ArrayList<Segment> sendFirst(Network net);

}
