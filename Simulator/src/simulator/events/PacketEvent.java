package simulator.events;

import simulator.Event;
import simulator.entities.traffic.Packet;

public abstract class PacketEvent extends Event {

	Packet packet;

	public PacketEvent(float eventTime, Packet packet) {
		super(eventTime);
		this.packet = packet;
	}

}
