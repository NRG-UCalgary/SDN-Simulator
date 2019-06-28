package system.events;

import entities.*;
import system.Event;
import system.Network;
import system.utility.Keywords;

public class DepartureFromHost extends Event {

	public DepartureFromHost(double eventTime, int hostID, Segment segment) {
		super(Keywords.DepartureFromHost, eventTime, hostID, segment);
	}

	public Network execute(Network net) {

		net.updateTime(eventTime);
		net.hosts.get(this.nodeID).releaseSegment(net, segment);

		return net;
	}

}
