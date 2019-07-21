package system.events;

import entities.*;
import system.Event;
import system.Network;

public class DepartureFromHost extends Event {

	int dstSwitchID;

	public DepartureFromHost(float eventTime, int hostID, int dstSwitchID, Packet packet) {
		super(eventTime, hostID, packet);
		this.dstSwitchID = dstSwitchID;
	}

	public void execute(Network net) {
		net.updateTime(eventTime);
		net.hosts.get(this.nodeID).releasePacket(net, dstSwitchID, packet);
	}

}
