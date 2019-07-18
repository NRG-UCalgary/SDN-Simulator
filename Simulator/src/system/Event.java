package system;

import entities.*;

public abstract class Event {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(eventTime);
		result = prime * result + nodeID;
		result = prime * result + ((packet == null) ? 0 : packet.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (Float.floatToIntBits(eventTime) != Float.floatToIntBits(other.eventTime))
			return false;
		if (nodeID != other.nodeID)
			return false;
		if (packet == null) {
			if (other.packet != null)
				return false;
		} else if (!packet.equals(other.packet))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

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
