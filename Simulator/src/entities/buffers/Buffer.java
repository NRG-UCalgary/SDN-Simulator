package entities.buffers;

import entities.Entity;
import system.*;

public abstract class Buffer extends Entity {

	protected final int capacity;
	protected int policy;
	public int occupancy;
	public float mostRecentSegmentDepartureTime;
	public BufferToken ccToken;

	/** ========== Statistical Counters ========== **/
	public int maxOccupancy;

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

	public abstract void updateCCToken(float arrivalToBufferTime, BufferToken token);

	public abstract float getBufferTime(float currentTime, float segmentTransmissionDelay);

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

	public boolean isFull() {
		if (occupancy < capacity) {
			return false;
		} else if (occupancy == capacity) {
			return true;
		}
		Main.error("Buffer", "isFull", "Invalid buffer occupancy(" + occupancy + ").");
		return false;
	}
}
