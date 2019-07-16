package system;

import entities.*;

public abstract class Event {
	public final String type; // Used for debugging only
	protected float eventTime;
	protected Packet packet;
	protected int nodeID;

	public Event(String type, float eventTime, int nodeID, Packet packet) {
		this.eventTime = eventTime;
		this.nodeID = nodeID;
		this.type = type;
		this.packet = packet;
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

	public abstract Network execute(Network net);
}
