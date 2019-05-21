package entities;

public abstract class Buffer extends Entity {

	public int mode;
	protected final int capacity;
	protected int policy;
	public int occupancy;
	public double mostRecntSegmentDepartureTime;

	public BufferToken releaseToken;

	public Buffer(int cap, int policy) {
		super(-1);
		this.capacity = cap;
		this.policy = policy;
		occupancy = 0;
		releaseToken = new BufferToken(-1);
	}

	/* --------------------------------------------------- */
	/* ---------- Abstract methods ----------------------- */
	/* --------------------------------------------------- */

	public abstract double getWaitTime(double currentTime, double transmissionTime);

	public abstract double getACKWaitTime(double currentTime, double transmissionTime);

	public abstract boolean isFull();

	public abstract void deQueue();

	public abstract void setReleaseTokenTime(double time);
}
