package system.events;

import entities.*;
import system.Event;
import system.Network;
import system.utility.Keywords;

public class ArrivalToHost extends Event {

	public ArrivalToHost(double startTime, int hostID, Packet packet) {
		super(Keywords.ArrivalToHost, startTime, hostID, packet);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		net.updateTime(eventTime);
		return net.hosts.get(nodeID).recvPacket(net, packet);
	}
}
