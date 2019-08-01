package simulator.entities.links;

import simulator.entities.Link;
import simulator.entities.Packet;
import utility.Mathematics;

public class DefaultLink extends Link {

	public DefaultLink(int ID, int sourceID, int destinationID, float propagationDelay, float band, int bufferSize,
			int bufferPolicy) {
		super(ID, sourceID, destinationID, propagationDelay, band, bufferSize, bufferPolicy);
	}

	public float bufferPacket(float currentTime, Packet packet) {
		if (packet.segment != null) {
			updateSegementArrivalToLinkCounters(currentTime, packet.segment.getFlowID());
		}
		return buffer.enQueue(currentTime, getTransmissionDelay(packet.getSize()));
	}

	public float transmitPacket(float currentTime, Packet packet) {
		float transmissionDelay = getTransmissionDelay(packet.getSize());
		buffer.deQueue();
		if (packet.segment != null) {
			updateUtilizationCounters(currentTime, packet.segment.getFlowID(), transmissionDelay);
		}
		return Mathematics.addFloat(transmissionDelay, propagationDelay);
	}
}
