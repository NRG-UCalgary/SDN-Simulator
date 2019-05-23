package entities;

import java.util.ArrayList;

public abstract class Buffer extends Entity {

	protected final int capacity;
	protected int policy;
	public int occupancy;
	public double mostRecentSegmentDepartureTime;
	public double mostRecentACKDeparture;

	public ArrayList<BufferToken> releaseTokens;

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

	public abstract double getBufferTime(double currentTime, int segmentType, double segmentTransmissionDelay);

	public abstract double getACKWaitTime(double currentTime, double transmissionTime);

	/* --------------------------------------------------- */
	/* ---------- Implemented methods -------------------- */
	/* --------------------------------------------------- */
	public double getWaitTime(double currentTime, double transmissionTime) {
		occupancy++;
		double waitTime;
		double timeToEmpty = 0;
		if (mostRecentSegmentDepartureTime > currentTime) { // TODO probably is not needed
			timeToEmpty = mostRecentSegmentDepartureTime - currentTime;
		}

		if (occupancy == 1) {
			waitTime = 0;
		} else {
			waitTime = timeToEmpty;
		}
		mostRecentSegmentDepartureTime = currentTime + waitTime + transmissionTime;
		return waitTime;
	}

	public void deQueue() {
		if (occupancy > 0) {
			occupancy--;
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
