package system.events;

import entities.Segment;
import system.Event;
import system.Network;
import system.utility.Keywords;

public class ArrivalToHost extends Event {

	public ArrivalToHost(double startTime, int switchID, Segment segment) {
		super(Keywords.ArrivalToHost, startTime, switchID, segment);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		// Debugger.event(this.type, this.time, this.nodeID, this.segment, null);
		net.updateTime(eventTime);
		return net.hosts.get(nodeID).recvSegment(net, segment);
	}
}
