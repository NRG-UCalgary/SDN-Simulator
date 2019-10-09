package simulator.entities.network.links;

import simulator.Network;
import simulator.entities.network.Link;
import simulator.entities.traffic.Packet;
import simulator.events.*;
import utility.*;

public class DefaultLink extends Link {
	boolean flag = false;

	public DefaultLink(int ID, int sourceID, int destinationID, float propagationDelay, float band, short bufferType,
			int bufferSize, int bufferPolicy) {
		super(ID, sourceID, destinationID, propagationDelay, band, bufferType, bufferSize, bufferPolicy);
	}

	public void bufferPacket(Network net, Packet packet) {

		if (packet.segment != null) {
			updateSegementArrivalToLinkCounters(net.getCurrentTime(), packet.segment.getFlowID());
		}

		float bufferTime = buffer.enQueue(net.getCurrentTime(), getTransmissionDelay(packet.getSize()));
		if (bufferTime >= 0) {
			float transmissionDelay = getTransmissionDelay(packet.getSize());
			float nextTime = Mathematics.addFloat(net.getCurrentTime(), bufferTime);
			nextTime = Mathematics.addFloat(nextTime, transmissionDelay);
			net.eventList.addEvent(new DepartureFromNode(nextTime, this.ID, packet));

			/** ===== Statistical Counters ===== **/
			if (isMonitored) {
				if (packet.segment != null) {
					net.hosts.get(packet.segment.getSrcHostID()).updateFlowTotalBufferTime(bufferTime);
					net.hosts.get(packet.segment.getSrcHostID())
							.updateDataSegmentsDepartures(packet.segment.getSeqNum(), net.getCurrentTime());
					updateUtilizationCounters(net.getCurrentTime(), packet.segment.getFlowID(), transmissionDelay);
					updateQueueLenghtCounter(net.getCurrentTime(), buffer.occupancy);
				}

			}
			/** ================================ **/
		} else {
			Debugger.debugToConsole("Packet drop happens");
			// Packet Drop happens here
		}
	}

	public void transmitPacket(Network net, Packet packet) {
		buffer.deQueue();
		float nextTime = Mathematics.addFloat(net.getCurrentTime(), getPropagationDelay());
		net.eventList.addEvent(new ArrivalToNode(nextTime, srcNodeID, dstNodeID, packet));
		/** ===== Statistical Counters ===== **/
		updateQueueLenghtCounter(net.getCurrentTime(), buffer.occupancy);
		updateMaxQueueLengthCounter(buffer.occupancy);
		/** ================================ **/
	}
}
