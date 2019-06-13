package events;

import entities.Segment;
import system.Event;
import system.Network;

public class ArrivalToHost extends Event {

	public ArrivalToHost(double startTime, int switchID, Segment segment) {
		super(startTime, switchID, segment);
		name = "Arrival to Host";
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		net.updateTime(currentTime);
		return net.hosts.get(currentNodeID).recvSegment(net, currentSegment);
	}
}
