package simulator.entities.network.buffers;

import simulator.entities.network.Buffer;
import utility.Mathematics;

public class Bufferv1 extends Buffer {

	public Bufferv1(int capacity, int bufferPolicy) {
		super(capacity, bufferPolicy);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Buffer) -------- */
	/* --------------------------------------------------- */

	public float enQueue(float currentTime, float segmentTransmissionDelay) {
		float waitTime = 0;
		float ccDelay = 0;
		if (isFull()) {
			waitTime = Float.NEGATIVE_INFINITY;
		} else {
			occupancy++;
			waitTime = Mathematics.subtractFloat(mostRecentSegmentDepartureTime, currentTime);
			if (waitTime <= 0) {
				waitTime = 0;
			}
			if ((ccDelay = ccToken.getCongestionControlDelay(currentTime)) > 0) {
				if (ccDelay >= waitTime) {
					waitTime = ccDelay;
				}
			}
			mostRecentSegmentDepartureTime = Mathematics.addFloat(currentTime,
					Mathematics.addFloat(waitTime, segmentTransmissionDelay));
		}
		return waitTime;

	}

	public void updateCCToken(float arrivalToBufferTime, BufferToken ccToken) {
		ccToken.setArrivalToBufferTime(arrivalToBufferTime);
		this.ccToken = ccToken;
	}

}
