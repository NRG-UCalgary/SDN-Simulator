package system.events;

import entities.*;
import system.Event;
import system.Network;

public class ArrivalToHost extends Event {

	public ArrivalToHost(float eventTime, int hostID, Packet packet) {
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
