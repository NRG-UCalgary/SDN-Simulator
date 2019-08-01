package simulator;

import simulator.entities.Packet;

public abstract class Event {

	protected float eventTime;
	protected int nodeID;
	protected Packet packet;

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
