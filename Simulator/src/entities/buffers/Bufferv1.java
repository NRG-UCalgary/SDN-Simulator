package entities.buffers;

public class Bufferv1 extends Buffer {

	public Bufferv1(int capacity, int bufferPolicy) {
		super(capacity, bufferPolicy);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Buffer) -------- */
	/* --------------------------------------------------- */

	public double getBufferTime(double currentTime, double segmentTransmissionDelay) {
		double waitTime = 0;
		if (isFull()) {
			waitTime = Double.NEGATIVE_INFINITY;
		} else {
			occupancy++;
			if (mostRecentSegmentDepartureTime < currentTime) {
				waitTime = 0;
			} else {
				waitTime = currentTime - mostRecentSegmentDepartureTime;
			}
			waitTime += ccToken.getCongestionControlDelay(currentTime);
			mostRecentSegmentDepartureTime = currentTime + waitTime + segmentTransmissionDelay;

		}
		return waitTime;
	}

	/* --------------------------------------------------- */

	/* Called by SDNSwitch when executing the control message from controller */
	public void updateTokenList(double arrivalToBufferTime, BufferToken token) {
		token.setArrivalToBufferTime(arrivalToBufferTime);
		this.ccToken = token;
	}

}
