package system;

import entities.*;

public abstract class Event {

	protected float eventTime;
	protected Packet packet;
	protected int nodeID;

	public Event(float eventTime, int nodeID, Packet packet) {
		this.eventTime = eventTime;
		this.nodeID = nodeID;
		this.packet = packet;
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

	public abstract void execute(Network net);
}
