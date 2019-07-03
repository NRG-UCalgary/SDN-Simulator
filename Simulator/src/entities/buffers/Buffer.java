package entities.buffers;

import entities.Entity;

public abstract class Buffer extends Entity {

	protected final int capacity;
	protected int policy;
	public int occupancy;
	public double mostRecentSegmentDepartureTime;
	public BufferToken ccToken;

	public Buffer(int cap, int policy) {
		super(-1);
		this.capacity = cap;
		this.policy = policy;
		occupancy = 0;
		mostRecentSegmentDepartureTime = 0;
		ccToken = new BufferToken();
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

	public abstract void updateTokenList(double arrivalToBufferTime, BufferToken token);

	public abstract double getBufferTime(double currentTime, double segmentTransmissionDelay);

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */
	public void deQueue() {
		if (occupancy > 0) {
			occupancy--;
			if (occupancy == 0) {
				mostRecentSegmentDepartureTime = 0;
			}
		} else if (occupancy < 0) {
			System.out.println("Error Class::Buffer--Invalid buffer occupancy(" + occupancy + ").");
		}
	}

	public boolean isFull() {
		if (occupancy < capacity) {
			return false;
		} else if (occupancy == capacity) {
			return true;
		}
		System.out.println("Error Class::Buffer--Invalid buffer occupancy(" + occupancy + ").");
		return false;
	}
}
