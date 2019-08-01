package simulator.events.arrivals;

import simulator.Network;
import simulator.entities.Packet;
import simulator.events.PacketArrival;

public class ToController extends PacketArrival {

	public ToController(float eventTime, int nodeID, Packet packet) {
		super(eventTime, nodeID, packet);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public void execute(Network net) {
		net.updateTime(eventTime);
		net.controller.recvPacket(net, nodeID, packet);
	}

}
