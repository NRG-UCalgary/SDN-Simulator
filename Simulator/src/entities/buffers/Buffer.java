package entities.buffers;

import entities.Entity;
import system.*;

public abstract class Buffer extends Entity {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + capacity;
		result = prime * result + ((ccToken == null) ? 0 : ccToken.hashCode());
		result = prime * result + maxOccupancy;
		result = prime * result + Float.floatToIntBits(mostRecentSegmentDepartureTime);
		result = prime * result + occupancy;
		result = prime * result + policy;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Buffer other = (Buffer) obj;
		if (capacity != other.capacity)
			return false;
		if (ccToken == null) {
			if (other.ccToken != null)
				return false;
		} else if (!ccToken.equals(other.ccToken))
			return false;
		if (maxOccupancy != other.maxOccupancy)
			return false;
		if (Float.floatToIntBits(mostRecentSegmentDepartureTime) != Float
				.floatToIntBits(other.mostRecentSegmentDepartureTime))
			return false;
		if (occupancy != other.occupancy)
			return false;
		if (policy != other.policy)
			return false;
		return true;
	}

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
