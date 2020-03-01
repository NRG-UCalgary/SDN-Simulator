package ca.ucalgary.cpsc.nrg.sdnsimulator.core.event;

import ca.ucalgary.cpsc.nrg.sdnsimulator.core.Event;
import ca.ucalgary.cpsc.nrg.sdnsimulator.core.entity.traffic.Packet;

public abstract class PacketEvent extends Event {

	Packet packet;

	public PacketEvent(float eventTime, Packet packet) {
		super(eventTime);
		this.packet = packet;
	}

}
