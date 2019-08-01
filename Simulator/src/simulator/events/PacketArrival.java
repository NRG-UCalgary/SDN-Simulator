package simulator.events;

import simulator.Event;
import simulator.entities.Packet;

public abstract class PacketArrival extends Event {

	public PacketArrival(float eventTime, int nodeID, Packet packet) {
		super(eventTime, nodeID, packet);
	}

}
