package simulator.events.arrivals;

import simulator.Network;
import simulator.entities.Packet;
import simulator.events.PacketArrival;

public class ToHost extends PacketArrival {

	public ToHost(float eventTime, int hostID, Packet packet) {
		super(eventTime, hostID, packet);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public void execute(Network net) {
		net.updateTime(eventTime);
		net.hosts.get(nodeID).recvPacket(net, packet);
	}
}
