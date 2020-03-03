package nrg.sdnsimulator.core.event;

import nrg.sdnsimulator.core.Event;
import nrg.sdnsimulator.core.entity.traffic.Packet;

public abstract class PacketEvent extends Event {

	protected Packet packet;

	public PacketEvent(float eventTime, Packet packet) {
		super(eventTime);
		this.packet = packet;
	}

}
