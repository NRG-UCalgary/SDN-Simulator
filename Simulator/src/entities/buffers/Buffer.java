package entities.buffers;

import java.util.ArrayList;

import entities.Entity;

public abstract class Buffer extends Entity {

	protected final int capacity;
	protected int policy;
	public int occupancy;
	public double mostRecentSegmentDepartureTime;
	public double mostRecentACKDeparture;

	// Solution A
	public ArrayList<BufferToken> releaseTokens;
	// Solution B
	public BufferToken congestionControlToken;

	public Buffer(int cap, int policy) {
		super(-1);
		this.capacity = cap;
		this.policy = policy;
		occupancy = 0;
		mostRecentSegmentDepartureTime = 0;
		mostRecentACKDeparture = 0;
		releaseTokens = new ArrayList<BufferToken>();
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */
	public abstract void updateTokenList(ArrayList<BufferToken> tokens);

	public abstract double getBufferTime(double currentTime, int segmentType, double segmentTransmissionDelay,
			boolean isHodlingACK);

	public abstract double getACKWaitTime(double currentTime, double transmissionTime);

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */
	public double getWaitTime(double currentTime, double transmissionTime) {
		occupancy++;
		double timeToEmpty;
		if (mostRecentSegmentDepartureTime > currentTime) { // TODO probably is not needed
			timeToEmpty = mostRecentSegmentDepartureTime - currentTime;
		} else {
			timeToEmpty = 0;
		}
		mostRecentSegmentDepartureTime = currentTime + timeToEmpty + transmissionTime;
		return timeToEmpty;
	}

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
