package buffers;

import entities.Buffer;
import system.Keywords;

public class Bufferv1 extends Buffer {

	public Bufferv1(int capacity, int bufferPolicy) {
		super(capacity, bufferPolicy);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Buffer) -------- */
	/* --------------------------------------------------- */
	public double getWaitTime() {

		return 0;
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

	public void deQueue() {
		if (occupancy > 0) {
			occupancy--;
		} else if (occupancy < 0) {
			System.out.println("Error Class::Buffer--Invalid buffer occupancy(" + occupancy + ").");
		}
	}

	/* --------------------------------------------------- */
	public double getWaitTime(double currentTime, double transmissionTime) {
		occupancy++;
		double waitTime;
		if (occupancy == 1) {
			waitTime = 0;
		} else {
			waitTime = mostRecntSegmentDepartureTime - currentTime;
		}
		mostRecntSegmentDepartureTime = currentTime + waitTime + transmissionTime;
		return waitTime;
	}

	public double getACKWaitTime(double currentTime, double transmissionTime) {
		switch (this.mode) {
		case Keywords.FlushBuffer:
			return getWaitTime(currentTime, transmissionTime);
		case Keywords.TokenBasedBuffer:
			this.mode = Keywords.FlushBuffer;
			return releaseToken.time - currentTime;
		default:
			return Double.NEGATIVE_INFINITY;
		}
	}

	public void setReleaseTokenTime(double time) {
		this.releaseToken.time = time;
	}

}
