package system.events;

import entities.Segment;
import system.*;
import system.utility.Keywords;

public class Departure extends Event {

	public Departure(double startTime, int switchID, Segment segment) {
		super(Keywords.Departure, startTime, switchID, segment);
	}

	/* --------------------------------------------------- */
	/* ---------- Inherited methods (from Event) --------- */
	/* --------------------------------------------------- */
	public Network execute(Network net) {
		//Debugger.event(this.type, this.time, this.nodeID, this.segment, null);
		net.updateTime(time);
		net.switches.get(this.nodeID).releaseSegment(net, segment);
		return net;
	}
}
