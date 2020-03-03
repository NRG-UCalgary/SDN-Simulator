package nrg.sdnsimulator.core.entity.network;

import nrg.sdnsimulator.core.entity.Entity;
import nrg.sdnsimulator.core.entity.network.buffer.BufferToken;

public abstract class Buffer extends Entity {

	protected final int capacity;
	protected BufferToken ccToken;
	protected int maxOccupancy;
	protected float mostRecentSegmentDepartureTime;
	protected int occupancy;
	protected int policy;

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

	public void deQueue() {
		if (occupancy > 0) {
			occupancy--;
			if (occupancy == 0) {
				mostRecentSegmentDepartureTime = 0;
			}
		} else if (occupancy < 0) {
		}
	}

	public abstract float enQueue(float currentTime, float packetTransmissionDelay);

	public boolean isFull() {
		if (occupancy < capacity) {
			return false;
		} else if (occupancy == capacity) {
			return true;
		}
		return false;
	}

	public abstract void updateCCToken(float arrivalToBufferTime, BufferToken token);

	public BufferToken getCcToken() {
		return ccToken;
	}

	public void setCcToken(BufferToken ccToken) {
		this.ccToken = ccToken;
	}

	public int getMaxOccupancy() {
		return maxOccupancy;
	}

	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}

	public float getMostRecentSegmentDepartureTime() {
		return mostRecentSegmentDepartureTime;
	}

	public void setMostRecentSegmentDepartureTime(float mostRecentSegmentDepartureTime) {
		this.mostRecentSegmentDepartureTime = mostRecentSegmentDepartureTime;
	}

	public int getOccupancy() {
		return occupancy;
	}

	public void setOccupancy(int occupancy) {
		this.occupancy = occupancy;
	}

	public int getPolicy() {
		return policy;
	}

	public void setPolicy(int policy) {
		this.policy = policy;
	}

	public int getCapacity() {
		return capacity;
	}

}
