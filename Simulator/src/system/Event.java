package system;

import entities.*;

public abstract class Event {
	public final String type; // Used for debugging only
	protected double eventTime;
	protected Segment segment;
	protected int nodeID;
	protected CtrlMessage ctrlMessage;

	public Event(String type, double startTime, int nodeID, Segment segment) {
		this.eventTime = startTime;
		this.segment = segment;
		this.nodeID = nodeID;
		this.type = type;
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

	public abstract Network execute(Network net);
}
