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
		double timeToEmpty = 0;
		double ccDelay = 0;
		if (isFull()) {
			waitTime = Double.NEGATIVE_INFINITY;
		} else {
			occupancy++;
			timeToEmpty = mostRecentSegmentDepartureTime - currentTime;
			if (timeToEmpty <= 0) {
				timeToEmpty = 0;
				waitTime = 0;
			} else {
				waitTime = timeToEmpty;
			}
			if ((ccDelay = ccToken.getCongestionControlDelay(currentTime)) > 0) {
				if (ccDelay >= timeToEmpty) {
					waitTime = ccDelay;
				} else {
					waitTime = timeToEmpty;
				}
			}
			mostRecentSegmentDepartureTime = currentTime + waitTime + segmentTransmissionDelay;
		}
		return waitTime;

	}

	/* --------------------------------------------------- */

	/* Called by SDNSwitch when executing the control message from controller */
	public void updateCCToken(double arrivalToBufferTime, BufferToken token) {
		token.setArrivalToBufferTime(arrivalToBufferTime);
		this.ccToken = token;
	}

}
