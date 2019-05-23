package system;

import entities.*;

public abstract class Event {

	protected double currentTime;
	protected Segment currentSegment;
	protected int currentNodeID;

	public Event(double startTime, int nodeID, Segment segment) {
		this.currentTime = startTime;
		this.currentSegment = segment;
		this.currentNodeID = nodeID;
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

	public abstract Network execute(Network net);
}
