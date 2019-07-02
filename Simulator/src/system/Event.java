package system;

import entities.*;

public abstract class Event {
	public final String type; // Used for debugging only
	protected double eventTime;
	protected Packet packet;
	protected int nodeID;

	public Event(String type, double startTime, int nodeID, Packet packet) {
		this.eventTime = startTime;
		this.nodeID = nodeID;
		this.type = type;
		this.packet = packet;
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

	public abstract Network execute(Network net);
}
