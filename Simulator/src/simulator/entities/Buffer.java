package simulator.entities;

import simulator.entities.buffers.BufferToken;
import system.Main;

public abstract class Buffer extends Entity {

	protected final int capacity;
	public BufferToken ccToken;
	/** ========== Statistical Counters ========== **/
	public int maxOccupancy;
	public float mostRecentSegmentDepartureTime;
	public int occupancy;

	protected int policy;

	/** ========================================== **/

	public Buffer(int capacity, int policy) {
		super(-1);
		this.capacity = capacity;
		this.policy = policy;
		occupancy = 0;
		mostRecentSegmentDepartureTime = 0;
		ccToken = new BufferToken();

		/** ========== Statistical Counters ========== **/
		maxOccupancy = 0;
		/** ========================================== **/
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

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
			Main.error("Buffer", "deQueue", "Invalid buffer occupancy(" + occupancy + ").");
		}
	}

	public abstract float enQueue(float currentTime, float packetTransmissionDelay);

	public boolean isFull() {
		if (occupancy < capacity) {
			return false;
		} else if (occupancy == capacity) {
			return true;
		}
		Main.error("Buffer", "isFull", "Invalid buffer occupancy(" + occupancy + ").");
		return false;
	}

	public abstract void updateCCToken(float arrivalToBufferTime, BufferToken token);
}
