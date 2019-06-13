package events;

import entities.Segment;
import system.*;

public class Departure extends Event {

	public Departure(double startTime, int switchID, Segment segment) {
		super(startTime, switchID, segment);
		name = "Departure";
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		net.updateTime(currentTime);
		net.switches.get(this.currentNodeID).releaseSegment(net, currentSegment);
		return net;
	}
}
