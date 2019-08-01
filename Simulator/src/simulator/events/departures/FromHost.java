package simulator.events.departures;

import simulator.Network;
import simulator.entities.Packet;
import simulator.events.PacketDeparture;

public class FromHost extends PacketDeparture {

	int dstSwitchID;

	public FromHost(float eventTime, int hostID, int dstSwitchID, Packet packet) {
		super(eventTime, hostID, packet);
		this.dstSwitchID = dstSwitchID;
	}

	public void execute(Network net) {
		net.updateTime(eventTime);
		net.hosts.get(this.nodeID).releasePacket(net, dstSwitchID, packet);
	}

}
