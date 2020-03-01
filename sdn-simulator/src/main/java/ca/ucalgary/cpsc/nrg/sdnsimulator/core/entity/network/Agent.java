package ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.network;

import java.util.ArrayList;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Network;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.Entity;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Flow;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Segment;

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
