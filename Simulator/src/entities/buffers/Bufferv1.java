package entities.buffers;

import system.utility.Debugger;

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
					Debugger.debug("ccDelay: " + ccDelay + "  timeToEmpty: " + timeToEmpty);
				} else {
					waitTime = timeToEmpty;
				}
			}
			mostRecentSegmentDepartureTime = currentTime + waitTime + segmentTransmissionDelay;
		}
		Debugger.debug("This is waitTime: " + waitTime);
		Debugger.debug("This is departure time: " + mostRecentSegmentDepartureTime);
		return waitTime;

	}

	/* --------------------------------------------------- */

	/* Called by SDNSwitch when executing the control message from controller */
	public void updateTokenList(double arrivalToBufferTime, BufferToken token) {
		token.setArrivalToBufferTime(arrivalToBufferTime);
		this.ccToken = token;
	}

}
