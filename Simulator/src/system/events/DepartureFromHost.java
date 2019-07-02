package system.events;

import entities.*;
import system.Event;
import system.Network;
import system.utility.Keywords;

public class DepartureFromHost extends Event {

	int dstSwitchID;

	public DepartureFromHost(double eventTime, int hostID, int dstSwitchID, Packet packet) {
		super(Keywords.DepartureFromHost, eventTime, hostID, packet);
		this.dstSwitchID = dstSwitchID;
	}

	public Network execute(Network net) {
		net.updateTime(eventTime);
		net.hosts.get(this.nodeID).releasePacket(net, dstSwitchID, packet);
		return net;
	}

}
