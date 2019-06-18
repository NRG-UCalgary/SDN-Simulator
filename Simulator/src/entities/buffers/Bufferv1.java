package entities.buffers;

import java.util.ArrayList;

import system.utility.Keywords;

public class Bufferv1 extends Buffer {

	public Bufferv1(int capacity, int bufferPolicy) {
		super(capacity, bufferPolicy);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Buffer) -------- */
	/* --------------------------------------------------- */

	public double getBufferTime(double currentTime, int segmentType, double segmentTransmissionDelay,
			boolean isHoldingACK) {
		if (isFull()) {
			return Double.NEGATIVE_INFINITY;
		} else {
			switch (segmentType) {
			case Keywords.ACK:
				if (isHoldingACK) {
					return getACKWaitTime(currentTime, segmentTransmissionDelay);
				} else {
					return getWaitTime(currentTime, segmentTransmissionDelay);
				}

			default:
				// Anything other type than ACK
				return getWaitTime(currentTime, segmentTransmissionDelay);
			}
		}
	}

	/* --------------------------------------------------- */

	/* Called by SDNSwitch when executing the control message from controller */
	public void updateTokenList(ArrayList<BufferToken> tokens) {
		this.releaseTokens = tokens;
	}

	public double getACKWaitTime(double currentTime, double transmissionDelay) {
		double waitTime = 0;
		double timeToEmpty = 0;

		if (this.releaseTokens.size() != 0) { // TODO might create a problem

			if (mostRecentACKDeparture > currentTime) {
				timeToEmpty = mostRecentACKDeparture - currentTime;
			}
			if (this.releaseTokens.get(0).ACKsToGo > 0) {
				if (releaseTokens.get(0).isFirst) {
					waitTime = releaseTokens.get(0).waitTime;
					releaseTokens.get(0).isFirst = false;
				}
				waitTime += timeToEmpty;
				(this.releaseTokens.get(0).ACKsToGo)--;
			} else {
				releaseTokens.remove(0);
				waitTime = getACKWaitTime(currentTime, transmissionDelay);
			}
			mostRecentACKDeparture = currentTime + waitTime + transmissionDelay;
		}
		return waitTime;
	}

}
