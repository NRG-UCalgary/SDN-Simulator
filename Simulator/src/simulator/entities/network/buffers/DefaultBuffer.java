package simulator.entities.network.buffers;

import simulator.entities.network.Buffer;
import utility.Mathematics;

public class DefaultBuffer extends Buffer {

	public DefaultBuffer(int capacity, int bufferPolicy) {
		super(capacity, bufferPolicy);
	}

	public float enQueue(float currentTime, float packetTransmissionDelay) {
		float waitTime = 0;
		if (isFull()) {
			waitTime = Float.NEGATIVE_INFINITY;
		} else {
			occupancy++;
			waitTime = Mathematics.subtractFloat(mostRecentSegmentDepartureTime, currentTime);
			if (waitTime <= 0) {
				waitTime = 0;
			}
			mostRecentSegmentDepartureTime = Mathematics.addFloat(currentTime,
					Mathematics.addFloat(waitTime, packetTransmissionDelay));
		}
		return waitTime;
	}

	public void updateCCToken(float arrivalToBufferTime, BufferToken token) {
		// SHould be eliminated in future
	}

}
