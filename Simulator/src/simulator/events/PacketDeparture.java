package simulator.events;

import simulator.Event;
import simulator.entities.Packet;

public abstract class PacketDeparture extends Event {

	public PacketDeparture(float eventTime, int nodeID, Packet packet) {
		super(eventTime, nodeID, packet);
	}

}
