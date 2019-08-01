package simulator.events.departures;

import simulator.Network;
import simulator.entities.Packet;
import simulator.events.PacketDeparture;

public class FromSwitch extends PacketDeparture {
	int dstNodeID;

	public FromSwitch(float eventTime, int switchID, int dstNodeID, Packet packet) {
		super(eventTime, switchID, packet);
		this.dstNodeID = dstNodeID;
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public void execute(Network net) {
		net.updateTime(eventTime);
		net.switches.get(this.nodeID).releasePacket(net, dstNodeID, packet);
	}
}
