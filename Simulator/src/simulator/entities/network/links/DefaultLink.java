package simulator.entities.network.links;

import simulator.Network;
import simulator.entities.network.Link;
import simulator.entities.traffic.Packet;
import simulator.events.ArrivalToNode;
import simulator.events.DepartureFromNode;
import utility.Debugger;
import utility.Mathematics;

public class DefaultLink extends Link {

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
			float nextTime = Mathematics.addFloat(net.getCurrentTime(), bufferTime);
			net.eventList.addEvent(new DepartureFromNode(nextTime, this.ID, packet));

			if (packet.segment == null) {
				Debugger.debugToConsole(
						"   Contrl message buffered by link: " + ID + " at time: " + net.getCurrentTime());
				Debugger.debugToConsole("   The message will be transmitted at: " + nextTime);
			}
			/** ===== Statistical Counters ===== **/
			// TODO what will you do about statistical counters
			if (isMonitored) {
				net.hosts.get(packet.segment.getSrcHostID()).updateFlowTotalBufferTime(bufferTime);
			}
			/** ================================ **/

		} else {
			// Packet Drop happens here
		}
	}

	public void transmitPacket(Network net, Packet packet) {
		/** ===== Statistical Counters ===== **/
		// TODO what will you do about statistical counters
		if (isMonitored) {
			net.hosts.get(packet.segment.getSrcHostID()).updateDataSegmentsDepartures(packet.segment.getSeqNum(),
					net.getCurrentTime());
		}
		/** ================================ **/
		float transmissionDelay = getTransmissionDelay(packet.getSize());
		buffer.deQueue();
		if (packet.segment != null) {
			updateUtilizationCounters(net.getCurrentTime(), packet.segment.getFlowID(), transmissionDelay);
		}

		float nextTime = Mathematics.addFloat(net.getCurrentTime(), getTotalDelay(packet.getSize()));
		net.eventList.addEvent(new ArrivalToNode(nextTime, srcNodeID, dstNodeID, packet));

		if (packet.segment == null) {
			Debugger.debugToConsole(
					"Contrl message starts to trasnmitted by link: " + ID + " at time: " + net.getCurrentTime());
			Debugger.debugToConsole("The message will arrive to node: " + dstNodeID + " at: " + nextTime);
		}

	}
}
