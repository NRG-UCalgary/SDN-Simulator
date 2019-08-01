package simulator.events.arrivals;

import simulator.Network;
import simulator.entities.Packet;
import simulator.events.PacketArrival;

public class ToSwitch extends PacketArrival {

	public ToSwitch(float eventTime, int switchID, Packet packet) {
		super(eventTime, switchID, packet);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public void execute(Network net) {
		net.updateTime(eventTime);
		net.switches.get(this.nodeID).recvPacket(net, packet);
	}

}
