package entities.buffers;

public class Bufferv1 extends Buffer {

	public Bufferv1(int capacity, int bufferPolicy) {
		super(capacity, bufferPolicy);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Buffer) -------- */
	/* --------------------------------------------------- */

	public float getBufferTime(float currentTime, float segmentTransmissionDelay) {
		float waitTime = 0;
		float timeToEmpty = 0;
		float ccDelay = 0;
		if (isFull()) {
			waitTime = Float.NEGATIVE_INFINITY;
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
	public void updateCCToken(float arrivalToBufferTime, BufferToken ccToken) {
		ccToken.setArrivalToBufferTime(arrivalToBufferTime);
		this.ccToken = ccToken;
	}

}
